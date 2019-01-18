package jp.spring.boot.typingscore.cloudant;

import lombok.Data;

/**
 * Bean（IBM Cloudant用）の基底クラス
 * 
 * @author tejc999999
 *
 */
@Data
public class CloudantObject {

	/**
	 * 識別情報
	 */
	private String _id;
	
	/**
	 * 更新情報
	 */
	private String _rev;
}
