package jp.spring.boot.typingscore.cloudant.store;

/**
 * 
 * User Store Factory.
 * When another database user, edit this class.
 * 
 * @author tejc999999
 *
 */
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
