package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
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
import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "DASHBOARD_PARAM_TREE")
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "getDashboardParamTree.search", classes = {
                @ConstructorResult(targetClass = CatDepartmentDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "code", type = String.class),
                                @ColumnResult(name = "name", type = String.class),
                                @ColumnResult(name = "parent", type = Long.class),
                                @ColumnResult(name = "startTime", type = Date.class),
                                @ColumnResult(name = "endTime", type = Date.class)
                        })
        })
})
public class DashboardParamTreeEntity {
    private Long id;
    private String code;
    private String name;
    private String parent;
    private String typeParam;
    private Long status;
    private String image;
    private Long paramOrder;
    private String groupType;
    private Long levelNode;
    private String subCode;
    private Date modifiedDate;
    private Date startTime;
    private Date endTime;
    private Long deptId;
    private Long parentDeptId;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DASHBOARD_PARAM_TREE_SEQ")
    @SequenceGenerator(name = "DASHBOARD_PARAM_TREE_SEQ", sequenceName = "DASHBOARD_PARAM_TREE_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "CODE", nullable = true, length = 100)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "NAME", nullable = true, length = 200)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "PARENT", nullable = true, length = 100)
    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Basic
    @Column(name = "TYPE_PARAM", nullable = true, length = 50)
    public String getTypeParam() {
        return typeParam;
    }

    public void setTypeParam(String typeParam) {
        this.typeParam = typeParam;
    }

    @Basic
    @Column(name = "STATUS", nullable = true, precision = 0)
    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    @Basic
    @Column(name = "IMAGE", nullable = true, length = 200)
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Basic
    @Column(name = "PARAM_ORDER", nullable = true, precision = 0)
    public Long getParamOrder() {
        return paramOrder;
    }

    public void setParamOrder(Long paramOrder) {
        this.paramOrder = paramOrder;
    }

    @Basic
    @Column(name = "GROUP_TYPE", nullable = true, length = 50)
    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    @Basic
    @Column(name = "LEVEL_NODE", nullable = true, precision = 0)
    public Long getLevelNode() {
        return levelNode;
    }

    public void setLevelNode(Long levelNode) {
        this.levelNode = levelNode;
    }

    @Basic
    @Column(name = "SUB_CODE", nullable = true, length = 100)
    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    @Basic
    @Column(name = "MODIFIED_DATE", nullable = true)
    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Basic
    @Column(name = "START_TIME", nullable = true)
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Basic
    @Column(name = "END_TIME", nullable = true)
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Basic
    @Column(name = "DEPT_ID", nullable = true, length = 50)
    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    @Basic
    @Column(name = "PARENT_DEPT_ID", nullable = true, length = 50)
    public Long getParentDeptId() {
        return parentDeptId;
    }

    public void setParentDeptId(Long parentDeptId) {
        this.parentDeptId = parentDeptId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashboardParamTreeEntity that = (DashboardParamTreeEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(code, that.code) &&
                Objects.equals(name, that.name) &&
                Objects.equals(parent, that.parent) &&
                Objects.equals(typeParam, that.typeParam) &&
                Objects.equals(status, that.status) &&
                Objects.equals(image, that.image) &&
                Objects.equals(paramOrder, that.paramOrder) &&
                Objects.equals(groupType, that.groupType) &&
                Objects.equals(levelNode, that.levelNode) &&
                Objects.equals(subCode, that.subCode) &&
                Objects.equals(modifiedDate, that.modifiedDate) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime) &&
                Objects.equals(deptId, that.deptId) &&
                Objects.equals(parentDeptId, that.parentDeptId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, parent, typeParam, status, image, paramOrder, groupType, levelNode, subCode, modifiedDate, startTime, endTime, deptId, parentDeptId);
    }
}
