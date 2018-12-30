package jp.spring.boot.typingscore.bean;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jp.spring.boot.typingscore.db.ScoreId;

/**
 * Typing Score Bean.
 * 
 * @author tejc999999
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scores")
public class ScoreBean {

	/**
	 * Compound key.
	 * username and committime(Timestamp).
	 * 
	 */
	@EmbeddedId
	private ScoreId id;
	
	/**
	 * input time(second).
	 */
	private int inputtime;
	
	/**
	 * misstype count;
	 */
	private int misstype;
	
	/**
	 * typing score.
	 * point = inputtime + (misstype * 2)
	 */
	private int point;
}