package jp.spring.boot.typingscore.form;

import java.sql.Timestamp;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({"userName", "inputTime", "missTypeCnt", "point", "commitTime"})
@Data
@NoArgsConstructor
public class ScoreForm {

	@JsonProperty("userName")
	@NotNull
	@NotBlank(message = "必須入力です。")
	private String username;
	@JsonProperty("inputTime")
	@Min(value = 0, message = "{value} 以下は入力できません。")
	@NotNull
	private int inputtime;
	@JsonProperty("missTypeCnt")
	@Min(value = 0, message = "{value} 以下は入力できません。")
	@NotNull
	private int misstype;
	@JsonProperty("point")
	private int point;
	@JsonProperty("commitTime")
	private Timestamp committime;

}