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

@Service
public class LoginUserDetailsService implements UserDetailsService {
	
	@Autowired
	UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserBean userbean = null;
		if(VCAPHelper.VCAP_SERVICES  != null) {
			// IBM Cloudantの処理
			UserStore userStore = UserStoreFactory.getInstance();
			User user = userStore.get(username);
			userbean = new UserBean();
			userbean.setUsername(user.getUsername());
			userbean.setPassword(user.getPassword());
			userbean.setRole(user.getRole());
		} else {
			// H2データベースの処理
			Optional<UserBean> opt = userRepository.findById(username);
			userbean = opt.orElseThrow(() -> new UsernameNotFoundException("The requested user is not found."));
		}
		return new LoginUserDetails(userbean);
	}
}
