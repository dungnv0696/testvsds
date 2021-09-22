package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.LanguageExchangeDto;

import java.util.List;

public interface LanguageExchangeRepositoryCustom {
    List<LanguageExchangeDto> getLanguageExchanges(LanguageExchangeDto dto);
    List checkExistData(String tableName, String column, String value);
}
