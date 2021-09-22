package com.lifesup.gbtd.service;

import com.lifesup.gbtd.dto.object.ConfigDisplayQueryDto;
import com.lifesup.gbtd.repository.ConfigDisplayQueryRepository;
import com.lifesup.gbtd.service.inteface.IConfigDisplayQueryService;
import com.lifesup.gbtd.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ConfigDisplayQueryService extends BaseService implements IConfigDisplayQueryService {

    private ConfigDisplayQueryRepository configDisplayQueryRepository;

    @Autowired
    public ConfigDisplayQueryService(ConfigDisplayQueryRepository configDisplayQueryRepository) {
        this.configDisplayQueryRepository = configDisplayQueryRepository;
    }

    @Override
    public List<ConfigDisplayQueryDto> findByChartItemIds(List<Long> chartItemIds) {
        return configDisplayQueryRepository.findByItemChartIdInAndStatus(chartItemIds, Const.STATUS.ACTIVE)
                .stream()
                .map(e -> super.map(e, ConfigDisplayQueryDto.class))
                .collect(Collectors.toList());
    }
}
