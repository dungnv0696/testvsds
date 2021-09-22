package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.UserLogDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "USER_LOG")
@Getter
@Setter
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "userLogDto", classes = {
                @ConstructorResult(targetClass = UserLogDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "hostIp", type = String.class),
                                @ColumnResult(name = "method", type = String.class),
                                @ColumnResult(name = "prdId", type = String.class),
                                @ColumnResult(name = "prdHour", type = String.class),
                                @ColumnResult(name = "timeMilis", type = String.class),
                                @ColumnResult(name = "timeViewAble", type = String.class),
                                @ColumnResult(name = "areaCode", type = String.class),
                                @ColumnResult(name = "deptLevel", type = String.class),
                                @ColumnResult(name = "appVersion", type = String.class),
                                @ColumnResult(name = "request", type = String.class),
                                @ColumnResult(name = "endPoint", type = String.class),
                                @ColumnResult(name = "title", type = String.class),
                                @ColumnResult(name = "param", type = String.class),
                                @ColumnResult(name = "startTime", type = String.class),
                                @ColumnResult(name = "endTime", type = String.class),
                                @ColumnResult(name = "clientIp", type = String.class),
                                @ColumnResult(name = "token", type = String.class),
                                @ColumnResult(name = "requestType", type = String.class),

                        })
        }),

        @SqlResultSetMapping(name = "searchPersonal", classes = {
                @ConstructorResult(targetClass = UserLogDto.class,
                        columns = {
                                @ColumnResult(name = "userId", type = Long.class),
                                @ColumnResult(name = "endpointCode", type = String.class),
                                @ColumnResult(name = "usedNumber", type = Long.class),
                                @ColumnResult(name = "email", type = String.class),
                                @ColumnResult(name = "positionName", type = String.class),
                                @ColumnResult(name = "deptName", type = String.class),
                                @ColumnResult(name = "username", type = String.class),
                                @ColumnResult(name = "title", type = String.class),

                        })
        }),
        @SqlResultSetMapping(name = "searchDept", classes = {
                @ConstructorResult(targetClass = UserLogDto.class,
                        columns = {
                                @ColumnResult(name = "userId", type = Long.class),
                                @ColumnResult(name = "email", type = String.class),
                                @ColumnResult(name = "positionName", type = String.class),
                                @ColumnResult(name = "deptName", type = String.class),
                                @ColumnResult(name = "loginNumber", type = Long.class),
                                @ColumnResult(name = "configDashboardNumber", type = Long.class),
                                @ColumnResult(name = "configReportNumber", type = Long.class),
                                @ColumnResult(name = "title", type = String.class),

                        })
        }),
        @SqlResultSetMapping(name = "searchMenu", classes = {
                @ConstructorResult(targetClass = UserLogDto.class,
                        columns = {
                                @ColumnResult(name = "endpointCode", type = String.class),
                                @ColumnResult(name = "userNumber", type = Long.class),
                                @ColumnResult(name = "accessNumber", type = Long.class),
                                @ColumnResult(name = "title", type = String.class)
                        })
        }),
        @SqlResultSetMapping(name = "searchTop", classes = {
                @ConstructorResult(targetClass = UserLogDto.class,
                        columns = {
                                @ColumnResult(name = "title", type = String.class),
                                @ColumnResult(name = "endpointCode", type = String.class),
                                @ColumnResult(name = "total", type = Long.class),
                        })
        }),
        @SqlResultSetMapping(name = "getLoginLine", classes = {
                @ConstructorResult(targetClass = UserLogDto.class,
                        columns = {
                                @ColumnResult(name = "prdId", type = Long.class),
                                @ColumnResult(name = "endpointCode", type = String.class),
                                @ColumnResult(name = "total", type = Long.class),
                                @ColumnResult(name = "title", type = String.class),
                        })
        }),
        @SqlResultSetMapping(name = "getLoginLineLK", classes = {
                @ConstructorResult(targetClass = UserLogDto.class,
                        columns = {
                                @ColumnResult(name = "prdId", type = Long.class),
                                @ColumnResult(name = "total", type = Long.class),
                                @ColumnResult(name = "lk", type = Long.class),
                                @ColumnResult(name = "title", type = String.class)
                        })
        }),
})
public class UserLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_LOG_SEQ")
    @SequenceGenerator(name = "USER_LOG_SEQ", sequenceName = "USER_LOG_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;
    @Column(name = "HOST_IP")
    private String hostIp;
    @Column(name = "METHOD")
    private String method;
    @Column(name = "PRD_ID")
    private Long prdId;
    @Column(name = "PRD_HOUR")
    private String prdHour;
    @Column(name = "TIME_MILIS")
    private String timeMilis;
    @Column(name = "TIME_VIEWABLE")
    private String timeViewAble;
    @Column(name = "USER_ID")
    private Long userId;
    @Column(name = "AREA_CODE")
    private String areaCode;
    @Column(name = "DEPT_LEVEL")
    private String deptLevel;
    @Column(name = "APP_VERSION")
    private String appVersion;
    @Column(name = "REQUEST")
    private String request;
    @Column(name = "ENDPOINT_CODE")
    private String endPoint;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "PARAM")
    private String param;
    @Column(name = "START_TIME")
    private String startTime;
    @Column(name = "END_TIME")
    private String endTime;
    @Column(name = "CLIENT_IP")
    private String clientIp;
    @Column(name = "TOKEN")
    private String token;
    @Column(name = "REQUEST_TYPE")
    private String requestType;

}
