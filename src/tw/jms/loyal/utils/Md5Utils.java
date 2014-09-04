package tw.jms.loyal.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class Md5Utils {
	public static String getMD5String(String s) {
		return DigestUtils.md5Hex(s);
	}
}