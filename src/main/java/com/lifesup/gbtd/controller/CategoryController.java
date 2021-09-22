package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.CatItemDto;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.service.CatItemService;
import com.lifesup.gbtd.service.CategoryService;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Update;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/category")
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;
    private final CatItemService catItemService;

    @Autowired
    public CategoryController(CategoryService categoryService, CatItemService catItemService) {
        this.categoryService = categoryService;
        this.catItemService = catItemService;
    }

    @GetMapping("/loadDefault")
    public GenericResponse<HashMap> getAllCategoryAndCatItem() {
        GenericResponse<HashMap> res = new GenericResponse<>();
        HashMap<String, List> data = new HashMap<>();

        data.put("categories", categoryService.findAllByOrderByCategoryNameAsc());
        data.put("catItemsSearch", catItemService.getCatItems());
        data.put("catItems", catItemService.findAll(new CatItemDto()));
        res.setData(data);
        res.success();
        return res;
    }

    @PostMapping("/add")
    public GenericResponse<CatItemDto> addCatItem(@Validated(value = Add.class) @RequestBody CatItemDto dto) {
        GenericResponse<CatItemDto> res = new GenericResponse<>();
        catItemService.add(dto);
        res.success();
        return res;
    }

    @PostMapping("/update")
    public GenericResponse<CatItemDto> updateCatItem(@Validated(value = Update.class) @RequestBody CatItemDto dto) {
        GenericResponse<CatItemDto> res = new GenericResponse<>();
        catItemService.update(dto);
        res.success();
        return res;
    }

    @PostMapping("/findByParams")
    public GenericResponse<List<CatItemDto>> findByParams(@RequestBody CatItemDto dto) {
        GenericResponse<List<CatItemDto>> res = new GenericResponse<>();
//        res.setData(catItemService.findCatItems(dto.getCategoryIds(), dto.getParentName(), dto.getItemName(),
//                dto.getItemCode(), dto.getItemValue(), dto.getPage(), dto.getPageSize()));
        res.setData(catItemService.findCatItems(dto));
        res.setPaging(dto);
        res.success();
        return res;
    }

    @PostMapping("/findById")
    public GenericResponse<List<CatItemDto>> findById(@RequestBody CatItemDto dto) {
        GenericResponse<List<CatItemDto>> res = new GenericResponse<>();
        res.setData(catItemService.findByIds(dto.getItemIds()));
        res.success();
        return res;
    }

    @PostMapping("/delete")
    public GenericResponse<Boolean> deleteById(@RequestBody CatItemDto dto) {
        GenericResponse<Boolean> res = new GenericResponse<>();
        catItemService.deleteCatItemById(dto.getItemId());
        res.success();
        return res;
    }
}

