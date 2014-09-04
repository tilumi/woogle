package tw.jms.loyal;

import tw.jms.loyal.dao.ElasticSearchDao;
import tw.jms.loyal.utils.IndexConstants;

public class DbAdmin {

	public static void main(String[] args){
		ElasticSearchDao.forceCreateIndex(IndexConstants.INDEX_PROVIDENCE, IndexConstants.TYPE_WORD);
	}
}
