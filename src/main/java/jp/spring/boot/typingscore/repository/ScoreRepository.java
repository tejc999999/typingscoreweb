package jp.spring.boot.typingscore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jp.spring.boot.typingscore.bean.ScoreBean;
import jp.spring.boot.typingscore.db.ScoreId;

/**
 * Score repository.
 * 
 * @author tejc999999
 *
 */
public interface ScoreRepository extends JpaRepository<ScoreBean, ScoreId> {

	/**
	 * Acquire user name list of scores
	 * 
	 * @return Score user name list
	 */
	@Query(value = "select new java.lang.String(s.id.username) from ScoreBean s group by s.id.username")
	List<String> findUsernameOverlap();

	/**
	 * Sort by registration date and time to get all scores
	 * 
	 * @return All scores
	 */
	List<ScoreBean> findAllByOrderById_CommittimeDesc();

	/**
	 * Sort by score and retrieve all scores
	 * 
	 * @return All scores
	 */
	List<ScoreBean> findAllByOrderByPoint();
}