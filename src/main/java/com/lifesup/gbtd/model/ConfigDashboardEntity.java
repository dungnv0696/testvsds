package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import com.lifesup.gbtd.dto.object.ConfigDashboardDto;

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
@Table(name = "CONFIG_DASHBOARD")
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "configDashboard.doSearch", classes = {
                @ConstructorResult(targetClass = ConfigDashboardDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "dashboardName", type = String.class),
                                @ColumnResult(name = "dashboardType", type = Long.class),
                                @ColumnResult(name = "orderIndex", type = Long.class),
                                @ColumnResult(name = "profileId", type = Long.class),
                                @ColumnResult(name = "menuItemId", type = Long.class),
                                @ColumnResult(name = "status", type = Long.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "updateTime", type = Date.class),
                                @ColumnResult(name = "updateUser", type = String.class)
                        })
        })
})
public class ConfigDashboardEntity {
    private Long id;
    private String dashboardName;
    private Long dashboardType;
    private Long orderIndex;
    private Long profileId;
    private Long menuItemId;
    private Long status;
    private String description;
    private Date updateTime;
    private String updateUser;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONFIG_DASHBOARD_SEQ")
    @SequenceGenerator(name = "CONFIG_DASHBOARD_SEQ", sequenceName = "CONFIG_DASHBOARD_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "DASHBOARD_NAME", nullable = true, length = 500)
    public String getDashboardName() {
        return dashboardName;
    }

    public void setDashboardName(String dashboardName) {
        this.dashboardName = dashboardName;
    }

    @Basic
    @Column(name = "DASHBOARD_TYPE", nullable = true, precision = 0)
    public Long getDashboardType() {
        return dashboardType;
    }

    public void setDashboardType(Long dashboardType) {
        this.dashboardType = dashboardType;
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
    @Column(name = "PROFILE_ID", nullable = true, precision = 0)
    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    @Basic
    @Column(name = "MENU_ITEM_ID", nullable = true, precision = 0)
    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
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
        ConfigDashboardEntity that = (ConfigDashboardEntity) o;
        return id == that.id &&
                Objects.equals(dashboardName, that.dashboardName) &&
                Objects.equals(dashboardType, that.dashboardType) &&
                Objects.equals(orderIndex, that.orderIndex) &&
                Objects.equals(profileId, that.profileId) &&
                Objects.equals(menuItemId, that.menuItemId) &&
                Objects.equals(status, that.status) &&
                Objects.equals(description, that.description) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(updateUser, that.updateUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dashboardName, dashboardType, orderIndex, profileId, menuItemId, status, description, updateTime, updateUser);
    }
}
