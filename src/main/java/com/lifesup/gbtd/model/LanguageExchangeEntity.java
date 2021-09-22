package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.ConfigMenuItemDto;
import com.lifesup.gbtd.dto.object.LanguageExchangeDto;

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

@Entity
@Table(name = "LANGUAGE_EXCHANGE")
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "getLanguageExchangeMapping", classes = {
                @ConstructorResult(targetClass = LanguageExchangeDto.class,
                        columns = {
                                @ColumnResult(name = "leeId", type = Long.class),
                                @ColumnResult(name = "appliedBusiness", type = String.class),
                                @ColumnResult(name = "businessId", type = Long.class),
                                @ColumnResult(name = "defaultValue", type = String.class),
                                @ColumnResult(name = "leeLocale", type = String.class),
                                @ColumnResult(name = "leeValue", type = String.class),
                        })
        })
})
public class LanguageExchangeEntity {
    private Long leeId;
    private String appliedBusiness;
    private Long businessId;
    private String leeLocale;
    private String leeValue;

    @Id
    @Column(name = "LEE_ID", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LANGUAGE_EXCHANGE_SEQ")
    @SequenceGenerator(name = "LANGUAGE_EXCHANGE_SEQ", sequenceName = "LANGUAGE_EXCHANGE_SEQ", allocationSize = 1)
    public Long getLeeId() {
        return leeId;
    }

    public void setLeeId(Long leeId) {
        this.leeId = leeId;
    }

    @Basic
    @Column(name = "APPLIED_BUSINESS", nullable = true, length = 500)
    public String getAppliedBusiness() {
        return appliedBusiness;
    }

    public void setAppliedBusiness(String appliedBusiness) {
        this.appliedBusiness = appliedBusiness;
    }

    @Basic
    @Column(name = "BUSINESS_ID", nullable = true, precision = 0)
    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    @Basic
    @Column(name = "LEE_LOCALE", nullable = true, length = 500)
    public String getLeeLocale() {
        return leeLocale;
    }

    public void setLeeLocale(String leeLocale) {
        this.leeLocale = leeLocale;
    }

    @Basic
    @Column(name = "LEE_VALUE", nullable = true, length = 500)
    public String getLeeValue() {
        return leeValue;
    }

    public void setLeeValue(String leeValue) {
        this.leeValue = leeValue;
    }
}
