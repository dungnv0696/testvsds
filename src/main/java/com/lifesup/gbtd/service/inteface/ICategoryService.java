package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.CategoryDto;

import java.util.List;

public interface ICategoryService {
    List<CategoryDto> findAllByOrderByCategoryNameAsc();
}
