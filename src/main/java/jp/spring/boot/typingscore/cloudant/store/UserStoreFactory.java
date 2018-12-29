package jp.spring.boot.typingscore.cloudant.store;

public class UserStoreFactory {
	
    private static UserStore instance;

    public static UserStore getInstance() {
    	if(instance == null) {
        	// When another database use, getDB() write
    		
        	CloudantUserStore cvif = new CloudantUserStore();
            if (cvif.getDB() != null) {
                instance = cvif;
            }
    	}
        return instance;
    }
}
