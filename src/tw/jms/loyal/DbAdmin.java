package tw.jms.loyal;


import tw.jms.loyal.dao.ElasticSearchDao;
import tw.jms.loyal.utils.IndexConstants;

public class DbAdmin {
	
	public static void main(String args[]){
		if(args.length==0){
			printUsage();
		}else if(args[0].equals("initialize")){
			ElasticSearchDao.createIndex(IndexConstants.INDEX_PROVIDENCE, IndexConstants.TYPE_WORD);	
		}else if(args[0].equals("destroy")){
			ElasticSearchDao.deleteIndex(IndexConstants.INDEX_PROVIDENCE);
		}else{
			printUsage();
		}
	}	
	
	public static void printUsage(){
		System.out.println("Usage: DbAdmin {args}");
		System.out.println("initialize\t\t\tInitialize all tables");
		System.out.println("destroy\t\t\t\tDestroy all tables");
	}
	
}
