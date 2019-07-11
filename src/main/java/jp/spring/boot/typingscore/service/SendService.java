package jp.spring.boot.typingscore.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

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
	
	//総合ランキング取得済み判定
	Boolean isCallFromTotalRank;
	
	/**
	 * 総合1位取得
	 * @return
	 */
	public List<SendForm> getTotalRankingList(){
		List<SendForm> sendlist = new ArrayList<SendForm>();
		
		if(VCAPHelper.VCAP_SERVICES  != null && !VCAPHelper.VCAP_SERVICES.equals("{}")) {
			// DBがIBM Cloudの場合


		} else {
			isCallFromTotalRank = true;
			List<SendForm> totallist = this.getJapaneseRankingList(-1); 
			List<SendForm> englishlist = this.getEnglishRankingList(-1);
			isCallFromTotalRank = false;
			
			int min = -1;
			//日本語ランキングに英語のポイントを足していく
			for(SendForm sendForm : totallist) {
				for(SendForm ensendForm : englishlist) {
					if(sendForm.getUsername().equals(ensendForm.getUsername())) {
						sendForm.setGamecode("TOTAL");
						sendForm.setRank(1);
						sendForm.setJppoint(sendForm.getPoint());
						sendForm.setEnpoint(ensendForm.getPoint());
						sendForm.setPoint(sendForm.getPoint() + ensendForm.getPoint());
						sendForm.setEnusername(ensendForm.getUsername());
						sendForm.setEncommittime(ensendForm.getCommittime());
						if(min >= sendForm.getPoint() || min == -1) {
							//先頭に１位のデータが来るように追加
							sendlist.add(0,sendForm);
							min = sendForm.getPoint();
						}
					}
				}
			}
			
			//スコアが最小ではない要素の先頭の要素番号取得
			int cnt = 0;
			for(SendForm sendForm : sendlist) {
				if(sendForm.getPoint() == min) {
					cnt++;
				}
				else {
					break;
				}
			}
			sendlist.subList(cnt, sendlist.size()).clear();
		}
		
		return sendlist;
	}
	
	/**
	 * 日本語部門1～rank位まで取得
	 * @param rank 何位まで取得するか 　-1で全順位
	 * @return
	 */
	public List<SendForm> getJapaneseRankingList(int rank){
		Map<String, SendForm> sendMap = new LinkedHashMap<String, SendForm>();
		
		if(VCAPHelper.VCAP_SERVICES  != null && !VCAPHelper.VCAP_SERVICES.equals("{}")) {
			// DBがIBM Cloudの場合


		} else {
			List<ScoreBean> beanlist = scoreRepository.findAllByOrderByPoint();
			beanlist.removeIf(score -> score.getPoint() == 0);
			
			if(!isCallFromTotalRank) {
				for(SendForm sendForm : getTotalRankingList()) {
					beanlist.removeIf(score -> score.getId().getUsername().equals(sendForm.getUsername()));
				}	
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
					
					if(rankNum > rank && rank != -1) {
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
	 * 英語部門1～rank位まで取得
	 * 	@param rank 何位まで取得するか 　-1で全順位
	 * @return
	 */
	public List<SendForm> getEnglishRankingList(int rank){
		Map<String, SendForm> sendMap = new LinkedHashMap<String, SendForm>();
		
		if(VCAPHelper.VCAP_SERVICES  != null && !VCAPHelper.VCAP_SERVICES.equals("{}")) {
			// DBがIBM Cloudの場合


		} else {
			List<ScoreBean> beanlist = scoreRepository.findAllByOrderByPoint();
			beanlist.removeIf(score -> score.getPoint() == 0);
			
			if(!isCallFromTotalRank) {
				for(SendForm sendForm : getTotalRankingList()) {
					beanlist.removeIf(score -> score.getId().getUsername().equals(sendForm.getUsername()));
				}
				
				for(SendForm sendForm : getJapaneseRankingList(5)) {
					beanlist.removeIf(score -> score.getId().getUsername().equals(sendForm.getUsername()));
				}
				
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
					
					if(rankNum > 5 && rank != -1) {
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
	 * SendFormのランキングをJson形式に変換
	 * @param beanmap
	 * @return
	 */
	public String toJsonFromSendForm(List<SendForm>sendFormList) {
		String json = "[";
		for (SendForm sendForm : sendFormList) {

				 json += "{\"dept\": \"" + sendForm.getGamecode()
							+ "\", \"rank\": " + sendForm.getRank()
							+ ", \"score\": " + sendForm.getPoint() 
							+ ", \"course\": \"" + sendForm.getDepartment()
							+ "\", \"name\": \"" + sendForm.getUsernamename()
							+ "\"},";
		}
		json = json.substring(0,json.length() - 1);
		json += "]";
		return json;
	}
	
	/***
	 * 
	 * @param sendFormList
	 * @return
	 */
	public String  postRanking(List<SendForm> sendFormList) {
		HttpsURLConnection httpcon = null;
		int statusCode = -1;
		String result;
		try {
			
			URL url = new URL("https://tcc-typingranking.herokuapp.com/score");
//			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("172.17.0.2", 80));
//            httpcon = (HttpsURLConnection) url.openConnection(proxy);
			httpcon = (HttpsURLConnection) url.openConnection();
			httpcon.setRequestMethod("POST");
			httpcon.setRequestProperty("Content-Type", "application/json");
			httpcon.setDoOutput(true);
			httpcon.setDoInput(true);
			httpcon.connect();
			
			//リクエストボディの書き出しを行う。
			 OutputStreamWriter out = new OutputStreamWriter(
					 new BufferedOutputStream(httpcon.getOutputStream()));
            out.write(toJsonFromSendForm(sendFormList));
            out.close();
            
            //レスポンス表示
            statusCode = httpcon.getResponseCode();
            if(statusCode == 200) {
                result = "送信しました。";
            }
            else {
            	result = "送信失敗しました。　HTTP STATUS : " + statusCode;
            }
			
		}catch(Exception e) {
			e.printStackTrace();
			result = "送信失敗しました。　HTTP STATUS : " + statusCode;
		}finally {
            if (httpcon != null) {
                //7.コネクションを閉じる。
            	httpcon.disconnect();
            }
		}
		
		return result;
	}
	
	/**
	 * 同順位の存在判定　あり：true
	 * @param sendFormlist
	 * @return
	 */
	public Boolean checkedRankingTie(List<SendForm> sendFormlist) {
		Boolean isChecked = false;
		for(int i=0; i<sendFormlist.size(); i++) {
			if(sendFormlist.get(i).getRank() != i+1) {
				isChecked = true;
			}
		}
		
		return isChecked;
	}
}
