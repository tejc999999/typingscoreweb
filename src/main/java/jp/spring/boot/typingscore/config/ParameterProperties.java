package jp.spring.boot.typingscore.config;

import lombok.Data;

/**
 * プロパティファイル設定値
 * 
 * @author tejc999999
 *
 */
@Data
public class ParameterProperties {

	/**
	 * ログイン失敗閾値
	 */
    private int loginAttemptsThreshold;
    
    /**
     * コンストラクタ
     * 
     * @param ログイン失敗閾値
     */
    public ParameterProperties(int loginAttemptsThreshold) {
    	this.loginAttemptsThreshold = loginAttemptsThreshold;
    }
}
