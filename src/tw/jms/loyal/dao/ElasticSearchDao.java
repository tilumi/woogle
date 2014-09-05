package tw.jms.loyal.dao;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FuzzyLikeThisQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.gauss.GaussDecayFunctionBuilder;
import org.elasticsearch.search.SearchHits;

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
		FuzzyLikeThisQueryBuilder fuzzyLikeThisQuery = QueryBuilders
				.fuzzyLikeThisQuery("content").likeText(q).maxQueryTerms(12);
		FunctionScoreQueryBuilder functionScoreQueryBuilder = new FunctionScoreQueryBuilder(
				fuzzyLikeThisQuery).add(scoreFunction);

		SearchResponse response = client
				.prepareSearch(IndexConstants.INDEX_PROVIDENCE)
				.setTypes(IndexConstants.TYPE_WORD)
				.setQuery(functionScoreQueryBuilder).setFrom(from)
				.setSize(size).addHighlightedField("content", 100, 1)
				.setHighlighterPreTags("<em class='highlight'>")
				.setHighlighterPostTags("</em>").setExplain(true).execute()
				.actionGet();
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
		String mapping = "{\""
				+ type
				+ "\":{\"_all\":{\"indexAnalyzer\":\"ik\",\"searchAnalyzer\":\"ik\",\"term_vector\":\"no\",\"store\":\"false\"},\"properties\":{\"content\":{\"type\":\"string\",\"store\":\"no\",\"term_vector\":\"with_positions_offsets\",\"indexAnalyzer\":\"ik\",\"searchAnalyzer\":\"ik\",\"include_in_all\":\"true\",\"boost\":8}}}}";
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
