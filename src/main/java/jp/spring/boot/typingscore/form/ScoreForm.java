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
 * Score Form.
 * 
 * @author tejc999999
 *
 */
@JsonPropertyOrder({"userName", "inputTime", "missTypeCnt", "point", "commitTime"})
@Data
@NoArgsConstructor
public class ScoreForm {

	/**
	 * User name.
	 * Part of the primary key.
	 */
	@JsonProperty("userName")
	@NotNull
	@NotBlank(message = "required input.")
	private String username;
	
	/**
	 * Input time
	 * Unit is seconds.
	 */
	@JsonProperty("inputTime")
	@Min(value = 0, message = "can not enter {value} or less.")
	@NotNull
	private int inputtime;
	
	/**
	 * miss type.
	 * Number of times.
	 */
	@JsonProperty("missTypeCnt")
	@Min(value = 0, message = "can not enter {value} or less.")
	@NotNull
	private int misstype;
	
	/**
	 * score.
	 * calculation = inputtime + (misstype * 2)
	 */
	@JsonProperty("point")
	private int point;
	
	/**
	 * commit time.
 	 * Part of the primary key.
	 */
	@JsonProperty("commitTime")
	private Timestamp committime;
}