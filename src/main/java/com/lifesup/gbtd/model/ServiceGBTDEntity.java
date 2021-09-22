package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.BiTdServicesTreeDto;
import com.lifesup.gbtd.dto.object.ServiceGBTDDto;
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
@Table(name = "SERVICE_GBTD")
@Getter
@Setter
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "getServiceGBTDMapping", classes = {
                @ConstructorResult(targetClass = ServiceGBTDDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "serviceId", type = Long.class),
                                @ColumnResult(name = "serviceName", type = String.class),
                                @ColumnResult(name = "serviceDisplay", type = String.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "unitId", type = Long.class),
                                @ColumnResult(name = "unitCode", type = String.class),
                                @ColumnResult(name = "typeId", type = Long.class),
                                @ColumnResult(name = "calRate", type = Long.class),
                                @ColumnResult(name = "serviceType", type = Long.class),
                                @ColumnResult(name = "thresholdType", type = Long.class),
                                @ColumnResult(name = "status", type = Long.class),
                                @ColumnResult(name = "groupKpiCode", type = String.class),
                                @ColumnResult(name = "fomularDescript", type = String.class),
                                @ColumnResult(name = "updateTime", type = Date.class),
                                @ColumnResult(name = "updateUser", type = String.class),
                                @ColumnResult(name = "typeParam", type = String.class),
                                @ColumnResult(name = "enable", type = String.class),
                                @ColumnResult(name = "haveFormula", type = String.class),
                                @ColumnResult(name = "haveDefine", type = String.class)
                        })
        }),
        @SqlResultSetMapping(name = "serviceGbtd.getListServiceFormula", classes = {
                @ConstructorResult(targetClass = BiTdServicesTreeDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "deptCode", type = String.class),
                                @ColumnResult(name = "serviceId", type = Long.class),
                                @ColumnResult(name = "parentDeptCode", type = String.class),
                                @ColumnResult(name = "parentServiceId", type = Long.class),
                                @ColumnResult(name = "numOfDay", type = Long.class),
                                @ColumnResult(name = "parentNumOfDay", type = Long.class),
                                @ColumnResult(name = "rate", type = Float.class),
                                @ColumnResult(name = "typeCalc", type = Long.class),
                                @ColumnResult(name = "status", type = Long.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "deptId", type = Long.class),
                                @ColumnResult(name = "parentDeptId", type = Long.class),
                                @ColumnResult(name = "serviceName", type = String.class),
                                @ColumnResult(name = "fomularDescript", type = String.class),
                                @ColumnResult(name = "source", type = String.class),
                                @ColumnResult(name = "updateTime", type = Date.class),
                                @ColumnResult(name = "updateUser", type = String.class),
                                @ColumnResult(name = "typeParam", type = String.class)
                        })
        }),
        @SqlResultSetMapping(name = "serviceGbtd.findAll", classes = {
                @ConstructorResult(targetClass = ServiceGBTDDto.class,
                        columns = {
                                @ColumnResult(name = "serviceId", type = Long.class),
                                @ColumnResult(name = "serviceName", type = String.class)
                        })
        }),
        @SqlResultSetMapping(name = "serviceGbtd.targetDto", classes = {
                @ConstructorResult(targetClass = ServiceGBTDDto.class,
                        columns = {
                                @ColumnResult(name = "serviceDeptId", type = String.class),
                                @ColumnResult(name = "serviceId", type = Long.class),
                                @ColumnResult(name = "serviceName", type = String.class),
                                @ColumnResult(name = "serviceDisplay", type = String.class),
                                @ColumnResult(name = "parentServiceId", type = Long.class),
                                @ColumnResult(name = "parentId", type = String.class),
                                @ColumnResult(name = "deptId", type = Long.class),
                                @ColumnResult(name = "deptCode", type = String.class),
                                @ColumnResult(name = "hasChildren", type = Long.class),
                        })
        }),
        @SqlResultSetMapping(name = "serviceGbtd.targetDto2", classes = {
                @ConstructorResult(targetClass = ServiceGBTDDto.class,
                        columns = {
                                @ColumnResult(name = "serviceDeptId", type = String.class),
                                @ColumnResult(name = "serviceId", type = Long.class),
                                @ColumnResult(name = "serviceName", type = String.class),
                                @ColumnResult(name = "serviceDisplay", type = String.class),
                                @ColumnResult(name = "parentServiceId", type = Long.class),
                                @ColumnResult(name = "parentId", type = String.class),
                                @ColumnResult(name = "deptId", type = Long.class),
                                @ColumnResult(name = "deptCode", type = String.class),
                                @ColumnResult(name = "hasChildren", type = Long.class),
                                @ColumnResult(name = "typeParam", type = String.class),
                        })
        }),
        @SqlResultSetMapping(name = "serviceGbtd.findServiceOfDept", classes = {
                @ConstructorResult(targetClass = ServiceGBTDDto.class,
                        columns = {
                                @ColumnResult(name = "serviceId", type = Long.class),
                                @ColumnResult(name = "serviceDisplay", type = String.class),
                                @ColumnResult(name = "orderIndex", type = Long.class),
                                @ColumnResult(name = "serviceName", type = String.class),
                                @ColumnResult(name = "typeUnit", type = Long.class)
                        })
        })
})
public class ServiceGBTDEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SERVICE_GBTD_SEQ")
    @SequenceGenerator(name = "SERVICE_GBTD_SEQ", sequenceName = "SERVICE_GBTD_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;
    @Column(name = "SERVICE_ID")
    private Long serviceId;
    @Column(name = "SERVICE_NAME")
    private String serviceName;
    @Column(name = "SERVICE_DISPLAY")
    private String serviceDisplay;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "UNIT_ID")
    private Long unitId;
    @Column(name = "UNIT_CODE")
    private String unitCode;
    @Column(name = "TYPE_ID")
    private Long typeId;
    @Column(name = "CAL_RATE")
    private Long calRate;
    @Column(name = "SERVICE_TYPE")
    private Long serviceType;
    @Column(name = "THRESHOLD_TYPE")
    private Long thresholdType;
    @Column(name = "STATUS")
    private Long status;
    @Column(name = "FOMULAR_DESCRIPT")
    private String fomularDescript;
    @Column(name = "UPDATE_TIME")
    private Date updateTime;
    @Column(name = "UPDATE_USER")
    private String updateUser;
    @Column(name = "ORDER_INDEX")
    private Long orderIndex;
}
