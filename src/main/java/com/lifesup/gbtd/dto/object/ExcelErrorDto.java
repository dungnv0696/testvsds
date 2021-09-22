package com.lifesup.gbtd.dto.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExcelErrorDto {
    private String lineError;
    private String columnError;
    private String detailError;
}
