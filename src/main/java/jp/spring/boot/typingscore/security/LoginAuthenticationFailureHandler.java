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
	 * ログイン用URL
	 */
	private String url;

	/**
	 * コンストラクタ
	 * 
	 * @param url ログイン用URL
	 */
	public LoginAuthenticationFailureHandler(String url) {
		this.url = url;
	}
	
	
	/**
	 * ログイン認証イベントハンドラ
	 * ログイン失敗時にGETで失敗情報を返す
	 * 
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 * @throws IOException
	 * @throws ServletException
	 */
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
        redirectStrategy.sendRedirect(request, response, url + "?" + errorpath);
	}
}
