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
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHits;
import org.pac4j.oauth.profile.google2.Google2Profile;
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tw.jms.loyal.dao.ElasticSearchDao;
import tw.jms.loyal.property.EnvConstants;
import tw.jms.loyal.property.EnvProperty;
import tw.jms.loyal.utils.SerializationUtils;

import com.spreada.utils.chinese.ZHConverter;

@Controller
@RequestMapping("/search")
public class SearchController {

	private static Logger LOG = Logger.getLogger(SearchController.class);

	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String index(Model model, @RequestParam(required = false) String q,
			@RequestParam(required = false) Integer page)
			throws JsonParseException, JsonMappingException, IOException {
		Authentication token = SecurityContextHolder.getContext()
				.getAuthentication();
		if (token instanceof ClientAuthenticationToken) {
			ClientAuthenticationToken clientToken = (ClientAuthenticationToken) token;
			Google2Profile userProfile = (Google2Profile) clientToken
					.getUserProfile();
			model.addAttribute("user", userProfile.getEmail());
		}

		if (q == null || q.isEmpty()) {
			return "search/index";
		} else {
			ZHConverter simplifiedConverter = ZHConverter
					.getInstance(ZHConverter.SIMPLIFIED);
			ZHConverter traditionalConverter = ZHConverter
					.getInstance(ZHConverter.TRADITIONAL);
			boolean isStoreInSimplifiedChinese = EnvProperty
					.getBoolean(EnvConstants.IS_STORE_IN_SIMPLIFIED_CHINESE);

			if (isStoreInSimplifiedChinese) {
				q = simplifiedConverter.convert(q);
			}
			if (page == null) {
				page = 1;
			}
			int from = (page - 1)
					* EnvProperty.getInt(EnvConstants.HITS_PER_PAGE);
			int size = EnvProperty.getInt(EnvConstants.HITS_PER_PAGE);
			SearchHits searchHits = ElasticSearchDao.query(q, from, size);

			List<Map<String, Object>> result = StreamSupport
					.stream(searchHits.spliterator(), false)
					.map(searchHit -> {
						Map<String, Object> hit = SerializationUtils
								.fromJsonString(searchHit.getSourceAsString(),
										HashMap.class);
						hit.put("id", searchHit.getId());
						String content = "";
						String title = "";
						try {
							Text[] titleHLFragments = searchHit
									.highlightFields().get("title")
									.getFragments();
							title = titleHLFragments[0].toString();
						} catch (Exception e) {
							title = hit.get("title").toString();
						}
						try {
							hit.put("publishDate", hit.get("publishDate")
									.toString().substring(0, 10));
						} catch (Exception e) {
						}

						try {
							Text[] contentHLFragments = searchHit
									.highlightFields().get("content")
									.getFragments();
							content = contentHLFragments[0].toString();
						} catch (Exception e) {
							content = hit.get("content").toString()
									.substring(0, 100);
						}
						if (isStoreInSimplifiedChinese) {
							content = traditionalConverter.convert(content);
							title = traditionalConverter.convert(title);
						}
						hit.put("content", content);
						hit.put("title", title);
						return hit;
					}).collect(Collectors.toList());
			model.addAttribute("result",
					SerializationUtils.toJsonString(result));

			long count = ElasticSearchDao.getCount(q);
			long numOfPages = (count / EnvProperty
					.getInt(EnvConstants.HITS_PER_PAGE)) + 1;
			model.addAttribute("numOfPages", numOfPages);
			return "search/result";
		}
	}
}
