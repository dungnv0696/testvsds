package com.lifesup.gbtd.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "CAT_LOCATION")
public class CatLocationEntity {
    private Long locationId;
    private String locationCode;
    private String locationName;
    private String levelName;
    private String description;
    private String locationNameFull;
    private String locationIdFull;
    private Integer parentId;
    private Long locationLevel;
    private String locationAdminLevel;
    private String locationCodeFull;
    private Long countryId;
    private Long areaId;
    private Long provinceId;
    private Long districId;
    private Long villageId;
    private String countryName;
    private String areaName;
    private String provinceName;
    private String districtName;
    private String villageName;
    private String countryCode;
    private String areaCode;
    private String provinceCode;
    private String districtCode;
    private String villageCode;

    @Id
    @Column(name = "LOCATION_ID", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CAT_LOCATION_SEQ")
    @SequenceGenerator(name = "CAT_LOCATION_SEQ", sequenceName = "CAT_LOCATION_SEQ", allocationSize = 1)
    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    @Basic
    @Column(name = "LOCATION_CODE", nullable = false, length = 90)
    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    @Basic
    @Column(name = "LOCATION_NAME", nullable = false, length = 150)
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @Basic
    @Column(name = "LEVEL_NAME", nullable = true, length = 75)
    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    @Basic
    @Column(name = "DESCRIPTION", nullable = true, length = 150)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "LOCATION_NAME_FULL", nullable = true, length = 2000)
    public String getLocationNameFull() {
        return locationNameFull;
    }

    public void setLocationNameFull(String locationNameFull) {
        this.locationNameFull = locationNameFull;
    }

    @Basic
    @Column(name = "LOCATION_ID_FULL", nullable = true, length = 4000)
    public String getLocationIdFull() {
        return locationIdFull;
    }

    public void setLocationIdFull(String locationIdFull) {
        this.locationIdFull = locationIdFull;
    }

    @Basic
    @Column(name = "PARENT_ID", nullable = true, precision = 0)
    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Basic
    @Column(name = "LOCATION_LEVEL", nullable = true, precision = 0)
    public Long getLocationLevel() {
        return locationLevel;
    }

    public void setLocationLevel(Long locationLevel) {
        this.locationLevel = locationLevel;
    }

    @Basic
    @Column(name = "LOCATION_ADMIN_LEVEL", nullable = true, length = 75)
    public String getLocationAdminLevel() {
        return locationAdminLevel;
    }

    public void setLocationAdminLevel(String locationAdminLevel) {
        this.locationAdminLevel = locationAdminLevel;
    }

    @Basic
    @Column(name = "LOCATION_CODE_FULL", nullable = true, length = 4000)
    public String getLocationCodeFull() {
        return locationCodeFull;
    }

    public void setLocationCodeFull(String locationCodeFull) {
        this.locationCodeFull = locationCodeFull;
    }

    @Basic
    @Column(name = "COUNTRY_ID", nullable = true, precision = 0)
    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    @Basic
    @Column(name = "AREA_ID", nullable = true, precision = 0)
    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    @Basic
    @Column(name = "PROVINCE_ID", nullable = true, precision = 0)
    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    @Basic
    @Column(name = "DISTRIC_ID", nullable = true, precision = 0)
    public Long getDistricId() {
        return districId;
    }

    public void setDistricId(Long districId) {
        this.districId = districId;
    }

    @Basic
    @Column(name = "VILLAGE_ID", nullable = true, precision = 0)
    public Long getVillageId() {
        return villageId;
    }

    public void setVillageId(Long villageId) {
        this.villageId = villageId;
    }

    @Basic
    @Column(name = "COUNTRY_NAME", nullable = true, length = 4000)
    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Basic
    @Column(name = "AREA_NAME", nullable = true, length = 4000)
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    @Basic
    @Column(name = "PROVINCE_NAME", nullable = true, length = 4000)
    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    @Basic
    @Column(name = "DISTRICT_NAME", nullable = true, length = 4000)
    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    @Basic
    @Column(name = "VILLAGE_NAME", nullable = true, length = 4000)
    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    @Basic
    @Column(name = "COUNTRY_CODE", nullable = true, length = 4000)
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Basic
    @Column(name = "AREA_CODE", nullable = true, length = 4000)
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @Basic
    @Column(name = "PROVINCE_CODE", nullable = true, length = 4000)
    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    @Basic
    @Column(name = "DISTRICT_CODE", nullable = true, length = 4000)
    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    @Basic
    @Column(name = "VILLAGE_CODE", nullable = true, length = 4000)
    public String getVillageCode() {
        return villageCode;
    }

    public void setVillageCode(String villageCode) {
        this.villageCode = villageCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CatLocationEntity that = (CatLocationEntity) o;
        return locationId == that.locationId &&
                Objects.equals(locationCode, that.locationCode) &&
                Objects.equals(locationName, that.locationName) &&
                Objects.equals(levelName, that.levelName) &&
                Objects.equals(description, that.description) &&
                Objects.equals(locationNameFull, that.locationNameFull) &&
                Objects.equals(locationIdFull, that.locationIdFull) &&
                Objects.equals(parentId, that.parentId) &&
                Objects.equals(locationLevel, that.locationLevel) &&
                Objects.equals(locationAdminLevel, that.locationAdminLevel) &&
                Objects.equals(locationCodeFull, that.locationCodeFull) &&
                Objects.equals(countryId, that.countryId) &&
                Objects.equals(areaId, that.areaId) &&
                Objects.equals(provinceId, that.provinceId) &&
                Objects.equals(districId, that.districId) &&
                Objects.equals(villageId, that.villageId) &&
                Objects.equals(countryName, that.countryName) &&
                Objects.equals(areaName, that.areaName) &&
                Objects.equals(provinceName, that.provinceName) &&
                Objects.equals(districtName, that.districtName) &&
                Objects.equals(villageName, that.villageName) &&
                Objects.equals(countryCode, that.countryCode) &&
                Objects.equals(areaCode, that.areaCode) &&
                Objects.equals(provinceCode, that.provinceCode) &&
                Objects.equals(districtCode, that.districtCode) &&
                Objects.equals(villageCode, that.villageCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationId, locationCode, locationName, levelName, description, locationNameFull, locationIdFull, parentId, locationLevel, locationAdminLevel, locationCodeFull, countryId, areaId, provinceId, districId, villageId, countryName, areaName, provinceName, districtName, villageName, countryCode, areaCode, provinceCode, districtCode, villageCode);
    }
}
