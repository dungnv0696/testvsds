package com.lifesup.gbtd.dto.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LanguageExchangeDto {
    private Long leeId;
    private String appliedBusiness;
    private Long businessId;
    private String leeLocale;
    private String leeValue;

    // dto only
//    private Long itemIdBusinessTable;
//    private Long itemIdLanguageCode;
    private Long translateStatus;
    private String defaultValue;
    private String businessIdCol;
    private String leeValueCol;
    private List<String> errorsMess;

    public LanguageExchangeDto(Long leeId, String appliedBusiness, Long businessId, String defaultValue, String leeLocale, String leeValue) {
        this.leeId = leeId;
        this.appliedBusiness = appliedBusiness;
        this.businessId = businessId;
        this.leeLocale = leeLocale;
        this.leeValue = leeValue;
        this.defaultValue = defaultValue;
        this.errorsMess = new ArrayList<>();
    }

    public LanguageExchangeDto() {
        this.errorsMess = new ArrayList<>();
    }
}
