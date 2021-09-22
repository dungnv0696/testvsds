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
@Table(name = "ACTION_AUDIT")
@Getter
@Setter
public class ActionAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "action_audit_seq")
    @SequenceGenerator(name = "action_audit_seq",
            sequenceName = "action_audit_seq",
            allocationSize = 1)
    private Integer id;
    @Column(name = "TABLE_NAME")
    private String tableName;
    @Column(name = "OBJECT_ID")
    private String objectId;
    @Column(name = "ACTION")
    private String action;
    @Column(name = "OLD_VALUE")
    private String oldValue;
    @Column(name = "NEW_VALUE")
    private String newValue;
    @Column(name = "USER_NAME")
    private String user;
    @Column(name = "IP")
    private String ip;
    @Column(name = "CREATE_DATE")
    private Date createDate;
}
