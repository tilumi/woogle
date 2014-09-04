package tw.jms.loyal.utils;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

@SuppressWarnings("unchecked")
public class SerializationUtils {

	private static ObjectMapper mapper = new ObjectMapper();  
	
	static{
		mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, false);
	}
	
	public static <T> T fromJsonString(String input, Class<T> type) {
		try {
			return (T) mapper.readValue(input, type);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	//Sample collectionClass = List.class
	public static <T> T fromJsonStringAsCollection(String input, Class collectionClass, Class type) {
		try {
			JavaType jType = mapper.getTypeFactory().constructCollectionType(collectionClass, type);
			return (T) mapper.readValue(input, jType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String toJsonString(Object object){
		try {
			return mapper.writeValueAsString(object);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}