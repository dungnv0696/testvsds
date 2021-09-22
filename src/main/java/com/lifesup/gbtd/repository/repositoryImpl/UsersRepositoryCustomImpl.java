package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import com.lifesup.gbtd.dto.request.UserInfoRequest;
import com.lifesup.gbtd.model.CatDepartmentEntity;
import com.lifesup.gbtd.model.UsersEntity;
import com.lifesup.gbtd.repository.UsersRepositoryCustom;
import org.apache.commons.lang.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class UsersRepositoryCustomImpl implements UsersRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<CatDepartmentDto> getUserDepartmentInfo(Long userId) {
        String sql = "SELECT " +
                "d.ID id, " +
                "d.CODE code, " +
                "d.NAME name, " +
                "d.DEPT_LEVEL deptLevel, " +
                "d.PARENT_ID parentId, " +
                "d.PARENT_CODE parentCode, " +
                "d.COUNTRY_ID countryId, " +
                "d.COUNTRY_CODE countryCode, " +
                "d.COUNTRY_NAME countryName, " +
                "d.PROVINCE_ID provinceId, " +
                "d.PROVINCE_CODE provinceCode, " +
                "d.PROVINCE_NAME provinceName, " +
                "d.COMPANY_ID companyId, " +
                "d.COMPANY_CODE companyCode, " +
                "d.COMPANY_NAME companyName, " +
                "d.START_TIME startTime, " +
                "d.END_TIME endTime " +
                "FROM users_dept ud " +
                "LEFT JOIN CAT_DEPARTMENT d ON d.ID = ud.DEPT_ID " +
                "WHERE ud.USER_ID = :userId " +
                "AND d.status = 1 ";

        Query query = em.createNativeQuery(sql, "catDept.userInfoMapping")
                .setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<UsersEntity> findUserInfo(UserInfoRequest userInfoRequest) {
        String sql = "SELECT " +
                "ID , " +
                "USERNAME , " +
                "NAME , " +
                "FIRST_NAME , " +
                "LAST_NAME , " +
                "PHONE , " +
                "EMAIL , " +
                "STAFF_CODE , " +
                "POSITION_CODE , " +
                "POSITION_NAME , " +
                "IMAGE_URL , " +
                "DEPT_ID , " +
                "ROLE_CODE , " +
                "ROLE_NAME , " +
                "STATUS , " +
                "CREATE_USER , " +
                "CREATE_TIME , " +
                "UPDATE_TIME , " +
                "UPDATE_USER  " +
                "FROM USERS " +
                "WHERE 1=1 ";

        if (StringUtils.isNotEmpty(userInfoRequest.getUsername())) {
            sql += "and USERNAME = :userName ";
        }
        if (StringUtils.isNotEmpty(userInfoRequest.getStaffCode())) {
            sql += "and staff_code = :staffCode ";
        }
        if (null != userInfoRequest.getUserId()) {
            sql += "and id = :userId ";
        }

        Query query = em.createNativeQuery(sql, UsersEntity.class);
        if (StringUtils.isNotEmpty(userInfoRequest.getUsername())) {
            query.setParameter("userName", userInfoRequest.getUsername());
        }
        if (StringUtils.isNotEmpty(userInfoRequest.getStaffCode())) {
            query.setParameter("staffCode", userInfoRequest.getStaffCode());
        }
        if (null != userInfoRequest.getUserId()) {
            query.setParameter("userId", userInfoRequest.getUserId());
        }
        return query.getResultList();
    }
}
