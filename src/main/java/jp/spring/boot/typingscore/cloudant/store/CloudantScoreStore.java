package jp.spring.boot.typingscore.cloudant.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.model.FindByIndexOptions;
import com.cloudant.client.api.model.IndexField;
import com.cloudant.client.api.model.IndexField.SortOrder;
import com.cloudant.client.api.views.Key;
import com.cloudant.client.org.lightcouch.NoDocumentException;


import jp.spring.boot.typingscore.cloudant.Score;

/**
 * IBM Cloudant Database connect. Score Data Store.
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
	public CloudantScoreStore() {
		CloudantClient cloudant = createClient();
		if (cloudant != null) {
			// case: Cloudant connect Client success.

			// DB instance create.
			setDB(cloudant.database(databaseName, true));
		}
	}

	/**
	 * init process. index create.
	 */
	public void init() {
		// index create.
		getDB().createIndex("committime", "committime", null,
				new IndexField[] { new IndexField("committime", SortOrder.desc) });
		getDB().createIndex("point", "point", null, new IndexField[] { new IndexField("point", SortOrder.asc) });
	}

	/**
	 * Gets all Scores from the store.
	 * 
	 * @return All Scores.
	 */
	public Collection<Score> getAll() {
		List<Score> docs;
		List<Score> removeIndexDocs = new ArrayList<Score>();
		try {
			docs = getDB().getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(Score.class);
			for (Score score : docs) {
				if (score.getCommittime() != null) {
					removeIndexDocs.add(score);
				}
			}
		} catch (IOException e) {
			return null;
		}
		return removeIndexDocs;
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
	public int findUsernameOverlapCnt(String username) {
		String selectorJson ="{\n" + 
				"   \"selector\": {\n" + 
				"      \"$and\": [\n" + 
				"         {\n" + 
				"            \"_id\": {\n" + 
				"               \"$gt\": 0\n" + 
				"            },\n" + 
				"            \"username\": {\n" + 
				"               \"$eq\": \"" + username + "\"\n" + 
				"            }\n" + 
				"         }\n" + 
				"      ]\n" + 
				"   }\n" + 
				"}";
		List<Score> scoreList = getDB().findByIndex(selectorJson, Score.class);

		if(scoreList == null || scoreList.size() == 0) {
			return 0;
		} else {
			return scoreList.size();
		}
	}
	
    /**
     * Gets high Score from the store.
     * 
     * @param username user name.
     * @return ScoreBean.
     */
	public Score findHighScore(String username) {
		
		String selectorJson ="{\n" + 
				"   \"selector\": {\n" + 
				"      \"$and\": [\n" + 
				"         {\n" + 
				"            \"_id\": {\n" + 
				"               \"$gt\": 0\n" + 
				"            },\n" + 
				"            \"highscoreflg\": {\n" + 
				"               \"$eq\": true\n" + 
				"            },\n" + 
				"            \"username\": {\n" + 
				"               \"$eq\": \"" + username + "\"\n" + 
				"            }\n" + 
				"         }\n" + 
				"      ]\n" + 
				"   }\n" + 
				"}";
		List<Score> scoreList = getDB().findByIndex(selectorJson, Score.class);

		if(scoreList == null || scoreList.size() == 0) {
			return null;
		} else {
			return scoreList.get(0);
		}
	}
	
    /**
     * Gets high Score List from the store.
     * 
     * @return ScoreBean List.
     */
	public Collection<Score> findHighScoreListOrderByPoint() {
		
		String selectorJson ="{\n" + 
				"   \"selector\": {\n" + 
				"      \"$and\": [\n" + 
				"         {\n" + 
				"            \"_id\": {\n" + 
				"               \"$gt\": 0\n" + 
				"            },\n" + 
				"            \"highscoreflg\": {\n" + 
				"               \"$eq\": true\n" + 
				"            }\n" + 
				"         }\n" + 
				"      ]\n" + 
				"   }\n" + 
				"}";
		FindByIndexOptions options = new FindByIndexOptions();
		options.sort(new IndexField("point", IndexField.SortOrder.asc));
		List<Score> scoreList = getDB().findByIndex(selectorJson, Score.class, options);

		if(scoreList == null || scoreList.size() == 0) {
			return null;
		} else {
			return scoreList;
		}
	}

	/**
	 * Gets an individual Score from the store.
	 * 
	 * @param id The ID of the Score to get.
	 * @return Score.
	 */
	@Override
	public Score get(String id) {
		return getDB().find(Score.class, id);
	}

	/**
	 * Persists a Score to the store.
	 * 
	 * @param score The Score to persist.
	 * @return The persisted Score. The Score will not have a unique ID..
	 */
	@Override
	public Score persist(Score score) {
		String id = getDB().save(score).getId();
		return getDB().find(Score.class, id);
	}

	/**
	 * Updates a Score in the store.
	 * 
	 * @param id    The ID of the Score to update.
	 * @param score The Visitor with updated information.
	 * @return The updated Score.
	 */
	@Override
	public Score update(String id, Score newScore) {
		Score score = null;
		try {
			score = getDB().find(Score.class, id);
		} catch (NoDocumentException e) {
			// case: Score data not exists.
			e.printStackTrace();
			return null;
		}
		score.setUsername(newScore.getUsername());
		score.setInputtime(newScore.getInputtime());
		score.setMisstype(newScore.getMisstype());
		score.setPoint(newScore.getPoint());
		score.setCommittime(newScore.getCommittime());
		score.setHighscoreflg(newScore.isHighscoreflg());
		getDB().update(score);
		
		return getDB().find(Score.class, id);
	}

	/**
	 * Deletes a Score from the store.
	 * 
	 * @param id delete score id.
	 */
	public void delete(String id) {
		Score score = getDB().find(Score.class, id);
		try {
			getDB().remove(id, score.get_rev());
		} catch (NoDocumentException e) {
			// case: Score data not exists.
			e.printStackTrace();
		}
	}

	/**
	 * Count score data.
	 * 
	 * @return Score count.
	 */
	public int count() throws Exception {
		return getAll().size();
	}
}
