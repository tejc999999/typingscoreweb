package jp.spring.boot.typingscore.bean;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jp.spring.boot.typingscore.db.ScoreId;

/**
 * スコア用Bean
 * 
 * @author tejc999999
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scores")
public class ScoreBean {

	/**
	 * 複合主キー
	 * ユーザ名と登録日時(Timestamp).
	 * 
	 */
	@EmbeddedId
	private ScoreId id;
	
	/**
	 * ユーザー名から分割した学科名
	 */
	private String usernamedepartment;
	
	/**
	 * ユーザー名から分割した名前
	 */
	private String usernamesplit;
	
	/**
	 * 入力時間（秒）
	 */
	private int inputtime;
	
	/**
	 * ミスタイプ数
	 */
	private int misstype;

	/**
	 * スコア
	 * 計算方法 = 入力時間 + (ミスタイプ数 * 2)
	 */
	private int point;
	
	/**
	 * ゲーム区分
	 */
	private String  gamecode;
	
}