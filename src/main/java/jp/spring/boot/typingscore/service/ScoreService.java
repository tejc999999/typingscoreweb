package jp.spring.boot.typingscore.service;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.spring.boot.typingscore.bean.ScoreBean;
import jp.spring.boot.typingscore.cloudant.Score;
import jp.spring.boot.typingscore.cloudant.store.ScoreStore;
import jp.spring.boot.typingscore.cloudant.store.ScoreStoreFactory;
import jp.spring.boot.typingscore.cloudant.store.VCAPHelper;
import jp.spring.boot.typingscore.config.ParameterProperties;
import jp.spring.boot.typingscore.db.ScoreId;
import jp.spring.boot.typingscore.form.ScoreForm;
import jp.spring.boot.typingscore.repository.ScoreRepository;

import org.apache.commons.lang.time.DateUtils;

/**
 * スコア用サービス
 * 
 * @author tejc999999
 *
 */
@Service
public class ScoreService {

	/**
	 * スコア用リポジトリ
	 */
	@Autowired
	ScoreRepository scoreRepository;
	
	@Autowired
	ParameterProperties parameterPropaties;

	/**
	 * スコアを登録する
	 * 
	 * @param scoreForm 登録スコア用ScoreForm
	 * @return Registered 登録後スコア用ScoreForm
	 */
	public ScoreForm create(ScoreForm scoreForm) {

		if (VCAPHelper.VCAP_SERVICES != null && !VCAPHelper.VCAP_SERVICES.equals("{}")) {
			// DBがIBM Cloudの場合

			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			Score score = new Score();
			// 複合主キーを作成する
			score.set_id(scoreForm.getUsername() + scoreForm.getCommittime());
			score.setUsername(scoreForm.getUsername());
			if (scoreForm.getCommittime() == null) {
				Timestamp dateSecond = new Timestamp(DateUtils.truncate(new Date(), Calendar.SECOND).getTime());
				score.setCommittime(dateSecond);
			} else {
				score.setCommittime(scoreForm.getCommittime());
			}
			score.setInputtime((scoreForm.getInputtimeMin() * 60) + scoreForm.getInputtimeSec());
			score.setMisstype(scoreForm.getMisstype());

			// スコア＝入力時間＋（ミスタイプ数×２）
			score.setPoint((scoreForm.getInputtimeMin() * 60) + scoreForm.getInputtimeSec() + (scoreForm.getMisstype() * 2));

	         //ゲーム区分
            if(scoreForm.getGamecode() == null) {
                score.setGamecode(parameterPropaties.getActiveGameCode());
            } else {
                // CSVインポート対応
                score.setGamecode(scoreForm.getGamecode());
            }
			
            score.setUsernamedepartment(scoreForm.getUsername().substring(0, scoreForm.getUsername().indexOf("科") + 1));
            score.setUsernamesplit(scoreForm.getUsername().substring(scoreForm.getUsername().indexOf("科") + 1,scoreForm.getUsername().length()).trim().replaceFirst("　", ""));    

            
			scoreStore.persist(score);

			scoreForm.setCommittime(score.getCommittime());
			scoreForm.setPoint(score.getPoint());
		} else {
			// DBがH2データベースの場合

			// 複合主キーを作成
			ScoreId scoreId = new ScoreId();

			scoreId.setUsername(scoreForm.getUsername());
			// H2データベースがミリ秒の先頭0詰めを正しく処理できない（バグ？）ため、
			// ミリ秒を切り捨てる
			if (scoreForm.getCommittime() == null) {
				Timestamp dateSecond = new Timestamp(DateUtils.truncate(new Date(), Calendar.SECOND).getTime());
				scoreId.setCommittime(dateSecond);
			} else {
				scoreId.setCommittime(scoreForm.getCommittime());
			}
			ScoreBean scoreBean = new ScoreBean();
			// 複合主キーを設定
			scoreBean.setId(scoreId);
			scoreBean.setInputtime((scoreForm.getInputtimeMin() * 60) + scoreForm.getInputtimeSec());
			scoreBean.setMisstype(scoreForm.getMisstype());

			// スコア＝入力時間＋（ミスタイプ数×２）
			scoreBean.setPoint((scoreForm.getInputtimeMin() * 60) + scoreForm.getInputtimeSec() + (scoreForm.getMisstype() * 2));
			
			//ゲーム区分
			if(scoreForm.getGamecode() == null) {
				scoreBean.setGamecode(parameterPropaties.getActiveGameCode());
			} else {
			    // CSVインポート対応
				scoreBean.setGamecode(scoreForm.getGamecode());
			}
			
			//TODO：不要
	         //登録したゲーム区分が日本語か英語の場合、学科名と名前を分割
//			if(scoreBean.getGamecode().equals("JA") || scoreBean.getGamecode().equals("EN")) {
			scoreBean.setUsernamedepartment(scoreId.getUsername().substring(0, scoreId.getUsername().indexOf("科") + 1));
			scoreBean.setUsernamesplit(scoreId.getUsername().substring(scoreId.getUsername().indexOf("科") + 1,scoreId.getUsername().length()).trim().replaceFirst("　", ""));	
//			}
			
			scoreBean = scoreRepository.save(scoreBean);
            //TODO：不要
//			if(scoreBean.getUsernamedepartment().length() > 0) {
//				return new ScoreForm();
//			}

			scoreForm.setCommittime(scoreBean.getId().getCommittime());
			scoreForm.setPoint(scoreBean.getPoint());
			
		}

		return scoreForm;
	}

