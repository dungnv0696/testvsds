package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.ExcelErrorDto;
import com.lifesup.gbtd.dto.object.FileImportDto;
import com.lifesup.gbtd.dto.object.LanguageExchangeDto;
import org.springframework.web.multipart.MultipartFile;

public interface ILanguageExchangeService {
    String exportExcel(LanguageExchangeDto dto);
    FileImportDto<LanguageExchangeDto> importExcel(MultipartFile file, String appliedBusiness, String leeLocale);
}
