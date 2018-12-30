package jp.spring.boot.typingscore.cloudant.store;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.org.lightcouch.NoDocumentException;

import jp.spring.boot.typingscore.cloudant.User;


/**
 * IBM Cloudant Database connect.
 * Login User Data Store.
 * 
 * @author tejc999999
 *
 */
public class CloudantUserStore extends CloudantStore implements UserStore {

    /**
     * database name for login user.
     */
    private static final String databaseName = "userdb";

    /**
     * Constructor
     * 
     */
    public CloudantUserStore(){
        CloudantClient cloudant =  createClient();
        if(cloudant!=null){
        	// case: Cloudant connect Client success.
        	
        	// DB instance create.
        	setDB(cloudant.database(databaseName, true));
        }
    }

    /**
     * Gets all Scores from the store.
     * 
     * @return All Scores.
     */
    @Override
    public Collection<User> getAll(){
        List<User> docs;
        try {
            docs = getDB().getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(User.class);
        } catch (IOException e) {
            return null;
        }
        return docs;
    }

    /**
     * Gets an individual User from the store.
     * @param id The ID of the ToDo to get.
     * @return The User.
     */
    @Override
    public User get(String id) throws NoDocumentException {
        return getDB().find(User.class, id);
    }

    /**
     * Persists a User to the store.
     * @param user The User to persist.
     * @return The persisted User.  The ToDo will not have a unique ID..
     */
    @Override
    public User persist(User user) {
        String id = getDB().save(user).getId();
        return getDB().find(User.class, id);
    }

    /**
     * Updates a User in the store.
     * 
     * @param id The ID of the User to update.
     * @param user The User with updated information.
     * @return The updated User.
     */
    @Override
    public User update(String id, User newUser) {
    	User user = getDB().find(User.class, id);
    	user.setUsername(newUser.getUsername());
    	user.setPassword(newUser.getPassword());
    	user.setRole(newUser.getRole());
    	getDB().update(user);
        return getDB().find(User.class, id);

    }

    /**
     * Deletes a User from the store.
     * 
     * @param id delete user id.
     */
    @Override
    public void delete(String id) {
        User user = getDB().find(User.class, id);
        getDB().remove(id, user.get_rev());
    }

    /**
     * Count user data.
     * 
     * @return User count.
     */
    @Override
    public int count() throws Exception {
        return getAll().size();
    }
}
