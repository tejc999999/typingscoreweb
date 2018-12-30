package jp.spring.boot.typingscore.db;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import lombok.Data;

/**
 * Composite primary key for score
 * 
 * @author tejc999999
 *
 */
@Embeddable
@Data
public class ScoreId implements Serializable {
	
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 6790969058470310683L;

	/**
	 * user name.
	 */
	@Embedded
	private String username;
	
	/**
	 * commit time.
	 */
//	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp committime;
	
}