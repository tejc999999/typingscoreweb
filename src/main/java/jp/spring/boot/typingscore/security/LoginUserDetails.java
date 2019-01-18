package jp.spring.boot.typingscore.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import jp.spring.boot.typingscore.bean.UserBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ログインユーザ認証クラス
 * 
 * @author tejc999999
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class LoginUserDetails extends User {

	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = -3601246438727975832L;
	
	/**
	 * ログインユーザ
	 */
	private final UserBean user;

	/**
	 * ログイン時の認証を行う
	 * 
	 * @param userBean ログインユーザUserBean
	 */
	public LoginUserDetails(UserBean userBean, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<GrantedAuthority> authorities) {
		super(userBean.getUsername(), userBean.getPassword(),
				true, true, true, userBean.isAccountNonLocked(), getAuthority(userBean));

		this.user = userBean;
	}
	/**
	 * ユーザアカウントのロック情報を取得する
	 * 
	 * @return アカウントロック情報（true: ロックなし、false: ロック）
	 */
	@Override
	public	boolean isAccountNonLocked() {
		return user.isAccountNonLocked();
	}

	/**
	 * ユーザアカウントの有効情報を取得する
	 * 
	 * @return 有効情報（true: 有効、false: 無効）※true固定
	 */
	public	boolean isEnabled() {
		return true;
	}
	
	/**
	 * ユーザカウントの有効期限切れ情報を取得する
	 * 
	 * @return アカウント有効期限切れ情報（true: 有効期限内、false: 有効期限切れ）※true固定
	 */
	public boolean isAccountNonExpired() {
		return true;
	}
	
	/**
	 * ユーザカウント認証情報の有効期限切れ情報を取得する
	 * 
	 * @return アカウント認証情報有効期限切れ情報（true: 有効期限内、false: 有効期限切れ）※true固定
	 */
	public boolean isAccountisCredentialsNonExpiredNonExpired() {
		return true;
	}

	/**
	 * 権限情報を取得する
	 * 
	 * @param userBean ログインユーザ用UserBean
	 * @return 権限情報リスト
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