package jp.spring.boot.typingscore.config;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 設定ファイル読み込み
 * (ConfigurationProperties アノテーションはうまく動作しなかったので今回は使用しない）
 * 
 * @author tejc999999
 *
 */
@Configuration
@PropertySource(value = {"classpath:parameter.properties"})
//@ConfigurationProperties(prefix = "spring.security.login")
public class ParameterConfig {

	/**
	 *ログイン失敗閾値
	 */
	@Value("${spring.security.login.loginAttemptsThreshold}")
    private int loginAttemptsThreshold;
    
	/**
	 * ゲーム区分,ゲーム区分名
	 */
	@Value("${typing.score.game.gameCode}")
	private String gameCode;
	
	/**
	 * 現在操作中のゲーム区分
	 */
	@Value("${typing.score.game.activeGameCode}")
	private String activeGameCode;
	
	
	/**
	 * プロパティ（ログイン失敗閾値）を取得する
	 * 
	 * @return プロパティ
	 */
	@Bean
	  public ParameterProperties parameterProperties() {
			HashMap<String, String> gameCodeMap = new HashMap<String, String>();
			for(String keyandvalue : gameCode.split(",")) {
				gameCodeMap.put(keyandvalue.split(":")[0], keyandvalue.split(":")[1]);
			}
		    return new ParameterProperties(loginAttemptsThreshold, gameCodeMap, activeGameCode);
	  }
	
}
