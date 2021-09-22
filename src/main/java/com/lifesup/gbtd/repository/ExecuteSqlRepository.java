package com.lifesup.gbtd.repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ExecuteSqlRepository {
    Integer getMaxPrdId(String sql, Map<String, Object> params);
    List<Object> executeSql(String sql, Map<String, Object> params);
}
