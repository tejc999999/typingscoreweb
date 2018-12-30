package jp.spring.boot.typingscore.cloudant;

import java.sql.Timestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * For IBM Cloudant.
 * Typing Score Bean.
 * 
 * @author tejc999999
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class Score extends CloudantObject {

	/**
	 * username.
	 * id = username + commit time.
	 */
	private String username = null;
	
	/**
	 * commit time.
	 * id = username + commit time.
	 */
	private Timestamp committime = null;

	/**
	 * input time(second).
	 */
	private Integer inputtime = null;
	
	/**
	 * misstype count;
	 */
	private Integer misstype = null;
	
	/**
	 * typing score.
	 * point = inputtime + (misstype * 2)
	 */
	private Integer point = null;
	
	/**
	 * Constructor.
	 */
    public Score() {
        this.username = "";
    }

}
