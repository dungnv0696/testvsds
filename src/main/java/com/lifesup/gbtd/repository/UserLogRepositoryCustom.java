package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.UserLogDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;


public interface UserLogRepositoryCustom {
    Long getPercentUserActive(Long prdId);
    Page<UserLogDto> searchPersonal(UserLogDto dto, Pageable pageable);
    Page<UserLogDto> searchDept(UserLogDto dto, Pageable pageable);
    Page<UserLogDto> searchMenu(UserLogDto dto, Pageable pageable);
    Map<String, Object> searchTop(UserLogDto dto);
    Map<String, Object> getLoginLine(Long prdId);
    Map<String, Object> getLoginLineLK(Long prdId);
    Long getNumberOfUser();
    Long getNumberOfUserLK(Long prdId);
}
