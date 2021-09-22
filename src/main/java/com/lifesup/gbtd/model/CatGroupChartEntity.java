package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.CatGroupChartDto;

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

@Entity
@Table(name = "CAT_GROUP_CHART")
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "catGroupChart", classes = {
                @ConstructorResult(targetClass = CatGroupChartDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "groupName", type = String.class),
                                @ColumnResult(name = "groupKpiCode", type = String.class),
                                @ColumnResult(name = "kpiIdMain", type = Long.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "status", type = Long.class),
                                @ColumnResult(name = "updateTime", type = Date.class),
                                @ColumnResult(name = "updateUser", type = String.class)
                        })
        })
})
public class CatGroupChartEntity {
    private Long id;
    private String groupName;
    private String groupKpiCode;
    private Long kpiIdMain;
    private String description;
    private Long status;
    private Date updateTime;
    private String updateUser;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CAT_GROUP_CHART_SEQ")
    @SequenceGenerator(name = "CAT_GROUP_CHART_SEQ", sequenceName = "CAT_GROUP_CHART_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "GROUP_NAME", nullable = true, length = 500)
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Basic
    @Column(name = "GROUP_KPI_CODE", nullable = true, length = 500)
    public String getGroupKpiCode() {
        return groupKpiCode;
    }

    public void setGroupKpiCode(String groupKpiCode) {
        this.groupKpiCode = groupKpiCode;
    }

    @Basic
    @Column(name = "KPI_ID_MAIN", nullable = true, precision = 0)
    public Long getKpiIdMain() {
        return kpiIdMain;
    }

    public void setKpiIdMain(Long kpiIdMain) {
        this.kpiIdMain = kpiIdMain;
    }

    @Basic
    @Column(name = "DESCRIPTION", nullable = true, length = 2000)
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
    @Column(name = "UPDATE_TIME", nullable = true)
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Basic
    @Column(name = "UPDATE_USER", nullable = true, length = 200)
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
        CatGroupChartEntity that = (CatGroupChartEntity) o;
        return id == that.id &&
                Objects.equals(groupName, that.groupName) &&
                Objects.equals(groupKpiCode, that.groupKpiCode) &&
                Objects.equals(kpiIdMain, that.kpiIdMain) &&
                Objects.equals(description, that.description) &&
                Objects.equals(status, that.status) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(updateUser, that.updateUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, groupName, groupKpiCode, kpiIdMain, description, status, updateTime, updateUser);
    }
}
