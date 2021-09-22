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
@Table(name = "CAT_THRESHOLD")
public class CatThresholdEntity {
    private Long id;
    private String groupType;
    private Long alarmLevelId;
    private Long thresholdType;
    private Long fromValue;
    private Long toValue;
    private Long status;
    private String description;
    private Date updateTime;
    private String updateUser;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CAT_THRESHOLD_SEQ")
    @SequenceGenerator(name = "CAT_THRESHOLD_SEQ", sequenceName = "CAT_THRESHOLD_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "GROUP_TYPE", nullable = true, length = 500)
    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    @Basic
    @Column(name = "ALARM_LEVEL_ID", nullable = true, precision = 0)
    public Long getAlarmLevelId() {
        return alarmLevelId;
    }

    public void setAlarmLevelId(Long alarmLevelId) {
        this.alarmLevelId = alarmLevelId;
    }

    @Basic
    @Column(name = "THRESHOLD_TYPE", nullable = true, precision = 0)
    public Long getThresholdType() {
        return thresholdType;
    }

    public void setThresholdType(Long thresholdType) {
        this.thresholdType = thresholdType;
    }

    @Basic
    @Column(name = "FROM_VALUE", nullable = true, precision = 0)
    public Long getFromValue() {
        return fromValue;
    }

    public void setFromValue(Long fromValue) {
        this.fromValue = fromValue;
    }

    @Basic
    @Column(name = "TO_VALUE", nullable = true, precision = 0)
    public Long getToValue() {
        return toValue;
    }

    public void setToValue(Long toValue) {
        this.toValue = toValue;
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
        CatThresholdEntity that = (CatThresholdEntity) o;
        return id == that.id &&
                Objects.equals(groupType, that.groupType) &&
                Objects.equals(alarmLevelId, that.alarmLevelId) &&
                Objects.equals(thresholdType, that.thresholdType) &&
                Objects.equals(fromValue, that.fromValue) &&
                Objects.equals(toValue, that.toValue) &&
                Objects.equals(status, that.status) &&
                Objects.equals(description, that.description) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(updateUser, that.updateUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, groupType, alarmLevelId, thresholdType, fromValue, toValue, status, description, updateTime, updateUser);
    }
}
