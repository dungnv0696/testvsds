package com.lifesup.gbtd.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import com.ibm.wsdl.util.IOUtils;
import com.lifesup.gbtd.dto.base.BaseDto;
import com.lifesup.gbtd.dto.object.ActionAuditDto;
import com.lifesup.gbtd.model.ActionAuditEntity;
import com.lifesup.gbtd.model.UsersEntity;
import com.lifesup.gbtd.repository.ActionAuditRepository;
import com.lifesup.gbtd.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ActionAuditRepository actionAuditRepository;

    protected <T, E> T map(E entity, Class<T> clazz) {
        return modelMapper.map(entity, clazz);
    }

    protected <T, E extends BaseDto> T map(E dto, Class<T> clazz) {
        return modelMapper.map(dto, clazz);
    }

    protected <T, E> void mapData(T source, E destination) {
        modelMapper.map(source, destination);
    }

    protected <T, E> List<T> mapList(List<E> inputData, Class<T> clazz) {
        return CollectionUtils.isEmpty(inputData) ?
                Collections.emptyList() :
                inputData.stream()
                        .map(i -> modelMapper.map(i, clazz))
                        .collect(Collectors.toList());
    }

    protected UsersEntity getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            if (!(auth.getPrincipal() instanceof String)) {
                return (UsersEntity) auth.getPrincipal();
            }
        }

        // TODO: 10/8/2020 test only
        UsersEntity testUser = new UsersEntity();
        testUser.setId(1000000L);
        testUser.setUsername("slgb_admin_bi");
        testUser.setDeptId(254L);
        return testUser;
    }

    protected Long getCurrentUserId() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        UserEntity user = (UserEntity) auth.getCredentials();
//        return user.getId();
        return this.getCurrentUser().getId();
    }

    protected String getCurrentUsername() {
        return this.getCurrentUser().getUsername();
    }

    protected String getClientIp() {
        // TODO getUsername by Authen
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    protected String getToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ticket = request.getHeader("Authorization").substring(7);
        return ticket;
    }

    protected String getUrl() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String url = request.getRequestURI();
        return url;
    }

    protected String objectToJson(Object obj) {
        try {
            String json = new ObjectMapper().writeValueAsString(obj);
            return json;
        } catch (Exception ex) {
            log.error("object to json", ex);
            return "";
        }

    }

    protected String getParamByMethodGET() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> mapResults = new HashMap<>();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            mapResults.put(entry.getKey(), entry.getValue()[0]);
        }
        String json = null;
        try {
            json = new ObjectMapper().writeValueAsString(mapResults);
        } catch (JsonProcessingException e) {
            log.error("object to json", e);
        }
        return json;
    }
//
//    protected String getServerIp() {
//        // TODO getUsername by Authen
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        return request.getLocalAddr();
//    }
//
//    protected Integer getServerPort() {
//        // TODO getUsername by Authen
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        return request.getServerPort();
//    }

    protected Long getCurrentUserDeptId() {
        return this.getCurrentUser().getDeptId();
    }

    protected Long getCurrentUserDeptLevel() {
        return 1L;
    }

    public ActionAuditDto createLogDto(String table, String action, String id, String user, String ip,
                                       Object oldValue, Object newValue) {
        return new ActionAuditDto.Builder()
                .tableName(table)
                .action(action)
                .objectId(id)
                .user(user)
                .userIP(ip)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();
    }

    public ActionAuditDto createLogDto(String table, String action, Long id, Object oldValue, Object newValue) {
        return this.createLogDto(
                table,
                action,
                id != null ? String.valueOf(id) : null,
                this.getCurrentUsername(),
                this.getClientIp(),
                oldValue,
                newValue);
    }

    protected String toJson(Object obj) {
        return ReflectionToStringBuilder.toString(obj, ToStringStyle.JSON_STYLE);
    }

    protected ActionAuditDto.Builder defaultLogBuilder() {
        return new ActionAuditDto.Builder()
                .user(this.getCurrentUsername())
                .userIP(this.getClientIp())
                .createDate();
    }

    protected ActionAuditDto insertLog(String table, Long id, Object inserted) {
        return this.defaultLogBuilder()
                .tableName(table)
                .action(Const.ACTION.INSERT)
                .objectId(id)
                .oldValueNull()
                .newValue(inserted)
                .build();
    }

    protected ActionAuditDto deleteLog(String table, Long id, Object deleted) {
        return this.defaultLogBuilder()
                .tableName(table)
                .action(Const.ACTION.DELETE)
                .objectId(id)
                .oldValue(deleted)
                .newValueNull()
                .build();
    }

    protected ActionAuditDto updateLog(String table, Long id, Object old, Object updated) {
        return this.defaultLogBuilder()
                .tableName(table)
                .action(Const.ACTION.UPDATE)
                .objectId(id)
                .oldValue(old)
                .newValue(updated)
                .build();
    }

    protected void saveLog(List<ActionAuditDto> actionAuditDtos) {
        actionAuditRepository.saveAll(this.mapList(actionAuditDtos, ActionAuditEntity.class));
    }

    protected void saveLog(ActionAuditDto actionAuditDto) {
        actionAuditRepository.save(this.map(actionAuditDto, ActionAuditEntity.class));
    }

    protected Long prepareStatus(Long statusInput) {
        return null != statusInput ? statusInput : Const.STATUS.ACTIVE;
    }
}
