package jp.spring.boot.typingscore.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.spring.boot.typingscore.bean.ScoreBean;
import jp.spring.boot.typingscore.cloudant.Score;
import jp.spring.boot.typingscore.cloudant.store.ScoreStore;
import jp.spring.boot.typingscore.cloudant.store.ScoreStoreFactory;
import jp.spring.boot.typingscore.cloudant.store.VCAPHelper;
import jp.spring.boot.typingscore.db.ScoreId;
import jp.spring.boot.typingscore.form.ScoreForm;
import jp.spring.boot.typingscore.repository.ScoreRepository;
import org.apache.commons.lang.time.DateUtils;
/**
 * スコアデータ サービスクラス
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
		// 登録時間：H2 databaseがミリ秒を正しく処理できないため、ミリ秒切り捨て
		Timestamp dateSecond = new Timestamp(DateUtils.truncate(new Date(), Calendar.SECOND).getTime());
		scoreId.setCommittime(dateSecond);
		
		ScoreBean scoreBean = new ScoreBean();
		// 複合主キーを登録
		scoreBean.setId(scoreId);
		// 入力時間
		scoreBean.setInputtime(scoreForm.getInputtime());
		// ミスタイプ数を登録
		scoreBean.setMisstype(scoreForm.getMisstype());
		// 点数：入力時間＋（ミスタイプ数×２）
		scoreBean.setPoint(scoreForm.getInputtime() + (scoreForm.getMisstype() * 2));

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// IBM Cloudantの処理
			
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			Score score = new Score();
			score.set_id(scoreBean.getId().getUsername() + scoreBean.getId().getCommittime());
			score.setUsername(scoreBean.getId().getUsername());
			score.setCommittime(scoreBean.getId().getCommittime());
			score.setInputtime(scoreBean.getInputtime());
			score.setMisstype(scoreBean.getMisstype());
			score.setPoint(scoreBean.getPoint());
			scoreStore.persist(score);
		} else {
			// H2データベースの処理
			scoreBean = scoreRepository.save(scoreBean);
		}
		scoreForm.setCommittime(scoreBean.getId().getCommittime());
		scoreForm.setPoint(scoreBean.getPoint());
		
		return scoreForm;
	}

	/**
	 * スコアデータ更新
	 * 
	 * @param scoreForm スコアデータForm
	 * @return 登録したスコアデータ
	 */
	public ScoreForm update(ScoreForm scoreForm) {

		// 複合主キーを作成
		ScoreId scoreId = new ScoreId();
		// ユーザ名
		scoreId.setUsername(scoreForm.getUsername());
		// 登録時間
		scoreId.setCommittime(scoreForm.getCommittime());

		ScoreBean scoreBean = new ScoreBean();
		// 複合主キーを登録
		scoreBean.setId(scoreId);
		// 入力時間
		scoreBean.setInputtime(scoreForm.getInputtime());
		// ミスタイプ数を登録
		scoreBean.setMisstype(scoreForm.getMisstype());
		// 点数：入力時間＋（ミスタイプ数×２）
		scoreBean.setPoint(scoreForm.getInputtime() + (scoreForm.getMisstype() * 2));

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// IBM Cloudantの処理
			
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			Score score = new Score();
			score.set_id(scoreBean.getId().getUsername() + scoreBean.getId().getCommittime());
			score.setUsername(scoreBean.getId().getUsername());
			score.setCommittime(scoreBean.getId().getCommittime());
			score.setInputtime(scoreBean.getInputtime());
			score.setMisstype(scoreBean.getMisstype());
			score.setPoint(scoreBean.getPoint());
			scoreStore.update(score.get_id(), score);
		} else {
			// H2データベースの処理
			scoreRepository.save(scoreBean);
		}

		return scoreForm;
	}

	/**
	 * スコアデータ取得
	 * 
	 * @return スコアデータ
	 */
	public ScoreForm findById(ScoreId id) {

		ScoreForm form = new ScoreForm();

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// IBM Cloudantの処理
			
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			Score score = scoreStore.get(id.getUsername() + id.getCommittime());

			form.setUsername(score.getUsername());
			form.setCommittime(score.getCommittime());
			form.setInputtime(score.getInputtime());
			form.setMisstype(score.getMisstype());
			form.setPoint(score.getPoint());
		} else {
			// H2データベースの処理
			Optional<ScoreBean> opt = scoreRepository.findById(id);
			opt.ifPresent(scoreBean -> {
				form.setUsername(scoreBean.getId().getUsername());
				form.setCommittime(scoreBean.getId().getCommittime());
				BeanUtils.copyProperties(scoreBean, form);
			});
		}

		return form;
	}

	/**
	 * スコアデータ全件取得
	 * 
	 * @return スコアデータ全件
	 */
	public List<ScoreForm> findAll() {
		
		List<ScoreBean> beanList = null;

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// IBM Cloudantの処理
			beanList = new ArrayList<ScoreBean>();
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
	        for (Score doc : scoreStore.getAll()) {
	        	ScoreBean scoreBean = new ScoreBean();
	        	ScoreId scoreId = new ScoreId();
	        	scoreId.setUsername(doc.getUsername());
	        	scoreId.setCommittime(doc.getCommittime());
	        	scoreBean.setId(scoreId);
	        	scoreBean.setInputtime(doc.getInputtime());
	        	scoreBean.setMisstype(doc.getMisstype());
	        	scoreBean.setPoint(doc.getPoint());
	        	
	        	beanList.add(scoreBean);
	        }
		} else {
			// H2データベースの処理
			beanList = scoreRepository.findAll();
		}
		List<ScoreForm> formList = new ArrayList<ScoreForm>();
		for (ScoreBean scoreBean : beanList) {
			ScoreForm scoreForm = new ScoreForm();
			scoreForm.setUsername(scoreBean.getId().getUsername());
			scoreForm.setCommittime(scoreBean.getId().getCommittime());
			BeanUtils.copyProperties(scoreBean, scoreForm);
			formList.add(scoreForm);
		}
		return formList;
	}

	/**
	 * スコアデータ全件取得（登録日時の遅い順にソート）
	 * 
	 * @return スコアデータ全件
	 */
	public List<ScoreForm> findAllOrderByCommittime() {

		List<ScoreBean> beanList = null;

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// IBM Cloudantの処理
			beanList = new ArrayList<ScoreBean>();
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
	        for (Score doc : scoreStore.getAllOrderByCommittime()) {
	        	ScoreBean scoreBean = new ScoreBean();
	        	ScoreId scoreId = new ScoreId();
	        	scoreId.setUsername(doc.getUsername());
	        	scoreId.setCommittime(doc.getCommittime());
	        	scoreBean.setId(scoreId);
	        	scoreBean.setInputtime(doc.getInputtime());
	        	scoreBean.setMisstype(doc.getMisstype());
	        	scoreBean.setPoint(doc.getPoint());
	        	
	        	beanList.add(scoreBean);
	        }
			
		} else {
			// H2データベースの処理
			beanList = scoreRepository.findAllByOrderById_CommittimeDesc();
			
			// TimeStampをDateに変換
			for(ScoreBean scoreBean: beanList) {
				scoreBean.getId().setCommittime(scoreBean.getId().getCommittime());
			}
				
		}
		List<ScoreForm> formList = new ArrayList<ScoreForm>();
		for (ScoreBean scoreBean : beanList) {
			ScoreForm scoreForm = new ScoreForm();
			scoreForm.setUsername(scoreBean.getId().getUsername());
			scoreForm.setCommittime(scoreBean.getId().getCommittime());
			BeanUtils.copyProperties(scoreBean, scoreForm);
			formList.add(scoreForm);
		}
		return formList;
	}

	/**
	 * スコアデータ全件取得（スコア順にソート）
	 * 
	 * @return スコアデータ全件
	 */
	public List<ScoreForm> findAllOrderByPoint() {

		List<ScoreBean> beanList = null;

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// IBM Cloudantの処理
			beanList = new ArrayList<ScoreBean>();
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
	        for (Score doc : scoreStore.getAllOrderByScore()) {
	        	ScoreBean scoreBean = new ScoreBean();
	        	ScoreId scoreId = new ScoreId();
	        	scoreId.setUsername(doc.getUsername());
	        	scoreId.setCommittime(doc.getCommittime());
	        	scoreBean.setId(scoreId);
	        	scoreBean.setInputtime(doc.getInputtime());
	        	scoreBean.setMisstype(doc.getMisstype());
	        	scoreBean.setPoint(doc.getPoint());
	        	beanList.add(scoreBean);
	        }
		} else {
			// H2データベースの処理
			beanList = scoreRepository.findAllByOrderByPoint();
		}
		List<ScoreForm> formList = new ArrayList<ScoreForm>();
		for (ScoreBean scoreBean : beanList) {
			ScoreForm scoreForm = new ScoreForm();
			scoreForm.setUsername(scoreBean.getId().getUsername());
			scoreForm.setCommittime(scoreBean.getId().getCommittime());
			BeanUtils.copyProperties(scoreBean, scoreForm);
			formList.add(scoreForm);
		}
		return formList;
	}

	/**
	 * ユーザ名重複無しスコアデータ取得
	 * 
	 * @return ユーザ名重複無しスコアデータ
	 */
	public boolean findUsernameOverlap(String username) {

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// IBM Cloudantの処理
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			for (String inUsername : scoreStore.findUsernameOverlap()) {
				if (inUsername.equals(username)) {
					return true;
				}
			}
		} else {
			// H2データベースの処理
			for (String inUsername : scoreRepository.findUsernameOverlap()) {
				if (inUsername.equals(username)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * スコアデータ削除
	 * 
	 * @param id スコアデータ識別情報
	 */
	public void delete(ScoreId id) {
		// bookRepository.delete(id);
		ScoreBean scoreBean = new ScoreBean();
		scoreBean.setId(id);
		if(VCAPHelper.VCAP_SERVICES  != null) {
			// IBM Cloudantの処理
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			scoreStore.delete(id.getUsername() + id.getCommittime());
			
		} else {
			// H2データベースの処理
			scoreRepository.delete(scoreBean);
		}
	}
	
	/**
	 * スコアデータ全件削除
	 * 
	 */
	public void deleteAll() {
		
		if(VCAPHelper.VCAP_SERVICES  != null) {
			// IBM Cloudantの処理
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			for(Score score : scoreStore.getAll()) {
				scoreStore.delete(score.get_id());
			}
			// インデックス再作成
			init();
		} else {
			// H2データベースの処理
			scoreRepository.deleteAll();
		}
	}
	
	public void init() {
		if(VCAPHelper.VCAP_SERVICES  != null) {
			// IBM Cloudantの処理
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			scoreStore.init();
		}
	}
}