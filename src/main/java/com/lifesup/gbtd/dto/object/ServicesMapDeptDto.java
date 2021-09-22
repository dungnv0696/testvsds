package com.lifesup.gbtd.dto.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServicesMapDeptDto {
    private Long id;
    private Long serviceId;
    private Long deptId;
    private String source;
    private String fomularDescript;
    private String groupKpiCode;

    public ServicesMapDeptDto() {};
}
