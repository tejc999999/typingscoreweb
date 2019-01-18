package jp.spring.boot.typingscore.form;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * スコア用Form
 * 
 * @author tejc999999
 *
 */
@Data
@NoArgsConstructor
public class ScoreResultForm {

	/**
	 * ユーザ名
	 * 複合主キーの一部
	 */
	private String username;

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
	 * 計算方法 = 入力時間 + (ミスタイプ数×2)
	 */
	private int point;

	/**
	 * 登録日時
	 * 複合主キーの一部
	 */
	private Date committime;

	/**
	 * 挑戦回数
	 */
	private int tryCnt;

	/**
	 * ランキング順位
	 */
	private int rank;

	/**
	 * 最高ランキング順位
	 */
	private int maxRank;

	/**
	 * 最高ポイント
	 */
	private int maxPoint;
	
	/**
	 * 最高ポイント時入力ミス回数
	 */
	private int maxMisstype;
	
	/**
	 * 最高ポイント時入力時間
	 */
	private int maxInputtime;

	/**
	 * 合計ランキング者数
	 */
	private int rankNum;

}
