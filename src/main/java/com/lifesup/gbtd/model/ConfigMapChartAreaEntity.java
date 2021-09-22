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
@Table(name = "CONFIG_MAP_CHART_AREA")
public class ConfigMapChartAreaEntity {
    private Long id;
    private Long chartId;
    private Long areaId;
    private Long orderIndex;
    private Long dashboardIdNextto;
    private Long status;
    private Date updateTime;
    private String updateUser;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONFIG_MAP_CHART_AREA_SEQ")
    @SequenceGenerator(name = "CONFIG_MAP_CHART_AREA_SEQ", sequenceName = "CONFIG_MAP_CHART_AREA_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "CHART_ID", nullable = true, precision = 0)
    public Long getChartId() {
        return chartId;
    }

    public void setChartId(Long chartId) {
        this.chartId = chartId;
    }

    @Basic
    @Column(name = "AREA_ID", nullable = true, precision = 0)
    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
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
    @Column(name = "DASHBOARD_ID_NEXTTO", nullable = true, precision = 0)
    public Long getDashboardIdNextto() {
        return dashboardIdNextto;
    }

    public void setDashboardIdNextto(Long dashboardIdNextto) {
        this.dashboardIdNextto = dashboardIdNextto;
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
        ConfigMapChartAreaEntity that = (ConfigMapChartAreaEntity) o;
        return id == that.id &&
                Objects.equals(chartId, that.chartId) &&
                Objects.equals(areaId, that.areaId) &&
                Objects.equals(orderIndex, that.orderIndex) &&
                Objects.equals(dashboardIdNextto, that.dashboardIdNextto) &&
                Objects.equals(status, that.status) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(updateUser, that.updateUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chartId, areaId, orderIndex, dashboardIdNextto, status, updateTime, updateUser);
    }
}
