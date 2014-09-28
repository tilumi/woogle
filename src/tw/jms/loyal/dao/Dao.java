package tw.jms.loyal.dao;

import java.util.Map;

public interface Dao {

	public boolean isValidUser(String user);

	public boolean logUserBehavior(String user, String controller,
			String action, Map<String, String> parameters, Long timestamp);

	boolean logPoorResult(String user, String searchTerm, String desc,
			Long timestamp);

}
