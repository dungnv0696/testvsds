package com.lifesup.gbtd.service;

import com.lifesup.gbtd.dto.object.CatGroupChartDto;
import com.lifesup.gbtd.repository.CatGroupChartRepository;
import com.lifesup.gbtd.service.inteface.ICatGroupChartService;
import com.lifesup.gbtd.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class CatGroupChartServiceImpl extends BaseService implements ICatGroupChartService {

    private final CatGroupChartRepository catGroupChartRepository;

    @Autowired
    public CatGroupChartServiceImpl(CatGroupChartRepository catGroupChartRepository) {
        this.catGroupChartRepository = catGroupChartRepository;
    }

    @Override
    public Page<CatGroupChartDto> findAllCatGroups(Long dashboardId, Long status, Pageable pageable) {
        return catGroupChartRepository.findAllCatGroups(dashboardId, status, pageable);
    }

    @Override
    public Page<CatGroupChartDto> findAllCatGroups(CatGroupChartDto dto, Pageable pageable) {
        return catGroupChartRepository.findAllCatGroups(dto, pageable);
    }
}
