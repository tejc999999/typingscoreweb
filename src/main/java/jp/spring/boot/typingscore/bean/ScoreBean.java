package jp.spring.boot.typingscore.bean;

import java.sql.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import jp.spring.boot.typingscore.db.ScoreId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="scores")
public class ScoreBean {

	@EmbeddedId
    private ScoreId id;
    private int inputtime;
    private int misstype;
    private int point;
}