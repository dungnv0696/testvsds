package com.lifesup.gbtd.dto.response;

import com.lifesup.gbtd.dto.base.BaseDto;
import com.lifesup.gbtd.dto.object.ChartDetailDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DataLookupResponse extends BaseDto {
    private Map<String, Object> filterParams;
    private List<ChartDetailDto> details;
}
