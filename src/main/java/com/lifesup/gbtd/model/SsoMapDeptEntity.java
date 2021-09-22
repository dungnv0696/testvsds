package com.lifesup.gbtd.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "SSO_MAP_DEPT")
public class SsoMapDeptEntity {
    private Long id;
    private Long ssoDeptId;
    private Long mapDeptId;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SSO_MAP_DEPT_SEQ")
    @SequenceGenerator(name = "SSO_MAP_DEPT_SEQ", sequenceName = "SSO_MAP_DEPT_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "SSO_DEPT_ID", nullable = true, precision = 0)
    public Long getSsoDeptId() {
        return ssoDeptId;
    }

    public void setSsoDeptId(Long ssoDeptId) {
        this.ssoDeptId = ssoDeptId;
    }

    @Basic
    @Column(name = "MAP_DEPT_ID", nullable = true, precision = 0)
    public Long getMapDeptId() {
        return mapDeptId;
    }

    public void setMapDeptId(Long mapDeptId) {
        this.mapDeptId = mapDeptId;
    }
}
