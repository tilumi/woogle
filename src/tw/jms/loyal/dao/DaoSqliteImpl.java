package tw.jms.loyal.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import tw.jms.loyal.model.UsageLog;
import tw.jms.loyal.property.EnvConstants;
import tw.jms.loyal.property.EnvProperty;
import tw.jms.loyal.utils.SerializationUtils;

public class DaoSqliteImpl implements Dao {

	@Override
	public boolean isValidUser(String user) {
		boolean valid = false;
		Connection conn = getConnection();
		try {
			PreparedStatement stmt = conn
					.prepareStatement("select count(*) from users where email = ?");
			stmt.setString(1, user);
			ResultSet result = stmt.executeQuery();
			if (result.getInt(1) > 0) {
				valid = true;
			}
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return valid;
	}

	public void addUser(String user) {
		if (isValidUser(user)) {
			System.out.println(user + " already exists.");
			return;
		}

		Connection conn = getConnection();
		try {
			PreparedStatement stmt = conn
					.prepareStatement("insert into users (email) values ( ? )");
			stmt.setString(1, user);
			int result = stmt.executeUpdate();
			if (result > 0) {
				System.out.println(user + " added.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteUser(String user) {
		if (!isValidUser(user)) {
			System.out.println(user + " not exists.");
			return;
		}

		Connection conn = getConnection();
		try {
			PreparedStatement stmt = conn
					.prepareStatement("delete from users where email = ? ");
			stmt.setString(1, user);
			int result = stmt.executeUpdate();
			if (result > 0) {
				System.out.println(user + " deleted.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initialize() {
		Connection conn = getConnection();
		Statement stmt;
		try {
			stmt = conn.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS users "
					+ "(email TEXT PRIMARY KEY  NOT NULL)";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE IF NOT EXISTS usageLog "
					+ "(user TEXT  NOT NULL, val TEXT NOT NULL, timestamp LONG NOT NULL)";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE IF NOT EXISTS poorResult "
					+ "(user TEXT  NOT NULL, searchTerm TEXT NOT NULL, timestamp LONG NOT NULL)";
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void dropTable() {

	}

	private Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("org.sqlite.JDBC");
			String sqliteDbPath = "jdbc:sqlite:"
					+ EnvProperty.getString(EnvConstants.SQLITE_DB);
			conn = DriverManager.getConnection(sqliteDbPath);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return conn;
	}
	
	@Override
	public boolean logPoorResult(String user, String searchTerm, Long timestamp) {
		Connection conn = getConnection();
		try {
			PreparedStatement stmt = conn
					.prepareStatement("INSERT INTO 'poorResult' (user, searchTerm, timestamp ) VALUES (?, ?, ?)");
			stmt.setString(1, user);
			stmt.setString(2, searchTerm);
			stmt.setLong(3, timestamp);
			stmt.executeUpdate();
			stmt.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean logUserBehavior(String user, String controller,
			String action, Map<String, String> parameters, Long timestamp) {
		Connection conn = getConnection();
		try {
			PreparedStatement stmt = conn
					.prepareStatement("INSERT INTO 'usageLog' (user, val, timestamp ) VALUES (?, ?, ?)");
			UsageLog log = new UsageLog();
			log.setController(controller);
			log.setAction(action);
			log.setParameters(parameters);
			String val = SerializationUtils.toJsonString(log);
			stmt.setString(1, user);
			stmt.setString(2, val);
			stmt.setLong(3, timestamp);
			stmt.executeUpdate();
			stmt.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

}
