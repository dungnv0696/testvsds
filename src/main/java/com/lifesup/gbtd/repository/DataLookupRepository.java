package com.lifesup.gbtd.repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DataLookupRepository {
    Long getLatestDate(Long[] serviceIds, String[] deptCodes, Long timeType);

    List<Object> executeGetDataSql(String sql, Map<String, Object> params);
}
