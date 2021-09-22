package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.ConfigProfileDto;
import com.lifesup.gbtd.repository.ConfigProfileRepositoryCustom;
import com.lifesup.gbtd.util.Const;
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

public class ConfigProfileRepositoryCustomImpl implements ConfigProfileRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<ConfigProfileDto> searchConfigProfiles(ConfigProfileDto dto, Pageable pageable) {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT DISTINCT ");
        sql.append(this.getFieldSelect());
        sql.append("cp.update_time as updateTime, ");
        sql.append("cp.update_user as updateUser, ");
        sql.append("(select count(id) from config_dashboard where profile_id = cp.id) as hasChild, ");
        sql.append(" '' as roleCode ");
        sql.append("From CONFIG_PROFILE cp INNER JOIN CONFIG_PROFILE_ROLE cpr ON cp.ID = cpr.PROFILE_ID ");
        sql.append("WHERE 1 = 1 ");

        if (StringUtils.isNotEmpty(dto.getProfileName())) {
            sql.append("AND LOWER(cp.profile_name) like :profileName ESCAPE '&' ");
            params.put("profileName", DataUtil.makeLikeParam(dto.getProfileName()));
        }
        if (Objects.nonNull(dto.getRoleType())) {
            sql.append("AND cp.role_type = :roleType ");
            params.put("roleType", dto.getRoleType());
        }
        if (Objects.nonNull(dto.getIsDefault())) {
            sql.append("AND cp.is_default = :isDefault ");
            params.put("isDefault", dto.getIsDefault());
        }
        if (StringUtils.isNotEmpty(dto.getUsernameUsed())) {
            sql.append("AND cpr.username_used IN (select username from USERS d where d.status = :statusUser and " +
                    "(LOWER(d.username) LIKE :usernameUsed ESCAPE '&' OR LOWER(d.name) LIKE :usernameUsed ESCAPE '&' OR " +
                    "LOWER(d.email) LIKE :usernameUsed ESCAPE '&' OR LOWER(d.staff_code) LIKE :usernameUsed ESCAPE '&' )) ");
            params.put("usernameUsed", DataUtil.makeLikeParam(dto.getUsernameUsed()));
            params.put("statusUser", Const.STATUS.ACTIVE);
        }
        if (Objects.nonNull(dto.getDeptId())) {
            sql.append("AND cpr.dept_id = :deptId ");
            params.put("deptId", dto.getDeptId());
        }
        sql.append("AND cp.status = :status ORDER BY cp.ID desc");
        Long status = dto.getStatus() != null ? dto.getStatus() : Const.STATUS.ACTIVE;
        params.put("status", status);

        String sqlCount = "SELECT COUNT(*) FROM ( " + sql.toString() + " )";
        Query queryCount = em.createNativeQuery(sqlCount);
        JpaUtil.setQueryParams(queryCount, params);

