package jp.spring.boot.typingscore.service;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import jp.spring.boot.typingscore.db.ScoreId;
import jp.spring.boot.typingscore.form.ScoreForm;
import jp.spring.boot.typingscore.repository.ScoreRepository;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;

/**
 * Score service.
 * 
 * @author tejc999999
 *
 */
@Service
public class ScoreService {

	/**
	 * Score repository.
	 */
	@Autowired
	ScoreRepository scoreRepository;

	/**
	 * Register score data
	 * 
	 * @param scoreForm Score data Form
	 * @return Registered score data
	 */
	public ScoreForm create(ScoreForm scoreForm) {

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant

			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			Score score = new Score();
			// Create compound primary key
			score.setUsername(scoreForm.getUsername());
			if(scoreForm.getCommittime() == null) {
				Timestamp dateSecond = new Timestamp(DateUtils.truncate(new Date(), Calendar.SECOND).getTime());
				score.setCommittime(dateSecond);
			} else {
				score.setCommittime(scoreForm.getCommittime());
			}
			score.setInputtime(scoreForm.getInputtime());
			score.setMisstype(scoreForm.getMisstype());

			// point = inputtime + (misstype * 2)
			score.setPoint(scoreForm.getInputtime() + (scoreForm.getMisstype() * 2));

			scoreStore.persist(score);

			scoreForm.setCommittime(score.getCommittime());
			scoreForm.setPoint(score.getPoint());
		} else {
			// Create compound primary key
			ScoreId scoreId = new ScoreId();

			scoreId.setUsername(scoreForm.getUsername());
			// Registration time
			// * Since H2 database can not correctly process milliseconds, it is truncated by milliseconds
			if(scoreForm.getCommittime() == null) {
				Timestamp dateSecond = new Timestamp(DateUtils.truncate(new Date(), Calendar.SECOND).getTime());
				scoreId.setCommittime(dateSecond);
			} else {
				scoreId.setCommittime(scoreForm.getCommittime());
			}
			// case: h2 database
			ScoreBean scoreBean = new ScoreBean();
			// Create compound primary key
			scoreBean.setId(scoreId);

			scoreBean.setInputtime(scoreForm.getInputtime());
			scoreBean.setMisstype(scoreForm.getMisstype());

			// point = inputtime + (misstype * 2)
			scoreBean.setPoint(scoreForm.getInputtime() + (scoreForm.getMisstype() * 2));
			scoreBean = scoreRepository.save(scoreBean);

			scoreForm.setCommittime(scoreBean.getId().getCommittime());
			scoreForm.setPoint(scoreBean.getPoint());
		}
		
