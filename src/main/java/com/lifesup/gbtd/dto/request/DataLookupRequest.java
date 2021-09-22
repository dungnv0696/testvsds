package com.lifesup.gbtd.dto.request;

import com.lifesup.gbtd.dto.base.BaseDto;
import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import com.lifesup.gbtd.dto.object.ServiceGBTDDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataLookupRequest extends BaseDto {
    private Integer fromTime;
    private Integer toTime;
//    private String typeParam;
    private List<Long> serviceIds;
    private List<CatDepartmentDto> depts;
    private List<String> serviceDepts;
    private Boolean lookupByFormula;
    private String typeChart;
    private Long timeType;
    private Long unitViewId;
    private String column;

    private String tableName;

    // not use in request
    private List<ServiceGBTDDto> children;
}
