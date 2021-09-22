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

@Entity
@Table(name = "users_dept")
@Getter
@Setter
public class UsersDeptEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USERS_DEPT_SEQ")
    @SequenceGenerator(name = "USERS_DEPT_SEQ", sequenceName = "USERS_DEPT_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;
    @Column(name = "USER_ID")
    private Long userId;
    @Column(name = "DEPT_ID")
    private Long deptId;
}
