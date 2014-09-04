package tw.jms.loyal.property;

public class EnvProperty {
	
	protected static ConfigProperty bundleProperty = new ConfigProperty();

	public EnvProperty() {
	}

	public static void setBundleNames(String... bundleNames) {
		bundleProperty = new ConfigProperty(bundleNames);
	}

	public static String getString(String key) {
		return bundleProperty.getString(key);
	}

	public static String getString(String key, Object... params) {
		return bundleProperty.getString(key, params);
	}

	public static int getInt(String key) {
		return bundleProperty.getInt(key);
	}

	public static boolean getBoolean(String key) {
		return bundleProperty.getBoolean(key);
	}
}