package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.ParamTreeDto;
import com.lifesup.gbtd.dto.object.ReportDto;
import com.lifesup.gbtd.dto.object.SheetDto;
import com.lifesup.gbtd.dto.object.TempReportDto;
import com.lifesup.gbtd.dto.response.ResponseCommon;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.repository.ParamTreeRepositoryCustom;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.JpaUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ParamTreeRepositoryCustomImpl implements ParamTreeRepositoryCustom {

    private static final String TYPE_PARAM = "typeParam";

    @PersistenceContext
    private EntityManager em;

    private String createDashboardParamTreeSql() {
        return "SELECT distinct a.ID as \"id\"," +
                "a.CODE as \"code\","
                + "a.NAME as \"name\","
                + "a.PARENT as \"parent\","
                + "a.TYPE_PARAM AS \"typeParam\","
                + "a.STATUS AS \"status\","
                + "a.IMAGE AS \"image\","
                + "a.PARAM_ORDER AS \"paramOrder\","
                + "a.GROUP_TYPE AS \"groupType\","
                + "a.LEVEL_NODE AS \"levelNode\","
                + "a.SUB_CODE AS \"subCode\","
                + "a.MODIFIED_DATE AS \"modifiedDate\","
                + "a.START_TIME AS \"startTime\","
                + "a.END_TIME AS \"endTime\" ";
    }

    public List<ParamTreeDto> getParent(ParamTreeDto obj) {
        HashMap<String, Object> params = new HashMap<>();
        String sql = this.createDashboardParamTreeSql()
                + " FROM DASHBOARD_PARAM_TREE a ";

        if (StringUtils.isNotEmpty(obj.getId())) {
            sql += " LEFT JOIN " +
                    "SELECT distinct id " +
                    "FROM " +
                    "DASHBOARD_PARAM_TREE b " +
                    "WHERE id = :id " +
                    "AND b.TYPE_PARAM = :typeParam " +
                    "AND b.STATUS =1 " +
                    "UNION ALL SELECT id " +
                    "FROM DASHBOARD_PARAM_TREE b " +
                    "WHERE b.TYPE_PARAM = :typeParam " +
                    "AND b.STATUS = 1 " +
                    "CONNECT BY Prior b.CODE = b.PARENT " +
                    "START WITH b.PARENT = :code " +
                    ") abc " +
                    "On abc.id = a.id ";

            params.put("id", obj.getId());
            params.put("code", obj.getCode());
        }
        sql += " WHERE a.TYPE_PARAM = :typeParam and a.STATUS = 1 ";
        if (StringUtils.isNotEmpty(obj.getId())) {
            sql += "and abc.id is null ";
        }
        params.put(TYPE_PARAM, obj.getTypeParam());

        Query query = em.createNativeQuery(sql.toString(), "paramTree.noValidity");
        JpaUtil.setQueryParams(query, params);

        return query.getResultList();
    }

    public List<ParamTreeDto> getListUnit(ReportDto reportDto) {
        if (reportDto.getTypeParams() == null) {
            List<String> listStr = new ArrayList<>();
            listStr.add("DTTD");
            reportDto.setTypeParams(listStr);
        }

        String sql = this.createDashboardParamTreeSql() +
                ", cd.ID AS \"deptId\" , CONCAT( CONCAT( a.NAME, ' (' ), CONCAT( a.CODE, ' )' ) ) AS \"fullName\"," +

                "(CASE " +
                "WHEN a.START_TIME IS NOT NULL AND TO_CHAR( a.START_TIME, 'YYYY-MM-DD' ) > ( :date ) " +
                "THEN a.NAME " +
                "WHEN a.END_TIME IS NOT NULL AND TO_CHAR( a.END_TIME, 'YYYY-MM-DD' ) < ( :date ) " +
                "THEN CONCAT(a.NAME, CONCAT(CONCAT( ' ( active from ', TO_CHAR( a.END_TIME, 'DD/MM/YYYY' ) ), ' )') ) " +
                "WHEN a.START_TIME IS NOT NULL AND a.END_TIME IS NOT NULL AND TO_CHAR( a.START_TIME, 'YYYY-MM-DD' ) <= ( :date ) AND TO_CHAR( a.END_TIME, 'YYYY-MM-DD' ) >= ( :date ) " +
                "THEN a.NAME " +
                "ELSE a.NAME " +
                "END) AS \"vadility\", " +

                "(CASE WHEN a.START_TIME IS NOT NULL AND TO_CHAR( a.START_TIME, 'YYYY-MM-DD' ) > ( :date ) " +
                "THEN 2 " +
                "WHEN a.END_TIME IS NOT NULL AND TO_CHAR( a.END_TIME, 'YYYY-MM-DD' ) < ( :date ) " +
                "THEN 3 " +
                "WHEN a.START_TIME IS NOT NULL AND a.END_TIME IS NOT NULL AND TO_CHAR( a.START_TIME, 'YYYY-MM-DD' ) < ( :date ) AND TO_CHAR( a.END_TIME, 'YYYY-MM-DD' ) > ( :date ) " +
                "THEN 1 " +
                "ELSE 1 " +
                "END) AS \"typeWarning\", " +

                "(case when (TO_CHAR(a.END_TIME, 'YYYY-MM-DD') < ( :date ) or TO_CHAR(a.START_TIME, 'YYYY-MM-DD') > ( :date )) " +
                "then 1 " +
                "end) AS \"checkCase\"   " +
                " FROM DASHBOARD_PARAM_TREE a  INNER JOIN CAT_DEPARTMENT cd ON a.CODE = cd.CODE ";

        if (!DataUtil.isNullObject(reportDto.getDeptLevels())) {
            sql += "inner join CAT_DEPARTMENT b on a.code = b.code ";
        }

        sql += "START WITH 1 = 1 " +
                "AND a.TYPE_PARAM IN :typeParam " +
                "AND a.STATUS = 1 ";

        if (!DataUtil.isNullObject(reportDto.getDeptLevels())) {
            sql += "AND b.DEPT_LEVEL in (:deptLevels) ";
        }

        if (reportDto.getDeptId() != null) {
            sql += "AND a.DEPT_ID = :deptId ";
        }

        sql += "CONNECT BY PRIOR a.CODE = a.parent " +
                "AND a.TYPE_PARAM IN :typeParam " +
                "AND a.STATUS = 1 ";

        if (!DataUtil.isNullObject(reportDto.getDeptLevels())) {
            sql += "AND b.DEPT_LEVEL in (:deptLevels) ";
        }

        sql += "ORDER BY a.PARAM_ORDER ";
        Query query = em.createNativeQuery(sql, "paramTree.listUnit");
        if (reportDto.getDeptId() != null) {
            query.setParameter("deptId", reportDto.getDeptId());
        }
        if (reportDto.getTypeParams() != null) {
            query.setParameter(TYPE_PARAM, reportDto.getTypeParams());
        }
        if (!DataUtil.isNullObject(reportDto.getDeptLevels())) {
            query.setParameter("deptLevels", reportDto.getDeptLevels());
        }
//        query.setParameter("date", reportDto.getFromDate() != null ? reportDto.getFromDate() : new Date());
        // old code
        if (reportDto.getFromDate() != null) {
            Format formatter = new SimpleDateFormat("yyyy-MM-dd");
            String date = formatter.format(reportDto.getFromDate());
            query.setParameter("date", date);
        } else {
            Date date = new Date();
            query.setParameter("date", date.toString());
        }

        return query.getResultList();
    }

    public List<ParamTreeDto> getListUnitDeptTree(ReportDto reportDto) {
//        if (reportDto.getTypeParams() == null) {
//            List<String> listStr = new ArrayList<>();
//            listStr.add("DTTD");
//            reportDto.setTypeParams(listStr);
//        }
        String sql = this.createDashboardParamTreeSql() +
                ", cd.ID AS \"deptId\" , CONCAT( CONCAT( a.NAME, ' (' ), CONCAT( a.CODE, ' )' ) ) AS \"fullName\" " +
                " FROM DASHBOARD_PARAM_TREE a  INNER JOIN CAT_DEPARTMENT cd ON a.CODE = cd.CODE ";
        if (!DataUtil.isNullObject(reportDto.getDeptLevels())) {
            sql += "inner join CAT_DEPARTMENT b on a.code = b.code ";
        }

        sql += "START WITH 1 = 1 " +
                "AND a.TYPE_PARAM = :typeParam " +
                "AND a.STATUS = 1 ";

//        if (!DataUtil.isNullObject(reportDto.getDeptLevels())) {
//            sql += "AND b.DEPT_LEVEL in (:deptLevels) ";
//        }

        if (reportDto.getDeptId() != null) {
            sql += "AND a.DEPT_ID = :deptId ";
        }

        sql += "CONNECT BY PRIOR a.CODE = a.parent " +
                "AND a.TYPE_PARAM = :typeParam " +
                "AND a.STATUS = 1 ";

//        if (!DataUtil.isNullObject(reportDto.getDeptLevels())) {
//            sql += "AND b.DEPT_LEVEL in (:deptLevels) ";
//        }

        sql += "ORDER BY a.PARAM_ORDER ";
        Query query = em.createNativeQuery(sql, "paramTree.listUnit2");
        if (reportDto.getDeptId() != null) {
            query.setParameter("deptId", reportDto.getDeptId());
        }
        if (reportDto.getTypeParam() != null) {
            query.setParameter(TYPE_PARAM, reportDto.getTypeParam());
        }
//        if (!DataUtil.isNullObject(reportDto.getDeptLevels())) {
//            query.setParameter("deptLevels", reportDto.getDeptLevels());
//        }
        return query.getResultList();
    }

    public List<ParamTreeDto> getListParamTree(ParamTreeDto obj) {
        String sql = this.createDashboardParamTreeSql()
                + ", CONCAT(CONCAT( a.NAME, ' ('), CONCAT( a.CODE, ' )')) AS \"fullName\","
                + "(case "
                + "when TO_CHAR(a.END_TIME,'YYYY-MM-DD') < (:timeTree) " +
                "then concat( CONCAT('Inactive (', TO_CHAR(a.START_TIME,'DD/MM/YYYY')), CONCAT(CONCAT(' -> ',TO_CHAR(a.END_TIME,'DD/MM/YYYY')),' )')) "
                + " when a.END_TIME is null and START_TIME is null " +
                "then null "
                + " when a.END_TIME is null and START_TIME is not null "
                + " then CONCAT('From ', TO_CHAR(a.START_TIME,'DD/MM/YYYY')) "
                + " when a.START_TIME is null and END_TIME is not null "
                + " then CONCAT(' To ',TO_CHAR(a.END_TIME,'DD/MM/YYYY')) "
                + " when TO_CHAR(a.END_TIME,'YYYY-MM-DD') >= (:timeTree) and TO_CHAR(a.START_TIME,'YYYY-MM-DD') <= (:timeTree) "
                + " then concat( CONCAT('', TO_CHAR(a.START_TIME,'DD/MM/YYYY')), CONCAT(' -> ',TO_CHAR(a.END_TIME,'DD/MM/YYYY'))) "
                + " end) AS \"vadility\" "

                + " FROM DASHBOARD_PARAM_TREE a "
                + " start with 1=1 ";
        if (StringUtils.isNotEmpty(obj.getName())) {
            sql += " and (upper(a.NAME) LIKE upper(:paramtreeName) escape '&' or upper(a.CODE) LIKE upper(:paramtreeName) escape '&') ";
        }
        sql += " and a.TYPE_PARAM = :typeParam and a.STATUS = 1 " +
                "  AND (TO_CHAR(a.START_TIME,'YYYY-MM-DD') <= (:timeTree) or START_TIME is null) " +
                " connect by a.CODE = prior a.parent and a.TYPE_PARAM = :typeParam " +
                " and a.STATUS = 1 " +
                " ORDER BY a.PARAM_ORDER ";
        Query query = em.createNativeQuery(sql, "paramTree.doSearch");

        if (StringUtils.isNotEmpty(obj.getName())) {
            query.setParameter("paramtreeName", "%" + obj.getName() + "%");
        }
        if (obj.getEffTime() != null) {
            Date date = new Date(obj.getEffTime().getTime());
            query.setParameter("timeTree", date.toString());
        }
        query.setParameter(TYPE_PARAM, obj.getTypeParam());

        return query.getResultList();
    }

    //Them moi chi tieu
    public int createParamTree(ParamTreeDto obj) {
        // insert record into DB
        String sql = "INSERT INTO " +
                "DASHBOARD_PARAM_TREE " +
                "(CODE, NAME, PARENT,START_TIME, END_TIME, STATUS, TYPE_PARAM, PARAM_ORDER, GROUP_TYPE, MODIFIED_DATE ) " +
                "VALUES " +
                "(:code, :name, :parent, :startTime, :endTime, 1, :typeParam, " +
                "(SELECT MAX(PARAM_ORDER) FROM DASHBOARD_PARAM_TREE WHERE TYPE_PARAM= :typeParam) + 1, :typeParam ,  SYSDATE) ";
        Query createQuery = em.createNativeQuery(sql);
        createQuery.setParameter("code", StringUtils.isNotEmpty(obj.getCode()) ? obj.getCode().toUpperCase() : obj.getCode());
        createQuery.setParameter("parent", StringUtils.isNotEmpty(obj.getParent()) ? obj.getParent().toUpperCase() : obj.getParent());
        createQuery.setParameter("name", obj.getName());
        createQuery.setParameter("startTime", obj.getStartTime());
        createQuery.setParameter("endTime", obj.getEndTime());
        createQuery.setParameter(TYPE_PARAM, obj.getTypeParam());

        return createQuery.executeUpdate();
    }

    //Search Name Dinh nghia cay don vi - paramtree
    public List<ParamTreeDto> getParamTreeByName(String name) {
        String sql =
                this.createDashboardParamTreeSql()
                        + " FROM DASHBOARD_PARAM_TREE a "
                        + " where 1=1 and a.TYPE_PARAM = 'DTTD' and a.STATUS = 1 ";
        if (StringUtils.isNotEmpty(name)) {
            sql += " and upper(a.NAME) LIKE upper(:paramtreeName) escape '&'  ";
        }
        Query query = em.createNativeQuery(sql.toString(), ParamTreeDto.class);
        if (StringUtils.isNotEmpty(name)) {
            query.setParameter("paramtreeName", name);
        }
        return query.getResultList();
    }

    //Search Code Dinh nghia cay don vi - paramtree
    public List<ParamTreeDto> getParamTreeByCode(ParamTreeDto obj) {
        String sql =
                "SELECT a.ID as \"id\"," +
                        "a.CODE as \"code\","
                        + "a.NAME as \"name\","
                        + "a.PARENT as \"parent\","
                        + "a.TYPE_PARAM AS \"typeParam\","
                        + "a.STATUS AS \"status\","
                        + "a.IMAGE AS \"image\","
                        + "a.PARAM_ORDER AS \"paramOrder\","
                        + "a.GROUP_TYPE AS \"groupType\","
                        + "a.LEVEL_NODE AS \"levelNode\","
                        + "a.SUB_CODE AS \"subCode\","
                        + "a.MODIFIED_DATE AS \"modifiedDate\","
                        + "a.START_TIME AS \"startTime\","
                        + "a.END_TIME AS \"endTime\""
                        + " FROM DASHBOARD_PARAM_TREE a "
                        + " where 1=1 " +
                        "and a.TYPE_PARAM = :typeParam and a.STATUS =1 "
                        + " and a.CODE = :paramtreeCode ";

        Query query = em.createNativeQuery(sql, "paramTree.noValidity");
        query.setParameter("paramtreeCode", obj.getCode());
        query.setParameter(TYPE_PARAM, obj.getTypeParam());

        return query.getResultList();
    }

    public List<ParamTreeDto> getParamTreeById(ParamTreeDto obj) {
        StringBuilder sql = new StringBuilder("SELECT a.ID as \"id\"," +
                "a.CODE as \"code\","
                + "a.NAME as \"name\","
                + "a.PARENT as \"parent\","
                + "a.TYPE_PARAM AS \"typeParam\","
                + "a.STATUS AS \"status\","
                + "a.IMAGE AS \"image\","
                + "a.PARAM_ORDER AS \"paramOrder\","
                + "a.GROUP_TYPE AS \"groupType\","
                + "a.LEVEL_NODE AS \"levelNode\","
                + "a.SUB_CODE AS \"subCode\","
                + "a.MODIFIED_DATE AS \"modifiedDate\","
                + "a.START_TIME AS \"startTime\","
                + "a.END_TIME AS \"endTime\""
                + " FROM DASHBOARD_PARAM_TREE a "
                + " where a.ID = :id ");

        Query query = em.createNativeQuery(sql.toString(), "paramTree.noValidity");
        query.setParameter("id", obj.getId());

        return query.getResultList();
    }

    public List<ParamTreeDto> getType() {
        String sql =
                "select app_param_name AS typeParam " +
                        "from app_param a " +
                        "where a.app_param_code = :dashboardTree " +
                        "and a.status = 1 ";

        Query query = em.createNativeQuery(sql, "paramTree.onlyTypeParam");
        query.setParameter("dashboardTree", Const.DASHBOARD_PARAM_TREE);
        return query.getResultList();
    }

    public List<ParamTreeDto> getIdMax(String type) {
        String sql =
                "select max(TO_NUMBER(id)) AS \"id\" " +
                        "from DASHBOARD_PARAM_TREE WHERE" +
                        " TYPE_PARAM = :typeParam  AND STATUS  =1 ";

        Query query = em.createNativeQuery(sql, Integer.class);

        query.setParameter(TYPE_PARAM, type);
        return query.getResultList();
    }

    //Edit
    public int editParamTree(ParamTreeDto obj) {
        StringBuilder updateSql = new StringBuilder("UPDATE DASHBOARD_PARAM_TREE SET ");
        updateSql.append(" CODE = :code ");
        updateSql.append(" ,NAME = :name");
        updateSql.append(" ,PARENT = :parent");
        updateSql.append(" ,START_TIME = :startTime");
        updateSql.append(" ,END_TIME = :endTime");
        updateSql.append(" ,TYPE_PARAM = :typeParam");
        updateSql.append(" ,MODIFIED_DATE = SYSDATE");
        updateSql.append(" WHERE ID = :id");

        Query updateQuery = em.createNativeQuery(updateSql.toString());
        updateQuery.setParameter("code", StringUtils.isNotEmpty(obj.getCode()) ? obj.getCode().toUpperCase() : obj.getCode());
        updateQuery.setParameter("name", obj.getName());
        updateQuery.setParameter("parent", StringUtils.isNotEmpty(obj.getParent()) ? obj.getParent().toUpperCase() : obj.getParent());
        updateQuery.setParameter("startTime", obj.getStartTime());
        updateQuery.setParameter("endTime", obj.getEndTime());
        updateQuery.setParameter(TYPE_PARAM, obj.getTypeParam());
        updateQuery.setParameter("id", obj.getId());
        return updateQuery.executeUpdate();
    }


    // edit chi
    public int editParamTreeChi(ParamTreeDto obj) {
        StringBuilder updateSql = new StringBuilder("UPDATE DASHBOARD_PARAM_TREE SET ");
        updateSql.append(" PARENT = :parent");
        updateSql.append(" ,MODIFIED_DATE = SYSDATE");
        updateSql.append(" WHERE TYPE_PARAM = :typeParam  AND STATUS  =1 and PARENT = :oldparent");

        Query updateQuery = em.createNativeQuery(updateSql.toString());
        updateQuery.setParameter("parent", StringUtils.isNotEmpty(obj.getCode()) ? obj.getCode().toUpperCase() : obj.getCode());
        updateQuery.setParameter("oldparent", obj.getOldParen());
        updateQuery.setParameter(TYPE_PARAM, obj.getTypeParam());

        return updateQuery.executeUpdate();
    }

    //Xoa ban ghi
    public ResponseCommon deleteParamTree(ParamTreeDto obj) {
        if (obj.getCode() == null) {
            throw new ServerException(Const.ERROR);
        }
        ResponseCommon rs = new ResponseCommon();

        String deleteSql = "UPDATE DASHBOARD_PARAM_TREE " +
                "SET status      = 0, " +
                "  modified_date = TO_DATE(:date, 'YYYY-MM-DD') " +
                "WHERE id       IN " +
                "  (SELECT id " +
                "  FROM DASHBOARD_PARAM_TREE a  " +
                "  WHERE id                          = :id  " +
                "  AND a.TYPE_PARAM = :typeParam  " +
                "  AND a.STATUS     =1 " +
                "  UNION ALL " +
                "  SELECT id " +
                "  FROM DASHBOARD_PARAM_TREE a " +
                "  WHERE a.TYPE_PARAM        = :typeParam  " +
                "  AND a.STATUS              =1 " +
                "    CONNECT BY Prior a.CODE = a.PARENT " +
                "    START WITH a.PARENT     = :parentCode  " +
                "  ) ";
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        Query deleteQuery = em.createNativeQuery(deleteSql)
                .setParameter("date", date)
                .setParameter("id", obj.getId())
                .setParameter("parentCode", obj.getCode())
                .setParameter(TYPE_PARAM, obj.getTypeParam());
        if (deleteQuery.executeUpdate() != 0) {
            rs.setErrorCode(Const.ERROR_CODE.SUCCESS);
            rs.setErrorMessage("success");
        } else {
            rs.setErrorCode(Const.ERROR_CODE.FAIL);
            rs.setErrorMessage("error");
        }
        return rs;
    }

    // tempReportDao Old ----
    public List<TempReportDto> getListTempReport(String userName) {
        String sql = "SELECT " +
                " tr.REPORT_ID as reportId, " +
                " tr.CUSTOMER_ID as customerId, " +
                " tr.REPORT_NAME as reportName, " +
                " tr.CREATE_USER as createUser, " +
                " tr.REPORT_TYPE as reportType, " +
                " tr.TYPE as type, " +
                " tr.DEPT_PARAM as deptParam, " +
                " tr.FROM_DATE as fromDate, " +
                " tr.CHECKED as checked, " +
                " tr.OWNER_BY as ownerBy, " +
                " tr.STATUS as status " +
                "from TEMP_REPORT tr " +
                "WHERE tr.OWNER_BY = :createUser " +
                "ORDER BY tr.REPORT_ID ";
        Query query = em.createNativeQuery(sql, "paramTree.getListTempReport")
                .setParameter("createUser", userName);
        return query.getResultList();
    }


    public List<TempReportDto> findTempReportByName(String reportName, String userName) {
        String sql = "SELECT " +
                " tr.REPORT_ID as reportId, " +
                " tr.CUSTOMER_ID as customerId, " +
                " tr.REPORT_NAME as reportName, " +
                " tr.CREATE_USER as createUser, " +
                " tr.REPORT_TYPE as reportType, " +
                " tr.TYPE as type, " +
                " tr.DEPT_PARAM as deptParam, " +
                " tr.FROM_DATE as fromDate, " +
                " tr.CHECKED as checked, " +
                " tr.OWNER_BY as ownerBy, " +
                " tr.STATUS as status " +
                " from TEMP_REPORT tr " +
                " WHERE tr.CREATE_USER = :createUser " +
                " AND UPPER(tr.REPORT_NAME) = UPPER(:reportName) ORDER BY tr.REPORT_ID ";
        Query query = em.createNativeQuery(sql, "paramTree.getListTempReport")
                .setParameter("createUser", userName)
                .setParameter("reportName", reportName);
        return query.getResultList();
    }

    @Transactional
    public int insertTempReport(TempReportDto tempReportDTO, String userName) {
        String sql =
                "INSERT INTO temp_report(TYPE, DEPT_PARAM, REPORT_TYPE, FROM_DATE, CHECKED, CUSTOMER_ID, REPORT_NAME, CREATE_USER, OWNER_BY, STATUS) " +
                        "VALUES ( :type, :deptParam, :reportType, :fromDate, :checked, :customerId , :reportName, :createUser, :ownerBy, :status)";
        Query query = em.createNativeQuery(sql)
                .setParameter("customerId", tempReportDTO.getCustomerId())
                .setParameter("reportName", tempReportDTO.getReportName())
                .setParameter("createUser", userName)
                .setParameter("type", tempReportDTO.getType())
                .setParameter("deptParam", tempReportDTO.getDeptParam())
                .setParameter("reportType", tempReportDTO.getReportType())
                .setParameter("fromDate", tempReportDTO.getFromDate())
                .setParameter("checked", tempReportDTO.getChecked())
                .setParameter("ownerBy", tempReportDTO.getOwnerBy())
                .setParameter("status", tempReportDTO.getStatus());
        return query.executeUpdate();
    }

    @Transactional
    public int updateTempReport(TempReportDto tempReportDTO) {
        String sql = "UPDATE temp_report tr " +
                "SET tr.TYPE = :type, tr.DEPT_PARAM = :deptParam, tr.REPORT_TYPE = :reportType, tr.FROM_DATE = :fromDate, " +
                "tr.CHECKED = :checked, tr.CUSTOMER_ID = :customerId, tr.REPORT_NAME = :reportName, " +
                "tr.CREATE_USER = :createUser, tr.OWNER_BY = :ownerBy, tr.STATUS = :status " +
                "WHERE tr.REPORT_ID = :reportId";
        Query query = em.createNativeQuery(sql)
                .setParameter("customerId", tempReportDTO.getCustomerId())
                .setParameter("reportName", tempReportDTO.getReportName())
                .setParameter("createUser", tempReportDTO.getCreateUser())
                .setParameter("type", tempReportDTO.getType())
                .setParameter("deptParam", tempReportDTO.getDeptParam())
                .setParameter("reportType", tempReportDTO.getReportType())
                .setParameter("fromDate", tempReportDTO.getFromDate())
                .setParameter("checked", tempReportDTO.getChecked())
                .setParameter("ownerBy", tempReportDTO.getOwnerBy())
                .setParameter("status", tempReportDTO.getStatus())
                .setParameter("reportId", tempReportDTO.getReportId());
        return query.executeUpdate();
    }

    @Transactional
    public int deleteTempReport(TempReportDto tempReportDTO) {
        String sql = "DELETE FROM temp_report WHERE REPORT_ID = :reportId";
        Query query = em.createNativeQuery(sql)
                .setParameter("reportId", tempReportDTO.getReportId());
        return query.executeUpdate();
    }

    public List<SheetDto> getSheet() {
        String sql = "SELECT " +
                "appParam.APP_PARAM_NAME as name, " +
                "appParam.APP_PARAM_VALUE as code " +
                "FROM " +
                " APP_PARAM appParam  " +
                "WHERE " +
                " APP_PARAM_CODE = 'SHEET_BIRT'  " +
                "ORDER BY " +
                " APP_PARAM_ID";
        Query query = em.createNativeQuery(sql, "paramTree.getSheet");
        return query.getResultList();
    }
}
