package com.lifesup.gbtd.dto.response;

import com.lifesup.gbtd.dto.base.BaseDto;
import com.lifesup.gbtd.dto.object.UsersDto;
import com.viettel.vps.webservice.AuthorizedData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoginResponseDto extends BaseDto {
    private String token;
    private UsersDto usersInfo;
    private AuthorizedData permission;
    private boolean showIntroduction;
    private List<String> listImages;
}