	/**
	 * スコアを更新する
	 * 
	 * @pram oldUserName 更新前ユーザ名
	 * @param scoreForm スコア更新用ScoreForm
	 * @return 更新後ScoreForm
	 */
	public ScoreForm update(String oldUserName, ScoreForm newScoreForm) {

		if (VCAPHelper.VCAP_SERVICES != null && !VCAPHelper.VCAP_SERVICES.equals("{}")) {
			// DBがIBM Cloudの場合

			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			Score score = new Score();

			if (!oldUserName.equals(newScoreForm.getUsername())) {
				scoreStore.delete(oldUserName + newScoreForm.getCommittime());
			}

			score.set_id(newScoreForm.getUsername()  + newScoreForm.getCommittime());

			score.setUsername(newScoreForm.getUsername());
			score.setCommittime(newScoreForm.getCommittime());

			score.setInputtime((newScoreForm.getInputtimeMin() * 60) + newScoreForm.getInputtimeSec());
			score.setMisstype(newScoreForm.getMisstype());

			// スコア＝入力時間＋（ミスタイプ数×２）
			score.setPoint((newScoreForm.getInputtimeMin() * 60) + newScoreForm.getInputtimeSec() + (newScoreForm.getMisstype() * 2));

	         //ゲーム区分登録
			score.setGamecode(newScoreForm.getGamecode());
            
            // 学科名と名前を分割
			score.setUsernamedepartment(newScoreForm.getUsername().substring(0, newScoreForm.getUsername().indexOf("科") + 1));
			score.setUsernamesplit(newScoreForm.getUsername().substring(newScoreForm.getUsername().indexOf("科") + 1,newScoreForm.getUsername().length()).trim().replaceFirst("　", ""));
			
			if (!oldUserName.equals(newScoreForm.getUsername())) {
				scoreStore.persist(score);
			} else {
				scoreStore.update(score.get_id(), score);
			}

		} else {
			// DBがH2データベースの場合

			if (!oldUserName.equals(newScoreForm.getUsername())) {
				ScoreBean oldScoreBean = new ScoreBean();
				ScoreId oldScoreId = new ScoreId();
				oldScoreId.setUsername(oldUserName);
				oldScoreId.setCommittime(newScoreForm.getCommittime());
				oldScoreBean.setId(oldScoreId);

				scoreRepository.delete(oldScoreBean);
			}

			// 複合主キーを作成
			ScoreId scoreId = new ScoreId();
			scoreId.setUsername(newScoreForm.getUsername());
			scoreId.setCommittime(newScoreForm.getCommittime());

			ScoreBean scoreBean = new ScoreBean();

			// 複合主キーを登録
			scoreBean.setId(scoreId);
			scoreBean.setInputtime((newScoreForm.getInputtimeMin() * 60) + newScoreForm.getInputtimeSec());
			scoreBean.setMisstype(newScoreForm.getMisstype());

			// スコア＝入力時間＋（ミスタイプ数×２）
			scoreBean.setPoint((newScoreForm.getInputtimeMin() * 60) + newScoreForm.getInputtimeSec() + (newScoreForm.getMisstype() * 2));
			
			//ゲーム区分登録
			scoreBean.setGamecode(newScoreForm.getGamecode());
			
			// TODO: 不要
			//登録したゲーム区分が日本語か英語の場合、学科名と名前を分割
//			if(scoreBean.getGamecode().equals("JA") || scoreBean.getGamecode().equals("EN")) {
			scoreBean.setUsernamedepartment(scoreId.getUsername().substring(0, scoreId.getUsername().indexOf("科") + 1));
			scoreBean.setUsernamesplit(scoreId.getUsername().substring(scoreId.getUsername().indexOf("科") + 1,scoreId.getUsername().length()).trim().replaceFirst("　", ""));	
//			}
			
			scoreRepository.save(scoreBean);
			
		}

		return newScoreForm;

	}

