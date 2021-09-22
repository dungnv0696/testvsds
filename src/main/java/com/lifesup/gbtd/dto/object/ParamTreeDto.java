package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ParamTreeDto extends BaseDto {
    private String id;
    private String code;
    private String name;

    private String image;
    private Long paramOrder;
    private String groupType;

    private String parent;
    private String typeParam;
    private Long status;

    private Date startTime;
    private Date endTime;
    private String vadility;

    private Long levelNode;
    private String subCode;
    private Date modifiedDate;

    private Long checkCase;
    private Date effTime;
    private Long deptId;

    private String fullName;
    private String oldParen;
    private int typeWarning;

    private Date parentDeptIdThthT;
    private List<String> typeParams;

    public ParamTreeDto() {
    }

    public ParamTreeDto(String id, String code, String name, String parent,
                        String typeParam, Long status, String image,
                        Long paramOrder, String groupType, Long levelNode,
                        String subCode, Date modifiedDate, Date startTime,
                        Date endTime) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.parent = parent;
        this.typeParam = typeParam;
        this.status = status;
        this.image = image;
        this.paramOrder = paramOrder;
        this.groupType = groupType;
        this.levelNode = levelNode;
        this.subCode = subCode;
        this.modifiedDate = modifiedDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public ParamTreeDto(String id, String code, String name, String parent,
                        String typeParam, Long status, String image,
                        Long paramOrder, String groupType, Long levelNode,
                        String subCode, Date modifiedDate, Date startTime,
                        Date endTime, String vadility) {
        this(id, code, name, parent, typeParam, status, image, paramOrder, groupType, levelNode, subCode,
                modifiedDate, startTime, endTime);
        this.vadility = vadility;
    }

    public ParamTreeDto(String typeParam) {
        this.typeParam = typeParam;
    }

    public ParamTreeDto(String id, String code, String name,
                        String parent, String typeParam, Long status,
                        String image, Long paramOrder, String groupType,
                        Long levelNode, String subCode, Date modifiedDate,
                        Date startTime, Date endTime, String fullName,
                        String vadility, int typeWarning, Long checkCase, Long deptId) {
        this(id, code, name, parent, typeParam, status, image, paramOrder, groupType, levelNode, subCode,
                modifiedDate, startTime, endTime, vadility);
        this.fullName = fullName;
        this.typeWarning = typeWarning;
        this.checkCase = checkCase;
        this.deptId = deptId;
    }

    public ParamTreeDto(String id, String code, String name,
                        String parent, String typeParam, Long status,
                        String image, Long paramOrder, String groupType,
                        Long levelNode, String subCode, Date modifiedDate,
                        Date startTime, Date endTime, String fullName,
                         Long deptId) {
        this(id, code, name, parent, typeParam, status, image, paramOrder, groupType, levelNode, subCode,
                modifiedDate, startTime, endTime);
        this.fullName = fullName;
//        this.typeWarning = typeWarning;
//        this.checkCase = checkCase;
        this.deptId = deptId;
    }
}
