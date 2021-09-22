package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SaveKpiInfoDto extends BaseDto {
    private List<ServiceGBTDDto> kpis;
    private String tableName;

    public SaveKpiInfoDto() {
    }

    public SaveKpiInfoDto(String tableName, List<ServiceGBTDDto> kpis) {
        this.kpis = kpis;
        this.tableName = tableName;
    }
}
