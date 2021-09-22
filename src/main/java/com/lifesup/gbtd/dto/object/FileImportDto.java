package com.lifesup.gbtd.dto.object;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FileImportDto<T> {
    String fileName;
    List<ExcelErrorDto> errors;
    List<T> data;
}
