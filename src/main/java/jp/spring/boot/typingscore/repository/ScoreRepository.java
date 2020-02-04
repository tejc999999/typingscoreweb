package jp.spring.boot.typingscore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jp.spring.boot.typingscore.bean.ScoreBean;
import jp.spring.boot.typingscore.db.ScoreId;

/**
 * スコア用リポジトリ
 * 
 * @author tejc999999
 *
 */
public interface ScoreRepository extends JpaRepository<ScoreBean, ScoreId> {

	/**
	 * ユーザ名の重複数を取得する
	 * 
	 * @return ユーザ名の重複数
	 */
	@Query(value = "select COUNT(*)  from ScoreBean s where s.id.username = :username AND s.gamecode = :gamecode")
	int findUsernameOverlapCnt(String username, String gamecode);

	/**
	 * 登録日時で並んだ全てのスコアを取得する
	 * 
	 * @return 登録日時順に並んだ全てのスコアリスト
	 */
	List<ScoreBean> findAllByOrderById_CommittimeDesc();

	/**
	 * スコア順で並んだ全てのスコアを取得する
	 * 
	 * @return スコア順に並んだ全てのスコアリスト
	 */
	List<ScoreBean> findAllByOrderByPoint();
	
	/**
	 * 同一ユーザ名のスコアをすべて取得する
	 * 
	 * @param username 対象ユーザ名
	 * @return 対象ユーザ名の全てのスコア
	 */
	List<ScoreBean> findById_Username(String username);
}