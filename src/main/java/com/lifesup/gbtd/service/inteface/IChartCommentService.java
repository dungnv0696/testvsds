package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.ChartCommentDto;

import java.util.List;

public interface IChartCommentService {
    ChartCommentDto createChartComment(ChartCommentDto chartCommentDto);
    void delete(Long id);
    List<ChartCommentDto> doSearch(ChartCommentDto dto);
}
