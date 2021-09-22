package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.UserLogDto;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.UserLogEntity;
import com.lifesup.gbtd.repository.CatDepartmentRepository;
import com.lifesup.gbtd.repository.UserLogRepository;
import com.lifesup.gbtd.repository.UsersRepository;
import com.lifesup.gbtd.service.inteface.IUserLogService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
@Slf4j
public class UserLogService extends BaseService implements IUserLogService {

    public static final String LOGIN = "LOGIN";
    public static final String CODE_LOGIN = "code.login";

    private UserLogRepository logRep;
    private CatDepartmentRepository catRepo;
    private final UsersRepository usersRepository;

    @Autowired
    public UserLogService(UserLogRepository logRep, CatDepartmentRepository catRepo, UsersRepository usersRepository) {
        this.logRep = logRep;
        this.catRepo = catRepo;
        this.usersRepository = usersRepository;
    }

    @Override
    public UserLogDto saveLog(UserLogDto userLogDto) {
        if (userLogDto.getRequest() == null) {
            userLogDto.setRequest(getUrl());
        }
        if (!("GET".equalsIgnoreCase(userLogDto.getMethod())
                || "POST".equalsIgnoreCase(userLogDto.getMethod()))) {
            userLogDto.setMethod("UNKNOWN");
        }
        if (userLogDto.getEndPoint() == null || "".equals(userLogDto.getEndPoint())) {
            userLogDto.setEndPoint("UNKNOWN");
        }
        UserLogDto logDto = Optional.ofNullable(super.map(userLogDto, UserLogEntity.class))
                .map(entity -> {

                    entity.setStartTime(System.currentTimeMillis() + "");
                    entity.setUserId(entity.getUserId() == null ? getCurrentUserId() : entity.getUserId());
                    entity.setDeptLevel("" + getCurrentUserDeptLevel());
                    entity.setClientIp(getClientIp());
                    entity.setTimeMilis(System.currentTimeMillis() + "");
                    entity.setRequestType("MANUAL");
                    entity.setTimeViewAble(getTime("yyyy/MM/dd HH:mm:ss"));
                    entity.setPrdId(Long.valueOf(getTime("yyyyMMdd")));
                    entity.setPrdHour(getTime("yyyyMMddHH"));
                    entity.setAreaCode(catRepo.findById(getCurrentUserDeptId()).get().getCode());
                    entity.setEndTime(System.currentTimeMillis() + "");
                    if (entity.getEndPoint() != LOGIN) {
                        entity.setToken(getToken());
                    }
                    if (null == entity.getParam()) {
                        if ("GET".equalsIgnoreCase(entity.getMethod())) {
                            entity.setParam(getParamByMethodGET());
                        }
                        if ("POST".equalsIgnoreCase(entity.getMethod())) {
                            entity.setParam(objectToJson(userLogDto));
                        }
                        if ("POST".equalsIgnoreCase(entity.getMethod()) && "api/language-exchange/import".contains(entity.getRequest())) {
                            entity.setParam(getParamByMethodGET());
                        }
                    }
                    try {
                        entity.setHostIp(InetAddress.getLocalHost().getHostAddress());
                    } catch (UnknownHostException e) {
                        log.error("get host ip", e);
                    }

                    return entity;
                })
                .map(logRep::save)
                .map(entity -> UserLogService.super.map(entity, UserLogDto.class))
                .orElseThrow(() -> new ServerException(ErrorCode.FAILED));

        return logDto;
    }

    @Override
    public Map<String, Object> getPercentUserActive(String prdId) {
        Long prdIdL = Long.parseLong(prdId);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String date = prdIdL / 10000 + "/" + (prdIdL / 100) % 100 + "/" + prdIdL % 100;
        Date curDate = null;
        try {
            curDate = format.parse(date);
        } catch (Exception ex) {
            log.error("not date format", ex);
            throw new ServerException(ErrorCode.SERVER_ERROR);
        }
//        Long currentMonth = (prdIdL / 100) * 100 + 1;
        Date nextMonth = this.getNextDate(curDate);

        // get number of user logon in prdId
        Long users = logRep.getPercentUserActive(prdIdL);
        // total user
        int totalUser = usersRepository.findByCreateTimeLessThan(new java.sql.Date(nextMonth.getTime())).size();
        Map<String, Object> result = new HashMap<>();
        result.put("currentUser", users);
        result.put("total", totalUser);
        return result;
    }

