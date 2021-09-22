package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.validator.FieldValue;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Find;
import com.lifesup.gbtd.validator.group.Update;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ServiceGBTDDto extends BaseDto {
    @NotNull(groups = {Update.class})
    private Long id;
    @NotNull(message = "ServiceId is required", groups = {Add.class, Update.class})
    private Long serviceId;
    @NotEmpty(message = "ServiceName is required", groups = {Add.class, Update.class})
    private String serviceName;
    @NotEmpty(message = "ServiceDisplay is required", groups = {Add.class, Update.class})
    private String serviceDisplay;
    private String description;
    @NotNull(message = "UnitId is required", groups = {Add.class, Update.class})
    private Long unitId;
    private String unitCode;
    private Long typeId;
    private Long calRate;
    @NotNull(message = "ServiceType is required", groups = {Add.class, Update.class})
    private Long serviceType;
    @NotNull(message = "ThresholdType is required", groups = {Add.class, Update.class})
    private Long thresholdType;
    @NotNull(message = "Status is required", groups = {Add.class, Update.class})
    private Long status;
    @NotEmpty(message = "GroupKpiCode is required", groups = {Add.class, Update.class})
    private String groupKpiCode;
    private String fomularDescript;
    private Date updateTime;
    private String updateUser;
    private String typeParam;
    private Long orderIndex;
    private Long typeUnit;

    private String enable;
    private String haveFormula;
    private String haveDefine;

    private List<Long> deptIdsDelete;
    private List<Long> deptIdsAdd;
    private List<String> typeParams;

    // Dept Id of table services_map_dept
//    @NotNull(message = "DeptId is required", groups = {Add.class, Update.class})
    private Long deptId;
    @NotNull(message = "DeptIds is required", groups = {Add.class, Update.class})
    private List<Long> deptIds;

    // dto only
    private Long parentServiceId;
    private Long rate;
    private Long treeStatus;
    private String treeDescription;
    private String source;
    private Long numOfDay;
    private Long parentNumOfDay;
    private Long typeCalc;
    private Long parentDeptId;
    private String parentDeptCode;
    private String deptCode;

    @NotNull(groups = {Find.class})
    @FieldValue(numbers = {Const.SERVICE_TYPE.CT_KINHDOANH, Const.SERVICE_TYPE.CT_NGUOIDUNG}, groups = Find.class)
    public Integer checkServiceType;
    public boolean hasChildren;
    public String serviceDeptId;
    public String parentId;
    private List<ServicesMapDeptDto> kpiMapDept;
    private String unitName;
    private String deptIdSearch;

    public ServiceGBTDDto() {
    }

    public ServiceGBTDDto(Long id, Long serviceId, String serviceName, String serviceDisplay, String description,
                          Long unitId, String unitCode, Long typeId, Long calRate,
                          Long serviceType, Long thresholdType, Long status) {
        this.id = id;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceDisplay = serviceDisplay;
        this.description = description;
        this.unitId = unitId;
        this.unitCode = unitCode;
        this.typeId = typeId;
        this.calRate = calRate;
        this.serviceType = serviceType;
        this.thresholdType = thresholdType;
        this.status = status;
    }

    public ServiceGBTDDto(Long id, Long serviceId, String serviceName, String serviceDisplay, String description,
                          Long unitId, String unitCode, Long typeId, Long calRate,
                          Long serviceType, Long thresholdType, Long status, String groupKpiCode,
                          String fomularDescript, Date updateTime, String updateUser, String typeParam,
                          String enable, String haveFormula, String haveDefine) {
        this(id, serviceId, serviceName, serviceDisplay, description, unitId, unitCode, typeId, calRate, serviceType,
                thresholdType, status);
        this.groupKpiCode = groupKpiCode;
        this.fomularDescript = fomularDescript;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.typeParam = typeParam;
        this.enable = enable;
        this.haveFormula = haveFormula;
        this.haveDefine = haveDefine;
    }

    public ServiceGBTDDto(Long id, Long serviceId, String serviceName,
                          String serviceDisplay, String description, Long unitId,
                          String unitCode, Long typeId, Long calRate,
                          Long serviceType, Long thresholdType, Long status,
//                          String groupKpiCode, String fomularDescript, Date updateTime,
                          String fomularDescript, Date updateTime,
                          String updateUser, Long deptId, Long parentServiceId,
                          Long rate, Long treeStatus, String treeDescription, String source) {
        this(id, serviceId, serviceName, serviceDisplay, description, unitId, unitCode, typeId, calRate, serviceType,
                thresholdType, status);
//        this.groupKpiCode = groupKpiCode;
        this.fomularDescript = fomularDescript;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.deptId = deptId;
        this.parentServiceId = parentServiceId;
        this.rate = rate;
        this.treeStatus = treeStatus;
        this.treeDescription = treeDescription;
        this.source = source;
    }

    public ServiceGBTDDto(Long serviceId, String serviceName) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
    }

    public ServiceGBTDDto(Long parentServiceId, Long serviceId, String serviceName) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.parentServiceId = parentServiceId;
    }

    public ServiceGBTDDto(Long id, String serviceDisplay, String description, Long unitId, String unitCode, Long typeId,
                          Long calRate, Long serviceType, Long thresholdType, Long status, String fomularDescript,
                          Date updateTime, String updateUser, Long serviceId, Long parentServiceId, Long deptId,
                          Long numOfDay, Long parentNumOfDay, Long rate, Long typeCalc, Long treeStatus, String treeDescription,
                          Long parentDeptId, String parentDeptCode, String deptCode) {
        this.id = id;
        this.serviceDisplay = serviceDisplay;
        this.description = description;
        this.unitId = unitId;
        this.unitCode = unitCode;
        this.typeId = typeId;
        this.calRate = calRate;
        this.serviceType = serviceType;
        this.thresholdType = thresholdType;
        this.status = status;
        this.fomularDescript = fomularDescript;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.serviceId = serviceId;
        this.parentServiceId = parentServiceId;
        this.deptId = deptId;
        this.numOfDay = numOfDay;
        this.parentNumOfDay = parentNumOfDay;
        this.rate = rate;
        this.typeCalc = typeCalc;
        this.treeStatus = treeStatus;
        this.treeDescription = treeDescription;
        this.parentDeptId = parentDeptId;
        this.parentDeptCode = parentDeptCode;
        this.deptCode = deptCode;
    }

    public ServiceGBTDDto(Long serviceId) {
        this.serviceId = serviceId;
    }

    public ServiceGBTDDto(Long serviceId, Long deptId) {
        this.serviceId = serviceId;
        this.deptId = deptId;
    }

    public ServiceGBTDDto(String serviceDeptId, Long serviceId, String serviceName, String serviceDisplay,
                          Long parentServiceId, String parentId, Long deptId, String deptCode, Long hasChildren) {
        this.serviceDeptId = serviceDeptId;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceDisplay = serviceDisplay;
        this.parentServiceId = parentServiceId;
        this.parentId = parentId;
        this.deptId = deptId;
        this.deptCode = deptCode;
        this.hasChildren = hasChildren > 0;
    }
    public ServiceGBTDDto(String serviceDeptId, Long serviceId, String serviceName, String serviceDisplay,
                          Long parentServiceId, String parentId, Long deptId, String deptCode, Long hasChildren,String typeParam) {
        this.serviceDeptId = serviceDeptId;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceDisplay = serviceDisplay;
        this.parentServiceId = parentServiceId;
        this.parentId = parentId;
        this.deptId = deptId;
        this.deptCode = deptCode;
        this.hasChildren = hasChildren > 0;
        this.typeParam = typeParam;
    }

    public ServiceGBTDDto(Long serviceId, String serviceDisplay, Long orderIndex, String serviceName, Long typeUnit) {
        this.serviceId = serviceId;
        this.serviceDisplay = serviceDisplay;
        this.orderIndex = orderIndex;
        this.serviceName = serviceName;
        this.typeUnit = typeUnit;
    }
}
