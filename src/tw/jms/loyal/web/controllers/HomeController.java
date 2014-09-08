package tw.jms.loyal.web.controllers;

import org.pac4j.oauth.profile.google2.Google2Profile;
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class HomeController {

	@RequestMapping("index")
	public String index(Model model) {
		try {
			Authentication token = SecurityContextHolder.getContext()
					.getAuthentication();
			if (token instanceof ClientAuthenticationToken) {
				ClientAuthenticationToken clientToken = (ClientAuthenticationToken) token;
				Google2Profile userProfile = (Google2Profile) clientToken
						.getUserProfile();
				model.addAttribute("user", userProfile.getEmail());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "home/index";
	}
}
