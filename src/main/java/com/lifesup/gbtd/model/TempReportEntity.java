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

@Getter
@Setter
@Entity
@Table(name = "TEMP_REPORT")
public class TempReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEMP_REPORT_SEQ")
    @SequenceGenerator(name = "TEMP_REPORT_SEQ", sequenceName = "TEMP_REPORT_SEQ", allocationSize = 1)
    @Column(name = "REPORT_ID")
    private Long reportId;
    @Column(name = "CUSTOMER_ID")
    private String customerId;
    @Column(name = "REPORT_NAME")
    private String reportName;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "TYPE")
    private String type;
    @Column(name = "DEPT_PARAM")
    private String deptParam;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "REPORT_TYPE")
    private String reportType;
    @Column(name = "FROM_DATE")
    private Date fromDate;
    @Column(name = "CHECKED")
    private String checked;
    @Column(name = "OWNER_BY")
    private String ownerBy;
}
