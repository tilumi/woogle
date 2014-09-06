package tw.jms.loyal;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class FeatureExtractor {

	public static String getTitle(String htmlContent) {
		Document doc = Jsoup.parse(htmlContent);
		Elements element = doc.select("font[style~=.*(1[5-9]|2[0-9])pt.*");
		return element.text().replace("聖子的話語", "").replace("＜", "")
				.replace("＞", "");
	}
}
