package com.lifesup.gbtd.dto.object;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SaveDisplayQueryDto extends ConfigDisplayQueryDto {
    public SaveDisplayQueryDto() {
        values = new ArrayList<>();
    }

    public SaveDisplayQueryDto(ConfigDisplayQueryDto dto) {
        this.setItemChartId(dto.getItemChartId());
        this.setId(dto.getId());
        this.setUpdateTime(dto.getUpdateTime());
        this.setUpdateUser(dto.getUpdateUser());
        this.setColumnChart(dto.getColumnChart());
        this.setColumnQuery(dto.getColumnQuery());
        this.setDataType(dto.getDataType());
        this.setDescription(dto.getDescription());
        this.setIsRequire(dto.getIsRequire());
        this.setStatus(dto.getStatus());
    }

    public ConfigDisplayQueryDto toDto() {
        ConfigDisplayQueryDto dto = new ConfigDisplayQueryDto();
        dto.setItemChartId(this.getItemChartId());
        dto.setId(this.getId());
        dto.setUpdateTime(this.getUpdateTime());
        dto.setUpdateUser(this.getUpdateUser());
        dto.setColumnChart(this.getColumnChart());
        dto.setColumnQuery(this.getColumnQuery());
        dto.setDataType(this.getDataType());
        dto.setDescription(this.getDescription());
        dto.setIsRequire(this.getIsRequire());
        dto.setStatus(this.getStatus());
        return dto;
    }

    private String tableName;

    private String fieldSql;

    private List<SaveDisplayQueryValueDto> values;
}
