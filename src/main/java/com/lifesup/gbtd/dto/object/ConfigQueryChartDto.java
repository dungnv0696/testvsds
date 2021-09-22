package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfigQueryChartDto extends BaseDto {
    private Long id;
    private String queryData;
    private String queryMaxPrdId;
    private String defaultValue;
    private Long status;
    private String description;
    private Date updateTime;
    private String updateUser;

    //dto only
    private List<Object> data;
    private Map<String, Object> params;

    public ConfigQueryChartDto(Long id, String queryData, String queryMaxPrdId, String defaultValue, Long status,
                               String description, Date updateTime, String updateUser) {
        this.id = id;
        this.queryData = queryData;
        this.queryMaxPrdId = queryMaxPrdId;
        this.defaultValue = defaultValue;
        this.status = status;
        this.description = description;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConfigQueryChartDto configQueryChartDTO = (ConfigQueryChartDto) o;
        if (configQueryChartDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), configQueryChartDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
