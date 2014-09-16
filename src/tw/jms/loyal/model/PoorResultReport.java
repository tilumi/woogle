package tw.jms.loyal.model;

import java.io.Serializable;

public class PoorResultReport  implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3145566231569520024L;
	
	private String searchTerm;

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	
}
