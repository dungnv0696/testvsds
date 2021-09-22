package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.ChartCommentDto;

import java.util.List;

public interface ChartCommentRepositoryCustom {
    List<ChartCommentDto> doSearch(ChartCommentDto dto);
}
