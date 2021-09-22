package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserLogDto extends BaseDto {
    private Long id;
    private String hostIp;
    private String method;
    private Long prdId;
    private String prdHour;
    private String timeMilis;
    private String timeViewAble;
    private Long userId;
    private String areaCode;
    private String deptLevel;
    private String appVersion;
    private String request;
    private String endPoint;
    private String title;
    private String param;
    private String startTime;
    private String endTime;
    private String clientIp;
    private String token;
    private String requestType;

    private String prdIdFrom;
    private String prdIdTo;
    // response search
    private String endpointCode;
    private Long usedNumber;
    private String email;
    private String positionName;
    private String deptName;
    private String username;

    private Long deptId;
    private List<Long> deptIds;
    private Long loginNumber;
    private Long configDashboardNumber;
    private Long configReportNumber;

    private Long userNumber;
    private Long accessNumber;
    private Long total;
    private Long lk;

    public UserLogDto(Long userId, String endpointCode, Long usedNumber, String email, String positionName,
                      String deptName, String username, String title) {
        this.userId = userId;
        this.endpointCode = endpointCode;
        this.usedNumber = usedNumber;
        this.email = email;
        this.positionName = positionName;
        this.deptName = deptName;
        this.username = username;
        this.title = title;
    }
    public UserLogDto(String method, String endPoint, String title, String param) {
        this.method = method;
        this.endPoint = endPoint;
        this.title = title;
        this.param = param;
    }

    public UserLogDto(Long userId, String email, String positionName, String deptName, Long loginNumber,
                      Long configDashboardNumber, Long configReportNumber, String title) {
        this.userId = userId;
        this.email = email;
        this.positionName = positionName;
        this.deptName = deptName;
        this.loginNumber = loginNumber;
        this.configDashboardNumber = configDashboardNumber;
        this.configReportNumber = configReportNumber;
        this.title = title;
    }

    public UserLogDto(String endpointCode, Long userNumber, Long accessNumber, String title) {
        this.endpointCode = endpointCode;
        this.userNumber = userNumber;
        this.accessNumber = accessNumber;
        this.title = title;
    }

    public UserLogDto(String title, String endpointCode, Long total) {
        this.title = title;
        this.endpointCode = endpointCode;
        this.total = total;
    }

    public UserLogDto(Long prdId, String endpointCode, Long total, String title) {
        this.prdId = prdId;
        this.endpointCode = endpointCode;
        this.total = total;
        this.title = title;
    }

    public UserLogDto(Long prdId, Long total, Long lk, String title) {
        this.prdId = prdId;
        this.total = total;
        this.lk = lk;
        this.title = title;
    }
    public UserLogDto(String method, String endPoint, String title) {
        this.method = method;
        this.endPoint = endPoint;
        this.title = title;
    }
}
