package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.ServicesMapDeptDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

@Entity
@Table(name = "SERVICES_MAP_DEPT")
@Getter
@Setter
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "findWithServiceIdsMapping", classes = {
                @ConstructorResult(targetClass = ServicesMapDeptDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "serviceId", type = Long.class),
                                @ColumnResult(name = "deptId", type = Long.class)
                        })
        })
})
@NoArgsConstructor
public class ServicesMapDeptEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SERVICES_MAP_DEPT_SEQ")
    @SequenceGenerator(name = "SERVICES_MAP_DEPT_SEQ", sequenceName = "SERVICES_MAP_DEPT_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;
    @Column(name = "SERVICE_ID")
    private Long serviceId;
    @Column(name = "DEPT_ID")
    private Long deptId;
    @Column(name = "SOURCE")
    private String source;
    @Column(name = "FOMULAR_DESCRIPT")
    private String fomularDescript;
    @Column(name = "GROUP_KPI_CODE")
    private String groupKpiCode;

    public ServicesMapDeptEntity(Long serviceId, Long deptId, String groupKpiCode) {
        this.serviceId = serviceId;
        this.deptId = deptId;
        this.groupKpiCode = groupKpiCode;
    }
}
