package tw.jms.loyal.aspects;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.pac4j.oauth.profile.google2.Google2Profile;
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import tw.jms.loyal.dao.Dao;

@Component
@Aspect
public class UserTrackingAspect {

	@Resource
	Dao dao;

	@AfterReturning("@annotation(tw.jms.loyal.annotation.Loggable)")
	public void log(JoinPoint jointPoint) throws UnsupportedEncodingException {
		String user = null;
		Authentication token = SecurityContextHolder.getContext()
				.getAuthentication();
		if (!(token instanceof ClientAuthenticationToken)) {
			return;
		}
		ClientAuthenticationToken clientToken = (ClientAuthenticationToken) token;
		Google2Profile userProfile = (Google2Profile) clientToken
				.getUserProfile();
		user = userProfile.getEmail();
		String controller = jointPoint.getTarget().getClass().getSimpleName();
		String action = jointPoint.getSignature().getName();

		MethodSignature methodSignature = (MethodSignature) jointPoint
				.getSignature();
		String[] parameterNames = methodSignature.getParameterNames();
		Map<String, String> parameters = new HashMap<String, String>();
		for (int i = 0; i < jointPoint.getArgs().length; i++) {
			Object arg = jointPoint.getArgs()[i];
			if (arg instanceof String && i < parameterNames.length) {
				parameters.put(parameterNames[i], URLDecoder.decode(arg.toString(),"UTF-8"));
			}
		}
		dao.logUserBehavior(user, controller, action, parameters,
				System.currentTimeMillis());

	}
}
