package tw.jms.loyal.property;


import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

public class ConfigProperty {
	private static String DEFAULT_BUNDLE_NAME = "config-default";
	private static String OVERWRITE_BUNDLE_NAME = "config";
	private String[] bundleNames;
	private final Map<String, ResourceBundle> cachedResourceBundles = new HashMap<String, ResourceBundle>();
	private final PropertyPlaceholderResolver resolver = new PropertyPlaceholderResolver(
			this);
	private Properties properties = new Properties();

	public ConfigProperty(String... inputBundles) {
		this.bundleNames = new String[2 + inputBundles.length];

		this.bundleNames[0] = DEFAULT_BUNDLE_NAME;
		this.bundleNames[1] = OVERWRITE_BUNDLE_NAME;

		System.arraycopy(inputBundles, 0, this.bundleNames, 2,
				inputBundles.length);
	}

	public String getString(String key) {
		String value = getMessage(key);
		if (value != null) {
			value = resolver.replacePlaceholders(value);
		}

		if (value != null) {
			return value.trim();
		} else {
			return value;
		}
	}

	public String getString(String key, Object... params) {
		String message = getString(key);
		return MessageFormat.format(message, params);
	}

	public void setString(String key, String value) {
		properties.setProperty(key, value);
	}

	public int getInt(String key) {
		int value = 0;
		String numStr = getString(key);

		try {
			if (numStr != null && !"".equals(numStr.trim())) {
				value = Integer.valueOf(numStr.trim());
			}
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}

		return value;
	}

	public void setInt(String key, int value) {
		setString(key, String.valueOf(value));
	}

	public boolean getBoolean(String key) {
		return "true".equalsIgnoreCase(getString(key));
	}

	public void setBoolean(String key, boolean value) {
		setString(key, String.valueOf(value));
	}

	Properties getProperties() {
		return properties;
	}

	private String getMessage(String code) throws IllegalArgumentException {
		if (code == null) {
			return null;
		}

		String msg = resolveCodeWithoutArguments(code);
		if (msg != null) {
			return msg;
		} else {
			return null;
		}
	}

	private String resolveCodeWithoutArguments(String code) {
		String result = properties.getProperty(code);
		if (result != null) {
			return result;
		} else {
			for (int i = bundleNames.length - 1; i >= 0; i--) {
				ResourceBundle bundle = getResourceBundle(bundleNames[i]);
				if (bundle != null) {
					result = getStringOrNull(bundle, code);
					if (result != null) {
						return result;
					}
				}
			}
		}

		return null;
	}

	private ResourceBundle getResourceBundle(String basename) {
		synchronized (cachedResourceBundles) {
			ResourceBundle bundle = cachedResourceBundles.get(basename);
			if (bundle != null) {
				return bundle;
			}

			try {
				bundle = ResourceBundle.getBundle(basename);
				cachedResourceBundles.put(basename, bundle);

				return bundle;
			} catch (MissingResourceException ex) {
				ex.printStackTrace();
				return null;
			}
		}
	}

	private String getStringOrNull(ResourceBundle bundle, String key) {
		try {
			return bundle.getString(key);
		} catch (MissingResourceException ex) {
			return null;
		}
	}
}