    @Override
    @Transactional
    public Page<UserLogDto> doSearchPersonal(UserLogDto dto, Pageable pageable) {
//        dto.setMethod("GET");
//        dto.setEndPoint("SEARCH_PERSON TRA_CUU_TAN_SUAT_SU_DUNG");
//        dto.setTitle("Tìm kiếm theo người dùng - Tra cứu tần suất sử dụng");
//        this.saveLog(dto);
        return logRep.searchPersonal(dto, pageable);
    }

    @Override
    @Transactional
    public Page<UserLogDto> doSearchDept(UserLogDto dto, Pageable pageable) {
//        dto.setMethod("GET");
//        dto.setEndPoint("SEARCH_DEPT TRA_CUU_TAN_SUAT_SU_DUNG");
//        dto.setTitle("Tìm kiếm theo đơn vị - Tra cứu tần suất sử dụng");
//        this.saveLog(dto);
        return logRep.searchDept(dto, pageable);
    }

    @Override
    @Transactional
    public Page<UserLogDto> doSearchMenu(UserLogDto dto, Pageable pageable) {
//        dto.setMethod("GET");
//        dto.setEndPoint("SEARCH_MENU TRA_CUU_TAN_SUAT_SU_DUNG");
//        dto.setTitle("Tìm kiếm theo chức năng - Tra cứu tần suất sử dụng");
//        this.saveLog(dto);
        return logRep.searchMenu(dto, pageable);
    }

    @Override
    public Map<String, Object> doSearchTop(UserLogDto dto) {
        return logRep.searchTop(dto);
    }

    @Override
    public Map<String, Object> getLineLogin(String prdId) {
        Long prdIdL = null;
        try {
            prdIdL = Long.parseLong(prdId);
        } catch (Exception ex) {
            log.error("not date format", ex);
            throw new ServerException(ErrorCode.NOT_VALID, "prdId");
        }

        Map<String, Object> map = logRep.getLoginLine(prdIdL);
        List<UserLogDto> fullCur = new ArrayList<>();
        List<UserLogDto> fullBef = new ArrayList<>();
        List<UserLogDto> curMonth = (List<UserLogDto>) map.get("curMonth");
        List<UserLogDto> befMonth = (List<UserLogDto>) map.get("befMonth");
        int maxCurrent = Const.getMaxDateCur(prdId);
        for (int i = 0; i <= maxCurrent - 1; i++) {
            UserLogDto dto = new UserLogDto();
            dto.setTitle(MessageUtil.getMessage(CODE_LOGIN));
            dto.setTotal(0L);
            dto.setPrdId(prdIdL + i);
            dto.setEndpointCode(LOGIN);
            fullCur.add(dto);
        }
        int maxBefore = Const.getMaxDateBefore(prdIdL);
        if ("0101".equals(prdId.substring(4))){
            Long prdIdBf = Const.calculatorPrdIdBefore(prdIdL);
            for (int i = 0; i <= maxBefore - 1; i++) {
                UserLogDto dtot = new UserLogDto();
                dtot.setTitle(MessageUtil.getMessage(CODE_LOGIN));
                dtot.setTotal(0L);
                dtot.setPrdId((prdIdBf / 100 ) * 100 + i + 1);
                dtot.setEndpointCode(LOGIN);
                fullBef.add(dtot);
            }
        }else {
            for (int i = 0; i <= maxBefore - 1; i++) {
                UserLogDto dtot = new UserLogDto();
                dtot.setTitle(MessageUtil.getMessage(CODE_LOGIN));
                dtot.setTotal(0L);
                dtot.setPrdId((prdIdL / 100 - 1) * 100 + i + 1);
                dtot.setEndpointCode(LOGIN);
                fullBef.add(dtot);
            }
        }
        fullCur = setListValueUserForAllDayOfMonth(fullCur, curMonth);
        fullBef = setListValueUserForAllDayOfMonth(fullBef, befMonth);
        Map<String, Object> fullMap = new HashMap<>();
        fullMap.put("curMonth", fullCur);
        fullMap.put("befMonth", fullBef);
        return fullMap;
    }

