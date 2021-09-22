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
@Table(name = "CONFIG_AREA")
public class ConfigAreaEntity {
    private Long id;
    private String areaName;
    private Long orderIndex;
    private String positionJson;
    private Long dashboardId;
    private Long timeRefresh;
    private String description;
    private Date updateTime;
    private String updateUser;
    private Long pageDashboard;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONFIG_AREA_SEQ")
    @SequenceGenerator(name = "CONFIG_AREA_SEQ", sequenceName = "CONFIG_AREA_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "AREA_NAME", nullable = true, length = 500)
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
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
    @Column(name = "POSITION_JSON", nullable = true, length = 2000)
    public String getPositionJson() {
        return positionJson;
    }

    public void setPositionJson(String positionJson) {
        this.positionJson = positionJson;
    }

    @Basic
    @Column(name = "DASHBOARD_ID", nullable = true, precision = 0)
    public Long getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(Long dashboardId) {
        this.dashboardId = dashboardId;
    }

    @Basic
    @Column(name = "TIME_REFRESH", nullable = true, precision = 0)
    public Long getTimeRefresh() {
        return timeRefresh;
    }

    public void setTimeRefresh(Long timeRefresh) {
        this.timeRefresh = timeRefresh;
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

    @Basic
    @Column(name = "PAGE_DASHBOARD", nullable = true, precision = 0)
    public Long getPageDashboard() {
        return pageDashboard;
    }

    public void setPageDashboard(Long pageDashboard) {
        this.pageDashboard = pageDashboard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigAreaEntity that = (ConfigAreaEntity) o;
        return id == that.id &&
                Objects.equals(areaName, that.areaName) &&
                Objects.equals(orderIndex, that.orderIndex) &&
                Objects.equals(positionJson, that.positionJson) &&
                Objects.equals(dashboardId, that.dashboardId) &&
                Objects.equals(timeRefresh, that.timeRefresh) &&
                Objects.equals(description, that.description) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(updateUser, that.updateUser) &&
                Objects.equals(pageDashboard, that.pageDashboard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, areaName, orderIndex, positionJson, dashboardId, timeRefresh, description, updateTime, updateUser, pageDashboard);
    }
}
