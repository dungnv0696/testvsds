package com.lifesup.gbtd.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "BI_TD_SERVICES_TREE")
@Getter
@Setter
public class BiTdServicesTreeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BI_TD_SERVICES_TREE_SEQ")
    @SequenceGenerator(name = "BI_TD_SERVICES_TREE_SEQ", sequenceName = "BI_TD_SERVICES_TREE_SEQ", allocationSize = 1)
    private Long id;
    @Column(name = "SERVICE_ID")
    private Long serviceId;
    @Column(name = "PARENT_SERVICE_ID")
    private Long parentServiceId;
    @Column(name = "DEPT_ID")
    private Long deptId;
    @Column(name = "NUM_OF_DAY")
    private Long numOfDay;
    @Column(name = "PARENT_NUM_OF_DAY")
    private Long parentNumOfDay;
    @Column(name = "RATE")
    private Float rate;
    @Column(name = "TYPE_CALC")
    private Long typeCalc;
    @Column(name = "STATUS")
    private Long status;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "PARENT_DEPT_ID")
    private Long parentDeptId;
    @Column(name = "PARENT_DEPT_CODE")
    private String parentDeptCode;
    @Column(name = "DEPT_CODE")
    private String deptCode;
    @Column(name = "UPDATE_TIME")
    private Date updateTime;
    @Column(name = "UPDATE_USER")
    private String updateUser;
    @Column(name = "TYPE_PARAM")
    private String typeParam;

}
