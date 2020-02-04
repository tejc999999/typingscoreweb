package jp.spring.boot.typingscore.db;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * スコア用複合主キー
 * 
 * @author tejc999999
 *
 */
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreId implements Serializable {
	
	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = 6790969058470310683L;

	/**
	 * ユーザ名
	 */
	//@Embedded
	private String username;
	
	/**
	 * 登録日時
	 */
	private Timestamp committime;
	
}