package com.lifesup.gbtd.dto.object;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UsersDto extends BaseDto {
    private Long id;
    private String username;
    private String name;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String staffCode;
    private String positionCode;
    private String positionName;
    private String imageUrl;
    private Long deptId;
    private String roleCode;
    private String roleName;
    private Integer status;
    private String createUser;
    private Date createTime;
    private Date updateTime;
    private String updateUser;
    private String deptName;

    // dto
    private List<CatDepartmentDto> depts;

    public UsersDto(Long id, String username, String name, String firstName, String lastName, String phone, String email,
                    String staffCode, String positionCode, String positionName, String imageUrl, Long deptId,
                    String roleCode, String roleName, Integer status, String createUser, Date createTime,
                    Date updateTime, String updateUser) {
        this.id = id;
        this.username = username;
        this.name = name;

        this.email = email;
        this.staffCode = staffCode;
        this.positionCode = positionCode;

        this.roleCode = roleCode;
        this.roleName = roleName;
        this.status = status;

        this.positionName = positionName;
        this.imageUrl = imageUrl;
        this.deptId = deptId;

        this.createUser = createUser;
        this.createTime = createTime;

        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;

        this.updateTime = updateTime;
        this.updateUser = updateUser;
    }

    public UsersDto(String username, String name) {
        this.username = username;
        this.name = name;
    }
}
