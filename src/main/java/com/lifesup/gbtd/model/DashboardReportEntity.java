package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.DashboardReportDto;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "DASHBOARD_REPORT")
@Entity
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "getDashboardReport.search", classes = {
                @ConstructorResult(targetClass = DashboardReportDto.class,
                        columns = {
                                @ColumnResult(name = "reportId", type = Long.class),
                                @ColumnResult(name = "reportCode", type = String.class),
                                @ColumnResult(name = "reportName", type = String.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "folderId", type = Long.class),
                                @ColumnResult(name = "status", type = Long.class),
                                @ColumnResult(name = "fileName", type = String.class),
                                @ColumnResult(name = "modifiedDate", type = Date.class),
                                @ColumnResult(name = "splitSheet", type = Long.class),
                                @ColumnResult(name = "updateUser", type = String.class),
                                @ColumnResult(name = "ipServer", type = String.class),
                                @ColumnResult(name = "folderName", type = String.class)
                        })
        })
})
public class DashboardReportEntity {
    private Long reportId;
    private String reportCode;
    private String reportName;
    private String description;
    private Long folderId;
    private Long status;
    private String fileName;
    private Date modifiedDate;
    private Long splitSheet;
    private String updateUser;
    private String ipServer;

    @Id
    @Column(name = "REPORT_ID", nullable = true, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DASHBOARD_REPORT_SEQ")
    @SequenceGenerator(name = "DASHBOARD_REPORT_SEQ", sequenceName = "DASHBOARD_REPORT_SEQ", allocationSize = 1)
    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    @Basic
    @Column(name = "REPORT_CODE", nullable = true, length = 50)
    public String getReportCode() {
        return reportCode;
    }

    public void setReportCode(String reportCode) {
        this.reportCode = reportCode;
    }

    @Basic
    @Column(name = "REPORT_NAME", nullable = true, length = 200)
    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    @Basic
    @Column(name = "DESCRIPTION", nullable = true, length = 500)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "FOLDER_ID", nullable = true, precision = 0)
    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    @Basic
    @Column(name = "STATUS", nullable = true, precision = 0)
    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    @Basic
    @Column(name = "FILE_NAME", nullable = true, length = 100)
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Basic
    @Column(name = "MODIFIED_DATE", nullable = true)
    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Column(name = "SPLIT_SHEET")
    public Long getSplitSheet() {
        return splitSheet;
    }

    public void setSplitSheet(Long splitSheet) {
        this.splitSheet = splitSheet;
    }

    @Column(name = "UPDATE_USER")
    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @Column(name = "IP_SERVER")
    public String getIpServer() {
        return ipServer;
    }

    public void setIpServer(String ipServer) {
        this.ipServer = ipServer;
    }
}
