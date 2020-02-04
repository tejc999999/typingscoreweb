package jp.spring.boot.typingscore.config;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EmbeddedId;

import lombok.Data;

/**
 * プロパティファイル設定値
 * 
 *
 */
@Data
public class ParameterProperties {

	/**
	 * ログイン失敗閾値
	 */
    private int loginAttemptsThreshold;
    
	/**
	 * key   :  タイピングゲーム区分コード
	 * value : タイピングゲーム区分名
	 */
	private HashMap<String,String> gameType;
	
	
	/**
	 * 現在選択しているゲーム区分
	 * 
	 */
	private String activeGameCode;
    
    /**
     * コンストラクタ
     * 
     * @param ログイン失敗閾値
     */
//    public ParameterProperties(int loginAttemptsThreshold, List<String> gameTypeList, String activeGameCode) {
//    	
//		this.gameType = new HashMap<String, String>();
//    	for(String keyandvalue : gameTypeList) {
//    		this.gameType.put(keyandvalue.split(",")[0], keyandvalue.split(",")[1]);
//    		System.out.println(keyandvalue.split(",")[0] + " : " + keyandvalue.split(",")[1]);
//    	}
//    	this.loginAttemptsThreshold = loginAttemptsThreshold;
//    	this.activeGameCode = activeGameCode.split(",")[0];
//    }
    
    /**
     * コンストラクタ
     * 
     * @param ログイン失敗閾値
     */
    public ParameterProperties(int loginAttemptsThreshold, HashMap<String,String> gameType, String activeGameCode) {
    	
		this.gameType = gameType;
    	this.loginAttemptsThreshold = loginAttemptsThreshold;
    	this.activeGameCode = activeGameCode;
    }
}

