package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CatGroupChartDto extends BaseDto {
    private Long id;
    private String groupName;
    private String groupKpiCode;
    private Long kpiIdMain;
    private String description;
    private Long status;
    private Date updateTime;
    private String updateUser;

    public CatGroupChartDto(Long id, String groupName, String groupKpiCode, Long kpiIdMain, String description, Long status, Date updateTime, String updateUser) {
        this.id = id;
        this.groupName = groupName;
        this.groupKpiCode = groupKpiCode;
        this.kpiIdMain = kpiIdMain;
        this.description = description;
        this.status = status;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
    }
}

