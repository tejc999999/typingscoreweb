package jp.spring.boot.typingscore.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.session.SessionFixationProtectionEvent;
import org.springframework.stereotype.Component;

import jp.spring.boot.typingscore.bean.UserBean;
import jp.spring.boot.typingscore.form.UserForm;
import jp.spring.boot.typingscore.service.UserService;

/**
 * 
 * 認証成功イベントハンドラ
 * 
 * @author tejc999999
 *
 */
@Component
public class LoginSuccessAuthEventListeners {

	/**
	 * ユーザ用サービス
	 */
	@Autowired
	UserService userService;

	/**
	 * AuthenticationProviderによる認証が成功した場合のハンドラ
	 * 
	 * @param event AuthenticationProviderによる認証成功イベント
	 */
	@EventListener
	public void authSuccessEventHandler(AuthenticationSuccessEvent event) {
	}

	/**
	 *  固定セッション攻撃対策の処理に成功した場合のハンドラ
	 *  
	 * @param event 固定セッション攻撃の対策処理の成功イベント
	 */
	@EventListener
	public void SessionFixationProtectionEventHandler(SessionFixationProtectionEvent event) {
	}
	
	/**
	 * 全ての認証に成功した場合のハンドラ
	 * ログイン失敗回数をクリアする
	 * 
	 * @param event 全ての認証に成功したイベント
	 */
	@EventListener
	public void InteractiveAuthSuccessEventHandle(InteractiveAuthenticationSuccessEvent event) {
		
		String userId = event.getAuthentication().getName();
		recordLoginFailureCntClear(userId);
	}
	
	/**
	 * ログイン失敗回数をクリアする
	 * 
	 * @param username 対象ユーザ名
	 */
	private void recordLoginFailureCntClear(String username) {
		UserForm userForm = null;
			
		userForm = userService.getDBUserForm(username);

		if(userForm != null) {
			
			userForm.setLoginfailurecnt(0);
			userService.setDBUserForm(userForm);
		}
	}
}
