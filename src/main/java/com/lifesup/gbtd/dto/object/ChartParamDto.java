package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ChartParamDto extends BaseDto {
    private List<String> kpiIds;
    private String timeType;
    private String inputLevel;
    private String fromDate;
    private String toDate;
    private String prdId;
    private String objectCode;
    private List<String> objOrParentCode;
    private String tableName;
    private List<String> deptCodes;
    private List<String> branchCodes;
    private Long rowNum;
    //drill down bieu do
    private String drillDown;


    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (!DataUtil.isNullOrEmpty(kpiIds))
            map.put(Const.FILTER_PARAMS.KPI_IDS_PARAM, kpiIds);
        if (StringUtils.isNotEmpty(timeType))
            map.put(Const.FILTER_PARAMS.TIME_TYPE_PARAM, timeType);
        if (StringUtils.isNotEmpty(inputLevel))
            map.put(Const.FILTER_PARAMS.INPUT_LEVEL_PARAM, inputLevel);
        if (StringUtils.isNotEmpty(prdId))
            map.put(Const.FILTER_PARAMS.PRD_ID_PARAM, prdId);
        if (StringUtils.isNotEmpty(fromDate))
            map.put(Const.FILTER_PARAMS.FROM_DATE_PARAM, fromDate);
        if (StringUtils.isNotEmpty(toDate))
            map.put(Const.FILTER_PARAMS.TO_DATE_PARAM, toDate);
        if (StringUtils.isNotEmpty(objectCode))
            map.put(Const.FILTER_PARAMS.OBJECT_CODE, objectCode);
        if (StringUtils.isNotEmpty(tableName))
            map.put(Const.FILTER_PARAMS.TABLE_NAME, tableName);
        if (!DataUtil.isNullOrEmpty(deptCodes))
            map.put(Const.FILTER_PARAMS.DEPT_CODES, deptCodes);
        if (!DataUtil.isNullOrEmpty(objOrParentCode))
            map.put(Const.FILTER_PARAMS.OBJ_OR_PARENT_CODE, objOrParentCode);
        if (!DataUtil.isNullOrEmpty(branchCodes))
            map.put(Const.FILTER_PARAMS.BRANCH_CODE, branchCodes);
        if (!DataUtil.isNullOrEmpty(rowNum))
            map.put(Const.FILTER_PARAMS.ROW_NUM, rowNum);
        if (!DataUtil.isNullOrEmpty(drillDown))
            map.put(Const.FILTER_PARAMS.DRILL_DOWN, drillDown);
        return map;
    }
}
