package tw.jms.loyal.web.controllers;

import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.action.get.GetResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import tw.jms.loyal.dao.ElasticSearchDao;

@Controller
@RequestMapping("/document")
public class DocumentController {

	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public ResponseEntity<String> document(@PathVariable String id,
			HttpServletResponse response) {
		GetResponse hit = ElasticSearchDao.get(id);		
		String content = hit.getSource().get("html").toString();
		MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add("Content-Type", "text/html; charset=utf-8");
		return new ResponseEntity<String>(content, headers,HttpStatus.CREATED);
	}
}
