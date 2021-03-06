package tw.jms.loyal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xml.sax.SAXException;

import tw.jms.loyal.dao.ElasticSearchDao;
import tw.jms.loyal.property.EnvConstants;
import tw.jms.loyal.property.EnvProperty;
import tw.jms.loyal.utils.ElasticSearchConnection;
import tw.jms.loyal.utils.HtmlConvertor;
import tw.jms.loyal.utils.IndexConstants;
import tw.jms.loyal.utils.InputParams;
import tw.jms.loyal.utils.Md5Utils;
import tw.jms.loyal.web.controllers.SearchController;

import com.google.common.base.Splitter;
import com.spreada.utils.chinese.ZHConverter;

public class DocumentImporter {

	private String inputFolder;
	private String htmlFolder;
	private boolean force;

	private static Logger LOG = Logger.getLogger(SearchController.class);

	public static void main(String[] args) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException,
			SAXException, TikaException {
		DocumentImporter importer = new DocumentImporter();
		if (setArgs(args, importer)) {
			importer.run();
		}
	}

	private static boolean setArgs(String[] args, Object driver)
			throws IllegalAccessException, InvocationTargetException {
		List<String> argumentsList = getDriverArguments(driver.getClass());
		if (argumentsList.size() != args.length) {
			printDriverUsage(driver.getClass());
			return false;
		}

		Method[] methods = driver.getClass().getMethods();
		for (Method method : methods) {
			InputParams inputParams = method.getAnnotation(InputParams.class);
			if (inputParams != null) {
				Class<?>[] types = method.getParameterTypes();
				Object[] object = new Object[types.length];
				for (int i = 0; i < types.length; i++) {
					String typeStr = types[i].getSimpleName();
					if (typeStr.equals("String")) {
						object[i] = args[i];
					} else if (typeStr.equals("Integer")
							|| typeStr.equals("int")) {
						object[i] = Integer.parseInt(args[i]);
					} else if (typeStr.equals("Long") || typeStr.equals("long")) {
						object[i] = Long.parseLong(args[i]);
					} else if (typeStr.equals("Double")
							|| typeStr.equals("double")) {
						object[i] = Double.parseDouble(args[i]);
					} else if (typeStr.equals("Float")
							|| typeStr.equals("float")) {
						object[i] = Float.parseFloat(args[i]);
					} else if (typeStr.equals("Boolean")
							|| typeStr.equals("boolean")) {
						object[i] = Boolean.parseBoolean(args[i]);
					} else {
						throw new RuntimeException("Unsopported type "
								+ typeStr);
					}
				}
				for (Object o : object) {
					System.out.println(o);
				}
				method.invoke(driver, object);
			}
		}
		return true;
	}

	private static List<String> getDriverArguments(Class<?> driverClass) {
		List<String> result = new ArrayList<String>();
		Method[] methods = driverClass.getMethods();
		String parameters = null;
		for (Method method : methods) {
			InputParams inputParams = method.getAnnotation(InputParams.class);
			if (inputParams != null) {
				parameters = inputParams.parameters();

				Iterator<String> it = Splitter.on(",").trimResults()
						.omitEmptyStrings().split(parameters).iterator();
				result = new ArrayList<String>();
				while (it.hasNext()) {
					result.add(it.next());
				}
			}
		}
		return result;
	}

	private static void printDriverUsage(Class<?> driverClass) {
		System.out.println("Usage: woogle import ");
		List<String> driverArgumentList = getDriverArguments(driverClass);
		for (String driverArg : driverArgumentList) {
			System.out.print("[" + driverArg + "] ");
		}
		System.out.println();
		System.out.println();
	}

	@InputParams(parameters = "inputFolder, htmlFolder, force")
	public void setParameters(String inputFolder, String htmlFolder,
			boolean force) {
		this.inputFolder = inputFolder;
		this.htmlFolder = htmlFolder;
		this.force = force;
	}

	public void run() throws IOException, SAXException, TikaException {
		Collection<File> files = null;
		if (new File(inputFolder).isDirectory()) {
			files = FileUtils.listFiles(new File(inputFolder),
					new String[] { "docx", "doc" }, true);
		} else {
			files = new ArrayList<File>();
			files.add(new File(inputFolder));
		}
		Client client = ElasticSearchConnection.get();
		int i = 1;
		for (File word : files) {
			if (word.getName().contains("韓中")
					|| word.getName().contains("kor_chi")) {
				i++;
				continue;
			}
			LOG.info("Import: " + word.getName());
			InputStream in = new FileInputStream(word);
			BodyContentHandler textHandler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			AutoDetectParser parser = new AutoDetectParser();
			parser.parse(in, textHandler, metadata);
			in.close();
			String content = textHandler.toString();
			String htmlContent = HtmlConvertor.convert(word, htmlFolder, force);
			String title = FeatureExtractor.getTitle(htmlContent);
			String publishDate = FeatureExtractor.getPublishDate(
					word.getName(), content);
			String category = FeatureExtractor.getCategory(word.getName(), content);
			String lastModified = metadata.get(Metadata.LAST_MODIFIED);
			if (publishDate == null || publishDate.isEmpty()) {
				publishDate = lastModified;
			}
			if (title == null || title.isEmpty()) {
				title = word.getName();
			}
			LOG.info("file: " + word.getName());
			importToES(client, title, publishDate, category, lastModified,
					content, htmlContent, force);
			System.out.println("Files to index: " + i + "/" + files.size());
			i++;
		}
		HtmlConvertor.close();
		client.close();
	}

	private static void importToES(Client client, String title,
			String publishDate, String category, String lastModified,
			String content, String htmlContent, boolean force) {
		String id = Md5Utils.getMD5String(content);
		boolean isStoreInSimplifiedChinese = EnvProperty
				.getBoolean(EnvConstants.IS_STORE_IN_SIMPLIFIED_CHINESE);

		if (isStoreInSimplifiedChinese) {
			ZHConverter converter = ZHConverter
					.getInstance(ZHConverter.SIMPLIFIED);
			content = converter.convert(content);
			title = converter.convert(title);
		}

		if (!force) {
			GetResponse getResponse = ElasticSearchDao.get(id);
			if (getResponse.isExists()) {
				return;
			}
		}
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("title", title);
		json.put("content", content);
		json.put("html", htmlContent);
		json.put("category", category);
		json.put("publishDate", publishDate);
		json.put("lastModified", lastModified);
		LOG.info("Category: " + category);
		LOG.info("Title: " + title);
		LOG.info("Publish Date: " + publishDate);
		LOG.info("Last Modified: " + lastModified);
		IndexResponse response = client
				.prepareIndex(IndexConstants.INDEX_PROVIDENCE,
						IndexConstants.TYPE_WORD, id).setSource(json).execute()
				.actionGet();
		System.out.println("ID: " + response.getId());
		System.out.println("Version: " + response.getVersion());
	}
}
