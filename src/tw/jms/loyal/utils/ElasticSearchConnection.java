package tw.jms.loyal.utils;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import tw.jms.loyal.property.EnvConstants;
import tw.jms.loyal.property.EnvProperty;

public class ElasticSearchConnection {

	private static Client esClient = null;
	private static String elasticSearchHost;

	static{
		elasticSearchHost = EnvProperty
				.getString(EnvConstants.ELASTIC_SEARCH_HOST);
	}
	
	private ElasticSearchConnection() {
		
	}

	public static Client get() {
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("similarity.custom.type", "BM25")
				.put("similarity.custom.k1", 2.0f)
				.put("similarity.custom.b", 1.5f).build();
		esClient = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(
						elasticSearchHost, 9300));
		return esClient;
	}
}
