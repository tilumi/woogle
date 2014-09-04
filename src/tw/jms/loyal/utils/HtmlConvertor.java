package tw.jms.loyal.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

import tw.jms.loyal.property.EnvConstants;
import tw.jms.loyal.property.EnvProperty;

public class HtmlConvertor {

	public static OfficeManager officeManager;
	
	public static String convert(File file, String htmlFolder,  boolean force) throws IOException {
		FileUtils.forceMkdir(LocalPathConstants.HTML_OUTPUT_DIR.toFile());
		if (officeManager == null) {
			officeManager = new DefaultOfficeManagerConfiguration()
					.setOfficeHome(EnvProperty.getString(EnvConstants.OFFICE_HOME))
					.buildOfficeManager();
			officeManager.start();
		}
		String fileanme = file.getName();
		Path outputPath = Paths.get(htmlFolder).resolve(fileanme
				+ ".html");
		if (!outputPath.toFile().exists() || force) {			
			OfficeDocumentConverter converter = new OfficeDocumentConverter(
					officeManager);
			converter.convert(file, outputPath.toFile());
		}
		return FileUtils.readFileToString(outputPath.toFile());
	}

	public static void close() {
		if (officeManager != null)
			officeManager.stop();
	}

}