	/**
	 * スコアを取得する.
	 * 
	 * @param id 取得するスコアの複合主キー
	 * @return ScoreForm
	 */
	public ScoreForm findById(ScoreId id) {

		ScoreForm form = new ScoreForm();

		if (VCAPHelper.VCAP_SERVICES != null && !VCAPHelper.VCAP_SERVICES.equals("{}")) {
			// DBがIBM Cloudの場合
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			Score score = scoreStore.get(id.getUsername() + id.getCommittime());

			form.setUsername(score.getUsername());
			form.setCommittime(score.getCommittime());
			form.setInputtimeMin(score.getInputtime() / 60);
			form.setInputtimeSec(score.getInputtime() % 60);
			BeanUtils.copyProperties(score, form);
		} else {
			// DBがH2データベースの場合
			Optional<ScoreBean> opt = scoreRepository.findById(id);
			opt.ifPresent(scoreBean -> {
				form.setUsername(scoreBean.getId().getUsername());
				form.setCommittime(scoreBean.getId().getCommittime());
				form.setInputtimeMin(scoreBean.getInputtime() / 60);
				form.setInputtimeSec(scoreBean.getInputtime() % 60);
				form.setGamecode(scoreBean.getGamecode());
				BeanUtils.copyProperties(scoreBean, form);
			});
		}

		return form;
	}

	/**
	 * 全てのスコアを取得する
	 * 
	 * @return 全スコアリスト
	 */
	public List<ScoreForm> findAll() {

		List<ScoreForm> formList = new ArrayList<ScoreForm>();

		if (VCAPHelper.VCAP_SERVICES != null && !VCAPHelper.VCAP_SERVICES.equals("{}")) {
			// DBがIBM Cloudの場合

			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			for (Score score : scoreStore.getAll()) {

				ScoreForm scoreForm = new ScoreForm();
				scoreForm.setUsername(score.getUsername());
				scoreForm.setCommittime(score.getCommittime());
				scoreForm.setInputtimeMin(score.getInputtime() / 60);
				scoreForm.setInputtimeSec(score.getInputtime() % 60);
				BeanUtils.copyProperties(score, scoreForm);
				formList.add(scoreForm);
			}
		} else {
			// DBがH2データベースの場合
			for (ScoreBean scoreBean : scoreRepository.findAll()) {
				ScoreForm scoreForm = new ScoreForm();
				scoreForm.setUsername(scoreBean.getId().getUsername());
				scoreForm.setCommittime(scoreBean.getId().getCommittime());
				scoreForm.setInputtimeMin(scoreBean.getInputtime() / 60);
				scoreForm.setInputtimeSec(scoreBean.getInputtime() % 60);
				scoreForm.setGamecode(scoreBean.getGamecode());
				BeanUtils.copyProperties(scoreBean, scoreForm);
				formList.add(scoreForm);
			}
		}
		return formList;
	}
	
