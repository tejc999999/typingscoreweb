package jp.spring.boot.typingscore.security;

public enum RoleName {
	ROLE_ADMIN("ROLE_ADMIN"),
	ROLE_USER("ROLE_USER"),
	;
	
    private final String str;

    private RoleName(final String str) {
        this.str = str;
    }

    public String getString() {
        return this.str;
    }
}
