package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.CatUnitDto;
import com.lifesup.gbtd.dto.object.CatUnitRateDto;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.repository.CatUnitRepository;
import com.lifesup.gbtd.service.inteface.ICatUnitService;
import com.lifesup.gbtd.util.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatUnitService extends BaseService implements ICatUnitService {

    private final CatUnitRepository catUnitRepository;

    @Autowired
    public CatUnitService(CatUnitRepository catUnitRepository) {
        this.catUnitRepository = catUnitRepository;
    }

    @Override
    public List<CatUnitDto> findByStatus(Long status) {
        return catUnitRepository.findAllByStatus(status)
                .stream()
                .map(e -> super.map(e, CatUnitDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public CatUnitDto findById(Long id) {
        return catUnitRepository.findByIdAndStatus(id, Const.STATUS.ACTIVE)
                .map(e -> super.map(e, CatUnitDto.class))
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "catUnit"));
    }

    @Override
    public List<CatUnitDto> findByIds(List<Long> ids) {
        return catUnitRepository.findByIdInAndStatus(ids, Const.STATUS.ACTIVE).stream()
                .map(e -> super.map(e, CatUnitDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CatUnitRateDto> findConverter(Long before, List<Long> after) {
        return catUnitRepository.findConverter(before, after);
    }

    @Override
    public List<CatUnitRateDto> findConverter(Long before, Long after) {
        return catUnitRepository.findConverter(before, Collections.singletonList(after));
    }

    @Override
    public List<CatUnitDto> getAll(CatUnitDto dto) {
        return super.mapList(catUnitRepository.findWithParam(dto), CatUnitDto.class);
    }
}
