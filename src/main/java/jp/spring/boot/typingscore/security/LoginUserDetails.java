package jp.spring.boot.typingscore.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import jp.spring.boot.typingscore.bean.UserBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class LoginUserDetails extends User {

	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = -3601246438727975832L;
	
	private final UserBean user;

	public LoginUserDetails(UserBean userBean) {
		super(userBean.getUsername(), userBean.getPassword(), AuthorityUtils.createAuthorityList("ROLE_USER"));
		this.user = userBean;
	}
}