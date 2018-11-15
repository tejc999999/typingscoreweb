package jp.spring.boot.typingscore.db;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Embeddable
@Data
public class ScoreId implements Serializable {
    @Embedded
    private String username;
    @Temporal(TemporalType.DATE)
    private Date committime;
}