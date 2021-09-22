package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.UsersDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.service.inteface.IUsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UsersController {

    private final IUsersService usersService;

    @Autowired
    public UsersController(IUsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/get-for-combo")
    public DataListResponse<UsersDto> getAllUserNameAndName() {
        DataListResponse<UsersDto> res = new DataListResponse<>();
        res.setData(usersService.getAllUserNameAndName());
        return res;
    }
}
