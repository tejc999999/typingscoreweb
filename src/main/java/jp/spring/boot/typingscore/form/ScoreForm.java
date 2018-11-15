package jp.spring.boot.typingscore.form;

import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScoreForm {

	@NotNull
	private String username;
	@Min(0)
	@NotNull
    private int inputtime;
	@Min(0)
	@NotNull
    private int misstype;
	
	private int point;
	
	private Date committime;

}