package com.lifesup.gbtd.service;

import com.lifesup.gbtd.dto.object.CategoryDto;
import com.lifesup.gbtd.repository.CategoryRepository;
import com.lifesup.gbtd.service.inteface.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService extends BaseService implements ICategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryDto> findAllByOrderByCategoryNameAsc() {
        return categoryRepository.findAllByOrderByCategoryNameAsc()
                .stream()
                .map(e -> super.map(e, CategoryDto.class))
                .collect(Collectors.toList());
    }
}
