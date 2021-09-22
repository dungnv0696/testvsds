package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.DashboardFolderDto;
import com.lifesup.gbtd.dto.object.TreeDto;
import com.lifesup.gbtd.repository.DashboardFolderRepositoryCustom;
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

public class DashboardFolderRepositoryCustomImpl implements DashboardFolderRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<TreeDto> getDashboardFolderTree(DashboardFolderDto dto) {
        String sql = "SELECT " +
                "d.FOLDER_ID id, " +
                "d.FOLDER_CODE code, " +
                "d.FOLDER_NAME name, " +
                "d.FOLDER_PARENT_ID parent " +
                "FROM DASHBOARD_FOLDER d " +
                "WHERE STATUS = 1 ";
//                "CONNECT BY PRIOR d.FOLDER_ID = d.FOLDER_PARENT_ID " +
//                "START WITH STATUS = 1"


        Query query = em.createNativeQuery(sql, "getDashboardFolder.tree");
        return query.getResultList();
    }

    @Override
    public List<DashboardFolderDto> getDashboardFolderTrees(DashboardFolderDto dto) {
        String sql = "SELECT " +
                "d.FOLDER_ID folderId, " +
                "d.FOLDER_CODE folderCode, " +
                "d.FOLDER_NAME folderName, " +
                "d.FOLDER_PARENT_ID folderParentId, " +
                "d.description description, " +
                "d.status status, " +
                "d.modified_date modifiedDate " +
                "FROM DASHBOARD_FOLDER d " +
                "WHERE STATUS = 1 ";
//                "CONNECT BY PRIOR d.FOLDER_ID = d.FOLDER_PARENT_ID " +
//                "START WITH STATUS = 1"


        Query query = em.createNativeQuery(sql, "getDashboardFolder.searchTree");
        return query.getResultList();
    }

    @Override
    public Page<DashboardFolderDto> doSearch(DashboardFolderDto dto, Pageable pageable) {
        HashMap<String, Object> params = new HashMap<>();
        String sql = "SELECT " +
                "b.FOLDER_ID folderId, " +
                "b.folder_parent_id folderParentId," +
                "b.folder_code folderCode, " +
                "b.folder_name folderName, " +
                "b.description, " +
                "b.status, " +
                "b.modified_date modifiedDate, " +
                "df.FOLDER_NAME AS folderNameParent, " +
                "count(dr.REPORT_ID) AS reportId, " +
                "b.update_user updateUser " +
                "FROM dashboard_folder b " +
                "left join DASHBOARD_FOLDER df on df.FOLDER_ID = b.FOLDER_PARENT_ID " +
                "left join DASHBOARD_REPORT dr on dr.FOLDER_ID = b.FOLDER_ID " +
                "WHERE 1 = 1 ";

        if (StringUtils.isNotEmpty(dto.getFolderCode())) {
            sql += "AND LOWER(b.folder_code) LIKE :folderCode ESCAPE '&' ";
            params.put("folderCode", DataUtil.makeLikeParam(dto.getFolderCode()));
        }
        if (StringUtils.isNotEmpty(dto.getFolderName())) {
            sql += "AND LOWER(b.folder_name) LIKE :folderName ESCAPE '&' ";
            params.put("folderName", DataUtil.makeLikeParam(dto.getFolderName()));
        }
        if (null != dto.getFolderParentId()) {
            sql += "AND b.folder_parent_id = :folderParentId ";
            params.put("folderParentId", dto.getFolderParentId());
        }
        if (null != dto.getStatus()){
            sql += "AND b.status = :status ";
            params.put("status", dto.getStatus());
        }
        sql += "GROUP BY b.FOLDER_ID, b.folder_parent_id, b.folder_code, b.folder_name, b.description, b.status, b.modified_date, df.FOLDER_NAME, b.update_user " +
               "ORDER BY b.modified_date DESC ";

        String sqlCount = "SELECT COUNT(*) FROM ( " + sql + " )";
        Query queryCount = em.createNativeQuery(sqlCount);
        Query query = em.createNativeQuery(sql, "getDashboardFolder.search");
        return JpaUtil.getPageableResult(query, queryCount, params, pageable);
    }
}
