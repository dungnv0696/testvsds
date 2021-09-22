package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.UserInfoDto;
import com.lifesup.gbtd.dto.response.LoginResponseDto;
import com.lifesup.gbtd.model.NotShowIntroductionEntity;
import com.lifesup.gbtd.server.NettyServer;
import com.lifesup.gbtd.service.inteface.IAuthenticationService;
import com.lifesup.gbtd.dto.request.UserInfoRequest;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.dto.response.ResponseCommon;
import com.lifesup.gbtd.service.inteface.INotShowIntroductionService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/")
@Slf4j
public class LoginController {

    private IAuthenticationService authenticationService;
    private NettyServer nettyServer;

    @Autowired
    public LoginController(IAuthenticationService authenticationService, NettyServer nettyServer) {
        this.authenticationService = authenticationService;
        this.nettyServer = nettyServer;
    }

    @PostMapping(value = "/getUserInfo")
    public GenericResponse<UserInfoDto> getUserInfo(@RequestBody UserInfoRequest userInfoRequest) {
        return GenericResponse.success(authenticationService.getUserInfo(userInfoRequest));
    }

    @PostMapping("/login-bot")
    public ResponseCommon loginBot() {
        ResponseCommon responseCommon = new ResponseCommon();
        try {
            JSONObject response = authenticationService.callApiAuthenticateChatbot();
            String token = response.getString("content");
            responseCommon.setContent(token);
            responseCommon.setErrorMessage(Const.SUCCESS);
            responseCommon.setErrorCode(Const.ERROR_CODE.SUCCESS);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            responseCommon.setContent(null);
            responseCommon.setErrorMessage(Const.ERROR);
            responseCommon.setErrorCode(Const.ERROR_CODE.FAIL);
        }
        return responseCommon;
    }

    @GetMapping("/logout-bot")
    public ResponseCommon logoutBot(@RequestParam String token) {
        ResponseCommon responseCommon = new ResponseCommon();
        try {
            authenticationService.logoutChatBot(token);
            responseCommon.setErrorMessage(Const.SUCCESS);
            responseCommon.setErrorCode(Const.ERROR_CODE.SUCCESS);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            responseCommon.setContent(null);
            responseCommon.setErrorMessage(Const.ERROR);
            responseCommon.setErrorCode(Const.ERROR_CODE.FAIL);
        }
        return responseCommon;
    }

    @GetMapping("/login")
    public GenericResponse<LoginResponseDto> login(@RequestParam String ticket) {
        LoginResponseDto dto = authenticationService.login(ticket);
        log.info(Const.SUCCESS);
        log.info(ReflectionToStringBuilder.toString(dto));
        if (!nettyServer.isInit()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    nettyServer.run();
                }
            };
            thread.start();
            nettyServer.setInit(true);
        }

        return GenericResponse.success(dto);
    }

    @PostMapping("/change-department")
    public GenericResponse<LoginResponseDto> changeDept(@RequestBody UserInfoRequest userInfoRequest) {
        return GenericResponse.success(authenticationService.changeDept(userInfoRequest));
    }

    //xu ly truong hop F5 thi cap nhat dc list anh
    @GetMapping("/images")
    public GenericResponse<LoginResponseDto> getImages(@RequestParam Long userId) {
        LoginResponseDto dto = authenticationService.getImages(userId);
        log.info(Const.SUCCESS);
        log.info(ReflectionToStringBuilder.toString(dto));
        return GenericResponse.success(dto);
    }

}
