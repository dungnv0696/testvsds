package com.lifesup.gbtd.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
public class BiTdServiceTreeId implements Serializable {

    private Long serviceId;
    private Long parentServiceId;
    private Long deptId;

    public BiTdServiceTreeId(Long serviceId, Long parentServiceId, Long deptId) {
        this.serviceId = serviceId;
        this.parentServiceId = parentServiceId;
        this.deptId = deptId;
    }
}
