package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import com.lifesup.gbtd.dto.request.UserInfoRequest;
import com.lifesup.gbtd.model.CatDepartmentEntity;
import com.lifesup.gbtd.model.UsersEntity;

import java.util.List;

public interface UsersRepositoryCustom {
    List<CatDepartmentDto> getUserDepartmentInfo(Long userId);

    List<UsersEntity> findUserInfo(UserInfoRequest userInfoRequest);
}
