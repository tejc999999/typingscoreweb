package jp.spring.boot.typingscore.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import jp.spring.boot.typingscore.bean.UserBean;
import lombok.Data;

@Data
public class LoginUserDetails extends User {

	private final UserBean user;

    public LoginUserDetails(UserBean userBean) {
        super(userBean.getUsername(), userBean.getPassword(),
        		AuthorityUtils.createAuthorityList("ROLE_USER"));
        this.user = userBean;
    }
}