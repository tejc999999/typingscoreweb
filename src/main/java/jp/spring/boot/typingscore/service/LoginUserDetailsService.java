package jp.spring.boot.typingscore.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jp.spring.boot.typingscore.bean.UserBean;
import jp.spring.boot.typingscore.security.LoginUserDetails;
import jp.spring.boot.typingscore.security.RoleName;

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
	UserService userService;
	  
	/**
	 * Perform login authentication.
	 * 
	 * @param username user name.
	 * @throws UsernameNotFoundException user name not found.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		UserBean userbean = userService.getBean(username);
		
		return new LoginUserDetails(userbean, true, true, userbean.isAccountNonLocked(), getAuthorities(userbean));
	}
	
	private Collection<GrantedAuthority> getAuthorities(UserBean userbean) {
		if(userbean.getRole().equals(RoleName.ROLE_ADMIN.getString())) {
			return AuthorityUtils.createAuthorityList(RoleName.ROLE_ADMIN.toString());
		} else {
			return AuthorityUtils.createAuthorityList(RoleName.ROLE_USER.toString());
		}
	}
}
