package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import com.lifesup.gbtd.dto.object.DashboardReportDto;
import com.lifesup.gbtd.repository.DashboardReportRepositoryCustom;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.JpaUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DashboardReportRepositoryCustomImpl implements DashboardReportRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<DashboardReportDto> doSearch(DashboardReportDto dto, Pageable pageable) {
        HashMap<String, Object> params = new HashMap<>();
        String sql = "SELECT " +
                " d.REPORT_ID reportId, " +
                " d.REPORT_CODE reportCode, " +
                " d.REPORT_NAME reportName, " +
                " d.DESCRIPTION description, " +
                " d.FOLDER_ID folderId, " +
                " d.STATUS status, " +
                " d.FILE_NAME fileName, " +
                " d.MODIFIED_DATE modifiedDate,  " +
                " d.SPLIT_SHEET splitSheet, " +
                " d.UPDATE_USER updateUser, " +
                " d.IP_SERVER ipServer, " +
                " df.FOLDER_NAME folderName " +
                "FROM DASHBOARD_REPORT d " +
                "LEFT JOIN DASHBOARD_FOLDER df ON d.FOLDER_ID = df.FOLDER_ID " +
                "WHERE 1=1 ";

        if (StringUtils.isNotEmpty(dto.getReportCode())) {
            sql += "AND LOWER(d.REPORT_CODE) LIKE :reportCode ESCAPE '&' ";
            params.put("reportCode", DataUtil.makeLikeParam(dto.getReportCode()));
        }
        if (StringUtils.isNotEmpty(dto.getReportName())) {
            sql += "AND LOWER(d.REPORT_NAME) LIKE :reportName ESCAPE '&' ";
            params.put("reportName", DataUtil.makeLikeParam(dto.getReportName()));
        }
        if (StringUtils.isNotEmpty(dto.getFileName())) {
            sql += "AND LOWER(d.FILE_NAME) LIKE :fileName ESCAPE '&' ";
            params.put("fileName", DataUtil.makeLikeParam(dto.getFileName()));
        }
        if (null != dto.getFolderId()) {
            sql += "AND d.FOLDER_ID = :folderId ";
            params.put("folderId", dto.getFolderId());
        }
        if (null != dto.getStatus()) {
            sql += "AND d.STATUS = :status ";
            params.put("status", dto.getStatus());
        }
        if (null != dto.getSplitSheet()) {
            sql += "AND d.SPLIT_SHEET = :splitSheet ";
            params.put("splitSheet", dto.getSplitSheet());
        }

        sql += "ORDER BY d.modified_date DESC";

        String sqlCount = "SELECT COUNT(*) FROM ( " + sql + " )";
        Query queryCount = em.createNativeQuery(sqlCount);
        Query query = em.createNativeQuery(sql, "getDashboardReport.search");
        return JpaUtil.getPageableResult(query, queryCount, params, pageable);
    }

}