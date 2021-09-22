package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.ParamTreeDto;
import com.lifesup.gbtd.dto.object.SheetDto;
import com.lifesup.gbtd.dto.object.TempReportDto;
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
@Table(name = "DASHBOARD_PARAM_TREE")
@Getter
@Setter
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "paramTree.doSearch", classes = {
                @ConstructorResult(targetClass = ParamTreeDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = String.class),
                                @ColumnResult(name = "code", type = String.class),
                                @ColumnResult(name = "name", type = String.class),
                                @ColumnResult(name = "parent", type = String.class),
                                @ColumnResult(name = "typeParam", type = String.class),
                                @ColumnResult(name = "status", type = Long.class),
                                @ColumnResult(name = "image", type = String.class),
                                @ColumnResult(name = "paramOrder", type = Long.class),
                                @ColumnResult(name = "groupType", type = String.class),
                                @ColumnResult(name = "levelNode", type = Long.class),
                                @ColumnResult(name = "subCode", type = String.class),
                                @ColumnResult(name = "modifiedDate", type = Date.class),
                                @ColumnResult(name = "startTime", type = Date.class),
                                @ColumnResult(name = "endTime", type = Date.class),
                                @ColumnResult(name = "vadility", type = String.class)
                        })
        }),
        @SqlResultSetMapping(name = "paramTree.noValidity", classes = {
                @ConstructorResult(targetClass = ParamTreeDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = String.class),
                                @ColumnResult(name = "code", type = String.class),
                                @ColumnResult(name = "name", type = String.class),
                                @ColumnResult(name = "parent", type = String.class),
                                @ColumnResult(name = "typeParam", type = String.class),
                                @ColumnResult(name = "status", type = Long.class),
                                @ColumnResult(name = "image", type = String.class),
                                @ColumnResult(name = "paramOrder", type = Long.class),
                                @ColumnResult(name = "groupType", type = String.class),
                                @ColumnResult(name = "levelNode", type = Long.class),
                                @ColumnResult(name = "subCode", type = String.class),
                                @ColumnResult(name = "modifiedDate", type = Date.class),
                                @ColumnResult(name = "startTime", type = Date.class),
                                @ColumnResult(name = "endTime", type = Date.class),
                        })
        }),
        @SqlResultSetMapping(name = "paramTree.onlyTypeParam", classes = {
                @ConstructorResult(targetClass = ParamTreeDto.class,
                        columns = {
                                @ColumnResult(name = "typeParam", type = String.class)
                        })
        }),
        @SqlResultSetMapping(name = "paramTree.listUnit", classes = {
                @ConstructorResult(targetClass = ParamTreeDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = String.class),
                                @ColumnResult(name = "code", type = String.class),
                                @ColumnResult(name = "name", type = String.class),
                                @ColumnResult(name = "parent", type = String.class),
                                @ColumnResult(name = "typeParam", type = String.class),
                                @ColumnResult(name = "status", type = Long.class),
                                @ColumnResult(name = "image", type = String.class),
                                @ColumnResult(name = "paramOrder", type = Long.class),
                                @ColumnResult(name = "groupType", type = String.class),
                                @ColumnResult(name = "levelNode", type = Long.class),
                                @ColumnResult(name = "subCode", type = String.class),
                                @ColumnResult(name = "modifiedDate", type = Date.class),
                                @ColumnResult(name = "startTime", type = Date.class),
                                @ColumnResult(name = "endTime", type = Date.class),
                                @ColumnResult(name = "fullName", type = String.class),
                                @ColumnResult(name = "vadility", type = String.class),
                                @ColumnResult(name = "typeWarning", type = int.class),
                                @ColumnResult(name = "checkCase", type = Long.class),
                                @ColumnResult(name = "deptId", type = Long.class),
                        })
        }),
        @SqlResultSetMapping(name = "paramTree.listUnit2", classes = {
                @ConstructorResult(targetClass = ParamTreeDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = String.class),
                                @ColumnResult(name = "code", type = String.class),
                                @ColumnResult(name = "name", type = String.class),
                                @ColumnResult(name = "parent", type = String.class),
                                @ColumnResult(name = "typeParam", type = String.class),
                                @ColumnResult(name = "status", type = Long.class),
                                @ColumnResult(name = "image", type = String.class),
                                @ColumnResult(name = "paramOrder", type = Long.class),
                                @ColumnResult(name = "groupType", type = String.class),
                                @ColumnResult(name = "levelNode", type = Long.class),
                                @ColumnResult(name = "subCode", type = String.class),
                                @ColumnResult(name = "modifiedDate", type = Date.class),
                                @ColumnResult(name = "startTime", type = Date.class),
                                @ColumnResult(name = "endTime", type = Date.class),
                                @ColumnResult(name = "fullName", type = String.class),
//                                @ColumnResult(name = "vadility", type = String.class),
//                                @ColumnResult(name = "typeWarning", type = int.class),
//                                @ColumnResult(name = "checkCase", type = Long.class),
                                @ColumnResult(name = "deptId", type = Long.class),
                        })
        }),
        @SqlResultSetMapping(name = "paramTree.getSheet", classes = {
                @ConstructorResult(targetClass = SheetDto.class,
                        columns = {
                                @ColumnResult(name = "name", type = String.class),
                                @ColumnResult(name = "code", type = Long.class)
                        })
        }),
        @SqlResultSetMapping(name = "paramTree.getListTempReport", classes = {
                @ConstructorResult(targetClass = TempReportDto.class,
                        columns = {
                                @ColumnResult(name = "reportId", type = Long.class),
                                @ColumnResult(name = "customerId", type = String.class),
                                @ColumnResult(name = "reportName", type = String.class),
                                @ColumnResult(name = "createUser", type = String.class),
                                @ColumnResult(name = "reportType", type = String.class),
                                @ColumnResult(name = "type", type = String.class),
                                @ColumnResult(name = "deptParam", type = String.class),
                                @ColumnResult(name = "fromDate", type = Date.class),
                                @ColumnResult(name = "checked", type = String.class),
                                @ColumnResult(name = "ownerBy", type = String.class),
                                @ColumnResult(name = "status", type = Long.class)
                        })
        })
})
public class ParamTreeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DASHBOARD_PARAM_TREE_SEQ")
    @SequenceGenerator(name = "DASHBOARD_PARAM_TREE_SEQ", sequenceName = "DASHBOARD_PARAM_TREE_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private String id;
    @Column(name = "CODE")
    private String code;
    @Column(name = "NAME")
    private String name;
    @Column(name = "PARENT")
    private String parent;
    @Column(name = "TYPE_PARAM")
    private String typeParam;
    @Column(name = "STATUS")
    private Long status;
    @Column(name = "IMAGE")
    private String image;
    @Column(name = "PARAM_ORDER")
    private Long paramOrder;
    @Column(name = "GROUP_TYPE")
    private String groupType;
    @Column(name = "LEVEL_NODE")
    private Long levelNode;
    @Column(name = "SUB_CODE")
    private String subCode;
    @Column(name = "MODIFIED_DATE")
    private Date modifiedDate;
    @Column(name = "START_TIME")
    private Date startTime;
    @Column(name = "END_TIME")
    private Date endTime;
    @Column(name = "DEPT_ID")
    private Long deptId;
    @Column(name = "PARENT_DEPT_ID")
    private Long parentDeptId;
}
