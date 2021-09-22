package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class ConfigDisplayQueryDto extends BaseDto {
    private Long id;
    private Long itemChartId;
    private String columnQuery;

    private Long status;
    private String description;
    private Date updateTime;

    private String dataType;
    private String columnChart;
    private Long isRequire;

    private List<SaveDisplayQueryValueDto> values;

    private String updateUser;

    public ConfigDisplayQueryDto() {
        values = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConfigDisplayQueryDto configDisplayQueryDTO = (ConfigDisplayQueryDto) o;
        if (configDisplayQueryDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), configDisplayQueryDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
