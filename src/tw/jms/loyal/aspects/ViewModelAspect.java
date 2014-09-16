package tw.jms.loyal.aspects;

import org.pac4j.oauth.profile.google2.Google2Profile;
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ViewModelAspect {

	@ModelAttribute("user")
	public String user(){		
		Authentication token = SecurityContextHolder.getContext()
				.getAuthentication();
		if (token instanceof ClientAuthenticationToken) {
			ClientAuthenticationToken clientToken = (ClientAuthenticationToken) token;
			Google2Profile userProfile = (Google2Profile) clientToken
					.getUserProfile();			
			return userProfile.getDisplayName();
		}
		return null;
		
	}
}