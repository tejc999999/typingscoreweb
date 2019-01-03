package jp.spring.boot.typingscore.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.session.SessionFixationProtectionEvent;
import org.springframework.stereotype.Component;

import com.cloudant.client.org.lightcouch.NoDocumentException;

import jp.spring.boot.typingscore.bean.UserBean;
import jp.spring.boot.typingscore.cloudant.User;
import jp.spring.boot.typingscore.cloudant.store.UserStore;
import jp.spring.boot.typingscore.cloudant.store.UserStoreFactory;
import jp.spring.boot.typingscore.cloudant.store.VCAPHelper;
import jp.spring.boot.typingscore.repository.UserRepository;
import jp.spring.boot.typingscore.service.UserService;

/**
 * 
 * Event Handler at Successful Authentication
 * 
 * @author tejc999999
 *
 */
@Component
public class LoginSuccessAuthEventListeners {

	/**
	 * user repository.
	 */
	@Autowired
	UserService userService;

	/**
	 * AuthenticationProvider auth success.
	 * 
	 * @param event AuthenticationProvider auth success.
	 */
	@EventListener
	public void authSuccessEventHandler(AuthenticationSuccessEvent event) {
	}

	/**
	 *  Successful handling of fixed session attack measures.
	 *  
	 * @param event Successful handling of fixed session attack measures.
	 */
	@EventListener
	public void SessionFixationProtectionEventHandler(SessionFixationProtectionEvent event) {
	}
	
	/**
	 * All auth success.
	 * Clear the authentication failure count of the account
	 * 
	 * @param event all auth success.
	 */
	@EventListener
	public void InteractiveAuthSuccessEventHandle(InteractiveAuthenticationSuccessEvent event) {
		
		String userId = event.getAuthentication().getName();
		recordLoginFailureCntClear(userId);
	}
	
	/**
	 * Record login success information in the DB
	 * 
	 * @param username user name.
	 */
	private void recordLoginFailureCntClear(String username) {
		UserBean userbean = null;
		try {
			
			userbean = userService.getBean(username);
		} catch(UsernameNotFoundException e) {
			
		}

		if(userbean != null) {
			
			userbean.setLoginfailurecnt(0);
			userService.updateBean(userbean);
		}
	}
}
