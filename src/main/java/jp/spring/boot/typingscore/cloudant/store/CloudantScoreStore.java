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
 * スコア用ストア（IBM Cloudant用）
 * 
 * @author tejc999999
 *
 */
public class CloudantScoreStore extends CloudantStore implements ScoreStore {

	/**
	 * データベース名
	 */
	private static final String databaseName = "scoredb";

	/**
	 * コンストラクタ
	 * 
	 */
	public CloudantScoreStore() {
		CloudantClient cloudant = createClient();
		if (cloudant != null) {
        	// IBM Cloudantへの接続に成功した場合
        	
        	// DBインスタンス生成
			setDB(cloudant.database(databaseName, true));
		}
	}

	/**
	 * 初期化処理
	 * インデックスを作成する
	 * 
	 */
	public void init() {
		// index create.
		getDB().createIndex("committime", "committime", null,
				new IndexField[] { new IndexField("committime", SortOrder.desc) });
		getDB().createIndex("point", "point", null, new IndexField[] { new IndexField("point", SortOrder.asc) });
	}

	/**
	 * 全てのスコア情報を取得する
	 * 
	 * @return 全てのスコアリスト
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
	 * 登録日時順（降順）に並んだ全てのスコアリストを取得する
	 * 
	 * @return 登録日時順（降順）に並んだ全てのスコアリスト
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
	 * スコア順（昇順）に並んだ全てのスコアリストを取得する
	 * 
	 * @return スコア順（昇順）に並んだ全てのスコアリスト
	 */
	public Collection<Score> getAllOrderByPoint() {
		List<Score> docs;

		String selectorJson = "\"selector\": {  }";
		FindByIndexOptions options = new FindByIndexOptions();
		options.sort(new IndexField("point", IndexField.SortOrder.asc));
		docs = getDB().findByIndex(selectorJson, Score.class, options);

		return docs;
	}
	
	/**
	 * 同一ユーザ名のスコア登録数を取得する
	 * 
	 * @param username 対象ユーザ名
	 * @return 同一ユーザ名の登録数
	 */
	public int findByUsernameOverlapCnt(String username) {
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
     * 対象ユーザの全スコアを取得する
     * 
     * @param username 対象ユーザ名
     * @return 対象ユーザの全スコアリスト
     */
	public Collection<Score> findByUsername(String username) {
		
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
		
		return scoreList;
	}
	
    /**
     * スコア順の各ユーザ最高スコアリストを取得する
     * 
     * @return スコア順の各ユーザ最高スコアリスト
     */
	public Collection<Score> findHighScoreListOrderByPoint() {
		
		String selectorJson ="{\n" + 
				"   \"selector\": {\n" + 
				"            \"_id\": {\n" + 
				"               \"$gt\": 0\n" + 
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
	 * スコア情報を取得する
	 * 
	 * @param id 対象スコアID
	 * @return スコアBean
	 */
	public Score get(String id) {
		Score score = null;
		try {
			score = getDB().find(Score.class, id);
		} catch(NoDocumentException e) {
			score = null;
			e.printStackTrace();
		}
		return score;
	}

	/**
	 * スコアを登録する
	 * 
	 * @param score 登録スコアBean
	 * @return 登録後スコアBean
	 */
	public Score persist(Score score) {
		Score persistScore = null;
		try {
			String id = getDB().save(score).getId();
			persistScore = getDB().find(Score.class, id);
		} catch(NoDocumentException e) {
			persistScore = null;
			e.printStackTrace();
		}
		return persistScore;
	}

	/**
	 * スコアを更新する
	 * 
	 * @param id 更新対象スコアID
	 * @param score 更新スコアBean
	 * @return 更新後スコアBean
	 */
	public Score update(String id, Score newScore) {
		Score score = null;
		try {
			score = getDB().find(Score.class, id);
			score.setUsername(newScore.getUsername());
			score.setInputtime(newScore.getInputtime());
			score.setMisstype(newScore.getMisstype());
			score.setPoint(newScore.getPoint());
			score.setCommittime(newScore.getCommittime());
//			score.setHighscoreflg(newScore.isHighscoreflg());
			getDB().update(score);
			score = getDB().find(Score.class, id);
		} catch (NoDocumentException e) {
			// スコアが存在しない場合
			score = null;
			e.printStackTrace();
		}
		return score;
	}

	/**
	 * スコアを削除する
	 * 
	 * @param id 対象スコアID
	 */
	public void delete(String id) {
		System.out.println("DEBUG:" + id);
		Score score = getDB().find(Score.class, id);
		try {
			getDB().remove(id, score.get_rev());
		} catch (NoDocumentException e) {
			// スコア情報が存在しない場合
			e.printStackTrace();
		}
	}

	/**
	 * 全てのスコア数を取得する
	 * 
	 * @return 全てのスコア数
	 */
	public int count()  {
		return getAll().size();
	}
}
