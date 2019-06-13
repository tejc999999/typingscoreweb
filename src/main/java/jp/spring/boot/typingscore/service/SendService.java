package jp.spring.boot.typingscore.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.spring.boot.typingscore.bean.ScoreBean;
import jp.spring.boot.typingscore.cloudant.store.VCAPHelper;
import jp.spring.boot.typingscore.config.ParameterProperties;
import jp.spring.boot.typingscore.form.SendForm;
import jp.spring.boot.typingscore.repository.ScoreRepository;

/**
 * 
 * @author 160120
 *
 */
@Service
public class SendService {
	
	@Autowired
	ScoreRepository scoreRepository;
	
	@Autowired
	ParameterProperties parameterPropaties;
	
	/**
	 * 総合1位取得
	 * @return
	 */
	public List<SendForm> getTotalRankingList(){
		Map<String, SendForm> sendMap = new LinkedHashMap<String, SendForm>();
		
		if(VCAPHelper.VCAP_SERVICES  != null) {
			// DBがIBM Cloudの場合


		} else {
			List<ScoreBean> beanlist = scoreRepository.findAllByOrderByPoint();
			beanlist.removeIf(score -> score.getPoint() == 0);
			
			//総合1位取得
			int beforePoint = beanlist.get(0).getPoint();
			for (ScoreBean scoreBean : beanlist) {
				if(beforePoint == scoreBean.getPoint()) {
					SendForm sendForm = new SendForm();
					if(sendMap.containsKey(scoreBean.getId().getUsername())) {
						if(sendMap.get(scoreBean.getId().getUsername()).getPoint() > scoreBean.getPoint()) {
							sendForm.setUsername(scoreBean.getId().getUsername());
							sendForm.setDepartment(scoreBean.getUsernamedepartment());
							sendForm.setUsernamename(scoreBean.getUsernamename());
							sendForm.setGamecode("TOTAL");
							sendForm.setRank(1);
							sendForm.setPoint(scoreBean.getPoint());
							sendForm.setCommittime(scoreBean.getId().getCommittime());
							sendMap.put(scoreBean.getId().getUsername(), sendForm);
						}
					} else {
						sendForm.setUsername(scoreBean.getId().getUsername());
						sendForm.setDepartment(scoreBean.getUsernamedepartment());
						sendForm.setUsernamename(scoreBean.getUsernamename());
						sendForm.setGamecode("TOTAL");
						sendForm.setRank(1);
						sendForm.setPoint(scoreBean.getPoint());
						sendForm.setCommittime(scoreBean.getId().getCommittime());
						sendMap.put(scoreBean.getId().getUsername(), sendForm);
					}
					beforePoint = scoreBean.getPoint();
				}
			}
		}
		
		return new ArrayList<SendForm>(sendMap.values());
	}
	
	/**
	 * 日本語部門１～５位取得
	 * @return
	 */
	public List<SendForm> getJapaneseRankingList(){
		Map<String, SendForm> sendMap = new LinkedHashMap<String, SendForm>();
		
		if(VCAPHelper.VCAP_SERVICES  != null) {
			// DBがIBM Cloudの場合


		} else {
			List<ScoreBean> beanlist = scoreRepository.findAllByOrderByPoint();
			beanlist.removeIf(score -> score.getPoint() == 0);
			
			for(SendForm sendForm : getTotalRankingList()) {
				beanlist.removeIf(score -> score.getId().getUsername().equals(sendForm.getUsername()));
			}
			
			//日本語部門１～５位
			int rankNum = 0;
			int overlapRankNum = 0;
			int beforePoint = -1;
			for (ScoreBean scoreBean : beanlist) {
				if(scoreBean.getGamecode().equals("JA")) {
					if(beforePoint == scoreBean.getPoint()) {
						// スコアが直前と同じ場合、同順処理のため一時的なカウンタに記録する
						overlapRankNum++;
					} else {
						// 順位を記録する
						beforePoint = scoreBean.getPoint();
						rankNum = rankNum + overlapRankNum + 1;
						overlapRankNum = 0;
					}
					
					if(rankNum > 5) {
						break;
					}
					SendForm sendForm = new SendForm();
					if(sendMap.containsKey(scoreBean.getId().getUsername())) {
						if(sendMap.get(scoreBean.getId().getUsername()).getPoint() > scoreBean.getPoint()) {
							sendForm.setUsername(scoreBean.getId().getUsername());
							sendForm.setDepartment(scoreBean.getUsernamedepartment());
							sendForm.setUsernamename(scoreBean.getUsernamename());
							sendForm.setGamecode(scoreBean.getGamecode());
							sendForm.setRank(rankNum);
							sendForm.setPoint(scoreBean.getPoint());
							sendForm.setCommittime(scoreBean.getId().getCommittime());
							sendMap.put(scoreBean.getId().getUsername(), sendForm);
						}
					} else {
						sendForm.setUsername(scoreBean.getId().getUsername());
						sendForm.setDepartment(scoreBean.getUsernamedepartment());
						sendForm.setUsernamename(scoreBean.getUsernamename());
						sendForm.setGamecode(scoreBean.getGamecode());
						sendForm.setRank(rankNum);
						sendForm.setPoint(scoreBean.getPoint());
						sendForm.setCommittime(scoreBean.getId().getCommittime());
						sendMap.put(scoreBean.getId().getUsername(), sendForm);
					}
					
				}
			}
		}
		
		return new ArrayList<SendForm>(sendMap.values());
	}
	
