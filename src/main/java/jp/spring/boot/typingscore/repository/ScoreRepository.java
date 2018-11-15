package jp.spring.boot.typingscore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.spring.boot.typingscore.bean.ScoreBean;
import jp.spring.boot.typingscore.db.ScoreId;

public interface ScoreRepository extends JpaRepository<ScoreBean, ScoreId>{
}