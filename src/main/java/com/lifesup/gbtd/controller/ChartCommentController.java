package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.ChartCommentDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.service.ChartCommentService;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Find;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chart-comment")
@Slf4j
public class ChartCommentController {
    private final ChartCommentService chartCommentService;

    @Autowired
    public ChartCommentController(ChartCommentService chartCommentService) {
        this.chartCommentService = chartCommentService;
    }

    @PostMapping("/add")
    public GenericResponse<ChartCommentDto> createChartComment(@Validated(value = Add.class) @RequestBody ChartCommentDto chartCommentDto) {
        return GenericResponse.success(chartCommentService.createChartComment(chartCommentDto));
    }

    @PostMapping("/delete")
    public GenericResponse delete(@RequestParam Long id) {
        GenericResponse res = new GenericResponse<>();
        chartCommentService.delete(id);
        res.success();
        return res;
    }

    @GetMapping
    public DataListResponse<ChartCommentDto> doSearch(ChartCommentDto dto) {
        return DataListResponse.success(chartCommentService.doSearch(dto));
    }
}
