package com.lifesup.gbtd.dto.object;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
public class SaveDashboardParamTreeDto {
    private List<@Valid DashboardParamTreeDto> dashboardParamTreeDtos;
    @Valid
    private CatDepartmentDto catDepartmentDto;
}
