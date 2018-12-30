package jp.spring.boot.typingscore.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import jp.spring.boot.typingscore.bean.UserBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Login user authentication.
 * 
 * @author tejc999999
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class LoginUserDetails extends User {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -3601246438727975832L;
	
	/**
	 * Login user.
	 */
	private final UserBean user;

	/**
	 * Perform login authentication
	 * 
	 * @param userBean login user bean.
	 */
	public LoginUserDetails(UserBean userBean) {
		super(userBean.getUsername(), userBean.getPassword(), getAuthority(userBean));
		this.user = userBean;
	}
	
	/**
	 * Perform login authentication
	 * 
	 * @param userBean login user bean.
	 * @return role auth list.
	 */
	private static Collection<GrantedAuthority> getAuthority(UserBean userBean){
		Collection<GrantedAuthority> authList;
		if(userBean.getRole().equals(RoleName.ROLE_ADMIN.getString())) {
			authList =  AuthorityUtils.createAuthorityList(RoleName.ROLE_ADMIN.getString());
		} else {
			authList =  AuthorityUtils.createAuthorityList(RoleName.ROLE_USER.getString());
		}
		
		return authList;
    }
}