    public List<UserLogDto> setListValueUserForAllDayOfMonth(List<UserLogDto> listFull, List<UserLogDto> list) {
        listFull.forEach(e -> {
            list.forEach(e1 -> {
                if (e.getPrdId().equals(e1.getPrdId())) {
                    e.setTotal(e1.getTotal());
                    e.setTitle(e1.getTitle());
                }
            });
        });
        return listFull;
    }

    @Override
    public Map<String, Object> getLineLoginLK(String prdId) {
        Long prdIdL = null;
        try {
            prdIdL = Long.parseLong(prdId);
        } catch (Exception ex) {
            log.error("not date format", ex);
            throw new ServerException(ErrorCode.NOT_VALID, "prdId");
        }
        Map<String, Object> map = logRep.getLoginLineLK(prdIdL);
        List<UserLogDto> curMonth = (List<UserLogDto>) map.get("curMonth");
        List<UserLogDto> befMonth = (List<UserLogDto>) map.get("befMonth");
        List<UserLogDto> fullCur = new ArrayList<>();
        List<UserLogDto> fullBef = new ArrayList<>();
        int maxCurrent = Const.getMaxDateCur(prdId);
        for (int i = 0; i <= maxCurrent - 1; i++) {
            UserLogDto dto = new UserLogDto();
            dto.setPrdId(prdIdL + i);dto.setEndpointCode(LOGIN);dto.setTitle(MessageUtil.getMessage(CODE_LOGIN));dto.setLk(0L);dto.setTotal(0L);
            fullCur.add(dto);
        }
        int maxBefore = Const.getMaxDateBefore(prdIdL);
        if ("0101".equals(prdId.substring(4))){
            Long prdIdBf = Const.calculatorPrdIdBefore(prdIdL);
            for (int i = 0; i <= maxBefore - 1; i++) {
                UserLogDto dtot = new UserLogDto();
                dtot.setPrdId((prdIdBf / 100 ) * 100 + i + 1);dtot.setEndpointCode(LOGIN);dtot.setTitle(MessageUtil.getMessage(CODE_LOGIN));dtot.setTotal(0L);dtot.setLk(0L);
                fullBef.add(dtot);
            }
        }else {
            for (int i = 0; i <= maxBefore - 1; i++) {
                UserLogDto dtot = new UserLogDto();
                dtot.setPrdId((prdIdL / 100 - 1) * 100 + i + 1);dtot.setEndpointCode(LOGIN);dtot.setTitle(MessageUtil.getMessage(CODE_LOGIN));dtot.setTotal(0L);dtot.setLk(0L);
                fullBef.add(dtot);
            }
        }
        fullCur = setListValueUserLkForAllDayOfMonth(fullCur, curMonth);
        fullBef = setListValueUserLkForAllDayOfMonth(fullBef, befMonth);
        Map<String, Object> fullMap = new HashMap<>();
        fullMap.put("curMonth", fullCur);
        fullMap.put("befMonth", fullBef);
        return fullMap;
    }

    public List<UserLogDto> setListValueUserLkForAllDayOfMonth(List<UserLogDto> listFull, List<UserLogDto> list) {
        for (int i = 0; i < listFull.size(); i++) {
            int finalI = i;
            for (UserLogDto dto : list) {
                if (listFull.get(finalI).getPrdId().equals(dto.getPrdId())) {
                    listFull.get(finalI).setTotal(dto.getTotal());
                    listFull.get(finalI).setLk(dto.getLk());
                    listFull.get(finalI).setTitle(dto.getTitle());
                    break;
                } else {
                    if (finalI != 0) {
                        listFull.get(finalI).setTotal(listFull.get(finalI - 1).getTotal());
                        listFull.get(finalI).setLk(listFull.get(finalI - 1).getLk());
                        listFull.get(finalI).setTitle(listFull.get(finalI - 1).getTitle());
                    }
                }
            }
        }
        return listFull;
    }

    @Override
    public String getTime(String pattern) {
        Instant instant = Instant.now();
        ZoneId zoneVN = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime hcm = instant.atZone(zoneVN);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(pattern);
        return hcm.format(dateFormat);
    }

    private Date getNextDate(Date curDate) {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, 1);
        return calendar.getTime();
    }

    @Override
    public Long getMaxPrdId(Long from, Long to) {
        return logRep.getMaxPrdId(from, to);
    }

    @Override
    public List<UserLogEntity> checkLoginFirstTime(Long userId) {
        return logRep.checkLoginFirstTime(userId);
    }
}
