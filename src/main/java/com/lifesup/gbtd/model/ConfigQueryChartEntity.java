package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.ConfigMenuItemDto;
import com.lifesup.gbtd.dto.object.ConfigQueryChartDto;

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
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "getCfQueryChartByChartId", classes = {
                @ConstructorResult(targetClass = ConfigQueryChartDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "queryData", type = String.class),
                                @ColumnResult(name = "queryMaxPrdId", type = String.class),
                                @ColumnResult(name = "defaultValue", type = String.class),
                                @ColumnResult(name = "status", type = Long.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "updateTime", type = Date.class),
                                @ColumnResult(name = "updateUser", type = String.class)
                        })
        })
})
@Table(name = "CONFIG_QUERY_CHART")
public class ConfigQueryChartEntity {
    private Long id;
    private String queryData;
    private String queryMaxPrdId;
    private String defaultValue;
    private Long status;
    private String description;
    private Date updateTime;
    private String updateUser;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONFIG_QUERY_CHART_SEQ")
    @SequenceGenerator(name = "CONFIG_QUERY_CHART_SEQ", sequenceName = "CONFIG_QUERY_CHART_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "QUERY_DATA", nullable = true)
    public String getQueryData() {
        return queryData;
    }

    public void setQueryData(String queryData) {
        this.queryData = queryData;
    }

    @Basic
    @Column(name = "QUERY_MAX_PRD_ID", nullable = true)
    public String getQueryMaxPrdId() {
        return queryMaxPrdId;
    }

    public void setQueryMaxPrdId(String queryMaxPrdId) {
        this.queryMaxPrdId = queryMaxPrdId;
    }

    @Basic
    @Column(name = "DEFAULT_VALUE", nullable = true)
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
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
    @Column(name = "DESCRIPTION", nullable = true, length = 2000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        ConfigQueryChartEntity that = (ConfigQueryChartEntity) o;
        return id == that.id &&
                Objects.equals(queryData, that.queryData) &&
                Objects.equals(queryMaxPrdId, that.queryMaxPrdId) &&
                Objects.equals(defaultValue, that.defaultValue) &&
                Objects.equals(status, that.status) &&
                Objects.equals(description, that.description) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(updateUser, that.updateUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, queryData, queryMaxPrdId, defaultValue, status, description, updateTime, updateUser);
    }
}
