package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.BiTdServicesTreeDto;
import com.lifesup.gbtd.dto.object.ServiceGBTDDto;
import com.lifesup.gbtd.dto.request.ServiceTargetResDto;
import com.lifesup.gbtd.model.ServiceGBTDEntity;
import com.lifesup.gbtd.repository.ServiceGBTDRepositoryCustom;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.JpaUtil;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ServiceGBTDRepositoryCustomImpl implements ServiceGBTDRepositoryCustom {

    private static final String SERVICE_ID = "serviceId";
    private static final String DEPT_ID = "deptId";

    @PersistenceContext
    private EntityManager em;

    //    select a.* from service_gbtd a , services_map_dept b
//    where a.service_id = b.service_id and a.service_id like '%?%'
//    and a.service_name like '%?%' and a.service_display like '%?%'
//    and b.dept_code = '?' and a.GROUP_KPI_CODE = '?'
//    and a.SERVICE_TYPE = '?' and a.status = '?';
    @Override
    public List<ServiceGBTDDto> findServiceGBTDs(ServiceGBTDDto dto, String username) {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT " +
                "DISTINCT a.ID id, " +
                "a.SERVICE_ID serviceId, " +
                "a.SERVICE_NAME serviceName, " +
                "a.SERVICE_DISPLAY serviceDisplay, " +
                "a.DESCRIPTION description, " +
                "a.UNIT_ID unitId, " +
                "a.UNIT_CODE unitCode, " +
                "a.TYPE_ID typeId, " +
                "a.CAL_RATE calRate, " +
                "a.SERVICE_TYPE serviceType, " +
                "a.THRESHOLD_TYPE thresholdType, " +
                "a.STATUS status, " +
                "b.GROUP_KPI_CODE groupKpiCode, " +
                "a.FOMULAR_DESCRIPT fomularDescript, " +
                "a.UPDATE_TIME updateTime, " +
                "a.UPDATE_USER updateUser, " +
                "null typeParam, " +
                "CASE WHEN sum(CASE WHEN ((SELECT COUNT( u.ID ) FROM users u WHERE  u.dept_id = b.dept_id AND u.USERNAME = :username) > 0) THEN 1 ELSE 0 " +
                "END) over() > 0 THEN 'true' ELSE 'false' " +
                "END AS enable, " +
                "CASE WHEN ((SELECT COUNT( bst.SERVICE_ID ) FROM bi_td_services_tree bst WHERE bst.PARENT_SERVICE_ID = a.SERVICE_ID) > 0) THEN 'true' ELSE 'false' " +
                "END AS haveFormula, " +
                "CASE WHEN ((SELECT COUNT( sgd.id ) FROM service_gbtd_define sgd WHERE sgd.SERVICE_ID = a.SERVICE_ID) > 0) THEN 'true' ELSE 'false' " +
                "END AS haveDefine " +
                "FROM service_gbtd a " +
                "INNER JOIN services_map_dept b ON a.service_id = b.service_id ");

        if (Objects.nonNull(dto.getDeptId())) {
            sql.append("AND b.dept_id = :deptId ");
            params.put(DEPT_ID, dto.getDeptId());
        }

        sql.append("left join users u on (b.dept_id = u.dept_id and u.username = :username ) ");
        sql.append("WHERE 1 = 1 ");
        params.put("username", username);
        if (Objects.nonNull(dto.getServiceId())) {
            sql.append("AND a.service_id like :serviceId ");
            params.put(SERVICE_ID, "%" + dto.getServiceId() + "%");
        }
        if (StringUtils.isNotEmpty(dto.getServiceName())) {
            sql.append("AND lower(a.service_name) like :serviceName ESCAPE '&' ");
            params.put("serviceName", DataUtil.makeLikeParam(dto.getServiceName()));
        }
        if (StringUtils.isNotEmpty(dto.getServiceDisplay())) {
            sql.append("AND lower(a.service_display) like :serviceDisplay ESCAPE '&' ");
            params.put("serviceDisplay", DataUtil.makeLikeParam(dto.getServiceDisplay()));
        }
        if (StringUtils.isNotEmpty(dto.getGroupKpiCode())) {
            sql.append("AND b.GROUP_KPI_CODE = :groupKpiCode ");
            params.put("groupKpiCode", dto.getGroupKpiCode());
        }
        if (Objects.nonNull(dto.getServiceType())) {
            sql.append("AND a.SERVICE_TYPE = :serviceType ");
            params.put("serviceType", dto.getServiceType());
        }
        if (Objects.nonNull(dto.getStatus())) {
            sql.append("AND a.status = :status ");
            params.put("status", dto.getStatus());
        }
        sql.append("ORDER BY a.service_id DESC ");

        // Count queried record
        String sqlCount = "SELECT COUNT(*) FROM ( " + sql.toString() + " )";
        Query queryCount = em.createNativeQuery(sqlCount);
        JpaUtil.setQueryParams(queryCount, params);

        // Query limit offset
        Query query = em.createNativeQuery(sql.toString(), "getServiceGBTDMapping");
        JpaUtil.setQueryParams(query, params);

        JpaUtil.setPaging(dto, query, queryCount);

        return query.getResultList();
    }

    /*
    public List<ServiceGBTDDto> getListServiceFormula(ServiceGBTDDto dto) {
        String sql = "SELECT " +
                "a.ID id, " +
                "a.SERVICE_ID serviceId, " +
                "a.SERVICE_NAME serviceName, " +
                "a.SERVICE_DISPLAY serviceDisplay, " +
                "a.DESCRIPTION description, " +
                "a.UNIT_ID unitId, " +
                "a.UNIT_CODE unitCode, " +
                "a.TYPE_ID typeId, " +
                "a.CAL_RATE calRate, " +
                "a.SERVICE_TYPE serviceType, " +
                "a.THRESHOLD_TYPE thresholdType, " +
                "a.STATUS status, " +
//                "a.GROUP_KPI_CODE groupKpiCode, " +
                "a.FOMULAR_DESCRIPT fomularDescript, " +
                "a.UPDATE_TIME updateTime, " +
                "a.UPDATE_USER updateUser, " +
//                "b.ID id, " +
                "b.dept_id deptId, " +
                "b.PARENT_SERVICE_ID parentServiceId, " +
                "b.RATE rate, " +
                "b.STATUS treeStatus, " +
                "b.DESCRIPTION treeDescription, " +
                "b.SOURCE source " +
                "FROM SERVICE_GBTD a, SERVICES_TREE_GBTD b " +
                "WHERE " +
                "a.SERVICE_ID = b.PARENT_SERVICE_ID " +
                "AND a.SERVICE_ID = :serviceId " +
                "ORDER BY a.ID DESC ";

        // Count queried record
        String sqlCount = "SELECT COUNT(*) FROM ( " + sql + " )";
        Query queryCount = em.createNativeQuery(sqlCount)
                .setParameter(SERVICE_ID, dto.getServiceId());

        Query query = em.createNativeQuery(sql, "serviceGbtd.getListServiceFormula")
                .setParameter(SERVICE_ID, dto.getServiceId());

        JpaUtil.setPaging(dto, query, queryCount);

        return query.getResultList();
    }*/

    @Override
    public List<BiTdServicesTreeDto> getListServiceFormula(ServiceGBTDDto dto) {
        String sql = "SELECT s.SERVICE_NAME serviceName, " +
                "s.FOMULAR_DESCRIPT fomularDescript, " +
                "st.TYPE_PARAM typeParam, " +
                "st.DEPT_CODE deptCode, " +
                "st.SERVICE_ID serviceId, " +
                "st.PARENT_DEPT_CODE parentDeptCode, " +
                "st.PARENT_SERVICE_ID parentServiceId, " +
                "st.NUM_OF_DAY numOfDay, " +
                "st.PARENT_NUM_OF_DAY parentNumOfDay, " +
                "st.RATE rate, " +
                "st.TYPE_CALC typeCalc, " +
                "st.STATUS status, " +
                "st.DESCRIPTION description, " +
                "st.DEPT_ID deptId, " +
                "st.PARENT_DEPT_ID parentDeptId, " +
                "st.ID id," +
                "st.UPDATE_TIME updateTime," +
                "st.UPDATE_USER updateUser," +
                "smd.source " +
                "FROM BI_TD_SERVICES_TREE st " +
                "LEFT JOIN SERVICE_GBTD s ON st.SERVICE_ID = s.SERVICE_ID " +
                "left join services_map_dept smd on smd.service_id = st.SERVICE_ID and smd.dept_id = st.DEPT_ID  " +
                "WHERE st.PARENT_SERVICE_ID = :serviceId ";

        if (Objects.nonNull(dto.getDeptId())) {
            sql += "AND st.PARENT_DEPT_ID = :deptId ";
        }
        if (Objects.nonNull(dto.getTypeParam())) {
            sql += "AND st.TYPE_PARAM = :typeParam ";
        }

        // Count queried record
        String sqlCount = "SELECT COUNT(*) FROM ( " + sql + " )";
        Query queryCount = em.createNativeQuery(sqlCount)
                .setParameter(SERVICE_ID, dto.getServiceId());

        Query query = em.createNativeQuery(sql, "serviceGbtd.getListServiceFormula")
                .setParameter(SERVICE_ID, dto.getServiceId());

        if (Objects.nonNull(dto.getDeptId())) {
            queryCount.setParameter(DEPT_ID, dto.getDeptId());
            query.setParameter(DEPT_ID, dto.getDeptId());
        }
        if (Objects.nonNull(dto.getTypeParam())) {
            queryCount.setParameter(Const.TYPE_PARAM, dto.getTypeParam());
            query.setParameter(Const.TYPE_PARAM, dto.getTypeParam());
        }

        JpaUtil.setPaging(dto, query, queryCount);

        return query.getResultList();
    }

    @Override
    public List<ServiceGBTDDto> getAllServiceGbtd() {
        String sql = "SELECT " +
                "SERVICE_NAME serviceName, " +
                "SERVICE_ID serviceId " +
                "FROM SERVICE_GBTD ";
        Query query = em.createNativeQuery(sql, "serviceGbtd.findAll");
        return query.getResultList();
    }

    @Override
    public int updateFormulaServiceParent(Long idParent, Long parentDeptId, String formula) {
        String sql = "UPDATE SERVICES_MAP_DEPT SET " +
                "FOMULAR_DESCRIPT = :formula " +
                "WHERE SERVICE_ID = :parentId " +
                "AND DEPT_ID = :parentDeptId";

        Query query = em.createNativeQuery(sql)
                .setParameter("parentId", idParent)
                .setParameter("parentDeptId", parentDeptId)
                .setParameter("formula", formula);

        return query.executeUpdate();
    }

    @Override
    @Transactional
    public void delete(Long serviceId) {

        String delServiceGBTD = "DELETE FROM service_gbtd WHERE SERVICE_ID = :serviceId";
        Query query = em.createNativeQuery(delServiceGBTD);
        query.setParameter(SERVICE_ID, serviceId);
        query.executeUpdate();

        String delMapDept = "DELETE FROM services_map_dept WHERE SERVICE_ID = :serviceId";
        query = em.createNativeQuery(delMapDept);
        query.setParameter(SERVICE_ID, serviceId);
        query.executeUpdate();

        String delTreeGBTD = "DELETE FROM bi_td_services_tree WHERE PARENT_SERVICE_ID = :serviceId";
        query = em.createNativeQuery(delTreeGBTD);
        query.setParameter(SERVICE_ID, serviceId);
        query.executeUpdate();

        String delGBTDDefine = "DELETE FROM service_gbtd_define WHERE SERVICE_ID = :serviceId";
        query = em.createNativeQuery(delGBTDDefine);
        query.setParameter(SERVICE_ID, serviceId);
        query.executeUpdate();
    }

    public List<ServiceGBTDDto> findServiceGBTDByDeptId(ServiceGBTDDto dto) {
        String sql = "select " +
                "a.service_id || '_' || b.dept_id serviceDeptId, " +
                "a.service_id serviceId, " +
                "a.service_name serviceName, " +
                "a.service_id || '_' || a.service_name serviceDisplay," +
                "null as parentServiceId, " +
                "null as parentId, " +
                "b.dept_id deptId, " +
                "null deptCode, " +
                "(SELECT count(id) FROM BI_TD_SERVICES_TREE WHERE PARENT_SERVICE_ID = a.SERVICE_ID) hasChildren " +
                "from service_gbtd a " +
                "inner join services_map_dept b on " +
                "a.SERVICE_ID = b.SERVICE_ID " +
                "and b.dept_id = :deptId " +
                "WHERE 1=1 ";

        if (Const.SERVICE_TYPE.CT_NGUOIDUNG == dto.getCheckServiceType()) {
            sql += "AND a.service_type = ('-1') ";
        } else if (Const.SERVICE_TYPE.CT_KINHDOANH == dto.getCheckServiceType()) {
            sql += "AND a.service_type NOT IN ('-1') ";
        }
        sql += "ORDER BY a.SERVICE_ID ";
        //fix Api-resultset
        Query query = em.createNativeQuery(sql, "serviceGbtd.targetDto")
                .setParameter(DEPT_ID, dto.getDeptId());
        return query.getResultList();
    }

    @Override
    public List<ServiceGBTDDto> findChildrenService(ServiceGBTDDto dto) {
        String sql = "SELECT " +
                "s.SERVICE_ID || '_' || st.dept_id serviceDeptId, st.TYPE_PARAM typeParam, " +
                "s.SERVICE_ID serviceId, " +
                "s.SERVICE_NAME serviceName, " +
                "s.SERVICE_ID || '_' || s.SERVICE_NAME   serviceDisplay, " +
                "st.PARENT_SERVICE_ID AS parentServiceId, " +
                "st.PARENT_SERVICE_ID || '_' || :deptId AS parentId, " +
                "st.DEPT_ID deptId, " +
                "st.DEPT_CODE deptCode, " +
                "(SELECT count(id) FROM BI_TD_SERVICES_TREE WHERE PARENT_SERVICE_ID = s.SERVICE_ID and PARENT_DEPT_ID is not null and st.TYPE_PARAM       IN (:typeParam)) hasChildren " +
                "FROM SERVICE_GBTD s " +
                "LEFT JOIN BI_TD_SERVICES_TREE st ON st.SERVICE_ID = s.SERVICE_ID " +
                "WHERE 1=1 " +
                "AND st.PARENT_SERVICE_ID = :parentServiceId " +
                "AND st.PARENT_DEPT_ID = :deptId  ";
        if ( null != dto.getTypeParams()){
            sql += "AND st.TYPE_PARAM IN (:typeParam) ";
        }

        if (null != dto.getCheckServiceType()) {
            if (Const.SERVICE_TYPE.CT_NGUOIDUNG == dto.getCheckServiceType()) {
                sql += "AND s.service_type = ('-1') ";
            } else if (Const.SERVICE_TYPE.CT_KINHDOANH == dto.getCheckServiceType()) {
                sql += "AND s.service_type NOT IN ('-1') ";
            }
        }
        sql += "ORDER BY s.order_index, s.SERVICE_ID ";

        Query query = em.createNativeQuery(sql, "serviceGbtd.targetDto2")
                .setParameter("parentServiceId", dto.getServiceId())
                .setParameter(DEPT_ID, dto.getDeptId());
        if ( null != dto.getTypeParams()){
            query.setParameter(Const.TYPE_PARAM,dto.getTypeParams());
        }
        return query.getResultList();
    }

    @Override
    public List<ServiceGBTDDto> findServiceOfDept(ServiceGBTDDto dto) {
        String sql = "select distinct  " +
                "a.service_id serviceId, " +
                "a.service_id ||'_'|| a.service_name serviceDisplay, " +
                "a.ORDER_INDEX orderIndex, " +
                "a.SERVICE_NAME serviceName, " +
                "cu.type_unit typeUnit " +
                "from service_gbtd a " +
                "left join cat_unit cu on cu.id = a.unit_id " +
                "inner join services_map_dept b on a.SERVICE_ID = b.SERVICE_ID and b.dept_id in (:deptIds) " +
                "where " +
                "a.status = 1 " +
                "and a.type_param in :typeParam " +
                "order by  " +
                "a.order_index, a.service_name ";

        Query query = em.createNativeQuery(sql, "serviceGbtd.findServiceOfDept")
                .setParameter(Const.TYPE_PARAM, dto.getTypeParams());
        if (!DataUtil.isNullOrEmpty(dto.getIds())) {
            query.setParameter("deptIds", dto.getIds());
        } else {
            query.setParameter("deptIds", dto.getDeptIds());
        }
        return query.getResultList();
    }

    @Override
    public List<ServiceGBTDDto> findServiceGBTDByDeptIdIn(ServiceGBTDDto dto) {
        String sql = "select DISTINCT " +
                "a.service_id || '_' || b.dept_id serviceDeptId, " +
                "a.service_id serviceId, " +
                "a.service_name serviceName, bi.TYPE_PARAM typeParam,  " +
                "a.service_id || '_' || a.service_name serviceDisplay," +
                "null as parentServiceId, " +
                "null as parentId, " +
                "b.dept_id deptId, " +
                "c.code deptCode, " +
                "(SELECT count(id) FROM BI_TD_SERVICES_TREE WHERE PARENT_SERVICE_ID = a.SERVICE_ID and PARENT_DEPT_ID is not null and bi.TYPE_PARAM IN (:typeParam ) ) hasChildren " +
                "from service_gbtd a " +
                "inner join services_map_dept b on " +
                "a.SERVICE_ID = b.SERVICE_ID " +
                "INNER JOIN CAT_DEPARTMENT c\n" +
                "ON c.id = b.dept_id " +
                "and b.dept_id in (:deptId) " +
                " INNER JOIN BI_TD_SERVICES_TREE bi ON bi.SERVICE_ID = a.SERVICE_ID " +
                "WHERE 1=1 ";
//        if ( null != dto.getTypeParams()){
//            sql += "AND a.TYPE_PARAM IN :typeParam ";
//        }

        if (!StringUtils.isEmpty(dto.getDeptIdSearch())) {
            sql += this.createSql(dto.getDeptIdSearch());
        } else {
            if ( null != dto.getTypeParams()){
                sql += "AND bi.TYPE_PARAM IN :typeParam ";
            }
        }

        if (Const.SERVICE_TYPE.CT_NGUOIDUNG == dto.getCheckServiceType()) {
            sql += "AND a.service_type = ('-1') ";
        } else if (Const.SERVICE_TYPE.CT_KINHDOANH == dto.getCheckServiceType()) {
            sql += "AND a.service_type NOT IN ('-1') ";
        }
        sql += "ORDER BY a.SERVICE_ID ";

        Query query = em.createNativeQuery(sql, "serviceGbtd.targetDto2")
                .setParameter(DEPT_ID, dto.getDeptIds());
        if ( null != dto.getTypeParams()){
            query.setParameter(Const.TYPE_PARAM,dto.getTypeParams());
        }
        if ( null != dto.getTypeParams() && StringUtils.isEmpty(dto.getDeptIdSearch())){
            query.setParameter(Const.TYPE_PARAM,dto.getTypeParams());
        }
        return query.getResultList();
    }

    private String createSql(String str) {
        String[] strArr = str.split(",");
        String temp = " AND (";
        for (int i = 0; i < strArr.length; i = i + 2) {
            if (i == 0) {
                temp += "( bi.TYPE_PARAM ='" + strArr[i+1] + "' AND b.DEPT_ID =" + strArr[i] + ")";
            } else {
                temp += " OR ( bi.TYPE_PARAM ='" + strArr[i+1] + "' AND b.DEPT_ID =" + strArr[i] + ")";
            }
        }
        temp += ")";
        return temp;
    }
}
