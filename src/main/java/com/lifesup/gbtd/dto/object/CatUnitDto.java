package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Date;

@Getter
@Setter
public class CatUnitDto extends BaseDto {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Long status;
    private Date updateTime;
    private String updateUser;
    private String nameDisplay;
    private Long typeUnit;
}
