package jp.spring.boot.typingscore.security;

/**
 * Login user role.
 * 
 * @author tejc999999
 *
 */
public enum RoleName {

	/**
	 * Role Enum.
	 */
	ROLE_ADMIN("ROLE_ADMIN"),
	ROLE_USER("ROLE_USER"),
	;
	
	/**
	 * Role Strng.
	 */
    private final String str;

    /**
     * Set Role name.
     * @param str role name.
     */
    private RoleName(final String str) {
        this.str = str;
    }

    /**
     * Get Role name.
     * @return role name.
     */
    public String getString() {
        return this.str;
    }

    /**
     * Get Role name without prefix.
     * @return role name without prefix.
     */
    public String getRoleLessString() {
        return this.str.replace("ROLE_", "");
    }
}
