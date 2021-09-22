package com.lifesup.gbtd.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "CONFIG_CHART_DEFAULT")
public class ConfigChartDefaultEntity {
    private Long id;
    private Long timeType;
    private String typeChart;
    private String columnQuery;
    private String dataType;
    private String columnChart;
    private Long orderIndex;
    private Long status;
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONFIG_CHART_DEFAULT_SEQ")
    @SequenceGenerator(name = "CONFIG_CHART_DEFAULT_SEQ",
            sequenceName = "CONFIG_CHART_DEFAULT_SEQ",
            allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "TIME_TYPE")
    public Long getTimeType() {
        return timeType;
    }

    public void setTimeType(Long timeType) {
        this.timeType = timeType;
    }

    @Basic
    @Column(name = "TYPE_CHART")
    public String getTypeChart() {
        return typeChart;
    }

    public void setTypeChart(String typeChart) {
        this.typeChart = typeChart;
    }

    @Basic
    @Column(name = "COLUMN_QUERY")
    public String getColumnQuery() {
        return columnQuery;
    }

    public void setColumnQuery(String columnQuery) {
        this.columnQuery = columnQuery;
    }

    @Basic
    @Column(name = "DATA_TYPE")
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @Basic
    @Column(name = "COLUMN_CHART")
    public String getColumnChart() {
        return columnChart;
    }

    public void setColumnChart(String columnChart) {
        this.columnChart = columnChart;
    }

    @Basic
    @Column(name = "ORDER_INDEX")
    public Long getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Long orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Basic
    @Column(name = "STATUS")
    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigChartDefaultEntity that = (ConfigChartDefaultEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(timeType, that.timeType) &&
                Objects.equals(typeChart, that.typeChart) &&
                Objects.equals(columnQuery, that.columnQuery) &&
                Objects.equals(dataType, that.dataType) &&
                Objects.equals(columnChart, that.columnChart) &&
                Objects.equals(orderIndex, that.orderIndex) &&
                Objects.equals(status, that.status) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timeType, typeChart, columnQuery, dataType, columnChart, orderIndex, status, description);
    }
}
