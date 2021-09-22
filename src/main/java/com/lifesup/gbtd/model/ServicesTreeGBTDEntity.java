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
@Table(name = "SERVICES_TREE_GBTD")
@Getter
@Setter
public class ServicesTreeGBTDEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SERVICES_TREE_GBTD_SEQ")
    @SequenceGenerator(name = "SERVICES_TREE_GBTD_SEQ", sequenceName = "SERVICES_TREE_GBTD_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;
    @Column(name = "SERVICE_ID")
    private Long serviceId;
    @Column(name = "PARENT_SERVICE_ID")
    private Long parentServiceId;
    @Column(name = "dept_id")
    private Long deptId;
    @Column(name = "RATE")
    private Long rate;
    @Column(name = "STATUS")
    private Long status;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "SOURCE")
    private String source;
    @Column(name = "UPDATE_TIME")
    private Date updateTime;
    @Column(name = "UPDATE_USER")
    private String updateUser;
}
