package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.ConfigChartDto;
import com.lifesup.gbtd.dto.object.TableDto;
import com.lifesup.gbtd.model.ConfigChartEntity;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.Select;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ConfigChartRepositoryCustom {

    List<ConfigChartEntity> findUserChartByType(ConfigChartDto dto, List<Long> userDeptIds, List<String> userNames);

    Page<ConfigChartEntity> doSearch(ConfigChartDto dto, List<Long> userDeptIds, List<String> userNames, Pageable pageable);

    List<TableDto> getDescriptionOfTableToMap(String tableName);

    List<ConfigChartEntity> findWithFilter(List<Long> ids, ConfigChartDto filter);
}
