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

//	/**
//	 * Acquire user name list of scores
//	 * 
//	 * @return Score user name list
//	 */
//	@Query(value = "select s from ScoreBean s where s.id.username = :username and highscoreflg = true")
//	List<ScoreBean> findHighScore(String username);
//
//	/**
//	 * Acquire user name list of scores
//	 * 
//	 * @return Score user name list
//	 */
//	@Query(value = "select s from ScoreBean s where highscoreflg = true order by s.point asc")
//	List<ScoreBean> findHighScoreListOrderByPoint();

//	/**
//	 * Acquire user name list of scores
//	 * 
//	 * @return Score user name list
//	 */
//	@Query(value = "select new java.lang.String(s.id.username) from ScoreBean s group by s.id.username")
//	List<String> findUsernameOverlap();

	/**
	 * Count the number of specified user name
	 * 
	 * @return number of specified user name
	 */
	@Query(value = "select COUNT(*)  from ScoreBean s where s.id.username = :username")
	int findUsernameOverlapCnt(String username);
	
	
	
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
	
	/**
	 * 
	 * @param username user name.
	 * @return All scores of target username.
	 */
	List<ScoreBean> findById_Username(String username);
}