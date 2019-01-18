package jp.spring.boot.typingscore.service;

import java.util.Collection;

import org.springframework.beans.BeanUtils;
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
 *  ログインユーザ用サービス
 *  
 * @author tejc999999
 *
 */
@Service
public class LoginUserDetailsService implements UserDetailsService {
	
	/**
	 * ユーザ用サービス
	 */
	@Autowired
	UserService userService;
	  
	/**
	 * ログイン認証を行う
	 * 
	 * @param username ログインユーザ名
	 * @throws UsernameNotFoundException ログインユーザが存在しない場合に発生する
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserBean userbean = new UserBean();
		BeanUtils.copyProperties(userService.getDBUserForm(username), userbean);

		return new LoginUserDetails(userbean, true, true, userbean.isAccountNonLocked(), getAuthorities(userbean));
	}

	/**
	 * ログインユーザに該当する認証情報を返す
	 * 
	 * @param userbean ログインユーザ情報
	 * @return 認証情報コレクション
	 */
	private Collection<GrantedAuthority> getAuthorities(UserBean userbean) {
		if(userbean.getRole().equals(RoleName.ROLE_ADMIN.getString())) {
			return AuthorityUtils.createAuthorityList(RoleName.ROLE_ADMIN.toString());
		} else {
			return AuthorityUtils.createAuthorityList(RoleName.ROLE_USER.toString());
		}
	}
}
