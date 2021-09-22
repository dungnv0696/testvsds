package com.lifesup.gbtd.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "RPT_DATA_NEWEST")
public class RptDataNewestEntity {
    private Long id;
    private Long serviceId;
    private Long inputLevel;
    private Long timeType;
    private Long prdId;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RPT_DATA_NEWEST_SEQ")
    @SequenceGenerator(name = "RPT_DATA_NEWEST_SEQ", sequenceName = "RPT_DATA_NEWEST_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "SERVICE_ID", nullable = true, precision = 0)
    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    @Basic
    @Column(name = "INPUT_LEVEL", nullable = true, precision = 0)
    public Long getInputLevel() {
        return inputLevel;
    }

    public void setInputLevel(Long inputLevel) {
        this.inputLevel = inputLevel;
    }

    @Basic
    @Column(name = "TIME_TYPE", nullable = true, precision = 0)
    public Long getTimeType() {
        return timeType;
    }

    public void setTimeType(Long timeType) {
        this.timeType = timeType;
    }

    @Basic
    @Column(name = "PRD_ID", nullable = true, precision = 0)
    public Long getPrdId() {
        return prdId;
    }

    public void setPrdId(Long prdId) {
        this.prdId = prdId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RptDataNewestEntity that = (RptDataNewestEntity) o;
        return id == that.id &&
                Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(inputLevel, that.inputLevel) &&
                Objects.equals(timeType, that.timeType) &&
                Objects.equals(prdId, that.prdId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, serviceId, inputLevel, timeType, prdId);
    }
}
