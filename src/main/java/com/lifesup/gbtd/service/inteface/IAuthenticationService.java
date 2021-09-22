package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.AccountEndCode;
import com.lifesup.gbtd.dto.object.UserInfoDto;
import com.lifesup.gbtd.dto.request.UserInfoRequest;
import com.lifesup.gbtd.dto.response.LoginResponseDto;
import com.lifesup.gbtd.model.UsersEntity;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.Map;
import java.util.Optional;

public interface IAuthenticationService {
    LoginResponseDto login(String ticket);
    LoginResponseDto loginMobile(AccountEndCode accountEndCode);
    UserInfoDto getUserInfo(UserInfoRequest userInfoRequest);

    JSONObject callApiAuthenticateChatbot();

    Optional<UsersEntity> findByToken(String token);

    void logout();

    void logoutChatBot(String token) throws Exception;

    LoginResponseDto changeDept(UserInfoRequest userInfoRequest);

    LoginResponseDto getImages(Long userId);

    Map<String, Object> getOtp(AccountEndCode accountEndCode);
}