	/**
	 * 登録日時順に並んだ全てのスコアを取得する
	 * 
	 * @return 登録日時順に並んだ全てのスコアのリスト
	 */
	public List<ScoreForm> findAllOrderByCommittime() {

		List<ScoreForm> formList = new ArrayList<ScoreForm>();

		if (VCAPHelper.VCAP_SERVICES != null && !VCAPHelper.VCAP_SERVICES.equals("{}")) {
			// DBがIBM Cloudの場合

			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			for (Score score : scoreStore.getAllOrderByCommittime()) {

				ScoreForm scoreForm = new ScoreForm();
				scoreForm.setUsername(score.getUsername());
				scoreForm.setCommittime(score.getCommittime());
				scoreForm.setInputtimeMin(score.getInputtime() / 60);
				scoreForm.setInputtimeSec(score.getInputtime() % 60);
				BeanUtils.copyProperties(score, scoreForm);
				formList.add(scoreForm);
			}

		} else {
			// DBがH2データベースの場合
			List<ScoreBean> beanlist = scoreRepository.findAllByOrderById_CommittimeDesc();
			beanlist.removeIf(score -> score.getGamecode().equals(parameterPropaties.getActiveGameCode()) == false);
			for (ScoreBean scoreBean : beanlist) {
				ScoreForm scoreForm = new ScoreForm();
				scoreForm.setUsername(scoreBean.getId().getUsername());
				scoreForm.setCommittime(scoreBean.getId().getCommittime());
				scoreForm.setInputtimeMin(scoreBean.getInputtime() / 60);
				scoreForm.setInputtimeSec(scoreBean.getInputtime() % 60);
				scoreForm.setGamecode(scoreBean.getGamecode());
				BeanUtils.copyProperties(scoreBean, scoreForm);
				formList.add(scoreForm);
			}
		}
		return formList;
	}

	/**
	 * スコア順に並んだ全てのスコアを取得する
	 * 
	 * @return スコア順に並んだ全てのスコアのリスト
	 */
	public List<ScoreForm> findAllOrderByPoint() {

		List<ScoreForm> formList = new ArrayList<ScoreForm>();

		if (VCAPHelper.VCAP_SERVICES != null && !VCAPHelper.VCAP_SERVICES.equals("{}")) {
			// DBがIBM Cloudの場合

			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			for (Score score : scoreStore.getAllOrderByPoint()) {
				ScoreForm scoreForm = new ScoreForm();
				scoreForm.setUsername(score.getUsername());
				scoreForm.setCommittime(score.getCommittime());
				scoreForm.setInputtimeMin(score.getInputtime() / 60);
				scoreForm.setInputtimeSec(score.getInputtime() % 60);
				BeanUtils.copyProperties(score, scoreForm);
				formList.add(scoreForm);
			}
		} else {
			// DBがH2データベースの場合
			List<ScoreBean> beanlist = scoreRepository.findAllByOrderByPoint();
			beanlist.removeIf(score -> score.getGamecode().equals(parameterPropaties.getActiveGameCode()) == false);
			
			for (ScoreBean scoreBean : beanlist) {
				ScoreForm scoreForm = new ScoreForm();
				scoreForm.setUsername(scoreBean.getId().getUsername());
				scoreForm.setCommittime(scoreBean.getId().getCommittime());
				scoreForm.setInputtimeMin(scoreBean.getInputtime() / 60);
				scoreForm.setInputtimeSec(scoreBean.getInputtime() % 60);
				scoreForm.setGamecode(scoreBean.getGamecode());
				BeanUtils.copyProperties(scoreBean, scoreForm);
				formList.add(scoreForm);
			}
		}
		return formList;
	}

