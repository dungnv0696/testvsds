package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class DashboardParamTreeDto extends BaseDto {
    private Long id;
    @NotNull
    private String code;
    @NotNull
    private String name;
    private String parent;
    @NotNull
    private String typeParam;
    @NotNull
    private Long status;
    private String image;
    @NotNull
    private Long paramOrder;
    private String groupType;
    private Long levelNode;
    private String subCode;
    private Date modifiedDate;
    private Date startTime;
    private Date endTime;
    @NotNull
    private Long deptId;
    private Long parentDeptId;

    public DashboardParamTreeDto(String code, String name, String parent, String typeParam, Long status, Long paramOrder,
                                 Date startTime, Date endTime, Long deptId, Long parentDeptId) {
        this.code = code;
        this.name = name;
        this.parent = parent;
        this.typeParam = typeParam;
        this.status = status;
        this.paramOrder = paramOrder;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deptId = deptId;
        this.parentDeptId = parentDeptId;
    }

    public DashboardParamTreeDto(String code, String name, String parent, String typeParam, Long status, Long paramOrder,
                                 Date startTime, Date endTime, Long deptId, Long parentDeptId, String groupType) {
        this(code, name, parent, typeParam, status, paramOrder, startTime, endTime, deptId, parentDeptId);
        this.groupType = groupType;
    }
}
