package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.CatItemDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.service.inteface.ICatItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cat-item")
@Slf4j
public class CatItemController {

    private ICatItemService catItemService;

    @Autowired
    public CatItemController(ICatItemService catItemService) {
        this.catItemService = catItemService;
    }

    @GetMapping("/get")
    public DataListResponse<CatItemDto> get(CatItemDto catItemDto) {
        DataListResponse<CatItemDto> res = new DataListResponse<>();
        res.success();
        res.setData(catItemService.findAll(catItemDto));
        return res;
    }
}
