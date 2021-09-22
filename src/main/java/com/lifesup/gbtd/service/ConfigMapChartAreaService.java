package com.lifesup.gbtd.service;

import com.lifesup.gbtd.repository.ConfigMapChartAreaRepository;
import com.lifesup.gbtd.service.inteface.IConfigMapChartAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConfigMapChartAreaService extends BaseService implements IConfigMapChartAreaService {

    private ConfigMapChartAreaRepository configMapChartAreaRepository;

    @Autowired
    public ConfigMapChartAreaService(ConfigMapChartAreaRepository configMapChartAreaRepository) {
        this.configMapChartAreaRepository = configMapChartAreaRepository;
    }
}
