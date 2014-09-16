package tw.jms.loyal.web.controllers;

import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.action.get.GetResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import tw.jms.loyal.annotation.Loggable;
import tw.jms.loyal.dao.ElasticSearchDao;

@Controller
@RequestMapping("/document")
public class DocumentController {

	@Loggable
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public String document(Model model, @PathVariable String id,
			HttpServletResponse response,
			@RequestHeader(value = "Referer") String referrer) {
		GetResponse hit = ElasticSearchDao.get(id);
		String content = hit.getSource().get("html").toString();
		model.addAttribute("content", content);
		return "document/index";
	}
}
