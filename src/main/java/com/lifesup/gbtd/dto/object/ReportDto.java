package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * Created by pmvt-os-chc-06 on 6/20/2020.
 */
@Getter
@Setter
public class ReportDto extends BaseDto {
    private Date fromDate;
    private String code;
    private String reportType;
    private String deptParam;
    private List<CheckedDto> checked;
    private String treeTarget;
    private Integer type;
    private String reportName;
    private Long deptId;
    private String ipServer;
    private String fileName;
    private String mapValue;
    private List<Long> deptLevels;
    private List<String> typeParams;
    //fix deptTree
    private String typeParam;
}

