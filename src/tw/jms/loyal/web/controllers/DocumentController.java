package tw.jms.loyal.web.controllers;

import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.action.get.GetResponse;
import org.pac4j.oauth.profile.google2.Google2Profile;
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import tw.jms.loyal.dao.ElasticSearchDao;

@Controller
@RequestMapping("/document")
public class DocumentController {

	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public String document(Model model, @PathVariable String id,
			HttpServletResponse response) {
		Authentication token = SecurityContextHolder.getContext()
				.getAuthentication();
		if (token instanceof ClientAuthenticationToken) {
			ClientAuthenticationToken clientToken = (ClientAuthenticationToken) token;
			Google2Profile userProfile = (Google2Profile) clientToken
					.getUserProfile();
			model.addAttribute("user", userProfile.getEmail());
		}

		GetResponse hit = ElasticSearchDao.get(id);
		String content = hit.getSource().get("html").toString();
		model.addAttribute("content", content);
		return "document/index";
	}
}
