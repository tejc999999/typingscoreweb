package jp.spring.boot.typingscore.form;

import java.sql.Timestamp;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * スコア用Form.
 * 
 * @author tejc999999
 *
 */
@JsonPropertyOrder({"userName", "inputTime", "missTypeCnt", "point", "commitTime", "highscoreflg"})
@Data
@NoArgsConstructor
public class ScoreForm {

	/**
	 * ユーザ名
	 * 複合主キーの一部
	 */
	@JsonProperty("userName")
	@NotNull
	@NotBlank(message = "required input.")
	private String username;
	
	/**
	 * 入力時間（分）
	 */
	@JsonProperty("inputTimeMin")
	@Min(value = 0, message = "can not enter {value} or less.")
	private int inputtimeMin;
	
	/**
	 * 入力時間（秒）
	 */
	@JsonProperty("inputTimeSec")
	@Min(value = 0, message = "can not enter {value} or less.")
	private int inputtimeSec;
	
	/**
	 * ミスタイプ数
	 */
	@JsonProperty("missTypeCnt")
	@Min(value = 0, message = "can not enter {value} or less.")
	@NotNull
	private int misstype;
	
	/**
	 * スコア
	 * 計算方法 = 入力時間 + (ミスタイプ数 * 2)
	 */
	@JsonProperty("point")
	private int point;
	
	/**
	 * 登録日時
	 * 複合主キーの一部
	 */
	@JsonProperty("commitTime")
	private Timestamp committime;
	
}