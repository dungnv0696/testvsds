package com.lifesup.gbtd.dto.object;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CatThresholdDto {
    private Long id;
    private String groupType;
    private Long alarmLevelId;

    private Long status;
    private String description;
    private Date updateTime;

    private Long thresholdType;
    private Long fromValue;
    private Long toValue;

    private String updateUser;
}
