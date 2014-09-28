package tw.jms.loyal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import tw.jms.loyal.dao.DaoSqliteImpl;
import tw.jms.loyal.dao.ElasticSearchDao;
import tw.jms.loyal.utils.IndexConstants;

public class DbAdmin {

	public static void main(String args[]) throws IOException {
		if (args.length == 0) {
			printUsage();
		} else if (args[0].equals("initialize")) {
			ElasticSearchDao.createIndex(IndexConstants.INDEX_PROVIDENCE,
					IndexConstants.TYPE_WORD);
		} else if (args[0].equals("destroy")) {
			System.out.print("Do you really want to destroy ElasticSearch DB? :");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String s = null;
			if((s = in.readLine()) != null && s.equals("yes")){	
				System.out.println("deleting index");
				ElasticSearchDao.deleteIndex(IndexConstants.INDEX_PROVIDENCE);
			}
		} else if (args[0].equals("init_user_db")) {
			DaoSqliteImpl dao = new DaoSqliteImpl();
			dao.initialize();
		} else if (args[0].equals("add_user") && args.length == 2) {
			String user = args[1];
			DaoSqliteImpl dao = new DaoSqliteImpl();
			dao.addUser(user);
		} else if (args[0].equals("delete_user") && args.length == 2) {
			String user = args[1];
			DaoSqliteImpl dao = new DaoSqliteImpl();
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
