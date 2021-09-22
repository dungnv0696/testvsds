package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.UsersDto;
import com.lifesup.gbtd.model.UserEntity;
import com.lifesup.gbtd.model.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface UsersRepository extends JpaRepository<UsersEntity, Long>, UsersRepositoryCustom {
    @Query(value = "SELECT u.* FROM USERS u WHERE u.DEPT_ID IN " +
            "(SELECT cd.ID FROM CAT_DEPARTMENT cd " +
            "Connect By Prior cd.ID = cd.PARENT_ID " +
            "START WITH cd.ID = :deptId) AND u.ID <> :userId", nativeQuery = true)
    List<UsersEntity> findUsersByDeptIdInAndIdNot(@Param("deptId") Long deptId, @Param("userId") Long userId);

    @Query(value = "SELECT new com.lifesup.gbtd.dto.object.UsersDto " +
            "(u.username, u.name ) FROM UsersEntity u ORDER BY u.username ASC")
    List<UsersDto> findUserNameAndName();

    List<UsersEntity> findByStatus(Integer status);

    List<UsersEntity> findByUsernameOrIdOrStaffCode(String username, Long userId, String staffCode);

    List<UsersEntity> findByCreateTimeLessThan(Date lessThan);
}
