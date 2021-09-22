package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.AccountEndCode;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.dto.response.LoginResponseDto;
import com.lifesup.gbtd.service.inteface.IAuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/mobile/")
@Slf4j
public class LoginMobileController {
    private IAuthenticationService authenticationService;

    @Autowired
    public LoginMobileController(IAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    @PostMapping("/login")
    public GenericResponse<LoginResponseDto> loginMobile(@RequestBody AccountEndCode accountEndCode) {
        LoginResponseDto rs = authenticationService.loginMobile(accountEndCode);
        if(rs != null){
            return GenericResponse.success(rs);
        }else{
            return (GenericResponse<LoginResponseDto>) GenericResponse.build(ErrorCode.FAILED);
        }

    }
    @GetMapping("/request-otp")
    public GenericResponse<Map> getOtp(@RequestBody AccountEndCode accountEndCode) {
         Map<String, Object> rs = authenticationService.getOtp(accountEndCode);
        return GenericResponse.success(rs);
    }
}
