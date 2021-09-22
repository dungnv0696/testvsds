package com.lifesup.gbtd.service;

import com.lifesup.gbtd.dto.object.ConfigAreaDto;
import com.lifesup.gbtd.dto.object.ConfigChartDto;
import com.lifesup.gbtd.dto.object.ConfigDashboardDto;
import com.lifesup.gbtd.dto.object.ConfigMapChartAreaDto;
import com.lifesup.gbtd.model.ConfigChartEntity;
import com.lifesup.gbtd.model.ConfigDashboardEntity;
import com.lifesup.gbtd.model.ConfigMapChartAreaEntity;
import com.lifesup.gbtd.repository.ConfigAreaRepository;
import com.lifesup.gbtd.repository.ConfigChartRepository;
import com.lifesup.gbtd.repository.ConfigDashboardRepository;
import com.lifesup.gbtd.repository.ConfigMapChartAreaRepository;
import com.lifesup.gbtd.service.inteface.IConfigAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ConfigAreaService extends BaseService implements IConfigAreaService {

    private final ConfigAreaRepository configAreaRepository;
    private final ConfigMapChartAreaRepository configMapChartAreaRepository;
    private final ConfigDashboardRepository configDashboardRepository;
    private final ConfigChartRepository configChartRepository;

    @Autowired
    public ConfigAreaService(ConfigAreaRepository configAreaRepository,
                             ConfigMapChartAreaRepository configMapChartAreaRepository,
                             ConfigDashboardRepository configDashboardRepository,
                             ConfigChartRepository configChartRepository) {
        this.configAreaRepository = configAreaRepository;
        this.configMapChartAreaRepository = configMapChartAreaRepository;
        this.configDashboardRepository = configDashboardRepository;
        this.configChartRepository = configChartRepository;
    }

    @Override
    public List<ConfigAreaDto> getByDashboardId(Long dashboardId, ConfigDashboardDto filter) {
        List<ConfigAreaDto> areaDtos = super.mapList(configAreaRepository.findByDashboardId(dashboardId), ConfigAreaDto.class);
        List<Long> areaIds = areaDtos.stream().map(ConfigAreaDto::getId).collect(Collectors.toList());
        List<ConfigMapChartAreaEntity> mapChartEntities = configMapChartAreaRepository.findByAreaIdIn(areaIds);

        List<Long> chartIds = new ArrayList<>();
        List<Long> nextDashboardIds = new ArrayList<>();
        mapChartEntities.forEach(e -> {
            if (Objects.nonNull(e.getChartId())) {
                chartIds.add(e.getChartId());
            }
            if (Objects.nonNull(e.getDashboardIdNextto())) {
                nextDashboardIds.add(e.getDashboardIdNextto());
            }
        });

        List<ConfigChartEntity> charts = configChartRepository.findWithFilter(chartIds, new ConfigChartDto(filter.getTimeType()));
        List<ConfigDashboardEntity> nextDashboards = configDashboardRepository.findAllById(nextDashboardIds);

        if (chartIds.size() != charts.size()) {
            mapChartEntities = mapChartEntities.stream()
                    .filter(mc -> charts.stream().anyMatch(c -> c.getId().equals(mc.getChartId())))
                    .collect(Collectors.toList());
            List<ConfigMapChartAreaDto> mapChartAreaDtos = super.mapList(mapChartEntities, ConfigMapChartAreaDto.class);
            areaDtos = areaDtos.stream().peek(a ->
                    this.setChartInfoForMapChart(mapChartAreaDtos, a, charts, nextDashboards)
            ).collect(Collectors.toList());
        } else {
            List<ConfigMapChartAreaDto> mapChartAreaDtos = super.mapList(mapChartEntities, ConfigMapChartAreaDto.class);
            areaDtos = areaDtos.stream().peek(a ->
                    this.setChartInfoForMapChart(mapChartAreaDtos, a, charts, nextDashboards)
            ).collect(Collectors.toList());
        }

        return areaDtos;
    }

    private void setChartInfoForMapChart(List<ConfigMapChartAreaDto> mapChartAreaDtos,
                                         ConfigAreaDto a,
                                         List<ConfigChartEntity> charts,
                                         List<ConfigDashboardEntity> nextDashboards) {
        a.setMapCharts(mapChartAreaDtos.stream()
                .filter(mc -> Objects.equals(mc.getAreaId(), a.getId()))
                .map(mc -> super.map(mc, ConfigMapChartAreaDto.class))
                .peek(mc -> {
                    if (Objects.nonNull(mc.getChartId())) {
                        mc.setChartName(charts.stream()
                                .filter(c -> Objects.equals(c.getId(), mc.getChartId()))
                                .findFirst()
                                .map(ConfigChartEntity::getChartName)
                                .orElse(null)
                        );
                        mc.setDashboardNexttoName(nextDashboards.stream()
                                .filter(d -> Objects.equals(d.getId(), mc.getDashboardIdNextto()))
                                .findFirst()
                                .map(ConfigDashboardEntity::getDashboardName)
                                .orElse(null)
                        );
                        mc.setTitleChart(charts.stream()
                                .filter(c -> Objects.equals(c.getId(), mc.getChartId()))
                                .findFirst()
                                .map(ConfigChartEntity::getTitleChart)
                                .orElse(null)
                        );
                    }
                }).collect(Collectors.toList()));
    }
}
