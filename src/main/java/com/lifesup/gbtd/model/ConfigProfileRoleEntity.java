package com.lifesup.gbtd.model;

import lombok.Getter;
import lombok.Setter;

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
@Table(name = "CONFIG_PROFILE_ROLE")
@Getter
@Setter
public class ConfigProfileRoleEntity {
    private Long id;
    private Long profileId;
    private Long deptId;
    private String usernameUsed;
    private String roleCode;
    private Date updateTime;
    private String updateUser;

    @Id
    @Column(name = "ID", nullable = true, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONFIG_PROFILE_ROLE_SEQ")
    @SequenceGenerator(name = "CONFIG_PROFILE_ROLE_SEQ", sequenceName = "CONFIG_PROFILE_ROLE_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "PROFILE_ID")
    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    @Basic
    @Column(name = "DEPT_ID", nullable = true, precision = 0)
    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    @Basic
    @Column(name = "USERNAME_USED", nullable = true, length = 100)
    public String getUsernameUsed() {
        return usernameUsed;
    }

    public void setUsernameUsed(String usernameUsed) {
        this.usernameUsed = usernameUsed;
    }

    @Basic
    @Column(name = "ROLE_CODE", nullable = true, length = 100)
    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
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
        ConfigProfileRoleEntity that = (ConfigProfileRoleEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(profileId, that.profileId) &&
                Objects.equals(deptId, that.deptId) &&
                Objects.equals(usernameUsed, that.usernameUsed) &&
                Objects.equals(roleCode, that.roleCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, deptId, usernameUsed, roleCode, updateTime, updateUser);
    }
}
