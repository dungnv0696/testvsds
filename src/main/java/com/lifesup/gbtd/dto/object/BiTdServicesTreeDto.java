package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class BiTdServicesTreeDto extends BaseDto {
    private Long id;
    @NotNull
    private String deptCode;
    @NotNull
    private Long serviceId;
    @NotNull
    private String parentDeptCode;
    @NotNull
    private Long parentServiceId;
    private Long numOfDay;
    private Long parentNumOfDay;
    @NotNull
    private Float rate;
    private Long typeCalc;
    @NotNull
    private Long status;
    private String description;
    @NotNull
    private Long deptId;
    @NotNull
    private Long parentDeptId;
    private Date updateTime;
    private String updateUser;

    private String serviceName;
    private String fomularDescript;
    private String source;
    private String typeParam;

    //kiem tra co phai chi tieu cha hay k
    private boolean parent;

    public BiTdServicesTreeDto(Long id, String deptCode, Long serviceId, String parentDeptCode, Long parentServiceId,
                               Long numOfDay, Long parentNumOfDay, Float rate, Long typeCalc, Long status,
                               String description, Long deptId, Long parentDeptId, String serviceName, String fomularDescript,
                               String source, Date updateTime, String updateUser, String typeParam) {
        this(id, deptCode, serviceId, parentDeptCode, parentServiceId, numOfDay, parentNumOfDay, rate,
                typeCalc, status, description, deptId, parentDeptId, serviceName, fomularDescript);
        this.source = source;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.typeParam = typeParam;
    }

    public BiTdServicesTreeDto(Long id, String deptCode, Long serviceId, String parentDeptCode, Long parentServiceId,
                               Long numOfDay, Long parentNumOfDay, Float rate, Long typeCalc, Long status,
                               String description, Long deptId, Long parentDeptId, String serviceName, String fomularDescript) {
        this.id = id;
        this.deptCode = deptCode;
        this.serviceId = serviceId;
        this.parentDeptCode = parentDeptCode;
        this.parentServiceId = parentServiceId;
        this.numOfDay = numOfDay;
        this.parentNumOfDay = parentNumOfDay;
        this.rate = rate;
        this.typeCalc = typeCalc;
        this.status = status;
        this.description = description;
        this.deptId = deptId;
        this.parentDeptId = parentDeptId;
        this.serviceName = serviceName;
        this.fomularDescript = fomularDescript;
    }
}
