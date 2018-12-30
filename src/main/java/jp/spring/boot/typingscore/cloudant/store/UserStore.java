package jp.spring.boot.typingscore.cloudant.store;

import java.util.Collection;

import jp.spring.boot.typingscore.cloudant.User;

/**
 * IBM Cloudant Database connect.
 * User Data Store.
 * 
 * @author tejc999999
 *
 */
public interface UserStore {

    /**
     * Get the target db object.
     * 
     * @return Database.
     * @throws Exception 
     */
    public Object getDB();

  
    /**
     * Gets all Scores from the store.
     * 
     * @return All Scores.
     */
    public Collection<User> getAll();

    /**
     * Gets an individual User from the store.
     * @param id The ID of the ToDo to get.
     * @return The User.
     */
    public User get(String id);

    /**
     * Persists a User to the store.
     * @param user The User to persist.
     * @return The persisted User.  The ToDo will not have a unique ID..
     */
    public User persist(User user);

    /**
     * Updates a User in the store.
     * 
     * @param id The ID of the User to update.
     * @param user The User with updated information.
     * @return The updated User.
     */
    public User update(String id, User user);

    /**
     * Deletes a User from the store.
     * 
     * @param id delete user id.
     */
    public void delete(String id);
  
    /**
     * Count user data.
     * 
     * @return User count.
     */
    public int count() throws Exception;
}
