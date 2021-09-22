package com.lifesup.gbtd.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserData {
    private String username;
    private String fullname;
    private String mobile;

    private String staffId;
    private String staffCode;

    private String shopId;
    private String shopCode;

    private String domain;
    private String device;
    private String isChattingBot;
    private String listComponents;

    private String isLogin;
    private String subId;
    private String botId;
    private String serviceCode;
    private String groupType;
    private String userType;

    private String deptLevel;
    private String deptId;
    private String position;
    private String unit;
}
