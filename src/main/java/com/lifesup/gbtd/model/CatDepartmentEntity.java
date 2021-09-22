package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "CAT_DEPARTMENT")
@Getter
@Setter
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "getDepartmentMapping", classes = {
                @ConstructorResult(targetClass = CatDepartmentDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "code", type = String.class),
                                @ColumnResult(name = "name", type = String.class),
                                @ColumnResult(name = "parent", type = Long.class),
                                @ColumnResult(name = "deptLevel", type = Long.class),
                                @ColumnResult(name = "deptLevelCodeFull", type = String.class),
                                @ColumnResult(name = "deptLevelNameFull", type = String.class),
                                @ColumnResult(name = "countryId", type = Long.class),
                                @ColumnResult(name = "provinceId", type = Long.class),
                        })
        }),
        @SqlResultSetMapping(name = "getDepartmentMapping2", classes = {
                @ConstructorResult(targetClass = CatDepartmentDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "code", type = String.class),
                                @ColumnResult(name = "name", type = String.class),
                                @ColumnResult(name = "parent", type = Long.class),
                                @ColumnResult(name = "deptLevel", type = Long.class),
                                @ColumnResult(name = "deptLevelCodeFull", type = String.class),
                                @ColumnResult(name = "deptLevelNameFull", type = String.class),
                                @ColumnResult(name = "countryId", type = Long.class),
                                @ColumnResult(name = "provinceId", type = Long.class),
                                @ColumnResult(name = "typeParam", type = String.class)
                        })
        }),
        @SqlResultSetMapping(name = "catDept.userInfoMapping", classes = {
                @ConstructorResult(targetClass = CatDepartmentDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "code", type = String.class),
                                @ColumnResult(name = "name", type = String.class),
                                @ColumnResult(name = "deptLevel", type = Long.class),
                                @ColumnResult(name = "parentId", type = Long.class),
                                @ColumnResult(name = "parentCode", type = String.class),
                                @ColumnResult(name = "countryId", type = Long.class),
                                @ColumnResult(name = "countryCode", type = String.class),
                                @ColumnResult(name = "countryName", type = String.class),
                                @ColumnResult(name = "provinceId", type = Long.class),
                                @ColumnResult(name = "provinceCode", type = String.class),
                                @ColumnResult(name = "provinceName", type = String.class),
                                @ColumnResult(name = "companyId", type = Long.class),
                                @ColumnResult(name = "companyCode", type = String.class),
                                @ColumnResult(name = "companyName", type = String.class),
                                @ColumnResult(name = "startTime", type = Date.class),
                                @ColumnResult(name = "endTime", type = Date.class)
                        })
        }),
        @SqlResultSetMapping(name = "catDept.getParamTreeDept", classes = {
                @ConstructorResult(targetClass = CatDepartmentDto.class,
                        columns = {
                                @ColumnResult(name = "deptId", type = Long.class),
                                @ColumnResult(name = "deptName", type = String.class),
                                @ColumnResult(name = "deptCode", type = String.class),
                                @ColumnResult(name = "parentCode", type = String.class),
                                @ColumnResult(name = "parentDeptId", type = Long.class),
                                @ColumnResult(name = "typeParam", type = String.class),
                                @ColumnResult(name = "startTime", type = Date.class),
                                @ColumnResult(name = "endTime", type = Date.class),
                                @ColumnResult(name = "deptLevel", type = Long.class)
                        })
        }),
        @SqlResultSetMapping(name = "getDepartmentMapping.search", classes = {
                @ConstructorResult(targetClass = CatDepartmentDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "code", type = String.class),
                                @ColumnResult(name = "name", type = String.class),
                                @ColumnResult(name = "parent", type = Long.class),
                                @ColumnResult(name = "deptLevel", type = Long.class),
                                @ColumnResult(name = "startTime", type = Date.class),
                                @ColumnResult(name = "endTime", type = Date.class),
                                @ColumnResult(name = "companyId", type = Long.class),
                                @ColumnResult(name = "companyCode", type = String.class),
                                @ColumnResult(name = "companyName", type = String.class),
                                @ColumnResult(name = "status", type = Long.class)
                        })
        })
})
public class CatDepartmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CAT_ITEM_SEQ")
    @SequenceGenerator(name = "CAT_ITEM_SEQ", sequenceName = "CAT_ITEM_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;
    @Column(name = "CODE")
    private String code;
    @Column(name = "NAME")
    private String name;
    @Column(name = "PARENT_ID")
    private Long parentId;
    @Column(name = "PARENT_CODE")
    private String parentCode;
    @Column(name = "DEPT_LEVEL")
    private Long deptLevel;
    @Column(name = "DEPT_LEVEL_CODE_FULL")
    private String deptLevelCodeFull;
    @Column(name = "DEPT_LEVEL_NAME_FULL")
    private String deptLevelNameFull;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "STATUS")
    private Long status;
    @Column(name = "COUNTRY_ID")
    private Long countryId;
    @Column(name = "COUNTRY_CODE")
    private String countryCode;
    @Column(name = "COUNTRY_NAME")
    private String countryName;
    @Column(name = "PROVINCE_ID")
    private Long provinceId;
    @Column(name = "PROVINCE_CODE")
    private String provinceCode;
    @Column(name = "PROVINCE_NAME")
    private String provinceName;
//    @Column(name = "DEPT_ID")
//    private Long deptId;
//    @Column(name = "DEPT_CODE")
//    private String deptCode;
//    @Column(name = "DEPT_NAME")
//    private String deptName;
    @Column(name = "COMPANY_ID")
    private Long companyId;
    @Column(name = "COMPANY_CODE")
    private String companyCode;
    @Column(name = "COMPANY_NAME")
    private String companyName;
    @Column(name = "UPDATE_TIME")
    private Date updateTime;
    @Column(name = "UPDATE_USER")
    private String updateUser;
    @Column(name = "START_TIME")
    private Date startTime;
    @Column(name = "END_TIME")
    private Date endTime;
}
