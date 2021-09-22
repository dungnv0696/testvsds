package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CatDepartmentDto extends BaseDto {
    private Long id;
    @NotEmpty
    private String code;
    @NotEmpty
    private String name;
    private Long parentId;
    private String parentCode;
    @NotNull
    private Long deptLevel;
    private String deptLevelCodeFull;
    private String deptLevelNameFull;
    private String description;
    @NotNull
    private Long status;
    private Long countryId;
    private String countryCode;
    private String countryName;
    private Long provinceId;
    private String provinceCode;
    private String provinceName;
//    private Long deptId;
//    private String deptCode;
//    private String deptName;
    private Long companyId;
    private String companyCode;
    private String companyName;
    private Date updateTime;
    private String updateUser;
    private Date startTime;
    private Date endTime;

    private List<Long> deptLevels;
    // parentCode, use in tree
    private Long parent;
    private Long deptId;
    private String deptCode;

    private String deptName;
    private Long parentDeptId;
    private String typeParam;

    public CatDepartmentDto(Long deptId,
                            String deptName,
                            String deptCode,
                            String parentCode,
                            Long parentDeptId,
                            String typeParam,
                            Date startTime,
                            Date endTime,
                            Long deptLevel) {
        this.deptId = deptId;
        this.deptName = deptName;
        this.deptCode = deptCode;
        this.parentCode = parentCode;
        this.parentDeptId = parentDeptId;
        this.typeParam = typeParam;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deptLevel = deptLevel;
    }

    //locationId in cat_location
    private Long locationId;
    private CatLocationDto locationDto;
    private CatDepartmentDto parentDepartmentDto;

    public CatDepartmentDto(Long id, String code, String name, Long parent, Long deptLevel,
                            String deptLevelCodeFull, String deptLevelNameFull, Long countryId, Long provinceId,String typeParam) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.parent = parent;
        this.deptLevel = deptLevel;
        this.deptLevelCodeFull = deptLevelCodeFull;
        this.deptLevelNameFull = deptLevelNameFull;
        this.countryId = countryId;
        this.provinceId = provinceId;
        this.typeParam = typeParam;
    }
    public CatDepartmentDto(Long id, String code, String name, Long parent, Long deptLevel,
                            String deptLevelCodeFull, String deptLevelNameFull, Long countryId, Long provinceId) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.parent = parent;
        this.deptLevel = deptLevel;
        this.deptLevelCodeFull = deptLevelCodeFull;
        this.deptLevelNameFull = deptLevelNameFull;
        this.countryId = countryId;
        this.provinceId = provinceId;
    }

    public CatDepartmentDto(Long id,
                            String code,
                            String name,
                            Long deptLevel,
                            Long parentId,
                            String parentCode,
                            Long countryId,
                            String countryCode,
                            String countryName,
                            Long provinceId,
                            String provinceCode,
                            String provinceName,
                            Long companyId,
                            String companyCode,
                            String companyName,
                            Date startTime,
                            Date endTime) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.parentId = parentId;
        this.parentCode = parentCode;
        this.deptLevel = deptLevel;
        this.countryId = countryId;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.provinceId = provinceId;
        this.provinceCode = provinceCode;
        this.provinceName = provinceName;
        this.companyId = companyId;
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public CatDepartmentDto(Long id, String code, String name, Long parent, Long deptLevel, Date startTime, Date endTime,
                            Long companyId, String companyCode, String companyName, Long status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.parent = parent;
        this.deptLevel = deptLevel;
        this.startTime = startTime;
        this.endTime = endTime;
        this.companyId = companyId;
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.status = status;
    }

    public CatDepartmentDto(Long id, String code, String name, Long parent, Date startTime, Date endTime) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.parent = parent;
        this.startTime = startTime;
        this.endTime = endTime;
    }

//    public Long getParent() {
//        return parent;
//    }
}
