package jp.spring.boot.typingscore.cloudant.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.model.FindByIndexOptions;
import com.cloudant.client.api.model.IndexField;
import com.cloudant.client.api.model.IndexField.SortOrder;
import com.cloudant.client.org.lightcouch.NoDocumentException;

import jp.spring.boot.typingscore.cloudant.Score;


/**
 * IBM Cloudant Database connect.
 * Score Data Store.
 * 
 * @author tejc999999
 *
 */
public class CloudantScoreStore extends CloudantStore implements ScoreStore {

    /**
     * database name for score.
     */
    private static final String databaseName = "scoredb";

    /**
     * Constructor
     * 
     */

    public CloudantScoreStore(){
        CloudantClient cloudant = createClient();
        if(cloudant!=null){
        	// case: Cloudant connect Client success.
        	
        	// DB instance create.
        	setDB(cloudant.database(databaseName, true));
        }
    }
    
    /**
     * init process.
     * index create.
     */
    public void init() {
    	// index create.
        getDB().createIndex("committime", "committime", null, new IndexField[]{ new IndexField("committime",SortOrder.desc)});
        getDB().createIndex("point", "point", null, new IndexField[]{ new IndexField("point",SortOrder.asc)});
    }

    /**
     * Get all score.
     * 
     * @return All Scores.
     **/
    @Override
    public Collection<Score> getAll(){
        List<Score> docs;
        try {
            docs = getDB().getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(Score.class);
        } catch (IOException e) {
            return null;
        }
        return docs;
    }

    /**
     * Get All score order by commit time DESC.
     * 
     * @return All Scores. commit time DESC.
     */
    public Collection<Score> getAllOrderByCommittime() {
        List<Score> docs;
        
    	String selectorJson = "\"selector\": {  }";
    	FindByIndexOptions options = new FindByIndexOptions();
    	options.sort(new IndexField("committime", IndexField.SortOrder.desc));
    	docs = getDB().findByIndex(selectorJson, Score.class, options);
        return docs;
    }

    /**
     * Get all score order by point ASC.
     * 
     * @return All Scores. point ASC.
     */
    public Collection<Score> getAllOrderByScore() {
        List<Score> docs;

    	String selectorJson = "\"selector\": {  }";
    	FindByIndexOptions options = new FindByIndexOptions();
    	options.sort(new IndexField("point", IndexField.SortOrder.asc));
    	docs = getDB().findByIndex(selectorJson, Score.class, options);

        return docs;
    }
    
    /**
     * Find score and overlap username exclusion.
     * 
     * @return All Scores. exclusion overlap username.
     */
    public Collection<String> findUsernameOverlap() {
        List<Score> docs;
        List<String> usernameList = new ArrayList<String>();
        try {
            docs = getDB().getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(Score.class);
            for(Score score : docs) {
            	usernameList.add(score.getUsername());
            }
        } catch (IOException e) {
            return null;
        }
        return usernameList;
    }

    /**
     * Gets an individual Score from the store.
     * @param id The ID of the Score to get.
     * @return Score.
     */
    @Override
    public Score get(String id) {
        return getDB().find(Score.class, id);
    }

    /**
     * score data persist.
     * 
     * @param score score bean.
     * @return Score.
     */
    @Override
    public Score persist(Score score) {
        String id = getDB().save(score).getId();
        return getDB().find(Score.class, id);
    }

    /**
     * score data update.
     * 
     * @return Score.
     */
    @Override
    public Score update(String id, Score newScore) {
    	Score score = null;
    	try {
    		score = getDB().find(Score.class, id);
    	} catch(NoDocumentException e) {
    		e.printStackTrace();
    		return null;
    	}
    	score.setUsername(newScore.getUsername());
    	score.setInputtime(newScore.getInputtime());
    	score.setMisstype(newScore.getMisstype());
    	score.setPoint(newScore.getPoint());
    	score.setCommittime(newScore.getCommittime());
    	getDB().update(score);
        return getDB().find(Score.class, id);

    }

    /**
     * score data delete.
     * 
     * @param id key.
     */
    @Override
    public void delete(String id) {
        Score score = getDB().find(Score.class, id);
        getDB().remove(id, score.get_rev());
    }

    /**
     * Count score data.
     * 
     * @return Score count.
     */
    @Override
    public int count() throws Exception {
        return getAll().size();
    }
}
