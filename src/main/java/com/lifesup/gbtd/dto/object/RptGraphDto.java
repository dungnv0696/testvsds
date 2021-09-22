package com.lifesup.gbtd.dto.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Table;
import java.sql.Time;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RptGraphDto {
    private Long prdId;
    private Long inputLevel;
    private Long serviceId;
    private String serviceName;
    private String objCode;
    private String objName;
    private String parentCode;
    private String parentName;
    private String companyCode;
    private String companyName;
    private String provinceCode;
    private String provinceName;
    private String districtCode;
    private String districtName;
    private Long unitId;
    private String unitCode;
    private Long plan;
    private Long planAcc;
    private Long val;
    private Long valAcc;
    private Long pPlan;
    private Long pPlanAcc;
    private Long lastVal;
    private Long lastValAcc;
    private Long lastValYear;
    private Long lastValAccYear;
    private Long lastPPlanAcc;
    private Long lastPPlan;
    private Long lastPPlanAccYear;
    private Long lastPPlanYear;
    private Long delVal;
    private Long delValAcc;
    private Long delValYear;
    private Long delValAccYear;
    private Long delPPlanAcc;
    private Long delPPlan;
    private Long delPPlanAccYear;
    private Long delPPlanYear;
    private Long pGrowVal;
    private Long pGrowValAcc;
    private Long pGrowValYear;
    private Long pGrowValAccYear;
    private Long pGrowPAcc;
    private Long pGrowPPlan;
    private Long pGrowPAccYear;
    private Long pGrowPPlanYear;
    private Long alarmLvPPlanAcc;
    private Long alarmLvPPlan;
    private Long alarmLvGrowVal;
    private Long alarmLvGrowAcc;
    private Long alarmLvGrowValY;
    private Long alarmLvGrowAccY;
    private Long alarmLvGrowPAcc;
    private Long alarmLvGrowPPlan;
    private Long alarmLvGrowPAccY;
    private Long alarmLvGrowPPlanY;
    private Long rankPPlan1;
    private Long rankPPlan2;
    private Long rankPPlanAcc1;
    private Long rankPPlanAcc2;
    private Long rankPGrowVal1;
    private Long rankPGrowVal2;
    private Long rankPGrowAcc1;
    private Long rankPGrowAcc2;
    private Long rankPGrowValY1;
    private Long rankPGrowValY2;
    private Long rankPGrowAccY1;
    private Long rankPGrowAccY2;
    private Long rankPGrowPAcc1;
    private Long rankPGrowPAcc2;
    private Long rankPGrowPPlan1;
    private Long rankPGrowPPlan2;
    private Long rankPGrowPAccY1;
    private Long rankPGrowPAccY2;
    private Long rankPGrowPPlanY1;
    private Long rankPGrowPPlanY2;
    private Time updateTime;
}