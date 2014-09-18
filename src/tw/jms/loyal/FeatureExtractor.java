package tw.jms.loyal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class FeatureExtractor {

	private static Logger LOG = Logger.getLogger(FeatureExtractor.class);

	public static String getTitle(String htmlContent) {
		Document doc = Jsoup.parse(htmlContent);
		Elements element = doc.select("font[style~=.*(1[5-9]|2[0-9])pt.*");
		String title = element.text().replace("聖子的話語", "").replace("＜", "")
				.replace("＞", "");
		if (title.isEmpty()) {
			element = doc.select("font[size=5]");
			title = element.text();
		}
		return title;
	}

	private static Pattern datePattern = Pattern
			.compile("(19|20)\\d\\d年(0[1-9]|[1-9]|1[012])月(0[1-9]|[1-9]|[12][0-9]|3[01])日");
	private static SimpleDateFormat fromDateFormat = new SimpleDateFormat(
			"yyyy年M月d日", Locale.TAIWAN);
	private static SimpleDateFormat toDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");
	private static final String[] categories = { "主日", "週三", "箴言", "啟示", "見證",
			"其他" };

	public static String getPublishDate(String fileName, String nonHtmlContent) {
		// LOG.info(nonHtmlContent);
		Matcher m = datePattern.matcher(fileName + " " + nonHtmlContent);
		if (m.find()) {
			try {
				Date date = fromDateFormat.parse(m.group());
				return toDateFormat.format(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}

	public static String getCategory(String fileName, String nonHtmlContent) {
		Map<String, Integer> categoryIndexMap = new HashMap<String, Integer>();
		for (String category : categories) {
			int index = (fileName + " " + nonHtmlContent).indexOf(category);
			if (index >= 0) {
				categoryIndexMap.put(category, index);
			}
		}
		Entry<String, Integer> leastIndexEntry = new SimpleEntry<String, Integer>(
				categories[categories.length - 1], Integer.MAX_VALUE);
		for (Entry<String, Integer> entry : categoryIndexMap.entrySet()) {
			if (entry.getValue() < leastIndexEntry.getValue()) {
				leastIndexEntry = entry;
			}
		}
		if(leastIndexEntry.getKey().equals("見證")){
			return "啟示";
		}
		return leastIndexEntry.getKey();
	}
}
