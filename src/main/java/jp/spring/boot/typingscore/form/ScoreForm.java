package jp.spring.boot.typingscore.form;

import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({"ユーザ名", "入力時間", "ミスタイプ数", "スコア", "登録日時"})
@Data
@NoArgsConstructor
public class ScoreForm {

	@JsonProperty("ユーザ名")
	@NotNull
	@NotBlank(message = "必須入力です。")
	private String username;
	@JsonProperty("入力時間")
	@Min(value = 0, message = "{value} 以下は入力できません。")
	@NotNull
	private int inputtime;
	@JsonProperty("ミスタイプ数")
	@Min(value = 0, message = "{value} 以下は入力できません。")
	@NotNull
	private int misstype;
	@JsonProperty("スコア")
	private int point;
	@JsonProperty("登録日時")
	private Date committime;

}