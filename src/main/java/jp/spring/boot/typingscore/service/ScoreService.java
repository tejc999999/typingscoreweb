package jp.spring.boot.typingscore.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.spring.boot.typingscore.bean.ScoreBean;
import jp.spring.boot.typingscore.db.ScoreId;
import jp.spring.boot.typingscore.form.ScoreForm;
import jp.spring.boot.typingscore.repository.ScoreRepository;

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
		// 登録時間：現在の日時情報を登録(ミリ秒を無効化するため、フォーマット指定）
		Date now = new Date(); // 現在時刻
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date convCommittime = null;
		try {
			convCommittime = format.parse(format.format(now));
		} catch (ParseException e) {
			convCommittime = new Date();
			e.printStackTrace();
		}
		scoreId.setCommittime(convCommittime);
		ScoreBean scoreBean = new ScoreBean();
		// 複合主キーを登録
		scoreBean.setId(scoreId);
		// 入力時間
		scoreBean.setInputtime(scoreForm.getInputtime());
		// ミスタイプ数を登録
		scoreBean.setMisstype(scoreForm.getMisstype());
		// 点数：入力時間＋（ミスタイプ数×２）
		scoreBean.setPoint(scoreForm.getInputtime() + (scoreForm.getMisstype() * 2));

		scoreBean = scoreRepository.save(scoreBean);
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

		scoreRepository.save(scoreBean);

		return scoreForm;
	}

	/**
	 * スコアデータ取得
	 * 
	 * @return スコアデータ
	 */
	public ScoreForm findById(ScoreId id) {

		ScoreForm form = new ScoreForm();

		Optional<ScoreBean> opt = scoreRepository.findById(id);
		opt.ifPresent(scoreBean -> {
			form.setUsername(scoreBean.getId().getUsername());
			form.setCommittime(scoreBean.getId().getCommittime());
			BeanUtils.copyProperties(scoreBean, form);
		});

		return form;
	}

	/**
	 * スコアデータ全件取得
	 * 
	 * @return スコアデータ全件
	 */
	public List<ScoreForm> findAll() {
		List<ScoreBean> beanList = scoreRepository.findAll();
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

		List<ScoreBean> beanList = scoreRepository.findAllByOrderById_CommittimeDesc();
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

		List<ScoreBean> beanList = scoreRepository.findAllByOrderByPoint();
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

		List<String> beanList = scoreRepository.findUsernameOverlap();
		for (String inUsername : beanList) {
			if (inUsername.equals(username)) {
				return true;
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
		scoreRepository.delete(scoreBean);
	}
	
	/**
	 * スコアデータ全件削除
	 * 
	 */
	public void deleteAll() {
		
		scoreRepository.deleteAll();
	}
	
}