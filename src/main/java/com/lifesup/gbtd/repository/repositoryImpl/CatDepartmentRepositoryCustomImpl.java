package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import com.lifesup.gbtd.dto.object.ParamTreeDto;
import com.lifesup.gbtd.repository.CatDepartmentRepositoryCustom;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.JpaUtil;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Transactional
public class CatDepartmentRepositoryCustomImpl implements CatDepartmentRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<CatDepartmentDto> getDepartmentTreeByDeptId2(Long deptId, List<Long> deptLevel, String typeParam) {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT " +
                "d.TYPE_PARAM typeParam, " +
                "b.id, " +
                "b.code, " +
                "b.name, " +
                "b.parent_id parent, " +
                "b.dept_level deptLevel, " +
                "b.dept_level_code_full deptLevelCodeFull, " +
                "b.dept_level_name_full deptLevelNameFull, " +
                "b.country_id countryId, " +
                "b.province_id provinceId " +
                "FROM CAT_DEPARTMENT b " + "INNER JOIN DASHBOARD_PARAM_TREE d\n" +
                "ON b.CODE                = d.code " +
                "WHERE b.status = 1 ");

        sql.append("CONNECT BY PRIOR b.id = b.parent_id ");

        if (Objects.nonNull(deptLevel) && !deptLevel.isEmpty()) {
            sql.append(" AND b.dept_level in :deptLevel ");
            params.put("deptLevel", deptLevel);
        }
        if (Objects.nonNull(typeParam)) {
            sql.append("AND d.TYPE_PARAM = :typeParam ");
            params.put("typeParam", typeParam);
        } else {
            sql.append("AND d.TYPE_PARAM = 'DTTD' ");
        }


        if (deptId != null) {
            sql.append("START WITH b.id IN (:deptId) ");
            params.put("deptId", deptId);
            if (Objects.nonNull(typeParam)) {
                sql.append("AND d.TYPE_PARAM = :typeParam ");

            }
        } else {
            sql.append("START WITH b.parent_id is null ");
            if (Objects.nonNull(typeParam)) {
                sql.append("AND d.TYPE_PARAM = :typeParam ");

            }
        }

        Query query = em.createNativeQuery(sql.toString(), "getDepartmentMapping2");

        JpaUtil.setQueryParams(query, params);
        return query.getResultList();
    }

    @Override
    public List<CatDepartmentDto> getDepartmentTreeByDeptId(Long deptId, List<Long> deptLevel) {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT " +
                "b.id, " +
                "b.code, " +
                "b.name, " +
                "b.parent_id parent, " +
                "b.dept_level deptLevel, " +
                "b.dept_level_code_full deptLevelCodeFull, " +
                "b.dept_level_name_full deptLevelNameFull, " +
                "b.country_id countryId, " +
                "b.province_id provinceId " +
                "FROM CAT_DEPARTMENT b " +
                "WHERE b.status = 1 ");


        sql.append("CONNECT BY PRIOR b.id = b.parent_id ");

        if (Objects.nonNull(deptLevel) && !deptLevel.isEmpty()) {
            sql.append(" AND b.dept_level in :deptLevel ");
            params.put("deptLevel", deptLevel);
        }

        if (deptId != null) {
            sql.append("START WITH b.id IN (:deptId) ");
            params.put("deptId", deptId);
        } else {
            sql.append("START WITH b.parent_id is null ");
        }

        Query query = em.createNativeQuery(sql.toString(), "getDepartmentMapping");

        JpaUtil.setQueryParams(query, params);
        return query.getResultList();
    }


    @Override
    public List<CatDepartmentDto> getParamTreeDept(ParamTreeDto obj) {
        String sql = "SELECT DISTINCT " +
                "cd.DEPT_ID deptId, " +
                "cd.DEPT_NAME deptName, " +
                "cd.dept_code deptCode, " +
                "cd.PARENT_CODE parentCode, " +
                "cd.PARENT_DEPT_ID parentDeptId, " +
                "cd.TYPE_PARAM typeParam, " +
                "cd.START_TIME startTime, " +
                "cd.END_TIME endTime, " +
                "cd.DEPT_LEVEL deptLevel " +
                "FROM (SELECT a.code as dept_code, " +
                "a.DEPT_ID, " +
                "b.NAME as dept_name, " +
                "a.PARENT as parent_code, " +
                "a.PARENT_DEPT_ID, " +
                "a.TYPE_PARAM, " +
                "a.START_TIME, " +
                "a.END_TIME, " +
                "b.DEPT_LEVEL " +
                "FROM DASHBOARD_PARAM_TREE a " +
                "INNER JOIN CAT_DEPARTMENT b ON a.DEPT_ID = b.id AND b.STATUS = 1 AND a.STATUS = 1 " +
                "WHERE a.type_param in :typeParam) cd " +
                "CONNECT BY PRIOR DEPT_ID = PARENT_DEPT_ID " +
                "START WITH 1 = 1 ";

        if (obj.getDeptId() != null) {
            sql += "AND DEPT_ID = :deptId ";
        } else {
            sql += "PARENT_DEPT_ID IS null ";
        }

        Query query = em.createNativeQuery(sql, "catDept.getParamTreeDept")
                .setParameter("typeParam", obj.getTypeParams());

        if (obj.getDeptId() != null) {
            query.setParameter("deptId", obj.getDeptId());
        }
        return query.getResultList();
    }

    @Override
    public List<CatDepartmentDto> getDepartmentTreeByDeptLevelAndName(CatDepartmentDto dto) {
        HashMap<String, Object> params = new HashMap<>();
        String sql = "SELECT DISTINCT " +
                "b.id, " +
                "b.code, " +
                "b.name, " +
                "b.parent_id parent, " +
                "b.dept_level deptLevel, " +
                "b.start_time startTime, " +
                "b.end_time endTime, " +
                "b.COMPANY_ID companyId, " +
                "b.COMPANY_CODE companyCode, " +
                "b.COMPANY_NAME companyName, " +
                "b.status status " +
                "FROM CAT_DEPARTMENT b " +
                "CONNECT BY PRIOR b.parent_id = b.id " +
                "START WITH 1 = 1 ";

        if (null == dto.getStatus()) {
            sql += "AND b.status = 1 ";
        }

        if (Objects.nonNull(dto.getDeptLevel())) {
            sql += "AND b.dept_level = :deptLevel ";
            params.put("deptLevel", dto.getDeptLevel());
        }
        if (Strings.isNotEmpty(dto.getName())) {
            sql += "AND LOWER(b.NAME) LIKE :name ESCAPE '&' ";
            params.put("name", DataUtil.makeLikeParam(dto.getName()));
        }
        sql += " ORDER BY b.DEPT_LEVEL";
        Query query = em.createNativeQuery(sql, "getDepartmentMapping.search");
        JpaUtil.setQueryParams(query, params);
        return query.getResultList();
    }
}
