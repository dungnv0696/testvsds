package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.BiTdServicesTreeDto;
import com.lifesup.gbtd.dto.object.ConfigMenuItemDto;
import com.lifesup.gbtd.dto.object.ServiceGBTDDto;

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
@Table(name = "CONFIG_MENU_ITEM")
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "getConfigMenuItemsByMenuIdAndProfileId", classes = {
                @ConstructorResult(targetClass = ConfigMenuItemDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "menuItemName", type = String.class)
                        })
        })
})
public class ConfigMenuItemEntity {
    private Long id;
    private String menuItemName;
    private Long isDefault;
    private Long orderIndex;
    private Long menuId;
    private Long chartId;
    private Long status;
    private String description;
    private Date updateTime;
    private String updateUser;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONFIG_MENU_ITEM_SEQ")
    @SequenceGenerator(name = "CONFIG_MENU_ITEM_SEQ", sequenceName = "CONFIG_MENU_ITEM_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "MENU_ITEM_NAME", nullable = true, length = 500)
    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    @Basic
    @Column(name = "IS_DEFAULT", nullable = true, precision = 0)
    public Long getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Long isDefault) {
        this.isDefault = isDefault;
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
    @Column(name = "MENU_ID", nullable = true, precision = 0)
    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
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
        ConfigMenuItemEntity that = (ConfigMenuItemEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(menuItemName, that.menuItemName) &&
                Objects.equals(isDefault, that.isDefault) &&
                Objects.equals(orderIndex, that.orderIndex) &&
                Objects.equals(menuId, that.menuId) &&
                Objects.equals(chartId, that.chartId) &&
                Objects.equals(status, that.status) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, menuItemName, isDefault, orderIndex, menuId, chartId, status, description, updateTime, updateUser);
    }
}
