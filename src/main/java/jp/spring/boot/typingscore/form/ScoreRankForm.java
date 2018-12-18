package jp.spring.boot.typingscore.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class ScoreRankForm extends ScoreForm {

	private int rank;

	private String blank;
}
