package jp.spring.boot.typingscore.form;

import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScoreResultForm {

	private String username;

	private int inputtime;

    private int misstype;
	
	private int point;
	
	private Date committime;
	
	/**
	 * 挑戦回数
	 */
	private int tryCnt;
	
	/**
	 * ランキング順位
	 */
	private int rank;
	
	/**
	 * 最大ランキング順位
	 */
	private int maxRank;

	/**
	 * 合計ランキング者数
	 */
	private int rankNum;
	
	/**
	 * 最高ポイント
	 */
	private int maxPoint;
}
