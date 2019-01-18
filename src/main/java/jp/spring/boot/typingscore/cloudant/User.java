package jp.spring.boot.typingscore.cloudant;

import jp.spring.boot.typingscore.security.RoleName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * ログインユーザBean（IBM Cloudant用）.
 * 
 * @author tejc999999
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class User extends CloudantObject {
	
	/**
	 * ユーザ名
	 */
	private String username = null;
	
	/**
	 * パスワード
	 * 
	 */
	private String password = null;
	
	/**
	 * ログイン失敗回数
	 * 
	 */
	private int loginfailurecnt = 0;
	
	/**
	 * 権限
	 * 
	 * ROLE_ADMINまたはROLE_USER.
	 */
	private String role = null;
	
	/**
	 * 非ロック情報
	 */
	private boolean accountNonLocked = true;

	/**
	 * コンストラクタ
	 */
    public User() {
        this.username = "";
        this.password = "";
        this.role = RoleName.ROLE_USER.getString();
    }
}
