package tw.jms.loyal.utils;

import java.util.Iterator;

import com.google.common.base.Splitter;

public class StringUtils {
	
	public static String getNthToken(String input, int n, String seperator) {
		Iterator<String> it = Splitter.on(seperator).split(input).iterator();
		for (int i = 0; i < (n - 1); i++) {
			if (it.hasNext()) {
				it.next();
			}
		}
		if (it.hasNext()) {
			String result = it.next();
			if (result.equals("\\N")) {
				return null;
			} else {
				return result;
			}
		}
		return null;
	}

}
