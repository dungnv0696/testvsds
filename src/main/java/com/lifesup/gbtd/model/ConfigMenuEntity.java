package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.ConfigMenuDto;
import com.lifesup.gbtd.dto.object.ConfigMenuItemDto;

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
@Table(name = "CONFIG_MENU")
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "getConfigMenus", classes = {
                @ConstructorResult(targetClass = ConfigMenuDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "menuName", type = String.class),
                                @ColumnResult(name = "profileId", type = Long.class),
                                @ColumnResult(name = "status", type = Long.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "updateTime", type = Date.class),
                                @ColumnResult(name = "updateUser", type = String.class),
                                @ColumnResult(name = "orderIndex", type = Long.class)
                        })
        })
})
public class ConfigMenuEntity {
    private Long id;
    private String menuName;
    private Long profileId;
    private Long status;
    private String description;
    private Date updateTime;
    private String updateUser;
    private Long orderIndex;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONFIG_MENU_SEQ")
    @SequenceGenerator(name = "CONFIG_MENU_SEQ", sequenceName = "CONFIG_MENU_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "MENU_NAME", nullable = true, length = 500)
    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
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

    @Basic
    @Column(name = "ORDER_INDEX", nullable = true, length = 22)
    public Long getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Long orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigMenuEntity that = (ConfigMenuEntity) o;
        return id == that.id &&
                Objects.equals(menuName, that.menuName) &&
                Objects.equals(profileId, that.profileId) &&
                Objects.equals(status, that.status) &&
                Objects.equals(description, that.description) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(updateUser, that.updateUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, menuName, profileId, status, description, updateTime, updateUser);
    }
}
