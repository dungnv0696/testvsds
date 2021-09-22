package com.lifesup.gbtd.dto.object;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ServiceGBTDChartDto {
    // base
    private Long id;
    private Long serviceId;
    private String serviceName;
    private String serviceDisplay;
    private String description;
    private Long unitId;
    private String unitCode;
    private Long typeId;
    private Long calRate;
    private Long serviceType;
    private Long thresholdType;
    private Long status;
    private String groupKpiCode;
    private String fomularDescript;
    private Date updateTime;
    private String updateUser;

    // chart
    private String unitName;
    private Double rate;
    private Long unitViewId;
    private String unitViewCode;
    private String unitViewName;
    private String unitDisplay;
    private List<ServicesMapDeptDto> kpiMapDept;
}
