package tw.jms.loyal.web.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.pac4j.oauth.profile.google2.Google2Profile;
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import tw.jms.loyal.dao.Dao;

public class ValidateUserFilter implements Filter {

	Logger LOG = Logger.getLogger(ValidateUserFilter.class);
	private Dao dao;

	public void setDao(Dao dao) {
		this.dao = dao;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		LOG.info("init filter");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		Authentication token = SecurityContextHolder.getContext()
				.getAuthentication();		
		if (token instanceof AnonymousAuthenticationToken) {
			chain.doFilter(request, response);
		} else {
			ClientAuthenticationToken clientToken = (ClientAuthenticationToken)token;
			Google2Profile userProfile = (Google2Profile) clientToken
					.getUserProfile();
			String email = userProfile.getEmail();
			if (dao.isValidUser(email)) {
				chain.doFilter(request, response);
			} else {
				HttpServletRequest httpRequest = (HttpServletRequest) request;
				httpRequest.logout();
				HttpServletResponse httpResponse = (HttpServletResponse) response;
				httpResponse.sendRedirect("/home/index.html");
			}
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

}
