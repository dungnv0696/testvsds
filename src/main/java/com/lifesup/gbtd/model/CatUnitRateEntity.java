package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import com.lifesup.gbtd.dto.object.CatUnitRateDto;

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
@Table(name = "CAT_UNIT_RATE")
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "catUnit.converter", classes = {
                @ConstructorResult(targetClass = CatUnitRateDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "unitIdBefore", type = Long.class),
                                @ColumnResult(name = "rate", type = Double.class),
                                @ColumnResult(name = "unitIdAfter", type = Long.class)
                        })
        })
})
public class CatUnitRateEntity {
    private Long id;
    private Long unitIdBefore;
    private Double rate;
    private Long unitIdAfter;
    private Long status;
    private Date updateTime;
    private String updateUser;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CAT_UNIT_RATE_SEQ")
    @SequenceGenerator(name = "CAT_UNIT_RATE_SEQ", sequenceName = "CAT_UNIT_RATE_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "UNIT_ID_BEFORE", nullable = true, precision = 0)
    public Long getUnitIdBefore() {
        return unitIdBefore;
    }

    public void setUnitIdBefore(Long unitIdBefore) {
        this.unitIdBefore = unitIdBefore;
    }

    @Basic
    @Column(name = "RATE", nullable = true, precision = 0)
    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    @Basic
    @Column(name = "UNIT_ID_AFTER", nullable = true, precision = 0)
    public Long getUnitIdAfter() {
        return unitIdAfter;
    }

    public void setUnitIdAfter(Long unitIdAfter) {
        this.unitIdAfter = unitIdAfter;
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
        CatUnitRateEntity that = (CatUnitRateEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(unitIdBefore, that.unitIdBefore) &&
                Objects.equals(rate, that.rate) &&
                Objects.equals(unitIdAfter, that.unitIdAfter) &&
                Objects.equals(status, that.status) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(updateUser, that.updateUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, unitIdBefore, rate, unitIdAfter, status, updateTime, updateUser);
    }

}
