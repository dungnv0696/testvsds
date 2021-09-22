package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.CatLocationDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICatLocationService {
    List<CatLocationDto> getLocationForDeptLevel(CatLocationDto dto, Pageable pageable);

}