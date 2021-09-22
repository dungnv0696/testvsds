package com.lifesup.gbtd.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "CAT_UNIT")
@Getter
@Setter
public class CatUnitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CAT_UNIT_SEQ")
    @SequenceGenerator(name = "CAT_UNIT_SEQ", sequenceName = "CAT_UNIT_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;
    @Column(name = "CODE")
    private String code;
    @Column(name = "NAME")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "STATUS")
    private Long status;
    @Column(name = "UPDATE_TIME")
    private Date updateTime;
    @Column(name = "UPDATE_USER")
    private String updateUser;
    @Column(name = "NAME_DISPLAY")
    private String nameDisplay;
    @Column(name = "TYPE_UNIT")
    private Long typeUnit;
}
