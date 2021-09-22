package com.lifesup.gbtd.service;

import com.lifesup.gbtd.dto.object.CatLocationDto;
import com.lifesup.gbtd.repository.CatLocationRepository;
import com.lifesup.gbtd.service.inteface.ICatLocationService;
import com.lifesup.gbtd.util.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CatLocationService extends BaseService implements ICatLocationService {
    private final CatLocationRepository catLocationRepository;
    public static final Long COUNTRY_ID_VN = 281L;

    @Autowired
    public CatLocationService(CatLocationRepository catLocationRepository) {
        this.catLocationRepository = catLocationRepository;
    }

    @Override
    public List<CatLocationDto> getLocationForDeptLevel(CatLocationDto dto, Pageable pageable) {
        List<CatLocationDto> catLocationDtos = new ArrayList<>();
        if (Const.DEPT_LEVEL.THI_TRUONG.equals(dto.getDeptLevel())) {
            catLocationDtos = super.mapList(
                    catLocationRepository.findByCountryIdNotAndLocationLevel(COUNTRY_ID_VN, Const.LOCATION_LEVEL.VN, pageable),
                    CatLocationDto.class);
        } else {
            if (Const.DEPT_LEVEL.TINH_TP.equals(dto.getDeptLevel())) {
                catLocationDtos = super.mapList(
                        catLocationRepository.findByCountryIdAndLocationLevel(dto.getCountryId(), Const.LOCATION_LEVEL.TINH, pageable),
                        CatLocationDto.class);
            } else if (Const.DEPT_LEVEL.QUAN_HUYEN.equals(dto.getDeptLevel())) {
                catLocationDtos = super.mapList(
                        catLocationRepository.findByProvinceIdAndLocationLevel(dto.getProvinceId(), Const.LOCATION_LEVEL.HUYEN, pageable),
                        CatLocationDto.class);
            }
        }
        return catLocationDtos;
    }
}