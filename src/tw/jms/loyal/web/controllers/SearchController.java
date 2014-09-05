package tw.jms.loyal.web.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tw.jms.loyal.dao.ElasticSearchDao;
import tw.jms.loyal.property.EnvConstants;
import tw.jms.loyal.property.EnvProperty;
import tw.jms.loyal.utils.SerializationUtils;

@Controller
@RequestMapping("/search")
public class SearchController {

	private static Logger LOG = Logger.getLogger(SearchController.class);

	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String index(Model model, @RequestParam(required = false) String q,
			@RequestParam(required = false) Integer page)
			throws JsonParseException, JsonMappingException, IOException {
		if (q == null || q.isEmpty()) {
			return "search/index";
		} else {

			if (page == null) {
				page = 1;
			}
			int from = page * EnvProperty.getInt(EnvConstants.HITS_PER_PAGE);
			int size = EnvProperty.getInt(EnvConstants.HITS_PER_PAGE);
			SearchHits searchHits = ElasticSearchDao.query(q, from, size);
			List<Map<String, Object>> result = StreamSupport
					.stream(searchHits.spliterator(), false)
					.map(searchHit -> {
						Map<String, Object> hit = SerializationUtils
								.fromJsonString(searchHit.getSourceAsString(),
										HashMap.class);
						hit.put("id", searchHit.getId());
						hit.put("content",
								searchHit.highlightFields().get("content")
										.getFragments()[0].toString());
						return hit;
					}).collect(Collectors.toList());
			model.addAttribute("result",
					SerializationUtils.toJsonString(result));
			model.addAttribute("numOfPages",
					EnvProperty.getInt(EnvConstants.NUM_OF_PAGES));
			return "search/result";
		}
	}
}
