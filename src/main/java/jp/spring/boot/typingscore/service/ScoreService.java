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
		
		ScoreBean scoreBean = new ScoreBean();
		// Create compound primary key
		scoreBean.setId(scoreId);

		scoreBean.setInputtime(scoreForm.getInputtime());
		scoreBean.setMisstype(scoreForm.getMisstype());

		// point = inputtime + (misstype * 2)
		scoreBean.setPoint(scoreForm.getInputtime() + (scoreForm.getMisstype() * 2));

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant

			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			Score score = createScore(scoreBean);

			// Switch highscoreflg
			Score highScore = scoreStore.findHighScore(scoreBean.getId().getUsername());
			if(highScore == null) {
				score.setHighscoreflg(true);
			} else if(highScore.getPoint() > score.getPoint()) {
				highScore.setHighscoreflg(false);
				scoreStore.update(highScore.get_id(), highScore);
				score.setHighscoreflg(true);
			} else {
				score.setHighscoreflg(false);
			}
//			System.out.println("DEBUG:" + score);
			scoreStore.persist(score);
		} else {
			// case: h2 database

			List<ScoreBean>  highScoreBeanList = scoreRepository.findHighScore(scoreId.getUsername());

			// Switch highscoreflg
			if(highScoreBeanList.size() == 0) {
				scoreBean.setHighscoreflg(true);
			} else if(highScoreBeanList.get(0).getPoint() > scoreBean.getPoint()) {
				highScoreBeanList.get(0).setHighscoreflg(false);
				scoreRepository.save(highScoreBeanList.get(0));
				scoreBean.setHighscoreflg(true);
			} else {
				scoreBean.setHighscoreflg(false);
			}
			
			scoreBean = scoreRepository.save(scoreBean);
		}
		scoreForm.setCommittime(scoreBean.getId().getCommittime());
		scoreForm.setPoint(scoreBean.getPoint());
		
		return scoreForm;
	}

	/**
	 * Update score data
	 * 
	 * @param scoreForm Score data Form
	 * @return Registered score data
	 */
	public ScoreForm update(ScoreForm scoreForm) {

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

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant

			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			Score score = createScore(scoreBean);
			
			scoreStore.update(score.get_id(), score);
			
			// Switch highscoreflg
			Score highScore = scoreStore.findHighScore(scoreBean.getId().getUsername());
			if(highScore == null) {
				score.setHighscoreflg(true);
			} else if(highScore.getPoint() > score.getPoint()) {
				highScore.setHighscoreflg(false);
				scoreStore.update(highScore.get_id(), highScore);
				score.setHighscoreflg(true);
			} else {
				score.setHighscoreflg(false);
			}

			scoreStore.persist(score);

			
		} else {
			// case: h2 database
			
			List<ScoreBean>  highScoreBeanList = scoreRepository.findHighScore(scoreId.getUsername());

			// Switch highscoreflg
			if(highScoreBeanList.size() == 0) {
				scoreBean.setHighscoreflg(true);
			} else if(highScoreBeanList.get(0).getPoint() > scoreBean.getPoint()) {
				highScoreBeanList.get(0).setHighscoreflg(false);
				scoreRepository.save(highScoreBeanList.get(0));
				scoreBean.setHighscoreflg(true);
			} else {
				scoreBean.setHighscoreflg(false);
			}

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
		
		List<ScoreBean> beanList = null;

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			beanList = new ArrayList<ScoreBean>();
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
	        for (Score doc : scoreStore.getAll()) {
	        	ScoreBean scoreBean = createScoreBean(doc);
	        	
	        	beanList.add(scoreBean);
	        }
		} else {
			// case: h2 database
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
	 * Acquire all score data.
	 *  (sorted in order of late registration date)
	 * 
	 * @return All score data.
	 */
	public List<ScoreForm> findAllOrderByCommittime() {

		List<ScoreBean> beanList = null;

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			beanList = new ArrayList<ScoreBean>();
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
	        for (Score doc : scoreStore.getAllOrderByCommittime()) {
	        	
	        	ScoreBean scoreBean = createScoreBean(doc);
	        	
	        	beanList.add(scoreBean);
	        }
			
		} else {
			// case: h2 database
			beanList = scoreRepository.findAllByOrderById_CommittimeDesc();
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
	 * Acquire all score data (sort by score).
	 * 
	 * @return All score data.
	 */
	public List<ScoreForm> findAllOrderByPoint() {

		List<ScoreBean> beanList = null;

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			beanList = new ArrayList<ScoreBean>();
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
	        for (Score doc : scoreStore.getAllOrderByScore()) {
	        	
	        	ScoreBean scoreBean = createScoreBean(doc);
	        	beanList.add(scoreBean);
	        }
		} else {
			// case: h2 database
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
			usernameOverlapCnt = scoreStore.findUsernameOverlapCnt(username);

		} else {
			// case: h2 database
			usernameOverlapCnt = scoreRepository.findUsernameOverlapCnt(username);
		}
			
		return usernameOverlapCnt;
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	public ScoreForm findHighScore(String username) {
		
		ScoreForm highScoreForm = new ScoreForm();
		
		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			Score score = scoreStore.findHighScore(username);
			highScoreForm.setUsername(score.getUsername());
			highScoreForm.setCommittime(score.getCommittime());
			highScoreForm.setInputtime(score.getInputtime());
			highScoreForm.setMisstype(score.getMisstype());
			highScoreForm.setPoint(score.getPoint());
			
		} else {
			// case: h2 database
			List<ScoreBean> scoreBeanList =  scoreRepository.findHighScore(username);
			if(scoreBeanList != null && scoreBeanList.size() > 0) {
				highScoreForm.setUsername(scoreBeanList.get(0).getId().getUsername());
				highScoreForm.setCommittime(scoreBeanList.get(0).getId().getCommittime());
				BeanUtils.copyProperties(scoreBeanList.get(0), highScoreForm);
			}
		}
			
		return highScoreForm;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<ScoreForm> findHighScoreListOrderByPoint() {
		
		List<ScoreBean> beanList = null;
		
		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			beanList = new ArrayList<ScoreBean>();
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			for (Score doc : scoreStore.findHighScoreListOrderByPoint()) {
				ScoreBean scoreBean = createScoreBean(doc);
				beanList.add(scoreBean);
			}
		} else {
			// case: h2 database
			beanList = scoreRepository.findHighScoreListOrderByPoint();
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
	
	/**
	 * 
	 * @param scoreBean
	 * @return
	 */
	private Score createScore(ScoreBean scoreBean) {
		
		Score score = new Score();
		score.set_id(scoreBean.getId().getUsername() + scoreBean.getId().getCommittime());
		score.setUsername(scoreBean.getId().getUsername());
		score.setCommittime(scoreBean.getId().getCommittime());
		score.setInputtime(scoreBean.getInputtime());
		score.setMisstype(scoreBean.getMisstype());
		score.setPoint(scoreBean.getPoint());

		return score;
	}
	
	/**
	 * 
	 * @param score
	 * @return
	 */
	private ScoreBean createScoreBean(Score score) {
		
    	ScoreBean scoreBean = new ScoreBean();
    	ScoreId scoreId = new ScoreId();
    	scoreId.setUsername(score.getUsername());
    	scoreId.setCommittime(score.getCommittime());
    	scoreBean.setId(scoreId);
    	scoreBean.setInputtime(score.getInputtime());
    	scoreBean.setMisstype(score.getMisstype());
    	scoreBean.setPoint(score.getPoint());
    	
		return scoreBean;
	}

}