        Query query = em.createNativeQuery(sql.toString(), "ConfigProfile.search")
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());
        JpaUtil.setQueryParams(query, params);

        List<ConfigProfileDto> configProfileDtos = query.getResultList();
        Long count = ((BigDecimal) queryCount.getSingleResult()).longValue();
        return new PageImpl<>(configProfileDtos, pageable, count);
    }

    @Override
    public Page<ConfigProfileDto> getAllConfigProfiles(Pageable pageable, String username, Long deptId) {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT ")
                .append("id, ")
                .append("profileName, ")
                .append("isDefault, ")
                .append("orderIndex, ")
                .append("roleType, ")
                .append("status, ")
                .append("description, ")
                .append("updateTime, ")
                .append("updateUser, ")
                .append("hasChild, ")
                .append("roleCode ")
                .append("from ( ");
        sql.append("SELECT DISTINCT ");
        sql.append(this.getFieldSelect());
        sql.append("cp.update_time as updateTime, ");
        sql.append("cp.update_user as updateUser, ");
        sql.append("(select count(id) from config_dashboard where profile_id = cp.id) as hasChild, ");
        sql.append("cpr.role_code as roleCode ");
        sql.append("From CONFIG_PROFILE cp INNER JOIN CONFIG_PROFILE_ROLE cpr ON cp.ID = cpr.PROFILE_ID ");
        sql.append("WHERE cp.status = :status AND cp.role_type = :role_type_dv " +
                "AND cpr.dept_id IN ( :deptId ) ");
        sql.append("UNION ALL ");
        sql.append("SELECT DISTINCT cp.ID as id, ");
        sql.append("cp.profile_name as profileName, ");
        sql.append("cp.is_default as isDefault, ");
        sql.append("cp.order_index as orderIndex, ");
        sql.append("cp.role_type as roleType, ");
        sql.append("cp.status as status, ");
        sql.append("cp.description as description, ");
        sql.append("cp.update_time as updateTime, ");
        sql.append("cp.update_user as updateUser, ");
        sql.append("(select count(id) from config_dashboard where profile_id = cp.id) as hasChild, ");
        sql.append("cpr.role_code as roleCode ");
        sql.append("From CONFIG_PROFILE cp INNER JOIN CONFIG_PROFILE_ROLE cpr ON cp.ID = cpr.PROFILE_ID ");
        sql.append("WHERE cp.status = :status AND cp.role_type = :role_type_cn " +
                "AND cpr.username_used IN (SELECT u.username FROM USERS u " +
                "WHERE u.status = :status and u.username = :username ) ");
        sql.append(") ")
                .append("ORDER BY isDefault desc, roleType, orderIndex, profileName ");

        params.put("status", Const.STATUS.ACTIVE);
        params.put("role_type_dv", Const.ROLE_TYPE.THEO_DON_VI);
        params.put("role_type_cn", Const.ROLE_TYPE.CA_NHAN);
        params.put("username", username);
        params.put("deptId", deptId);

        String sqlCount = "SELECT COUNT(*) FROM ( " + sql.toString() + " )";
        Query queryCount = em.createNativeQuery(sqlCount);
        JpaUtil.setQueryParams(queryCount, params);

        Query query = em.createNativeQuery(sql.toString(), "ConfigProfile.search")
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());
        JpaUtil.setQueryParams(query, params);

        List<ConfigProfileDto> configProfileDtos = query.getResultList();
        Long count = ((BigDecimal) queryCount.getSingleResult()).longValue();
        return new PageImpl<>(configProfileDtos, pageable, count);
    }

    @Override
    public List<ConfigProfileDto> getByDeptIdAndUsernameUsedAndIds(Long deptId, String userNameUsed, List<Long> ids) {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT DISTINCT ");
        sql.append(this.getFieldSelect());
        sql.append("cp.update_time as updateTime, ");
        sql.append("cp.update_user as updateUser ");
        sql.append("From CONFIG_PROFILE cp INNER JOIN CONFIG_PROFILE_ROLE cpr ON cp.ID = cpr.PROFILE_ID ");
        sql.append("WHERE cpr.dept_id = :deptId AND cpr.role_code= :roleCode AND cp.id in :ids  ");
        sql.append("UNION ALL ");
        sql.append("SELECT DISTINCT cp.ID as id, ");
        sql.append("cp.profile_name as profileName, ");
        sql.append("cp.is_default as isDefault, ");
        sql.append("cp.order_index as orderIndex, ");
        sql.append("cp.role_type as roleType, ");
        sql.append("cp.status as status, ");
        sql.append("cp.description as description, ");
        sql.append("cp.update_time as updateTime, ");
        sql.append("cp.update_user as updateUser ");
        sql.append("From CONFIG_PROFILE cp INNER JOIN CONFIG_PROFILE_ROLE cpr ON cp.ID = cpr.PROFILE_ID ");
        sql.append("WHERE cpr.USERNAME_USED = :userNameUsed AND cpr.role_code = :roleCode AND cp.id in :ids  ");

        params.put("deptId", deptId);
        params.put("roleCode", Const.ROLE_CODE.ADMIN);
        params.put("userNameUsed", userNameUsed);
        params.put("ids", ids);
        Query query = em.createNativeQuery(sql.toString(), "ConfigProfile");
        JpaUtil.setQueryParams(query, params);
        return query.getResultList();
    }

    @Override
    public List<ConfigProfileDto> getAllConfigProfilesAndOrderBy(ConfigProfileDto dto, String username, Long deptId, Pageable pageable) {
        HashMap<String, Object> params = new HashMap<>();
        String tempSubQuery = "";
        if (!StringUtils.isEmpty(dto.getRoleCode())) {
            tempSubQuery += "Where role_code = :roleCode ";
            params.put("roleCode", dto.getRoleCode());
        }
        String sql = "SELECT ID as id," +
                "profile_name as profileName, " +
                "is_default as isDefault, " +
                "role_type as roleType, " +
                "status as status, " +
                "ORDER_index as orderIndex, " +
                "description as description, " +
                "update_time as updateTime, " +
                "update_user as updateUser, " +
                "role_code as roleCode " +
                "FROM (SELECT DISTINCT " +
                "cp.ID, " +
                "cp.profile_name, " +
                "cp.is_default, " +
                "cp.role_type, " +
                "cp.status, " +
                "cp.ORDER_index, " +
                "cp.description, " +
                "cp.update_time, " +
                "cp.update_user, " +
                "cpr.role_code " +
                "From CONFIG_PROFILE cp INNER JOIN CONFIG_PROFILE_ROLE cpr ON cp.ID = cpr.PROFILE_ID " +
                "WHERE cp.status = :status AND cp.role_type = :role_type_dv " +
                "AND cpr.dept_id IN ( :deptId ) " +
                "UNION ALL " +
                "SELECT DISTINCT " +
                "cp.ID, " +
                "cp.profile_name, " +
                "cp.is_default, " +
                "cp.role_type, " +
                "cp.status, " +
                "cp.ORDER_index, " +
                "cp.description, " +
                "cp.update_time, " +
                "cp.update_user, " +
                "cpr.role_code " +
                "From CONFIG_PROFILE cp INNER JOIN CONFIG_PROFILE_ROLE cpr ON cp.ID = cpr.PROFILE_ID " +
                "WHERE cp.status = :status AND cp.role_type = :role_type_cn " +
                "AND cpr.username_used IN (SELECT u.username FROM USERS u " +
                "WHERE u.status = :status and u.username = :username )) " + tempSubQuery +
                "ORDER BY is_default DESC, role_type, ORDER_index, profile_name ";

        params.put("status", Const.STATUS.ACTIVE);
        params.put("role_type_dv", Const.ROLE_TYPE.THEO_DON_VI);
        params.put("role_type_cn", Const.ROLE_TYPE.CA_NHAN);
        params.put("username", username);
        params.put("deptId", deptId);

        Query query = em.createNativeQuery(sql, "ConfigProfile.forcbb");
        JpaUtil.setQueryParams(query, params);
        return query.getResultList();
    }

    private String getFieldSelect() {
        return "cp.ID as id, " +
                "cp.profile_name as profileName, " +
                "cp.is_default as isDefault, " +
                "cp.order_index as orderIndex, " +
                "cp.role_type as roleType, " +
                "cp.status as status, " +
                "cp.description as description, ";
    }
}
