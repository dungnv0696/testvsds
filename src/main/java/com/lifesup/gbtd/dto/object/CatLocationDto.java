package com.lifesup.gbtd.dto.object;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CatLocationDto {
    private Long locationId;
    private String locationCode;
    private String locationName;

    private String locationIdFull;
    private Integer parentId;
    private Long locationLevel;

    private String levelName;
    private String description;
    private String locationNameFull;

    private Long areaId;
    private Long provinceId;
    private Long districId;

    private String locationAdminLevel;
    private String locationCodeFull;
    private Long countryId;

    private String provinceName;
    private String districtName;
    private String villageName;

    private Long villageId;
    private String countryName;
    private String areaName;

    private String districtCode;
    private String villageCode;
    private Long deptLevel;

    private String countryCode;
    private String areaCode;
    private String provinceCode;
}
