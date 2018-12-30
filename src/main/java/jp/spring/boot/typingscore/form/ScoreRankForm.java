package jp.spring.boot.typingscore.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Score rank form.
 * Score form add rank.
 * 
 * @author tejc999999
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class ScoreRankForm extends ScoreForm {

	/**
	 * rank.
	 */
	private int rank;

	/**
	 * Store an icon display html.
	 */
	private String blank;
}
