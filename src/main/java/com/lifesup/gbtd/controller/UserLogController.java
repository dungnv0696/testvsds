package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.ConfigProfileDto;
import com.lifesup.gbtd.dto.object.UserLogDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.model.UserLogEntity;
import com.lifesup.gbtd.service.inteface.IUserLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("api/log")
@Slf4j
public class UserLogController {

    private final IUserLogService userLogService;

    @Autowired
    public UserLogController(IUserLogService userLogService) {
        this.userLogService = userLogService;
    }

    @PostMapping("/save")
    public GenericResponse<UserLogDto> saveLog(@RequestBody UserLogDto logDto){
        GenericResponse<UserLogDto> res = new GenericResponse<>();
        userLogService.saveLog(logDto);
        res.success();
        return res;
    }

    @GetMapping("/percent-user-active")
    public GenericResponse<?> getPercentUserActive(@RequestParam String prdId) throws ParseException {
        return GenericResponse.success(userLogService.getPercentUserActive(prdId));
    }

    @GetMapping("/search-personal")
    public DataListResponse<UserLogDto> doSearchPersonal(UserLogDto dto, Pageable pageable) {
        DataListResponse<UserLogDto> res = new DataListResponse<>();
        Page<UserLogDto> page = userLogService.doSearchPersonal(dto, pageable);
        res.setData(page.getContent());
        res.setPaging(page);
        return res;
    }

    @GetMapping("/search-dept")
    public DataListResponse<UserLogDto> doSearchDept(UserLogDto dto, Pageable pageable) {
        DataListResponse<UserLogDto> res = new DataListResponse<>();
        Page<UserLogDto> page = userLogService.doSearchDept(dto, pageable);
        res.setData(page.getContent());
        res.setPaging(page);
        return res;
    }

    @GetMapping("/search-menu")
    public DataListResponse<UserLogDto> doSearchMenu(UserLogDto dto, Pageable pageable) {
        DataListResponse<UserLogDto> res = new DataListResponse<>();
        Page<UserLogDto> page = userLogService.doSearchMenu(dto, pageable);
        res.setData(page.getContent());
        res.setPaging(page);
        return res;
    }

    @GetMapping("/top")
    public GenericResponse<Map> doSearchTop(UserLogDto dto) {
        GenericResponse<Map> res = new GenericResponse<>();
        res.setData(userLogService.doSearchTop(dto));
        return res;
    }

    @GetMapping("/search-log")
    public GenericResponse<Map> getLineLogin(@RequestParam String prdId) {
        GenericResponse<Map> res = new GenericResponse<>();
        res.setData(userLogService.getLineLogin(prdId));
        return res;
    }

    @GetMapping("/search-log-lk")
    public GenericResponse<Map> getLineLoginLK(@RequestParam String prdId) {
        GenericResponse<Map> res = new GenericResponse<>();
        res.setData(userLogService.getLineLoginLK(prdId));
        return res;
    }
    @GetMapping("/testparam")
    public GenericResponse getURL(@RequestParam List<String> name, @RequestParam List<Long> age) {
        GenericResponse<UserLogDto> res = new GenericResponse<>();
        UserLogDto dto = new UserLogDto();
        userLogService.saveLog(dto);
        res.success();
        return res;
    }
    @GetMapping("/maxPrdId")
    public Long getMaxPrdId(@RequestParam Long from,Long to){
        return userLogService.getMaxPrdId(from,to);
    }
}
