package jp.spring.boot.typingscore.security;

/**
 * ログインユーザ権限
 * 
 * @author tejc999999
 *
 */
public enum RoleName {

	/**
	 * 権限Enum.
	 */
	ROLE_ADMIN("ROLE_ADMIN"),
	ROLE_USER("ROLE_USER"),
	;
	
	/**
	 * Role Strng.
	 */
    private final String str;

    /**
     * 権限名称を設定する
     * 
     * @param str 顕現名称
     */
    private RoleName(final String str) {
        this.str = str;
    }

    /**
     * 権限名称を取得する
     * 
     * @return 権限名称
     */
    public String getString() {
        return this.str;
    }

    /**
     * 接尾辞なしの権限名称を取得する
     * 
     * @return 接尾辞なしの権限名称.
     */
    public String getRoleLessString() {
        return this.str.replace("ROLE_", "");
    }
}
