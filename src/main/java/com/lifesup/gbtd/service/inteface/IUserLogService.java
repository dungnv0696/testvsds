package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.UserLogDto;
import com.lifesup.gbtd.model.UserLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface IUserLogService {
    UserLogDto saveLog(UserLogDto log);
    Map<String, Object> getPercentUserActive(String prdId) throws ParseException;
    Page<UserLogDto> doSearchPersonal(UserLogDto dto, Pageable pageable);
    Page<UserLogDto> doSearchDept(UserLogDto dto, Pageable pageable);
    Page<UserLogDto> doSearchMenu(UserLogDto dto, Pageable pageable);
    Map<String, Object> doSearchTop(UserLogDto dto);
    Map<String, Object> getLineLogin(String prdId);
    Map<String, Object> getLineLoginLK(String prdId);
    Long getMaxPrdId(Long from, Long to);
    String getTime(String pattern);
    List<UserLogEntity> checkLoginFirstTime(Long userId);
}
