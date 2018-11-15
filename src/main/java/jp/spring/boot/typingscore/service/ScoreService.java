package jp.spring.boot.typingscore.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.spring.boot.typingscore.bean.ScoreBean;
import jp.spring.boot.typingscore.db.ScoreId;
import jp.spring.boot.typingscore.form.ScoreForm;
import jp.spring.boot.typingscore.repository.ScoreRepository;

/**
 * スコアデータ　サービスクラス
 * 
 * @author user01-m
 *
 */
@Service
public class ScoreService {

	  @Autowired
	  ScoreRepository scoreRepository;
	  
	  /**
	   * スコアデータ登録
	   * 
	   * @param scoreForm スコアデータForm
	   * @return 登録したスコアデータ
	   */
	  public ScoreForm create(ScoreForm scoreForm) {
		  
		  // 複合主キーを作成
		  ScoreId scoreId = new ScoreId();
		  // ユーザ名
		  scoreId.setUsername(scoreForm.getUsername());
		  // 登録時間：現在の日時情報を登録
		  scoreId.setCommittime(new Date());
		  System.out.println("DEBUG:" + scoreId.getCommittime());
		  ScoreBean scoreBean = new ScoreBean();
		  // 複合主キーを登録
		  scoreBean.setId(scoreId);
		  // 入力時間
		  scoreBean.setInputtime(scoreForm.getInputtime());
		  // ミスタイプ数を登録
		  scoreBean.setMisstype(scoreForm.getMisstype());
		  // 点数：入力時間＋（ミスタイプ数×２）
		  scoreBean.setPoint(scoreForm.getInputtime() + (scoreForm.getMisstype() * 2));
		  
		  scoreRepository.save(scoreBean);
		  
		  return scoreForm;
	  }
	  
	  /**
	   * スコアデータ全件取得
	   * 
	   * @return スコアデータ全件
	   */
	  public List<ScoreForm> findAll() {
		  List<ScoreBean> beanList =scoreRepository.findAll();
		  List<ScoreForm> formList = new ArrayList<ScoreForm>();
		  for(ScoreBean scoreBean: beanList) {
			  ScoreForm scoreForm = new ScoreForm();
			  scoreForm.setUsername(scoreBean.getId().getUsername());
			  scoreForm.setCommittime(scoreBean.getId().getCommittime());
			  System.out.println("DEBUG-2:" + scoreBean.getId().getCommittime());
			  BeanUtils.copyProperties(scoreBean, scoreForm);
		      formList.add(scoreForm);
		  }
		  return formList;
	  }
	  public void delete(ScoreId id ) {
		  // bookRepository.delete(id);
		  ScoreBean scoreBean = new ScoreBean();
		  scoreBean.setId(id);
		  scoreRepository.delete(scoreBean);
	  }
}