	/**
	 * 登録済みスコアのユーザ名重複数を取得する
	 * 
	 * @param username 対象ユーザ名
	 * @return ユーザ名の重複数
	 */
	public int findUsernameOverlapCnt(String username) {

		int usernameOverlapCnt = 0;

		if (VCAPHelper.VCAP_SERVICES != null && !VCAPHelper.VCAP_SERVICES.equals("{}")) {
			// DBがIBM Cloudの場合
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			usernameOverlapCnt = scoreStore.findByUsernameOverlapCnt(username);

		} else {
			// DBがH2データベースの場合
			usernameOverlapCnt = scoreRepository.findUsernameOverlapCnt(username, parameterPropaties.getActiveGameCode());
		}

		return usernameOverlapCnt;
	}

	/**
	 * 最高スコアを取得する
	 * 
	 * @param username 対象ユーザ名
	 * @return ScoreForm
	 */
	public ScoreForm findHighScore(String username) {

		ScoreForm highScoreForm = new ScoreForm();

		if (VCAPHelper.VCAP_SERVICES != null && !VCAPHelper.VCAP_SERVICES.equals("{}")) {
			// DBがIBM Cloudの場合

			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			Score highScore = null;
			for (Score score : scoreStore.findByUsername(username)) {
				if (highScore == null || score.getPoint() < highScore.getPoint()) {
					highScore = score;
				}
			}

			if (highScore != null) {
				highScoreForm.setUsername(highScore.getUsername());
				highScoreForm.setCommittime(highScore.getCommittime());
				highScoreForm.setInputtimeMin(highScore.getInputtime() / 60);
				highScoreForm.setInputtimeSec(highScore.getInputtime() % 60);
				BeanUtils.copyProperties(highScore, highScoreForm);
			}
		} else {
			// DBがH2データベースの場合
			
			List<ScoreBean> scoreBeanList = scoreRepository.findById_Username(username);
			scoreBeanList.removeIf(score -> score.getGamecode().equals(parameterPropaties.getActiveGameCode()) == false);
			ScoreBean highScoreBean = null;
			for (ScoreBean scoreBean : scoreBeanList) {
				if (highScoreBean == null || scoreBean.getPoint() < highScoreBean.getPoint()) {
					highScoreBean = scoreBean;
				}
			}

			if (highScoreBean != null) {
				highScoreForm.setUsername(highScoreBean.getId().getUsername());
				highScoreForm.setCommittime(highScoreBean.getId().getCommittime());
				highScoreForm.setInputtimeMin(highScoreBean.getInputtime() / 60);
				highScoreForm.setInputtimeSec(highScoreBean.getInputtime() % 60);
				highScoreForm.setGamecode(highScoreBean.getGamecode());
				BeanUtils.copyProperties(highScoreBean, highScoreForm);
			}
		}

		return highScoreForm;
	}

