package jp.spring.boot.typingscore.cloudant;

import jp.spring.boot.typingscore.security.RoleName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * For IBM Cloudant.
 * Login User Bean.
 * 
 * @author tejc999999
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class User extends CloudantObject {
	
	/**
	 * username.
	 */
	private String username = null;
	
	/**
	 * password.
	 * 
	 */
	private String password = null;
	
	/**
	 * role.
	 * 
	 * ROLE_ADMIN or ROLE_USER.
	 */
	private String role = null;
	
	/**
	 * Constructor.
	 * 
	 */
    public User() {
        this.username = "";
        this.password = "";
        this.role = RoleName.ROLE_USER.getString();
    }
}
