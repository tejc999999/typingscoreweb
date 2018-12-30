package jp.spring.boot.typingscore.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jp.spring.boot.typingscore.bean.UserBean;
import jp.spring.boot.typingscore.cloudant.User;
import jp.spring.boot.typingscore.cloudant.store.UserStore;
import jp.spring.boot.typingscore.cloudant.store.UserStoreFactory;
import jp.spring.boot.typingscore.cloudant.store.VCAPHelper;
import jp.spring.boot.typingscore.repository.UserRepository;
import jp.spring.boot.typingscore.security.LoginUserDetails;

/**
 *  Login user service.
 *  
 * @author tejc999999
 *
 */
@Service
public class LoginUserDetailsService implements UserDetailsService {
	
	/**
	 * user repository.
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * Perform login authentication.
	 * 
	 * @param username user name.
	 * @throws UsernameNotFoundException user name not found.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserBean userbean = null;
		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			UserStore userStore = UserStoreFactory.getInstance();
			User user = userStore.get(username);
			userbean = new UserBean();
			userbean.setUsername(user.getUsername());
			userbean.setPassword(user.getPassword());
			userbean.setRole(user.getRole());
		} else {
			// case: h2 database
			Optional<UserBean> opt = userRepository.findById(username);
			userbean = opt.orElseThrow(() -> new UsernameNotFoundException("The requested user is not found."));
		}
		return new LoginUserDetails(userbean);
	}
}
