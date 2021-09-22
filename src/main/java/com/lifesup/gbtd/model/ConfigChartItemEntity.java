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
@Table(name = "CONFIG_CHART_ITEM")
public class ConfigChartItemEntity {
    private Long chartId;
    private Long id;
    private String typeChart;
    private Long hasAvgLine;
    private String listColor;
    private Long orderIndex;
    private Long queryId;
    private String inputCondition;
    private Long status;
    private String description;
    private Date updateTime;
    private String updateUser;

    @Basic
    @Column(name = "CHART_ID", nullable = false, precision = 0)
    public Long getChartId() {
        return chartId;
    }

    public void setChartId(Long chartId) {
        this.chartId = chartId;
    }

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONFIG_CHART_ITEM_SEQ")
    @SequenceGenerator(name = "CONFIG_CHART_ITEM_SEQ", sequenceName = "CONFIG_CHART_ITEM_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "TYPE_CHART", nullable = true, length = 500)
    public String getTypeChart() {
        return typeChart;
    }

    public void setTypeChart(String typeChart) {
        this.typeChart = typeChart;
    }

    @Basic
    @Column(name = "HAS_AVG_LINE", nullable = true, precision = 0)
    public Long getHasAvgLine() {
        return hasAvgLine;
    }

    public void setHasAvgLine(Long hasAvgLine) {
        this.hasAvgLine = hasAvgLine;
    }

    @Basic
    @Column(name = "LIST_COLOR", nullable = true, length = 200)
    public String getListColor() {
        return listColor;
    }

    public void setListColor(String listColor) {
        this.listColor = listColor;
    }

    @Basic
    @Column(name = "ORDER_INDEX", nullable = true, precision = 0)
    public Long getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Long orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Basic
    @Column(name = "QUERY_ID", nullable = true, precision = 0)
    public Long getQueryId() {
        return queryId;
    }

    public void setQueryId(Long queryId) {
        this.queryId = queryId;
    }

    @Basic
    @Column(name = "INPUT_CONDITION", nullable = true)
    public String getInputCondition() {
        return inputCondition;
    }

    public void setInputCondition(String inputCondition) {
        this.inputCondition = inputCondition;
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
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConfigChartItemEntity)) {
            return false;
        }
        return id != null && id.equals(((ConfigChartItemEntity) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