	/**
	 * 英語部門１～５位取得
	 * @return
	 */
	public List<SendForm> getEnglishRankingList(){
		Map<String, SendForm> sendMap = new LinkedHashMap<String, SendForm>();
		
		if(VCAPHelper.VCAP_SERVICES  != null) {
			// DBがIBM Cloudの場合


		} else {
			List<ScoreBean> beanlist = scoreRepository.findAllByOrderByPoint();
			beanlist.removeIf(score -> score.getPoint() == 0);
			
			for(SendForm sendForm : getTotalRankingList()) {
				beanlist.removeIf(score -> score.getId().getUsername().equals(sendForm.getUsername()));
			}
			
			for(SendForm sendForm : getJapaneseRankingList()) {
				beanlist.removeIf(score -> score.getId().getUsername().equals(sendForm.getUsername()));
			}
			
			
			//日本語部門１～５位
			int rankNum = 0;
			int overlapRankNum = 0;
			int beforePoint = -1;
			for (ScoreBean scoreBean : beanlist) {
				if(scoreBean.getGamecode().equals("EN")) {
					if(beforePoint == scoreBean.getPoint()) {
						// スコアが直前と同じ場合、同順処理のため一時的なカウンタに記録する
						overlapRankNum++;
					} else {
						// 順位を記録する
						beforePoint = scoreBean.getPoint();
						rankNum = rankNum + overlapRankNum + 1;
						overlapRankNum = 0;
					}
					
					if(rankNum > 5) {
						break;
					}
					SendForm sendForm = new SendForm();
					if(sendMap.containsKey(scoreBean.getId().getUsername())) {
						if(sendMap.get(scoreBean.getId().getUsername()).getPoint() > scoreBean.getPoint()) {
							sendForm.setUsername(scoreBean.getId().getUsername());
							sendForm.setDepartment(scoreBean.getUsernamedepartment());
							sendForm.setUsernamename(scoreBean.getUsernamename());
							sendForm.setGamecode(scoreBean.getGamecode());
							sendForm.setRank(rankNum);
							sendForm.setPoint(scoreBean.getPoint());
							sendForm.setCommittime(scoreBean.getId().getCommittime());
							sendMap.put(scoreBean.getId().getUsername(), sendForm);
						}
					} else {
						sendForm.setUsername(scoreBean.getId().getUsername());
						sendForm.setDepartment(scoreBean.getUsernamedepartment());
						sendForm.setUsernamename(scoreBean.getUsernamename());
						sendForm.setGamecode(scoreBean.getGamecode());
						sendForm.setRank(rankNum);
						sendForm.setPoint(scoreBean.getPoint());
						sendForm.setCommittime(scoreBean.getId().getCommittime());
						sendMap.put(scoreBean.getId().getUsername(), sendForm);
					}
					
				}
			}
		}
		
		return new ArrayList<SendForm>(sendMap.values());
	}
	
	/**
	 * ScoreBeanのランキングJson形式のListに変換して返す
	 * @param beanmap
	 * @return
	 */
	public List<String> toJsonFromSendForm(List<SendForm>sendFormList) {
		List<String> jsonlist = new ArrayList<String>();
		for (SendForm sendForm : sendFormList) {

			jsonlist.add( "{\"dept\": \"" + sendForm.getGamecode() + "\", \"rank\": \"" + sendForm.getRank() + "\", \"score\": \"" + sendForm.getPoint() + "\", \"course\": \""
					+ sendForm.getDepartment() + "\", \"name\": \"" + sendForm.getUsernamename() + "\"}");
		}
		return jsonlist;
	}

}
