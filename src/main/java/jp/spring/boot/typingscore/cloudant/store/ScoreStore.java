package jp.spring.boot.typingscore.cloudant.store;

import java.util.Collection;

import jp.spring.boot.typingscore.cloudant.Score;

/**
 *スコア用ストア（IBM Cloudant用）インターフェース
 * 
 * @author tejc999999
 *
 */
public interface ScoreStore {

	/**
	 * データベースオブジェクトを取得する
	 * 
	 * @return データベースオブジェクト
	 * @throws Exception
	 */
	public Object getDB();

	/**
	 * 全てのスコア情報を取得する
	 * 
	 * @return 全てのスコアリスト
	 */
	public Collection<Score> getAll();

    /**
     * スコア順の各ユーザ最高スコアリストを取得する
     * 
     * @return スコア順の各ユーザ最高スコアリスト
     */
	public Collection<Score> findHighScoreListOrderByPoint();

	/**
	 * 登録日時順（降順）に並んだ全てのスコアリストを取得する
	 * 
	 * @return 登録日時順（降順）に並んだ全てのスコアリスト
	 */
	public Collection<Score> getAllOrderByCommittime();

	/**
	 * スコア順（昇順）に並んだ全てのスコアリストを取得する
	 * 
	 * @return スコア順（昇順）に並んだ全てのスコアリスト
	 */
	public Collection<Score> getAllOrderByPoint();

	/**
	 * 同一ユーザ名のスコア登録数を取得する
	 * 
	 * @param username 対象ユーザ名
	 * @return 同一ユーザ名の登録数
	 */
	public int findByUsernameOverlapCnt(String username);

    /**
     * 対象ユーザの全スコアを取得する
     * 
     * @param username 対象ユーザ名
     * @return 対象ユーザの全スコアリスト
     */
	public Collection<Score> findByUsername(String username);

	/**
	 * スコア情報を取得する
	 * 
	 * @param id 対象スコアID
	 * @return スコアBean
	 */
	public Score get(String id);

	/**
	 * スコアを登録する
	 * 
	 * @param score 登録スコアBean
	 * @return 登録後スコアBean
	 */
	public Score persist(Score score);

	/**
	 * スコアを更新する
	 * 
	 * @param id 更新対象スコアID
	 * @param score 更新スコアBean
	 * @return 更新後スコアBean
	 */
	public Score update(String id, Score score);

	/**
	 * スコアを削除する
	 * 
	 * @param id 対象スコアID
	 */
	public void delete(String id);

	/**
	 * 全てのスコア数を取得する
	 * 
	 * @return 全てのスコア数
	 */
	public int count() throws Exception;

	/**
	 * 初期化処理
	 * インデックスを作成する
	 * 
	 */
	public void init();
}
