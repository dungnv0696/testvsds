package com.lifesup.gbtd.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoRequest {
    private String username;
    private String staffCode;
    private Long userId;
    private Long deptId;

    public UserInfoRequest() {}

    public UserInfoRequest(String username) {
        this.username = username;
    }
}
