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
		Timestamp dateSecond = new Timestamp(DateUtils.truncate(new Date(), Calendar.SECOND).getTime());
		scoreId.setCommittime(dateSecond);
		
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
			Score score = new Score();
			score.set_id(scoreBean.getId().getUsername() + scoreBean.getId().getCommittime());
			score.setUsername(scoreBean.getId().getUsername());
			score.setCommittime(scoreBean.getId().getCommittime());
			score.setInputtime(scoreBean.getInputtime());
			score.setMisstype(scoreBean.getMisstype());
			score.setPoint(scoreBean.getPoint());
			scoreStore.persist(score);
		} else {
			// case: h2 database
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
			Score score = new Score();
			score.set_id(scoreBean.getId().getUsername() + scoreBean.getId().getCommittime());
			score.setUsername(scoreBean.getId().getUsername());
			score.setCommittime(scoreBean.getId().getCommittime());
			score.setInputtime(scoreBean.getInputtime());
			score.setMisstype(scoreBean.getMisstype());
			score.setPoint(scoreBean.getPoint());
			scoreStore.update(score.get_id(), score);
		} else {
			// case: h2 database
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
	 * User name No duplication score data acquisition.
	 * 
	 * @return User name No duplication score data.
	 */
	public boolean findUsernameOverlap(String username) {

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			ScoreStore scoreStore = ScoreStoreFactory.getInstance();
			for (String inUsername : scoreStore.findUsernameOverlap()) {
				if (inUsername.equals(username)) {
					return true;
				}
			}
		} else {
			// case: h2 database
			for (String inUsername : scoreRepository.findUsernameOverlap()) {
				if (inUsername.equals(username)) {
					return true;
				}
			}
		}

		return false;
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