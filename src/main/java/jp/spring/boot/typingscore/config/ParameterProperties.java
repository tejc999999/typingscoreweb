package jp.spring.boot.typingscore.config;

import lombok.Data;

/**
 * Hold application settings.
 * 
 * @author tejc999999
 *
 */
@Data
public class ParameterProperties {

	/**
	 * Login failure count threshold.
	 */
    private int loginAttemptsThreshold;
    
    /**
     * constructor.
     * 
     * @param loginAttemptsThreshold Login failure count threshold.
     */
    public ParameterProperties(int loginAttemptsThreshold) {
    	this.loginAttemptsThreshold = loginAttemptsThreshold;
    }
}
