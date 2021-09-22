package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.UsersDto;

import java.util.List;

public interface IUsersService {
    List<UsersDto> getAllUserNameAndName();
}
