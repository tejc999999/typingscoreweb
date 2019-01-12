package jp.spring.boot.typingscore.cloudant;

import lombok.Data;

/**
 * Cloudant Bean Base Class.
 * 
 * @author tejc999999
 *
 */
@Data
public class CloudantObject {

	/**
	 * id.
	 */
	private String _id;
	
	/**
	 * rev.
	 */
	private String _rev;
}
