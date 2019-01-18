package jp.spring.boot.typingscore.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureCredentialsExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.security.authentication.event.AuthenticationFailureExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.authentication.event.AuthenticationFailureServiceExceptionEvent;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import jp.spring.boot.typingscore.config.ParameterProperties;
import jp.spring.boot.typingscore.form.UserForm;
import jp.spring.boot.typingscore.service.UserService;

/**
 * 認証失敗イベントハンドラ
 * 
 * @author tejc999999
 *
 */
@Component
public class LoginFailureAuthEventListeners {
	
	/**
	 * ユーザ用サービス
	 */
	@Autowired
	UserService userService;

	/**
	 * セキュリティ情報
	 */
	@Autowired
	ParameterProperties securityPropertis;
	
	/**
	 * 認証失敗時に規定回数を超えるとアカウントをロックする
	 * 
	 * @param event BadCredentialsException発生イベント
	 */
	@EventListener
	public void authFailureBadCredentialsEventHandler(AuthenticationFailureBadCredentialsEvent event) {
		
		if (event.getException().getClass().equals(UsernameNotFoundException.class)) {
			// 存在しないユーザ名の場合は処理しない
			return;
		}

		String userId = event.getAuthentication().getName();

        recordLoginAttempts(userId);

        int failedLoginAttempts = countFailedLoginAttempts(userId);

        if (failedLoginAttempts >= securityPropertis.getLoginAttemptsThreshold()) {
            lockoutUser(userId);
        }
	}

	/**
	 * 無効なユーザIDによる認証が行われた場合のハンドラ
	 * 
	 * @param event DisabledException発生イベント
	 */
	@EventListener
	public void authFailureDisabledEventHandler(AuthenticationFailureDisabledEvent event) {
	}	
	
	/**
	 * ロックされたアカウントによる認証が行われた場合のハンドラ
	 * 
	 * @param event LockedException発生イベント
	 */
	@EventListener
	public void authFailureLockedEventHandler(AuthenticationFailureLockedEvent event) {
	}	
	
	/**
	 * 有効期限切れアカウントによる認証が行われた場合のハンドラ
	 * 
	 * @param event AccountExpiredException発生イベント
	 */
	@EventListener
	public void authFailureExpiredEventHandler(AuthenticationFailureExpiredEvent event) {
	}	
	
	/**
	 * 認証情報期限切れアカウントによる認証が行われた場合のハンドラ
	 * 
	 * @param event CredentialsExpiredException発生イベント
	 */
	@EventListener
	public void authFailureCredentialsExpiredEventHandler(AuthenticationFailureCredentialsExpiredEvent event) {
	}
	
	/**
	 * 認証サービスでエラーが発生した場合のハンドラ
	 * 
	 * @param event AuthenticationServiceException発生イベント
	 */
	@EventListener
	public void authFailureServiceExceptionEventHandler(AuthenticationFailureServiceExceptionEvent event) {
	}
	

	/**
	 * 認証失敗回数を記録する
	 * 
	 * @param 認証に失敗したユーザ名
	 */
	private void recordLoginAttempts(String username) {

		UserForm userForm = userService.getDBUserForm(username);
		userForm.setLoginfailurecnt(userForm.getLoginfailurecnt() + 1);
		userService.setDBUserForm(userForm);
	}
	
	/**
	 * 認証失敗回数を取得する
	 * 
	 * @param userName 対象ユーザ名
	 * @return 認証失敗回数
	 */
	private int countFailedLoginAttempts(String username) {

		int loginFailureCnt = 0;

		UserForm userForm = userService.getDBUserForm(username);
		loginFailureCnt = userForm.getLoginfailurecnt();
		
		return loginFailureCnt;
	}

	/**
	 * アカウントをロックする
	 * 
	 * @param username ロック対象ユーザ名
	 */
	private void lockoutUser(String username) {

		UserForm userForm = userService.getDBUserForm(username);
		userForm.setAccountNonLocked(false);
		userService.setDBUserForm(userForm);
	}
}
