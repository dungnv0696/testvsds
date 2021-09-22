package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Update;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class TargetFormulaDto {

    @NotNull(groups = {Update.class})
    private Integer id;
    @NotNull(message = "ServiceId is required", groups = {Add.class, Update.class})
    private Integer serviceId;
    @NotEmpty(message = "ServiceName is required", groups = {Add.class, Update.class})
    private String serviceName;
    @NotEmpty(message = "ServiceDisplay is required", groups = {Add.class, Update.class})
    private String serviceDisplay;
    private String description;
    @NotNull(message = "UnitId is required", groups = {Add.class, Update.class})
    private Integer unitId;
    private Integer unitCode;
    private Integer typeId;
    private Integer calRate;
    @NotNull(message = "ServiceType is required", groups = {Add.class, Update.class})
    private Integer serviceType;
    @NotNull(message = "ThresholdType is required", groups = {Add.class, Update.class})
    private Integer thresholdType;
    @NotNull(message = "Status is required", groups = {Add.class, Update.class})
    private Integer status;
//    @NotEmpty(message = "GroupKpiCode is required", groups = {Add.class, Update.class})
//    private String groupKpiCode;
    private String fomularDescript;
    private Date updateTime;
    private String updateUser;
    private String enable;
}
