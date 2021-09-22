package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.UserLogDto;
import com.lifesup.gbtd.model.UserLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLogRepository extends JpaRepository<UserLogEntity,Long>, UserLogRepositoryCustom {
    @Query(value = "SELECT MAX(PRD_ID) \n" +
            "FROM user_log \n" +
            "WHERE ENDPOINT_CODE = 'LOGIN'\n" +
            "and PRD_ID >= ?1 " +
            "and PRD_ID <= ?2 ", nativeQuery = true)
    public Long getMaxPrdId(Long from,Long to);
    @Query(value = "SELECT * FROM user_log where ENDPOINT_CODE = 'LOGIN' and user_id = ?1 ",nativeQuery = true)
    public List<UserLogEntity> checkLoginFirstTime(Long userId);
}
