package jp.spring.boot.typingscore.cloudant;

import java.sql.Timestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * スコア用Bean（IBM Cloudant用）
 * 
 * @author tejc999999
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class Score extends CloudantObject {

	/**
	 * ユーザ名
	 * 複合主キーの一部
	 */
	private String username = null;
	
	   /**
     * ユーザー名から分割した学科名
     */
    private String usernamedepartment = null;
    
    /**
     * ユーザー名から分割した名前
     */
    private String usernamesplit = null;
	
	/**
	 * 登録日時
	 * 複合主キーの一部
	 */
	private Timestamp committime = null;

	/**
	 * 入力時間(second).
	 */
	private Integer inputtime = null;
	
	/**
	 * ミスタイプ数
	 */
	private Integer misstype = null;
	
	/**
	 * スコア
	 * 計算方法 = 入力時間 + (ミスタイプ数×2)
	 */
	private Integer point = null;
	
	   /**
     * ゲーム区分
     */
    private String  gamecode = null;
    
	/**
	 * コンストラクタ
	 */
    public Score() {
        this.username = "";
    }

}