		return scoreForm;
	}

	/**
	 * Update score data
	 * 
	 * @param scoreForm Score data Form
	 * @return Registered score data
	 */
	public ScoreForm update(ScoreForm scoreForm) {



		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant

			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			Score score = new Score();
			
			score.setUsername(scoreForm.getUsername());
			score.setCommittime(scoreForm.getCommittime());

			score.setInputtime(scoreForm.getInputtime());
			score.setMisstype(scoreForm.getMisstype());
			
			// point = inputtime + (misstype * 2)
			score.setPoint(scoreForm.getInputtime() + (scoreForm.getMisstype() * 2));
			
			scoreStore.update(score.get_id(), score);

		} else {
			// case: h2 database
			
			// Create composite primary key
			ScoreId scoreId = new ScoreId();
			scoreId.setUsername(scoreForm.getUsername());
			scoreId.setCommittime(scoreForm.getCommittime());

			ScoreBean scoreBean = new ScoreBean();
			
			// Register composite primary key
			scoreBean.setId(scoreId);

			scoreBean.setInputtime(scoreForm.getInputtime());
			scoreBean.setMisstype(scoreForm.getMisstype());

			// point = inputtime + (misstype * 2)
			scoreBean.setPoint(scoreForm.getInputtime() + (scoreForm.getMisstype() * 2));
			
			scoreRepository.save(scoreBean);
		}

		return scoreForm;
	}

	/**
	 * Acquire score data.
	 * 
	 * @return Score data.
	 */
	public ScoreForm findById(ScoreId id) {

		ScoreForm form = new ScoreForm();

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			Score score = scoreStore.get(id.getUsername() + id.getCommittime());

			form.setUsername(score.getUsername());
			form.setCommittime(score.getCommittime());
			form.setInputtime(score.getInputtime());
			form.setMisstype(score.getMisstype());
			form.setPoint(score.getPoint());
		} else {
			// case: h2 database
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
	 * Acquire all score data
	 * 
	 * @return All score data
	 */
	public List<ScoreForm> findAll() {
		
		List<ScoreForm> formList = new ArrayList<ScoreForm>();

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
	        for (Score score : scoreStore.getAll()) {
	        	
	        	ScoreForm scoreForm = new ScoreForm();
	        	scoreForm.setUsername(score.getUsername());
	        	scoreForm.setCommittime(score.getCommittime());
	        	scoreForm.setInputtime(score.getInputtime());
	        	scoreForm.setMisstype(score.getMisstype());
	        	scoreForm.setPoint(score.getPoint());
	        	formList.add(scoreForm);
	        }
		} else {
			// case: h2 database

			for (ScoreBean scoreBean : scoreRepository.findAll()) {
				ScoreForm scoreForm = new ScoreForm();
				scoreForm.setUsername(scoreBean.getId().getUsername());
				scoreForm.setCommittime(scoreBean.getId().getCommittime());
				BeanUtils.copyProperties(scoreBean, scoreForm);
				formList.add(scoreForm);
			}
		}
		return formList;
	}

	/**
	 * Acquire all score data.
	 *  (sorted in order of late registration date)
	 * 
	 * @return All score data.
	 */
	public List<ScoreForm> findAllOrderByCommittime() {

		List<ScoreForm> formList = new ArrayList<ScoreForm>();

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant

			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
	        for (Score score : scoreStore.getAllOrderByCommittime()) {
	        	
	        	ScoreForm scoreForm = new ScoreForm();
	        	scoreForm.setUsername(score.getUsername());
	        	scoreForm.setCommittime(score.getCommittime());
	        	scoreForm.setInputtime(score.getInputtime());
	        	scoreForm.setMisstype(score.getMisstype());
	        	scoreForm.setPoint(score.getPoint());
	        	formList.add(scoreForm);
	        }
			
		} else {
			// case: h2 database

			for (ScoreBean scoreBean : scoreRepository.findAllByOrderById_CommittimeDesc()) {
				ScoreForm scoreForm = new ScoreForm();
				scoreForm.setUsername(scoreBean.getId().getUsername());
				scoreForm.setCommittime(scoreBean.getId().getCommittime());
				BeanUtils.copyProperties(scoreBean, scoreForm);
				formList.add(scoreForm);
			}
		}
		return formList;
	}

	/**
	 * Acquire all score data (sort by score).
	 * 
	 * @return All score data.
	 */
	public List<ScoreForm> findAllOrderByPoint() {

		List<ScoreForm> formList = new ArrayList<ScoreForm>();

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant

			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
	        for (Score score : scoreStore.getAllOrderByScore()) {
				ScoreForm scoreForm = new ScoreForm();
	        	scoreForm.setUsername(score.getUsername());
	        	scoreForm.setCommittime(score.getCommittime());
	        	scoreForm.setInputtime(score.getInputtime());
	        	scoreForm.setMisstype(score.getMisstype());
	        	scoreForm.setPoint(score.getPoint());
	        	formList.add(scoreForm);
	        }
		} else {
			// case: h2 database

			for (ScoreBean scoreBean : scoreRepository.findAllByOrderByPoint()) {
				ScoreForm scoreForm = new ScoreForm();
				scoreForm.setUsername(scoreBean.getId().getUsername());
				scoreForm.setCommittime(scoreBean.getId().getCommittime());
				BeanUtils.copyProperties(scoreBean, scoreForm);
				formList.add(scoreForm);
			}
		}
		return formList;
	}
	
	/**
	 * Acquire the score registration number of the target user.
	 * 
	 * @param username Check target user name.
	 * @return User name duplication determination result.
	 */
	public int findUsernameOverlapCnt(String username) {
		
		int usernameOverlapCnt = 0;
		
		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			usernameOverlapCnt = scoreStore.findByUsernameOverlapCnt(username);

		} else {
			// case: h2 database
			usernameOverlapCnt = scoreRepository.findUsernameOverlapCnt(username);
		}
			
		return usernameOverlapCnt;
	}
	
	/**
	 * get high score of the target user name.
	 * 
	 * @param username user name.
	 * @return 
	 */
	public ScoreForm findHighScore(String username) {
		
		ScoreForm highScoreForm = new ScoreForm();
		
		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			Score highScore = null;
			for(Score score : scoreStore.findByUsername(username)) {
				if(score == null || score.getPoint() < highScore.getPoint()) {
					highScore = score;
				}
			}
			
			if(highScore != null) {
				highScoreForm.setUsername(highScore.getUsername());
				highScoreForm.setCommittime(highScore.getCommittime());
				highScoreForm.setInputtime(highScore.getInputtime());
				highScoreForm.setMisstype(highScore.getMisstype());
				highScoreForm.setPoint(highScore.getPoint());
			}
		} else {
			// case: h2 database
			List<ScoreBean> scoreBeanList = scoreRepository.findById_Username(username);
			ScoreBean highScoreBean = null;
			for(ScoreBean scoreBean : scoreBeanList) {
				if(scoreBean == null || scoreBean.getPoint() < highScoreBean.getPoint()) {
					highScoreBean = scoreBean;
				}
			}

			if(highScoreBean != null) {
				highScoreForm.setUsername(scoreBeanList.get(0).getId().getUsername());
				highScoreForm.setCommittime(scoreBeanList.get(0).getId().getCommittime());
				BeanUtils.copyProperties(scoreBeanList.get(0), highScoreForm);
			}
		}
			
		return highScoreForm;
	}
	
	/**
	 * get high score of the target user name.
	 * 
	 * @param username user name.
	 * @return 
	 */
	public List<ScoreForm> findHighScoreList() {
		Map<String, ScoreForm> formMap = new HashMap<String, ScoreForm>();

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant

			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
	        for (Score score : scoreStore.getAll()) {
	
				if(formMap.containsKey(score.getUsername())) {
					if(formMap.get(score.getUsername()).getPoint() > score.getPoint()) {
						ScoreForm scoreForm = new ScoreForm();
						scoreForm.setUsername(score.getUsername());
						scoreForm.setCommittime(score.getCommittime());
						scoreForm.setInputtime(score.getInputtime());
						scoreForm.setMisstype(score.getMisstype());
						scoreForm.setPoint(score.getPoint());

						formMap.put(scoreForm.getUsername(), scoreForm);
					}
				} else {
					ScoreForm scoreForm = new ScoreForm();
					scoreForm.setUsername(score.getUsername());
					scoreForm.setCommittime(score.getCommittime());
					scoreForm.setInputtime(score.getInputtime());
					scoreForm.setMisstype(score.getMisstype());
					scoreForm.setPoint(score.getPoint());

					formMap.put(scoreForm.getUsername(), scoreForm);
				}
			}
		} else {
			// case: h2 database

			for (ScoreBean scoreBean : scoreRepository.findAll()) {
	
				if(formMap.containsKey(scoreBean.getId().getUsername())) {
					if(formMap.get(scoreBean.getId().getUsername()).getPoint() > scoreBean.getPoint()) {
						ScoreForm scoreForm = new ScoreForm();
						scoreForm.setUsername(scoreBean.getId().getUsername());
						scoreForm.setCommittime(scoreBean.getId().getCommittime());
						BeanUtils.copyProperties(scoreBean, scoreForm);
						formMap.put(scoreForm.getUsername(), scoreForm);
					}
				} else {
					ScoreForm scoreForm = new ScoreForm();
					scoreForm.setUsername(scoreBean.getId().getUsername());
					scoreForm.setCommittime(scoreBean.getId().getCommittime());
					BeanUtils.copyProperties(scoreBean, scoreForm);
					formMap.put(scoreForm.getUsername(), scoreForm);
				}
			}
		}
		return  new ArrayList<ScoreForm>(formMap.values());
	}
	
