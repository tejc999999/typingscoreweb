package jp.spring.boot.typingscore.form;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScoreRankForm extends ScoreForm {

	private int rank;
	
	private String blank;
}
