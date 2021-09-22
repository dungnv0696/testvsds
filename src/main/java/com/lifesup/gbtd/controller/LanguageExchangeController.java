package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.FileImportDto;
import com.lifesup.gbtd.dto.object.LanguageExchangeDto;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.service.inteface.ILanguageExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/language-exchange")
@Slf4j
public class LanguageExchangeController {

    private final ILanguageExchangeService languageExchangeService;

    @Autowired
    public LanguageExchangeController(ILanguageExchangeService languageExchangeService) {
        this.languageExchangeService = languageExchangeService;
    }

    @PostMapping("/export")
    public GenericResponse<String> doExport(@RequestBody LanguageExchangeDto dto) {
        return GenericResponse.success(languageExchangeService.exportExcel(dto));
    }

    @PostMapping("/import")
    public GenericResponse<FileImportDto> doImport(@RequestParam("file") MultipartFile file,
                                                   @RequestParam("appliedBusiness") String appliedBusiness,
                                                   @RequestParam("leeLocale") String leeLocale) {
        return GenericResponse.success(languageExchangeService.importExcel(file, appliedBusiness, leeLocale));
    }
}
