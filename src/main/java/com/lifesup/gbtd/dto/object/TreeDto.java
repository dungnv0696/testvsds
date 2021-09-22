package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TreeDto extends BaseDto {
    private Long id;
    private String code;
    private String name;
    private Long parent;

    private List<DashboardReportDto> dashboardReports;

    public TreeDto(Long id, String code, String name, Long parent) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.parent = parent;
    }
}
