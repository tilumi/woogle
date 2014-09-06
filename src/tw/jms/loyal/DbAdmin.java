package tw.jms.loyal;

import tw.jms.loyal.dao.DaoFromSqlite;
import tw.jms.loyal.dao.ElasticSearchDao;
import tw.jms.loyal.utils.IndexConstants;

public class DbAdmin {

	public static void main(String args[]) {
		if (args.length == 0) {
			printUsage();
		} else if (args[0].equals("initialize")) {
			ElasticSearchDao.createIndex(IndexConstants.INDEX_PROVIDENCE,
					IndexConstants.TYPE_WORD);
		} else if (args[0].equals("destroy")) {
			ElasticSearchDao.deleteIndex(IndexConstants.INDEX_PROVIDENCE);
		} else if (args[0].equals("init_user_db")) {
			DaoFromSqlite dao = new DaoFromSqlite();
			dao.initialize();
		} else if (args[0].equals("add_user") && args.length == 2) {
			String user = args[1];
			DaoFromSqlite dao = new DaoFromSqlite();
			dao.addUser(user);
		} else if (args[0].equals("delete_user") && args.length == 2) {
			String user = args[1];
			DaoFromSqlite dao = new DaoFromSqlite();
			dao.deleteUser(user);
		} else {
			printUsage();
		}
	}

	public static void printUsage() {
		System.out.println("Usage: DbAdmin {args}");
		System.out.println("initialize\t\t\tInitialize all tables");
		System.out.println("destroy\t\t\t\tDestroy all tables");
		System.out.println("init_user_db\t\t\tInitialize user database");
		System.out.println("add_user [user]\t\t\t\tAdd user");
		System.out.println("delete_user [user]\t\t\t\tDelete user");
	}

}
