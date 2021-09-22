package com.lifesup.gbtd.dto.object;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class UserInfoDto {

    private Date updateTime;
    private String updateUser;
    private String deptName;

    private Long id;
    private String username;
    private String name;

    private String email;
    private String staffCode;

    private Long deptId;
    private String roleCode;
    private String roleName;

    private String positionCode;
    private String positionName;
    private String imageUrl;

    private Integer status;
    private String createUser;
    private Date createTime;

    private String firstName;
    private String lastName;
    private String phone;


    // dto
    private List<CatDepartmentDto> depts;
}
