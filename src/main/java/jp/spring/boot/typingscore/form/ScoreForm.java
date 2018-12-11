package jp.spring.boot.typingscore.form;

import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScoreForm {

	@NotNull
	@NotBlank(message = "必須入力です。")
	private String username;
	@Min(value = 0, message = "{value} 以下は入力できません。")
	@NotNull
	private int inputtime;
	@Min(value = 0, message = "{value} 以下は入力できません。")
	@NotNull
	private int misstype;

	private int point;

	private Date committime;

}