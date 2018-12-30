package jp.spring.boot.typingscore.cloudant.store;

import java.util.Collection;

import jp.spring.boot.typingscore.cloudant.Score;

/**
 * IBM Cloudant Database connect.
 * Score Data Store.
 * 
 * @author tejc999999
 *
 */
public interface ScoreStore {

    /**
     * Get the target Score object.
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
    public Collection<Score> getAll();

    /**
     * Get All score order by commit time DESC.
     * 
     * @return All Scores. commit time DESC.
     */
    public Collection<Score> getAllOrderByCommittime();
    
    /**
     * Get all score order by point ASC.
     * 
     * @return All Scores. point ASC.
     */
    public Collection<Score> getAllOrderByScore();

    /**
     * Find score and overlap username exclusion.
     * 
     * @return All Scores. exclusion overlap username.
     */
    public Collection<String> findUsernameOverlap();
    
    /**
     * Gets an individual Score from the store.
     * @param id The ID of the Score to get.
     * @return The Score.
     */
    public Score get(String id);

    /**
     * Persists a Score to the store.
     * @param score The Score to persist.
     * @return The persisted Score.  The Score will not have a unique ID..
     */
    public Score persist(Score score);

    /**
     * Updates a Score in the store.
     * 
     * @param id The ID of the Score to update.
     * @param score The Visitor with updated information.
     * @return The updated Score.
     */
    public Score update(String id, Score score);

    /**
     * Deletes a Score from the store.
     * 
     * @param id delete score id.
     */
    public void delete(String id);
  
    /**
     * Count score data.
     * 
     * @return Score count.
     */
    public int count() throws Exception;
    
    /**
     * init process.
     * index create.
     */
    public void init();
}
