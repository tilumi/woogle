package tw.jms.loyal.model;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class UsageLog {

	private String controller;
	private String action;
	private long timestamp;
	private Map<String, String> parameters;

	
	public long getTimestamp() {
		return timestamp;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getController() {
		return controller;
	}

	public void setController(String controller) {
		this.controller = controller;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
