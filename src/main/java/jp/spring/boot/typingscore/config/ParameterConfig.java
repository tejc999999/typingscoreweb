package jp.spring.boot.typingscore.config;

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
	 * プロパティ（ログイン失敗閾値）を取得する
	 * 
	 * @return プロパティ
	 */
	@Bean
	  public ParameterProperties parameterProperties() {

		    return new ParameterProperties(loginAttemptsThreshold);
	  }
}
