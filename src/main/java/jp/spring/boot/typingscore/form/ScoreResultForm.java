package jp.spring.boot.typingscore.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * スコア用Form
 * 
 * @author tejc999999
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class ScoreResultForm extends ScoreForm {

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
	 * 最高ポイント時入力時間（表示用）
	 */
	private String maxInputtimeView;

	/**
	 * 合計ランキング者数
	 */
	private int rankNum;

}
