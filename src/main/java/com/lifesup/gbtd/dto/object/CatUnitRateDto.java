package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CatUnitRateDto extends BaseDto {
    private Long id;
    private Long unitIdBefore;
    private Double rate;
    private Long unitIdAfter;
    private Long status;
    private Date updateTime;
    private String updateUser;

    public CatUnitRateDto(Long id, Long unitIdBefore, Double rate, Long unitIdAfter) {
        this.id = id;
        this.unitIdBefore = unitIdBefore;
        this.rate = rate;
        this.unitIdAfter = unitIdAfter;
    }

    public CatUnitRateDto() {
    }
}
