package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import com.lifesup.gbtd.dto.object.DashboardParamTreeDto;
import com.lifesup.gbtd.repository.DashboardParamTreeRepositoryCustom;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.JpaUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;

@Transactional
public class DashboardParamTreeRepositoryCustomImpl implements DashboardParamTreeRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<CatDepartmentDto> doSearch(DashboardParamTreeDto dto) {
        HashMap<String, Object> params = new HashMap<>();
        String sql = "SELECT DISTINCT " +
                "b.DEPT_ID id, " +
                "b.code, " +
                "b.name, " +
                "b.PARENT_DEPT_ID parent, " +
                "b.start_time startTime, " +
                "b.end_time endTime " +
                "FROM DASHBOARD_PARAM_TREE b " +
                "CONNECT BY NOCYCLE PRIOR b.dept_id = b.PARENT_dept_id ";

        if (Strings.isNotEmpty(dto.getTypeParam())) {
            sql += "AND b.TYPE_PARAM = :typeParam ";
            params.put("typeParam", dto.getTypeParam());
        }
        if (Strings.isNotEmpty(dto.getName())) {
            sql += "AND LOWER(b.NAME) LIKE :name ESCAPE '&' ";
            params.put("name", DataUtil.makeLikeParam(dto.getName()));
        }
        sql += "START WITH 1 = 1 AND (select a.dept_level from cat_department a where a.code = b.code) in (1, 6) and b.DEPT_ID is not null ";

        if (Strings.isNotEmpty(dto.getTypeParam())) {
            sql += "AND b.TYPE_PARAM = :typeParam ";
            params.put("typeParam", dto.getTypeParam());
        }
        if (Strings.isNotEmpty(dto.getName())) {
            sql += "AND LOWER(b.NAME) LIKE :name ESCAPE '&' ";
            params.put("name", DataUtil.makeLikeParam(dto.getName()));
        }
        Query query = em.createNativeQuery(sql, "getDashboardParamTree.search");
        JpaUtil.setQueryParams(query, params);
        return query.getResultList();
    }

    @Override
    public List<CatDepartmentDto> doSearchList() {
        String sql = "SELECT DISTINCT " +
                "b.DEPT_ID id, " +
                "b.code, " +
                "b.name, " +
                "b.PARENT_DEPT_ID parent, " +
                "c.name parentName, " +
                "b.start_time startTime, " +
                "b.end_time endTime " +
                "FROM DASHBOARD_PARAM_TREE b inner join cat_department c on b.PARENT_DEPT_ID = c.id ";
        Query query = em.createNativeQuery(sql, "getDashboardParamTree.search");
        return query.getResultList();
    }
}
