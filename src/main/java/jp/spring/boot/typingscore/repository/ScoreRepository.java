package jp.spring.boot.typingscore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.spring.boot.typingscore.bean.ScoreBean;
import jp.spring.boot.typingscore.db.ScoreId;

public interface ScoreRepository extends JpaRepository<ScoreBean, ScoreId>{	

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