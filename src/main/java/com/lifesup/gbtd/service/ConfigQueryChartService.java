package com.lifesup.gbtd.service;

import com.lifesup.gbtd.dto.object.ConfigQueryChartDto;
import com.lifesup.gbtd.repository.ConfigQueryChartRepository;
import com.lifesup.gbtd.service.inteface.IConfigQueryChartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ConfigQueryChartService extends BaseService implements IConfigQueryChartService {

    private ConfigQueryChartRepository configQueryChartRepository;

    @Autowired
    public ConfigQueryChartService(ConfigQueryChartRepository configQueryChartRepository) {
        this.configQueryChartRepository = configQueryChartRepository;
    }

    @Override
    public List<ConfigQueryChartDto> findByIds(List<Long> ids) {
        return configQueryChartRepository.findByIdIn(ids)
                .stream()
                .map(e -> super.map(e, ConfigQueryChartDto.class))
                .collect(Collectors.toList());
    }
}
