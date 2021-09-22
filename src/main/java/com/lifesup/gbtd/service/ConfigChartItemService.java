package com.lifesup.gbtd.service;

import com.lifesup.gbtd.dto.object.ConfigChartItemDto;
import com.lifesup.gbtd.repository.ConfigChartItemRepository;
import com.lifesup.gbtd.service.inteface.IConfigChartItemService;
import com.lifesup.gbtd.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ConfigChartItemService extends BaseService implements IConfigChartItemService {

    private ConfigChartItemRepository configChartItemRepository;

    @Autowired
    public ConfigChartItemService(ConfigChartItemRepository configChartItemRepository) {
        this.configChartItemRepository = configChartItemRepository;
    }

    @Override
    public List<ConfigChartItemDto> findByChartId(Long chartId) {
        return configChartItemRepository.findByChartIdAndStatus(chartId, Const.STATUS.ACTIVE)
                .stream()
                .map(e -> super.map(e, ConfigChartItemDto.class))
                .collect(Collectors.toList());
    }
}
