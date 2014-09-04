package tw.jms.loyal.property;


import java.util.HashSet;
import java.util.Set;

public class PropertyPlaceholderResolver {
	public static final String PLACEHOLDER_PREFIX = "${";
	public static final String PLACEHOLDER_SUFFIX = "}";
	public static final String VALUE_SEPARATOR = ":";
	private ConfigProperty bundleProperty;

	public PropertyPlaceholderResolver(ConfigProperty bundleProperty) {
		this.bundleProperty = bundleProperty;
	}

	public String replacePlaceholders(String value) {
		return parseStringValue(value, new HashSet<String>());
	}

	private String parseStringValue(String strVal,
			Set<String> visitedPlaceholders) {
		StringBuilder buf = new StringBuilder(strVal);

		int startIndex = strVal.indexOf(PLACEHOLDER_PREFIX);
		while (startIndex != -1) {
			int endIndex = findPlaceholderEndIndex(buf, startIndex);
			if (endIndex != -1) {
				String placeholder = buf.substring(startIndex
						+ PLACEHOLDER_PREFIX.length(), endIndex);
				if (!visitedPlaceholders.add(placeholder)) {
					throw new IllegalArgumentException(
							"Circular placeholder reference '" + placeholder
									+ "' in property definitions");
				}
				// Recursive invocation, parsing placeholders contained in the
				// placeholder key.
				placeholder = parseStringValue(placeholder, visitedPlaceholders);

				// Now obtain the value for the fully resolved key...
				String propVal = resolvePlaceholder(placeholder);
				if (propVal == null && VALUE_SEPARATOR != null) {
					int separatorIndex = placeholder.indexOf(VALUE_SEPARATOR);
					if (separatorIndex != -1) {
						String actualPlaceholder = placeholder.substring(0,
								separatorIndex);
						String defaultValue = placeholder
								.substring(separatorIndex
										+ VALUE_SEPARATOR.length());
						propVal = resolvePlaceholder(actualPlaceholder);
						if (propVal == null) {
							propVal = defaultValue;
						}
					}
				}
				if (propVal != null) {
					// Recursive invocation, parsing placeholders contained in
					// the
					// previously resolved placeholder value.
					propVal = parseStringValue(propVal, visitedPlaceholders);
					buf.replace(startIndex,
							endIndex + PLACEHOLDER_SUFFIX.length(), propVal);
					startIndex = buf.indexOf(PLACEHOLDER_PREFIX, startIndex
							+ propVal.length());
				} else {
					throw new IllegalArgumentException(
							"Could not resolve placeholder '" + placeholder
									+ "'");
				}

				visitedPlaceholders.remove(placeholder);
			} else {
				startIndex = -1;
			}
		}

		return buf.toString();
	}

	private String resolvePlaceholder(String placeholderName) {
		String propVal = bundleProperty.getProperties().getProperty(
				placeholderName);

		if (propVal == null) {
			propVal = bundleProperty.getString(placeholderName);
		}

		if (propVal.equals(placeholderName)) {
			propVal = System.getProperty(placeholderName);
		}

		if (propVal == null) {
			// Fall back to searching the system environment.
			propVal = System.getenv(placeholderName);
		}

		return propVal;
	}

	private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
		int index = startIndex + PLACEHOLDER_PREFIX.length();
		int withinNestedPlaceholder = 0;
		while (index < buf.length()) {
			if (substringMatch(buf, index, PLACEHOLDER_SUFFIX)) {
				if (withinNestedPlaceholder > 0) {
					withinNestedPlaceholder--;
					index = index + PLACEHOLDER_PREFIX.length() - 1;
				} else {
					return index;
				}
			} else if (substringMatch(buf, index, PLACEHOLDER_PREFIX)) {
				withinNestedPlaceholder++;
				index = index + PLACEHOLDER_PREFIX.length();
			} else {
				index++;
			}
		}
		return -1;
	}

	private boolean substringMatch(CharSequence str, int index,
			CharSequence substring) {
		for (int j = 0; j < substring.length(); j++) {
			int i = index + j;
			if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
				return false;
			}
		}
		return true;
	}
}
