package tw.jms.loyal.dao;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.tika.io.IOUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.FuzzyLikeThisQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.gauss.GaussDecayFunctionBuilder;
import org.elasticsearch.search.SearchHits;
import org.springframework.core.io.ClassPathResource;

import tw.jms.loyal.property.EnvConstants;
import tw.jms.loyal.property.EnvProperty;
import tw.jms.loyal.utils.DateTimeUtils;
import tw.jms.loyal.utils.ElasticSearchConnection;
import tw.jms.loyal.utils.IndexConstants;

public class ElasticSearchDao {

	private static Logger LOG = Logger.getLogger(ElasticSearchDao.class);

	public static SearchHits query(String q, int from, int size) {

		Client client = ElasticSearchConnection.get();
		SearchHits hits = null;
		String currentDate = DateTimeUtils.getDate(System.currentTimeMillis());
		ScoreFunctionBuilder scoreFunction = new GaussDecayFunctionBuilder(
				"lastModified", currentDate, "365d");
		Fuzziness fuzziness = Fuzziness.ZERO;
		FuzzyLikeThisQueryBuilder fuzzyLikeThisQuery = QueryBuilders
				.fuzzyLikeThisQuery("content", "title").fuzziness(fuzziness)
				.likeText(q).maxQueryTerms(12);
		FunctionScoreQueryBuilder functionScoreQueryBuilder = new FunctionScoreQueryBuilder(
				fuzzyLikeThisQuery).add(scoreFunction);
		SearchRequestBuilder search = client
				.prepareSearch(IndexConstants.INDEX_PROVIDENCE)
				.setTypes(IndexConstants.TYPE_WORD)
				.setQuery(functionScoreQueryBuilder).setFrom(from)
				.setSize(size).addHighlightedField("content", 100, 1)
				.addHighlightedField("title", 100, 1)
				.setHighlighterPreTags("<em class='highlight'>")
				.setHighlighterPostTags("</em>");
		if (EnvProperty.getBoolean(EnvConstants.DEBUG)) {
			search.setExplain(true);
			LOG.info("request: " + search.toString());
		}
		SearchResponse response = search.execute().actionGet();
		if (EnvProperty.getBoolean(EnvConstants.DEBUG)) {
			LOG.info("response: " + response.toString());
		}
		hits = response.getHits();
		client.close();
		return hits;
	}

	public static GetResponse get(String id) {
		Client client = ElasticSearchConnection.get();
		GetResponse response = client
				.prepareGet(IndexConstants.INDEX_PROVIDENCE,
						IndexConstants.TYPE_WORD, id).execute().actionGet();
		client.close();
		return response;
	}

	public static void deleteIndex(String index) {
		Client client = ElasticSearchConnection.get();
		final IndicesExistsResponse res = client.admin().indices()
				.prepareExists(index).execute().actionGet();
		if (res.isExists()) {
			final DeleteIndexRequestBuilder delIdx = client.admin().indices()
					.prepareDelete(index);
			DeleteIndexResponse deleteIndexResponse = delIdx.execute()
					.actionGet();
			if (deleteIndexResponse.isAcknowledged()) {
				LOG.info("Delete Index: " + index + " succeed!");
			} else {
				LOG.info("Delete Index: " + index + " failed!");
			}
		}
		client.close();
	}

	public static void removeAlias(String alias, String index) {

	}

	public static void addAlias(String alias, String index) {

	}

	public static void updateIndex(String index, String type) {

	}

	public static boolean reIndex(String index) {
		Client client = ElasticSearchConnection.get();

		client.close();
		return true;
	}

	public static void createIndex(String index, String type) {
		Client client = ElasticSearchConnection.get();

		final CreateIndexRequestBuilder createIndexRequestBuilder = client
				.admin().indices().prepareCreate(index);

		// MAPPING GOES HERE
		String mapping = null;
		try {
			mapping = IOUtils.toString(new ClassPathResource("mapping/" + type + ".json")
					.getInputStream());
			mapping = mapping.replace("${type}", type);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		LOG.info("mapping:" + mapping);
		createIndexRequestBuilder.addMapping(type, mapping);

		// MAPPING DONE
		CreateIndexResponse response = createIndexRequestBuilder.execute()
				.actionGet();
		if (response.isAcknowledged()) {
			LOG.info("Create index succeed");
		} else {
			LOG.error("Create index failed");
		}
		client.close();
	}
}
