package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.ChartCommentDto;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.ChartCommentEntity;
import com.lifesup.gbtd.repository.ChartCommentRepository;
import com.lifesup.gbtd.repository.ConfigChartRepository;
import com.lifesup.gbtd.service.inteface.IChartCommentService;
import com.lifesup.gbtd.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ChartCommentService extends BaseService implements IChartCommentService {

    private final ChartCommentRepository chartCommentRepository;
    private final ConfigChartRepository configChartRepository;

    @Autowired
    public ChartCommentService(ChartCommentRepository chartCommentRepository, ConfigChartRepository configChartRepository) {
        this.chartCommentRepository = chartCommentRepository;
        this.configChartRepository = configChartRepository;
    }

    @Override
    public ChartCommentDto createChartComment(ChartCommentDto dto) {
        this.validateSave(dto);
        dto.setLogBuilder(super.defaultLogBuilder().action(Const.ACTION.INSERT));
        return this.save(dto);
    }

    @Override
    public void delete(Long id) {
        ChartCommentEntity chartComment = chartCommentRepository.findByIdAndUserName(id, super.getCurrentUsername()).
                orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, Const.TABLE.CHART_COMMENT));
        chartCommentRepository.delete(chartComment);
        super.saveLog(super.deleteLog(Const.TABLE.CHART_COMMENT, id, chartComment));
    }

    @Override
    public List<ChartCommentDto> doSearch(ChartCommentDto dto) {
        if (null == dto.getChartId()){
            throw new ServerException(ErrorCode.NOT_FOUND, "chartId");
        }
        return chartCommentRepository.doSearch(dto);
    }

    public ChartCommentDto save(ChartCommentDto dto) {
        dto.setContent(dto.getContent().trim());
        dto.setDateTime(new Date());
        dto.setUserName(super.getCurrentUsername());
        ChartCommentEntity entity = chartCommentRepository.save(super.map(dto, ChartCommentEntity.class));
        super.saveLog(super.insertLog(Const.TABLE.CHART_COMMENT, entity.getId(), entity));
        return super.map(entity, ChartCommentDto.class);
    }

    private void validateSave(ChartCommentDto dto) {
        if (Objects.nonNull(dto.getId())) {
            throw new ServerException(ErrorCode.ALREADY_EXIST, "chart comment");
        }
        configChartRepository.findById(dto.getChartId())
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "config chart"));
    }
}
