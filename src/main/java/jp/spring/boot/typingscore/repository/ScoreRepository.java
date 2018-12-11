package jp.spring.boot.typingscore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jp.spring.boot.typingscore.bean.ScoreBean;
import jp.spring.boot.typingscore.db.ScoreId;

public interface ScoreRepository extends JpaRepository<ScoreBean, ScoreId> {

	/**
	 * ユーザ名一覧取得
	 * 
	 * @return ユーザ名一覧
	 */
	@Query(value = "select new java.lang.String(s.id.username) from ScoreBean s group by s.id.username")
	List<String> findUsernameOverlap();

	/**
	 * 登録日時で並べ替えて全件取得
	 * 
	 * @return
	 */
	List<ScoreBean> findAllByOrderById_CommittimeDesc();

	/**
	 * スコアで並べ替えて全件取得
	 * 
	 * @return
	 */
	List<ScoreBean> findAllByOrderByPoint();
}