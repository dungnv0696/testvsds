package com.lifesup.gbtd.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "CONFIG_DISPLAY_QUERY")
public class ConfigDisplayQueryEntity {
    private Long id;
    private Long itemChartId;
    private String columnQuery;
    private String dataType;
    private String columnChart;
    private Long isRequire;
    private Long status;
    private String description;
    private Date updateTime;
    private String updateUser;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONFIG_DISPLAY_QUERY_SEQ")
    @SequenceGenerator(name = "CONFIG_DISPLAY_QUERY_SEQ", sequenceName = "CONFIG_DISPLAY_QUERY_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "ITEM_CHART_ID", nullable = true, precision = 0)
    public Long getItemChartId() {
        return itemChartId;
    }

    public void setItemChartId(Long itemChartId) {
        this.itemChartId = itemChartId;
    }

    @Basic
    @Column(name = "COLUMN_QUERY", nullable = true, length = 500)
    public String getColumnQuery() {
        return columnQuery;
    }

    public void setColumnQuery(String columnQuery) {
        this.columnQuery = columnQuery;
    }

    @Basic
    @Column(name = "DATA_TYPE", nullable = true, length = 500)
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @Basic
    @Column(name = "COLUMN_CHART", nullable = true, length = 500)
    public String getColumnChart() {
        return columnChart;
    }

    public void setColumnChart(String columnChart) {
        this.columnChart = columnChart;
    }

    @Basic
    @Column(name = "IS_REQUIRE", nullable = true, precision = 0)
    public Long getIsRequire() {
        return isRequire;
    }

    public void setIsRequire(Long isRequire) {
        this.isRequire = isRequire;
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
        ConfigDisplayQueryEntity that = (ConfigDisplayQueryEntity) o;
        return id == that.id &&
                Objects.equals(itemChartId, that.itemChartId) &&
                Objects.equals(columnQuery, that.columnQuery) &&
                Objects.equals(dataType, that.dataType) &&
                Objects.equals(columnChart, that.columnChart) &&
                Objects.equals(isRequire, that.isRequire) &&
                Objects.equals(status, that.status) &&
                Objects.equals(description, that.description) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(updateUser, that.updateUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemChartId, columnQuery, dataType, columnChart, isRequire, status, description, updateTime, updateUser);
    }
}
