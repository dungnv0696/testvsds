package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.ConfigChartDto;
import com.lifesup.gbtd.dto.object.TableDto;

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
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "CONFIG_CHART")
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "configChart.combo", classes = {
                @ConstructorResult(targetClass = ConfigChartDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "chartName", type = String.class)
                        })
        }),
        @SqlResultSetMapping(name = "configChart.tableDesc", classes = {
                @ConstructorResult(targetClass = TableDto.class,
                        columns = {
                                @ColumnResult(name = "field", type = String.class),
                                @ColumnResult(name = "type", type = String.class),
                                @ColumnResult(name = "displayName", type = String.class),
                                @ColumnResult(name = "comment", type = String.class),
                        })
        })
})
public class ConfigChartEntity {
    private Long id;
    private String chartName;
    private String titleChart;
    private String typeChart;
    private Long roleType;
    private Long timeType;
    private Long relativeTime;
    private Long unitIdView;
    private Long groupChartId;
    private String chartConfig;
    private Long chartIdNextto;
    private Long chartIdDepend;
    private Long orderIndex;
    private Long status;
    private String description;
    private Date updateTime;
    private String updateUser;
    private Long deptIdService;
    private String deptIdServices;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONFIG_CHART_SEQ")
    @SequenceGenerator(name = "CONFIG_CHART_SEQ", sequenceName = "CONFIG_CHART_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "CHART_NAME", nullable = true, length = 500)
    public String getChartName() {
        return chartName;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

    @Basic
    @Column(name = "TITLE_CHART", nullable = true, length = 500)
    public String getTitleChart() {
        return titleChart;
    }

    public void setTitleChart(String titleChart) {
        this.titleChart = titleChart;
    }

    @Basic
    @Column(name = "TYPE_CHART", nullable = true, length = 250)
    public String getTypeChart() {
        return typeChart;
    }

    public void setTypeChart(String typeChart) {
        this.typeChart = typeChart;
    }

    @Basic
    @Column(name = "ROLE_TYPE", nullable = true, precision = 0)
    public Long getRoleType() {
        return roleType;
    }

    public void setRoleType(Long roleType) {
        this.roleType = roleType;
    }

    @Basic
    @Column(name = "TIME_TYPE", nullable = true, precision = 0)
    public Long getTimeType() {
        return timeType;
    }

    public void setTimeType(Long timeType) {
        this.timeType = timeType;
    }

    @Basic
    @Column(name = "RELATIVE_TIME", nullable = true, precision = 0)
    public Long getRelativeTime() {
        return relativeTime;
    }

    public void setRelativeTime(Long relativeTime) {
        this.relativeTime = relativeTime;
    }

    @Basic
    @Column(name = "UNIT_ID_VIEW", nullable = true, precision = 0)
    public Long getUnitIdView() {
        return unitIdView;
    }

    public void setUnitIdView(Long unitIdView) {
        this.unitIdView = unitIdView;
    }

    @Basic
    @Column(name = "GROUP_CHART_ID", nullable = true, precision = 0)
    public Long getGroupChartId() {
        return groupChartId;
    }

    public void setGroupChartId(Long groupChartId) {
        this.groupChartId = groupChartId;
    }

    @Basic
    @Column(name = "CHART_CONFIG", nullable = true)
    public String getChartConfig() {
        return chartConfig;
    }

    public void setChartConfig(String chartConfig) {
        this.chartConfig = chartConfig;
    }

    @Basic
    @Column(name = "CHART_ID_NEXTTO", nullable = true, precision = 0)
    public Long getChartIdNextto() {
        return chartIdNextto;
    }

    public void setChartIdNextto(Long chartIdNextto) {
        this.chartIdNextto = chartIdNextto;
    }

    @Basic
    @Column(name = "CHART_ID_DEPEND", nullable = true, precision = 0)
    public Long getChartIdDepend() {
        return chartIdDepend;
    }

    public void setChartIdDepend(Long chartIdDepend) {
        this.chartIdDepend = chartIdDepend;
    }

    @Basic
    @Column(name = "ORDER_INDEX", nullable = true, precision = 0)
    public Long getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Long orderIndex) {
        this.orderIndex = orderIndex;
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
    @Column(name = "DESCRIPTION", nullable = true, length = 2000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "UPDATE_TIME", nullable = true)
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Basic
    @Column(name = "UPDATE_USER", nullable = true, length = 200)
    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @Basic
    @Column(name = "DEPT_ID_SERVICE", nullable = true, precision = 0)
    public Long getDeptIdService() {
        return deptIdService;
    }

    public void setDeptIdService(Long deptIdService) {
        this.deptIdService = deptIdService;
    }

    @Basic
    @Column(name = "DEPT_ID_SERVICES", nullable = true, precision = 0)
    public String getDeptIdServices() {
        return deptIdServices;
    }

    public void setDeptIdServices(String deptIdServices) {
        this.deptIdServices = deptIdServices;
    }
//
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConfigChartEntity)) {
            return false;
        }
        return id != null && id.equals(((ConfigChartEntity) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