	/**
	 * 最高スコアリストを取得する
	 * 
	 * @return 最高スコアリスト
	 */
	public List<ScoreForm> findHighScoreList() {
		Map<String, ScoreForm> formMap = new LinkedHashMap<String, ScoreForm>();

		if(VCAPHelper.VCAP_SERVICES  != null && !VCAPHelper.VCAP_SERVICES.equals("{}")) {
			// DBがIBM Cloudの場合

			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
	        for (Score score : scoreStore.getAllOrderByPoint()) {
	
				if(formMap.containsKey(score.getUsername())) {
					if(formMap.get(score.getUsername()).getPoint() > score.getPoint()) {
						ScoreForm scoreForm = new ScoreForm();
						scoreForm.setUsername(score.getUsername());
						scoreForm.setCommittime(score.getCommittime());
						scoreForm.setInputtimeMin(score.getInputtime() / 60);
						scoreForm.setInputtimeSec(score.getInputtime() % 60);
						BeanUtils.copyProperties(score, scoreForm);
						formMap.put(scoreForm.getUsername(), scoreForm);
					}
				} else {
					ScoreForm scoreForm = new ScoreForm();
					scoreForm.setUsername(score.getUsername());
					scoreForm.setCommittime(score.getCommittime());
					scoreForm.setInputtimeMin(score.getInputtime() / 60);
					scoreForm.setInputtimeSec(score.getInputtime() % 60);
					BeanUtils.copyProperties(score, scoreForm);
					formMap.put(scoreForm.getUsername(), scoreForm);
				}
			}
		} else {
			// DBがH2データベースの場合
			List<ScoreBean> beanlist = scoreRepository.findAllByOrderByPoint();
			beanlist.removeIf(score -> score.getPoint() == 0);
			beanlist.removeIf(score -> score.getGamecode().equals(parameterPropaties.getActiveGameCode()) == false);
			for (ScoreBean scoreBean : beanlist) {
	
				if(formMap.containsKey(scoreBean.getId().getUsername())) {
					if(formMap.get(scoreBean.getId().getUsername()).getPoint() > scoreBean.getPoint()) {
						ScoreForm scoreForm = new ScoreForm();
						scoreForm.setUsername(scoreBean.getId().getUsername());
						scoreForm.setCommittime(scoreBean.getId().getCommittime());
						scoreForm.setInputtimeMin(scoreBean.getInputtime() / 60);
						scoreForm.setInputtimeSec(scoreBean.getInputtime() % 60);
						scoreForm.setGamecode(scoreBean.getGamecode());
						BeanUtils.copyProperties(scoreBean, scoreForm);
						formMap.put(scoreForm.getUsername(), scoreForm);
					}
				} else {
					ScoreForm scoreForm = new ScoreForm();
					scoreForm.setUsername(scoreBean.getId().getUsername());
					scoreForm.setCommittime(scoreBean.getId().getCommittime());
					scoreForm.setInputtimeMin(scoreBean.getInputtime() / 60);
					scoreForm.setInputtimeSec(scoreBean.getInputtime() % 60);
					scoreForm.setGamecode(scoreBean.getGamecode());
					BeanUtils.copyProperties(scoreBean, scoreForm);
					formMap.put(scoreForm.getUsername(), scoreForm);
				}
			}
		}
		return  new ArrayList<ScoreForm>(formMap.values());
	}
	
	/**
	 * スコアを削除する
	 * 
	 * @param id 対象スコアの複合主キー
	 */
	public void delete(ScoreId id) {

		ScoreBean scoreBean = new ScoreBean();
		scoreBean.setId(id);
		if (VCAPHelper.VCAP_SERVICES != null && !VCAPHelper.VCAP_SERVICES.equals("{}")) {
			// DBがIBM Cloudの場合
			
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			scoreStore.delete(id.getUsername() + id.getCommittime());

		} else {
			// DBがH2データベースの場合
			
			scoreRepository.delete(scoreBean);
		}
	}

	/**
	 * 全てのスコアを削除する
	 * 
	 */
	public void deleteAll() {

		if (VCAPHelper.VCAP_SERVICES != null && !VCAPHelper.VCAP_SERVICES.equals("{}")) {
			// DBがIBM Cloudの場合
			
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			for (Score score : scoreStore.getAll()) {
				scoreStore.delete(score.get_id());
			}
			// インデックスを再作成する
			init();
		} else {
			// DBがH2データベースの場合
			
			scoreRepository.deleteAll();
		}
	}
	/**
	 * インデックスを作成する
	 */
	public void init() {
		if (VCAPHelper.VCAP_SERVICES != null && !VCAPHelper.VCAP_SERVICES.equals("{}")) {
			// DBがIBM Cloudの場合
			
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			scoreStore.init();
		}
	}
}