package jp.spring.boot.typingscore.cloudant.store;

import java.util.Collection;

import jp.spring.boot.typingscore.cloudant.User;

/**
 * スコアStore
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
     * @throws Exception 
     */
    public Collection<User> getAll();

    /**
     * Gets an individual ToDo from the store.
     * @param id The ID of the ToDo to get.
     * @return The ToDo.
     */
    public User get(String id);

    /**
     * Persists a Score to the store.
     * @param td The ToDo to persist.
     * @return The persisted ToDo.  The ToDo will not have a unique ID..
     */
    public User persist(User user);

    /**
     * Updates a ToDo in the store.
     * 
     * @param id The ID of the Score to update.
     * @param td The Visitor with updated information.
     * @return The updated Score.
     */
    public User update(String id, User user);

    /**
     * Deletes a ToDo from the store.
     * 
     * @param id 削除するスコアのID
     */
    public void delete(String id);
  
    /**
     * スコアの件数を数える
     * 
     * @return スコアの合計件数
     * @throws Exception 
     */
    public int count() throws Exception;
}
