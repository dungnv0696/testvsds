package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.ConfigProfileDto;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
import java.io.Serializable;
import java.util.Date;

/**
 * A ConfigProfile.
 */
@Entity
@Table(name = "config_profile")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "ConfigProfile.search", classes = {
                @ConstructorResult(targetClass = ConfigProfileDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "profileName", type = String.class),
                                @ColumnResult(name = "isDefault", type = Long.class),
                                @ColumnResult(name = "orderIndex", type = Long.class),
                                @ColumnResult(name = "roleType", type = Long.class),
                                @ColumnResult(name = "status", type = Long.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "updateTime", type = Date.class),
                                @ColumnResult(name = "updateUser", type = String.class),
                                @ColumnResult(name = "hasChild", type = Long.class),
                                @ColumnResult(name = "roleCode", type = String.class)
                        })
        }),
        @SqlResultSetMapping(name = "ConfigProfile", classes = {
                @ConstructorResult(targetClass = ConfigProfileDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "profileName", type = String.class),
                                @ColumnResult(name = "isDefault", type = Long.class),
                                @ColumnResult(name = "orderIndex", type = Long.class),
                                @ColumnResult(name = "roleType", type = Long.class),
                                @ColumnResult(name = "status", type = Long.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "updateTime", type = Date.class),
                                @ColumnResult(name = "updateUser", type = String.class)
                        })
        }),
        @SqlResultSetMapping(name = "ConfigProfile.forcbb", classes = {
                @ConstructorResult(targetClass = ConfigProfileDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "profileName", type = String.class),
                                @ColumnResult(name = "isDefault", type = Long.class),
                                @ColumnResult(name = "orderIndex", type = Long.class),
                                @ColumnResult(name = "roleType", type = Long.class),
                                @ColumnResult(name = "status", type = Long.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "updateTime", type = Date.class),
                                @ColumnResult(name = "updateUser", type = String.class),
                                @ColumnResult(name = "roleCode", type = String.class)
                        })
        })
})
public class ConfigProfileEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONFIG_PROFILE_SEQ")
    @SequenceGenerator(name = "CONFIG_PROFILE_SEQ", sequenceName = "CONFIG_PROFILE_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "profile_name")
    private String profileName;

    @Column(name = "is_default")
    private Long isDefault;

    @Column(name = "order_index")
    private Long orderIndex;

    @Column(name = "role_type")
    private Long roleType;

    @Column(name = "status")
    private Long status;

    @Column(name = "description")
    private String description;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "update_user")
    private String updateUser;

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "configProfile")
//    private Set<ConfigProfileRoleEntity> roleEntities ;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getProfileCode() {
//        return profileCode;
//    }
//
//    public ConfigProfile profileCode(String profileCode) {
//        this.profileCode = profileCode;
//        return this;
//    }
//
//    public void setProfileCode(String profileCode) {
//        this.profileCode = profileCode;
//    }
//
//    public String getProfileName() {
//        return profileName;
//    }
//
//    public ConfigProfile profileName(String profileName) {
//        this.profileName = profileName;
//        return this;
//    }
//
//    public void setProfileName(String profileName) {
//        this.profileName = profileName;
//    }
//
//    public Long getIsDefault() {
//        return isDefault;
//    }
//
//    public ConfigProfile isDefault(Long isDefault) {
//        this.isDefault = isDefault;
//        return this;
//    }
//
//    public void setIsDefault(Long isDefault) {
//        this.isDefault = isDefault;
//    }
//
//    public Long getOrderIndex() {
//        return orderIndex;
//    }
//
//    public ConfigProfile orderIndex(Long orderIndex) {
//        this.orderIndex = orderIndex;
//        return this;
//    }
//
//    public void setOrderIndex(Long orderIndex) {
//        this.orderIndex = orderIndex;
//    }
//
//    public String getRoleCode() {
//        return roleCode;
//    }
//
//    public ConfigProfile roleCode(String roleCode) {
//        this.roleCode = roleCode;
//        return this;
//    }
//
//    public void setRoleCode(String roleCode) {
//        this.roleCode = roleCode;
//    }
//
//    public Long getStatus() {
//        return status;
//    }
//
//    public ConfigProfile status(Long status) {
//        this.status = status;
//        return this;
//    }
//
//    public void setStatus(Long status) {
//        this.status = status;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public ConfigProfile description(String description) {
//        this.description = description;
//        return this;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public Instant getUpdateTime() {
//        return updateTime;
//    }
//
//    public ConfigProfile updateTime(Instant updateTime) {
//        this.updateTime = updateTime;
//        return this;
//    }
//
//    public void setUpdateTime(Instant updateTime) {
//        this.updateTime = updateTime;
//    }
//
//    public String getUpdateUser() {
//        return updateUser;
//    }
//
//    public ConfigProfile updateUser(String updateUser) {
//        this.updateUser = updateUser;
//        return this;
//    }
//
//    public void setUpdateUser(String updateUser) {
//        this.updateUser = updateUser;
//    }
//    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (!(o instanceof ConfigProfile)) {
//            return false;
//        }
//        return id != null && id.equals(((ConfigProfile) o).id);
//    }
//
//    @Override
//    public int hashCode() {
//        return 31;
//    }
//
//    @Override
//    public String toString() {
//        return "ConfigProfile{" +
//            "id=" + getId() +
//            ", profileCode='" + getProfileCode() + "'" +
//            ", profileName='" + getProfileName() + "'" +
//            ", isDefault=" + getIsDefault() +
//            ", orderIndex=" + getOrderIndex() +
//            ", roleCode='" + getRoleCode() + "'" +
//            ", status=" + getStatus() +
//            ", description='" + getDescription() + "'" +
//            ", updateTime='" + getUpdateTime() + "'" +
//            ", updateUser='" + getUpdateUser() + "'" +
//            "}";
//    }
}
