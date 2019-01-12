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

import jp.spring.boot.typingscore.bean.UserBean;
import jp.spring.boot.typingscore.config.ParameterProperties;
import jp.spring.boot.typingscore.service.UserService;

/**
 * Event handler at authentication failure
 * 
 * @author tejc999999
 *
 */
@Component
public class LoginFailureAuthEventListeners {
	
	/**
	 * user repository.
	 */
	@Autowired
	UserService userService;

	@Autowired
	ParameterProperties securityPropertis;
	// Threshold value of continuous authentication failure number of account
	// locking
//	private int loginAttemptsThreshold = 5;
	
	/**
	 * It counts the number of failed login attempts, and locks if the specified number of times is exceeded.
	 * Handling of authentication errors due to password matching failure.
	 * 
	 * @param event BadCredentialsException occurred
	 */
	@EventListener
	public void authFailureBadCredentialsEventHandler(AuthenticationFailureBadCredentialsEvent event) {
		
		if (event.getException().getClass().equals(UsernameNotFoundException.class)) {
			// Ignore if the user name does not exist.
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
	 * Handling of authentication errors by invalid user ID.
	 * 
	 * @param event DisabledException occurred
	 */
	@EventListener
	public void authFailureDisabledEventHandler(AuthenticationFailureDisabledEvent event) {
	}	
	
	/**
	 * Handling authentication errors due to account locking.
	 * @param event LockedException occurred
	 */
	@EventListener
	public void authFailureLockedEventHandler(AuthenticationFailureLockedEvent event) {
	}	
	
	/**
	 * Handling of authentication errors due to account expiration.
	 * @param event AccountExpiredException occurred
	 */
	@EventListener
	public void authFailureExpiredEventHandler(AuthenticationFailureExpiredEvent event) {
	}	
	
	/**
	 * Handling of authentication errors due to expiration of credentials.
	 * @param event CredentialsExpiredException occurred
	 */
	@EventListener
	public void authFailureCredentialsExpiredEventHandler(AuthenticationFailureCredentialsExpiredEvent event) {
	}
	
	/**
	 * Handling authentication service errors.
	 * @param event AuthenticationServiceException occurred
	 */
	@EventListener
	public void authFailureServiceExceptionEventHandler(AuthenticationFailureServiceExceptionEvent event) {
	}
	

	/**
	 * Login failure information is recorded in the DB
	 * 
	 * @param username user name of account failed to login
	 */
	private void recordLoginAttempts(String username) {

		UserBean userbean = userService.getBean(username);
		userbean.setLoginfailurecnt(userbean.getLoginfailurecnt() + 1);
		userService.updateBean(userbean);
	}
	
	/**
	 * Retrieve the number of consecutive unsuccessful login attempts
	 * 
	 * @param userName username
	 * @return Number of consecutive login failures
	 */
	private int countFailedLoginAttempts(String username) {

		int loginFailureCnt = 0;

		UserBean userbean = userService.getBean(username);
		loginFailureCnt = userbean.getLoginfailurecnt();
		
		return loginFailureCnt;
	}

	/**
	 * Lock account
	 * 
	 * @param username user name of account to lock.
	 */
	private void lockoutUser(String username) {

		UserBean userbean = userService.getBean(username);
		userbean.setAccountNonLocked(false);
		userService.updateBean(userbean);
	}
}
