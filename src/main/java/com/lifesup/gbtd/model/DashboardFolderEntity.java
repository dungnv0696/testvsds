package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.DashboardFolderDto;
import com.lifesup.gbtd.dto.object.TreeDto;

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
import java.util.Objects;

@Table(name = "DASHBOARD_FOLDER")
@Entity
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "getDashboardFolder.tree", classes = {
                @ConstructorResult(targetClass = TreeDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "code", type = String.class),
                                @ColumnResult(name = "name", type = String.class),
                                @ColumnResult(name = "parent", type = Long.class)
                        })
        }),
        @SqlResultSetMapping(name = "getDashboardFolder.searchTree", classes = {
                @ConstructorResult(targetClass = DashboardFolderDto.class,
                        columns = {
                                @ColumnResult(name = "folderId", type = Long.class),
                                @ColumnResult(name = "folderCode", type = String.class),
                                @ColumnResult(name = "folderName", type = String.class),
                                @ColumnResult(name = "folderParentId", type = Long.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "status", type = Long.class),
                                @ColumnResult(name = "modifiedDate", type = Date.class)
                        })
        }),
        @SqlResultSetMapping(name = "getDashboardFolder.search", classes = {
                @ConstructorResult(targetClass = DashboardFolderDto.class,
                        columns = {
                                @ColumnResult(name = "folderId", type = Long.class),
                                @ColumnResult(name = "folderParentId", type = Long.class),
                                @ColumnResult(name = "folderCode", type = String.class),
                                @ColumnResult(name = "folderName", type = String.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "status", type = Long.class),
                                @ColumnResult(name = "modifiedDate", type = Date.class),
                                @ColumnResult(name = "folderNameParent", type = String.class),
                                @ColumnResult(name = "reportId", type = Long.class),
                                @ColumnResult(name = "updateUser", type = String.class)
                        })
        })
})
public class DashboardFolderEntity {
    private Long folderId;
    private Long folderParentId;
    private String folderCode;
    private String folderName;
    private String description;
    private Long status;
    private Date modifiedDate;
    private String updateUser;

    @Id
    @Column(name = "FOLDER_ID", nullable = true, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DASHBOARD_FOLDER_SEQ")
    @SequenceGenerator(name = "DASHBOARD_FOLDER_SEQ", sequenceName = "DASHBOARD_FOLDER_SEQ", allocationSize = 1)
    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    @Basic
    @Column(name = "FOLDER_PARENT_ID", nullable = true, precision = 0)
    public Long getFolderParentId() {
        return folderParentId;
    }

    public void setFolderParentId(Long folderParentId) {
        this.folderParentId = folderParentId;
    }

    @Basic
    @Column(name = "FOLDER_CODE", nullable = true, length = 20)
    public String getFolderCode() {
        return folderCode;
    }

    public void setFolderCode(String folderCode) {
        this.folderCode = folderCode;
    }

    @Basic
    @Column(name = "FOLDER_NAME", nullable = true, length = 200)
    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    @Basic
    @Column(name = "DESCRIPTION", nullable = true, length = 200)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
    @Column(name = "MODIFIED_DATE", nullable = true)
    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Basic
    @Column(name = "UPDATE_USER", nullable = true)
    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashboardFolderEntity that = (DashboardFolderEntity) o;
        return Objects.equals(folderId, that.folderId) &&
                Objects.equals(folderParentId, that.folderParentId) &&
                Objects.equals(folderCode, that.folderCode) &&
                Objects.equals(folderName, that.folderName) &&
                Objects.equals(description, that.description) &&
                Objects.equals(status, that.status) &&
                Objects.equals(modifiedDate, that.modifiedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(folderId, folderParentId, folderCode, folderName, description, status, modifiedDate);
    }
}
