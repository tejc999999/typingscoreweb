package jp.spring.boot.typingscore.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

/**
 * スコアランク用Form
 * スコア用Formにランク情報とアイコン格納用フィールドを付与
 * 
 * @author tejc999999
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class ScoreRankForm extends ScoreForm {

	/**
	 * 順位
	 */
	private int rank;

	/**
	 * アイコン描画用HTML
	 */
	private String blank;

}
