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
	public LoginUserDetails(UserBean userBean, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<GrantedAuthority> authorities) {
		super(userBean.getUsername(), userBean.getPassword(),
				true, true, true, userBean.isAccountNonLocked(), getAuthority(userBean));

		this.user = userBean;
	}
	/**
	 * Get status of account is not locked
	 * 
	 * @return status of account is not locked
	 */
	@Override
	public	boolean isAccountNonLocked() {
		return user.isAccountNonLocked();
	}

	/**
	 * Get status of account is not locked
	 * (Always returns true)
	 * 
	 * @return status of account is not locked
	 */
	public	boolean isEnabled() {
		return true;
	}
	
	/**
	 * Acquire account expiration status
	 * 
	 * @return Account expiration status
	 */
	public boolean isAccountNonExpired() {
		return true;
	}
	
	/**
	 * Acquire the expiration status of the credential information
	 * 
	 * @return status of the credential information
	 */
	public boolean isAccountisCredentialsNonExpiredNonExpired() {
		return true;
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