package jp.spring.boot.typingscore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Config setting loading config
 * 
 * used @ConfigurationProperties but did not work
 * 
 * @author tejc999999
 *
 */
@Configuration
@PropertySource(value = {"classpath:parameter.properties"})
//@ConfigurationProperties(prefix = "spring.security.login")
public class ParameterConfig {

	/**
	 * Login failure count threshold.
	 */
	@Value("${spring.security.login.loginAttemptsThreshold}")
    private int loginAttemptsThreshold;
    
	/**
	 * Get application settings
	 * 
	 * @return Application setting value
	 */
	@Bean
	  public ParameterProperties parameterProperties() {
			System.out.println("DEBUG-BB:" + loginAttemptsThreshold);
		    return new ParameterProperties(loginAttemptsThreshold);
	  }
}
