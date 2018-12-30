package jp.spring.boot.typingscore.form;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author tejc999999
 *
 */
@Data
@NoArgsConstructor
public class ScoreResultForm {

	/**
	 * user name.
	 * Part of the primary key.
	 */
	private String username;

	/**
	 * Input time
	 * Unit is seconds.
	 */
	private int inputtime;

	/**
	 * miss type.
	 * Number of times.
	 */
	private int misstype;

	/**
	 * score.
	 * calculation = inputtime + (misstype * 2)
	 */
	private int point;

	/**
	 * commit time.
	 * Part of the primary key.
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
