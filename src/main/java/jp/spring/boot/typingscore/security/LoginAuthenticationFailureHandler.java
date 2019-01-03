package jp.spring.boot.typingscore.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class LoginAuthenticationFailureHandler implements AuthenticationFailureHandler {

	/**
	 * 
	 */
	private String errorUrl;

	/**
	 * 
	 * @param errorUrl
	 */
	public LoginAuthenticationFailureHandler(String errorUrl) {
		this.errorUrl = errorUrl;
	}
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		// パスワード間違い
		// org.springframework.security.authentication.BadCredentialsException
		// ユーザ名間違い
		// org.springframework.security.core.userdetails.UsernameNotFoundException
		// ユーザロック
		// org.springframework.security.authentication.LockedException

		String errorpath = null;
		if(exception.getClass().equals(org.springframework.security.authentication.BadCredentialsException.class)) {
			errorpath = "error";
		} else if(exception.getClass().equals(org.springframework.security.core.userdetails.UsernameNotFoundException.class)) {
			errorpath = "error";
		} else if(exception.getClass().equals(org.springframework.security.authentication.LockedException.class)) {
			errorpath = "lockederror";
		}
		
        DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        redirectStrategy.sendRedirect(request, response, "/login?" + errorpath);
	}
}