//	/**
//	 * 
//	 * @return
//	 */
//	public List<ScoreForm> findHighScoreListOrderByPoint() {
//		
//		List<ScoreBean> beanList = null;
//		
//		if(VCAPHelper.VCAP_SERVICES  != null) {
//			// case: IBM Cloudant
//			beanList = new ArrayList<ScoreBean>();
//			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
//			for (Score doc : scoreStore.findHighScoreListOrderByPoint()) {
//				ScoreBean scoreBean = createScoreBean(doc);
//				beanList.add(scoreBean);
//			}
//		} else {
//			// case: h2 database
//			beanList = scoreRepository.findHighScoreListOrderByPoint();
//		}
//		List<ScoreForm> formList = new ArrayList<ScoreForm>();
//		for (ScoreBean scoreBean : beanList) {
//			ScoreForm scoreForm = new ScoreForm();
//			scoreForm.setUsername(scoreBean.getId().getUsername());
//			scoreForm.setCommittime(scoreBean.getId().getCommittime());
//			BeanUtils.copyProperties(scoreBean, scoreForm);
//			formList.add(scoreForm);
//		}
//			
//		return formList;
//	}

	/**
	 * Delete score data.
	 * 
	 * @param id Score data identification information.
	 */
	public void delete(ScoreId id) {
		// bookRepository.delete(id);
		ScoreBean scoreBean = new ScoreBean();
		scoreBean.setId(id);
		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			scoreStore.delete(id.getUsername() + id.getCommittime());
			
		} else {
			// case: h2 database
			scoreRepository.delete(scoreBean);
		}
	}
	
	/**
	 * Delete all score data.
	 * 
	 */
	public void deleteAll() {
		
		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			for(Score score : scoreStore.getAll()) {
				scoreStore.delete(score.get_id());
			}
			// Re-create the index
			init();
		} else {
			// case: h2 database
			scoreRepository.deleteAll();
		}
	}
	
	/**
	 * 
	 * @param username
	 */
	public void highscoreupdate(String username) {
		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			for(Score score : scoreStore.getAll()) {
				scoreStore.delete(score.get_id());
			}

			// Re-create the index
			init();
		} else {
			// case: h2 database
			scoreRepository.deleteAll();
		}
	}	
	
	/**
	 * create sort index.
	 */
	public void init() {
		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			scoreStore.init();
		}
	}
}