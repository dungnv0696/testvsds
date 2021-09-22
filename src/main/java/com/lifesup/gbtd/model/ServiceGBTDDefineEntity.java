package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.ServiceGBTDDefineDto;
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
import java.util.Objects;

@Entity
@Table(name = "service_gbtd_define")
@Getter
@Setter
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "getServiceGBTDDefineMapping", classes = {
                @ConstructorResult(targetClass = ServiceGBTDDefineDto.class,
                        columns = {
                                @ColumnResult(name = "deptId", type = Long.class),
                                @ColumnResult(name = "defination", type = String.class),
                                @ColumnResult(name = "updateTime", type = Date.class),
                                @ColumnResult(name = "updateUser", type = String.class),
                                @ColumnResult(name = "timeTypesStr", type = String.class)
                        })
        })
})
public class ServiceGBTDDefineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SERVICE_GBTD_DEFINE_SEQ")
    @SequenceGenerator(name = "SERVICE_GBTD_DEFINE_SEQ", sequenceName = "SERVICE_GBTD_DEFINE_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;
    @Column(name = "dept_id")
    private Long deptId;
    @Column(name = "SERVICE_ID")
    private Long serviceId;
    @Column(name = "TIME_TYPE")
    private Long timeType;
    @Column(name = "DEFINATION")
    private String defination;
    @Column(name = "UPDATE_TIME")
    private Date updateTime;
    @Column(name = "UPDATE_USER")
    private String updateUser;
    @Column(name = "DEPT_CODE")
    private String deptCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceGBTDDefineEntity that = (ServiceGBTDDefineEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(deptId, that.deptId) &&
                Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(timeType, that.timeType) &&
                Objects.equals(defination, that.defination) &&
                Objects.equals(deptCode, that.deptCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, deptId, serviceId, timeType, defination, updateTime, updateUser, deptCode);
    }
}
