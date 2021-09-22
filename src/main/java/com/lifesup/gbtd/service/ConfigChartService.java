package com.lifesup.gbtd.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.*;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.*;
import com.lifesup.gbtd.repository.*;
import com.lifesup.gbtd.service.inteface.*;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ConfigChartService extends BaseService implements IConfigChartService {

    private final ConfigChartRepository configChartRepository;
    private final ConfigChartItemRepository configChartItemRepository;
    private final ConfigQueryChartRepository configQueryChartRepository;
    private final ConfigDisplayQueryRepository configDisplayQueryRepository;
    private final IBuildChartService buildChartService;
    private final ILogActionService logActionService;
    private final ConfigChartRoleRepository configChartRoleRepository;
    private final ConfigMenuItemRepository configMenuItemRepository;
    private final ConfigMapChartAreaRepository configMapChartAreaRepository;
    private final IBuildChartServiceTemp iBuildChartServiceTemp;
    private final UserLogService userLogService;
    private final ICatDepartmentService catDepartmentService;
    private IConfigChartRoleService configChartRoleService;

    @Autowired
    public ConfigChartService(ConfigChartRepository configChartRepository,
                              ICatDepartmentService catDepartmentService,
                              ConfigChartItemRepository configChartItemRepository,
                              ConfigQueryChartRepository configQueryChartRepository,
                              ConfigDisplayQueryRepository configDisplayQueryRepository,
                              IBuildChartService buildChartService,
                              ILogActionService logActionService,
                              ConfigChartRoleRepository configChartRoleRepository,
                              ConfigMenuItemRepository configMenuItemRepository,
                              ConfigMapChartAreaRepository configMapChartAreaRepository,
                              IBuildChartServiceTemp iBuildChartServiceTemp, UserLogService userLogService,
                              IConfigChartRoleService configChartRoleService) {
        this.configChartRepository = configChartRepository;
        this.configChartItemRepository = configChartItemRepository;
        this.configQueryChartRepository = configQueryChartRepository;
        this.configDisplayQueryRepository = configDisplayQueryRepository;
        this.buildChartService = buildChartService;
        this.logActionService = logActionService;
        this.configChartRoleRepository = configChartRoleRepository;
        this.configMenuItemRepository = configMenuItemRepository;
        this.configMapChartAreaRepository = configMapChartAreaRepository;
        this.iBuildChartServiceTemp = iBuildChartServiceTemp;
        this.userLogService = userLogService;
        this.catDepartmentService = catDepartmentService;
        this.configChartRoleService = configChartRoleService;
    }

    @Override
    public Page<ConfigChartDto> doSearch(ConfigChartDto dto, Pageable pageable) {
        List<Long> deptIds = Collections.singletonList(dto.getDeptId() != null
                ? dto.getDeptId()
                : super.getCurrentUserDeptId());
        Page<ConfigChartEntity> data = configChartRepository.doSearch(
                dto,
                deptIds,
                Collections.singletonList(super.getCurrentUsername()),
                pageable
        );

        List<ConfigChartDto> dtos = super.mapList(data.getContent(), ConfigChartDto.class);
        dtos.forEach(chartDto -> {
            chartDto.setRoleCode(
                    chartDto.getRoleType() == Const.ROLE_TYPE.THEO_DON_VI ?
                            configChartRoleRepository.findByChartIdAndDeptId(chartDto.getId(), super.getCurrentUserDeptId()).getRoleCode() :
                            configChartRoleRepository.findByChartIdAndUsernameUsed(chartDto.getId(), super.getCurrentUsername()).getRoleCode());
        });
//        UserLogDto userLogDto = new UserLogDto();
//        userLogDto.setMethod("GET");
//        userLogDto.setEndPoint("SEARCH CONFIG CHART");
//        userLogDto.setTitle("Tim kiem bieu do");
//        userLogService.saveLog(userLogDto);
        return new PageImpl<>(dtos, data.getPageable(), data.getTotalElements());
    }

    @Override
    public ChartResultDto buildChart(Long id, ChartParamDto params) throws ParseException, JsonProcessingException {
        Date startTime = new Date();
        log.info("start process request get-chart-result/" + id + " " + startTime);

        ConfigChartDto chart = this.findById(id);
        if (Objects.isNull(chart) || Const.STATUS.DISABLED.equals(chart.getStatus())) {
            throw new ServerException(ErrorCode.NOT_FOUND, Const.TABLE.CONFIG_CHART);
        }
//        List<ConfigChartItemDto> chartItems = super.mapList(
//                configChartItemRepository.findByChartIdAndStatus(id, Const.STATUS.ACTIVE),
//                ConfigChartItemDto.class
//        );

        List<ConfigChartItemDto> chartItems = chart.getConfigChartItemDtos();
        if (!DataUtil.isNullOrEmpty(chartItems)) {
            Set<Long> queryIds = new HashSet<>();
            Set<Long> chartItemIds = new HashSet<>();
            chartItems.forEach(e -> {
                queryIds.add(e.getQueryId());
                chartItemIds.add(e.getId());
            });

            List<ConfigQueryChartDto> queries = super.mapList(configQueryChartRepository.findByIdIn(new ArrayList<>(queryIds)), ConfigQueryChartDto.class);
            List<ConfigDisplayQueryDto> displayQueries = super.mapList(
                    configDisplayQueryRepository.findByItemChartIdInAndStatus(
                            new ArrayList<>(chartItemIds), Const.STATUS.ACTIVE),
                    ConfigDisplayQueryDto.class);


            chartItems = chartItems.stream().peek(i -> {
                String inputCondition = i.getInputCondition();
                List<ConfigDisplayQueryDto> dtosCfd = null;
                try {
                    JSONObject jsonObject = new JSONObject(inputCondition);
                    String columns = jsonObject.getString("columns");
                    ObjectMapper mapper = new ObjectMapper();
                    dtosCfd = mapper.readValue(columns, new TypeReference<List<ConfigDisplayQueryDto>>() {
                    });
                } catch (JSONException | JsonProcessingException e) {
                    e.printStackTrace();
                    log.error("loi parse JSON", e);
                }

                queries.stream().filter(q -> q.getId().equals(i.getQueryId()))
                        .findFirst()
                        .ifPresent(i::setQuery);

                List<ConfigDisplayQueryDto> displayConfigs = displayQueries.stream()
                        .filter(dq -> i.getId().equals(dq.getItemChartId()))
                        .collect(Collectors.toList());

                List<ConfigDisplayQueryDto> finalDtosCfd = dtosCfd;
                displayConfigs.forEach(e -> {
                    finalDtosCfd.forEach(e1 -> {
                        if (e.getColumnQuery().equals(e1.getColumnQuery())) {
                            e.setValues(e1.getValues());
                        }
                    });
                });

                if (!DataUtil.isNullOrEmpty(displayConfigs)) {
                    i.setDisplayConfigs(displayConfigs);
                }
            }).collect(Collectors.toList());
        }
        ChartResultDto result = buildChartService.getChartResult(chart, chartItems, params);

        if (chart.getChartIdNextto() != null) {
            Optional<ConfigChartEntity> chartNext = configChartRepository.findById(chart.getChartIdNextto());
            if (!chartNext.isPresent() || Const.STATUS.DISABLED.equals(chartNext.get().getStatus())) {
                result.setChartIdNextto(null);
            }
        }
        return result;
    }

    @Override
    public List<TableDto> getDescriptionOfTableToMap(String tableName) {
        List<TableDto> dtos = configChartRepository.getDescriptionOfTableToMap(tableName);
        String lower;
        if (Arrays.asList(
                Const.TABLE.RPT_GRAPH_YEAR,
                Const.TABLE.RPT_GRAPH_QUAR,
                Const.TABLE.RPT_GRAPH_MON,
                Const.TABLE.RPT_GRAPH_DAY).contains(tableName)) {
            lower = "rpt";
        } else {
            lower = tableName.toLowerCase();
        }
        dtos.forEach(dto -> {
            dto.setDisplayName(MessageUtil.getMessage("label.column." + lower + "." + dto.getField().toLowerCase(), ""));
        });
        return dtos;
    }

    private ConfigChartDto findById(Long chartId) {
//GET list cây
        DataListResponse<CatDepartmentDto> res = new DataListResponse<>();
        res.setData(catDepartmentService.getDepartmentByDeptLevel(null, null));
//Get List Role
        DataListResponse<ConfigChartRoleDto> mlistRole = new DataListResponse<>();
        mlistRole.setData(configChartRoleService.getByChartId(chartId));

        ConfigChartDto dto = configChartRepository.findById(chartId)
                .map(e -> super.map(e, ConfigChartDto.class))
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "configChart"));

        if ( (null == configChartRoleRepository.findByChartIdAndDeptId(dto.getId(), super.getCurrentUserDeptId()) )
                && (null == configChartRoleRepository.findByChartIdAndUsernameUsed(dto.getId(), super.getCurrentUsername()))
//                && !checkParent(res, super.getCurrentUserDeptId(), mlistRole)
        ) {
            throw new ServerException(ErrorCode.ACCESS_DENIED, "Cannot access chart!");
        }


        if (!Const.STATUS.ACTIVE.equals(dto.getStatus())) {
            throw new ServerException(ErrorCode.NOT_FOUND, "configChart disabled");
        }

        dto.setConfigChartRoleDtos(super.mapList(configChartRoleRepository.findByChartId(chartId), ConfigChartRoleDto.class));
        dto.setConfigChartItemDtos(super.mapList(configChartItemRepository.findByChartIdAndStatus(chartId, Const.STATUS.ACTIVE), ConfigChartItemDto.class));
        return dto;
    }

    private boolean checkParent(DataListResponse<CatDepartmentDto> mlistTree, Long currentUserDeptId,
                                DataListResponse<ConfigChartRoleDto> mlistRole) {
        for (ConfigChartRoleDto roleCode : mlistRole.getData()) {
            if (roleCode.getDeptId().intValue() == 254 && currentUserDeptId.intValue() == 254) return true;
            for (CatDepartmentDto mTree : mlistTree.getData()) {
                if (mTree.getParent() == null) continue;
                if (mTree.getId().intValue() == roleCode.getDeptId().intValue()
                        && mTree.getParent().intValue() == currentUserDeptId.intValue() ){
                    return true;
                }
            }
        }

        return false;
    }


    // config chart
    @Override
    @Transactional
    public ChartResultDto createConfigChart(SaveChartDto configChartDTO) {
        log.debug("REST request to save ConfigChart : {}", configChartDTO);
        if (configChartDTO.getId() != null) {
            throw new ServerException(ErrorCode.NOT_VALID, Const.TABLE.CONFIG_CHART);
        }
        configChartDTO.setLogBuilder(super.defaultLogBuilder().action(Const.ACTION.INSERT));
        if (Const.CAT_ITEM_CODE.MAP_CHART_TYPE.equals(configChartDTO.getTypeChart())) {
            return buildChartService.saveChart(configChartDTO);
        }
        this.validateChartItems(configChartDTO);
        for (int i = 0; i < configChartDTO.getItems().size(); i++) {
            SaveChartItemDto item = configChartDTO.getItems().get(i);
            if (DataUtil.isNullOrEmpty(item.getColumns())) {
                throw new ServerException(ErrorCode.MISSING_PARAMS, "columns");
            }
            if (item.getColumns().stream().anyMatch(c -> Const.COMMON_YES.equals(c.getIsRequire())
                    && (DataUtil.isNullOrEmpty(c.getValues()) || c.getValues().stream().allMatch(v -> StringUtils.isEmpty(v.getValue()))))) {
                throw new ServerException(ErrorCode.MISSING_PARAMS, "values");
            }
        }

        configChartDTO.setStatus(super.prepareStatus(configChartDTO.getStatus()));
        ChartResultDto result = buildChartService.saveChart(configChartDTO);
        this.createChartRole(result);
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST", "CREATE CAU_HINH_BIEU_DO", MessageUtil.getMessage("code.cau_hinh_bieu_do.create"), objectToJson(configChartDTO));
        userLogService.saveLog(userLogDto);
        return result;
    }

    private void createChartRole(ChartResultDto result) {
        ConfigChartRoleEntity roleEntity = new ConfigChartRoleEntity();
        roleEntity.setChartId(result.getId());
        if (Const.ROLE_TYPE.CA_NHAN.equals(result.getRoleType())) {
            roleEntity.setUsernameUsed(super.getCurrentUsername());
        } else if (Const.ROLE_TYPE.THEO_DON_VI.equals(result.getRoleType())) {
            roleEntity.setDeptId(super.getCurrentUserDeptId());
        } else {
            throw new ServerException(ErrorCode.NOT_VALID, "roleType");
        }
        roleEntity.setRoleCode(Const.ROLE_CODE.ADMIN);
        roleEntity.setUpdateTime(new Date());
        roleEntity.setUpdateUser(super.getCurrentUsername());
        roleEntity = configChartRoleRepository.save(roleEntity);

        super.saveLog(Collections.singletonList(super.insertLog(
                Const.TABLE.CONFIG_CHART_ROLE,
                roleEntity.getId(),
                roleEntity
        )));
    }

    @Override
    @Transactional
    public ChartResultDto updateConfigChart(SaveChartDto configChartDTO) {
        log.debug("REST request to save ConfigChart : {}", configChartDTO);
        if (null == configChartDTO.getId()) {
            throw new ServerException(ErrorCode.NOT_VALID, "id");
        }
        configChartDTO.setLogBuilder(super.defaultLogBuilder().action(Const.ACTION.UPDATE));
        ConfigChartEntity chart = configChartRepository.findById(configChartDTO.getId())
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, Const.TABLE.CONFIG_CHART));

        this.validateChartItems(configChartDTO);

        // update chart role if change
        if (!chart.getRoleType().equals(configChartDTO.getRoleType())) {
            this.updateConfigChartRole(super.map(configChartDTO, ConfigChartEntity.class));
        }

        configChartDTO.setStatus(super.prepareStatus(configChartDTO.getStatus()));
        configChartDTO.setUpdateTime(new Date());
        configChartDTO.setUpdateUser(super.getCurrentUsername());
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST", "UPDATE CAU_HINH_BIEU_DO", MessageUtil.getMessage("code.cau_hinh_bieu_do.upate"), objectToJson(configChartDTO));
        userLogService.saveLog(userLogDto);
        return buildChartService.saveChart(configChartDTO);
    }

    private void validateChartItems(SaveChartDto configChartDTO) {
        if (DataUtil.isNullOrEmpty(configChartDTO.getItems())) {
            throw new ServerException(ErrorCode.NOT_VALID, Const.TABLE.CONFIG_CHART);
        }
        List<String> tableNames = configChartDTO.getItems().stream()
                .map(i -> i.getKpiInfos().stream().map(SaveKpiInfoDto::getTableName).collect(Collectors.toList()))
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        if (tableNames.size() > 1) {
            throw new ServerException(ErrorCode.NOT_VALID, "OVERVIEW chart");
        }
    }

    @Override
    public ChartResultDto previewChart(SaveChartDto configChartDTO) throws ParseException, JsonProcessingException {
        if (Const.CAT_ITEM_CODE.MAP_CHART_TYPE.equals(configChartDTO.getTypeChart())) {
            return new ChartResultDto(configChartDTO);
        }
        this.validateChartItems(configChartDTO);

        configChartDTO.setStatus(Const.STATUS.ACTIVE);
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST", "PREVIEW CONFIG_CHART", MessageUtil.getMessage("code.cau_hinh_bieu_do.preview"), objectToJson(configChartDTO));
        userLogService.saveLog(userLogDto);
        return this.preview(configChartDTO);
    }

    public ChartResultDto preview(SaveChartDto dto) throws ParseException, JsonProcessingException {
        Map<SaveChartItemDto, List<SaveChartItemDto>> mergedMap = buildChartService.mergeQueries(dto);
        List<ConfigChartItemDto> allChartItems = new ArrayList<>();
        for (List<SaveChartItemDto> items : mergedMap.values()) {
            ConfigQueryChartDto query = buildChartService.buildQuery(items, false, null);
            items.forEach(i -> {
                try {
                    List<ConfigDisplayQueryDto> configDisplayQueryDtos = i.getColumns().stream()
                            .filter(c -> !DataUtil.isNullOrEmpty(c.getValues()) &&
                                    c.getValues().stream().anyMatch(v -> StringUtils.isNotEmpty(v.getValue())))
                            .collect(Collectors.toList());

                    ConfigChartItemDto chartItem = i.toDto();
                    chartItem.setQuery(query);
                    chartItem.setStatus(Const.STATUS.ACTIVE);
                    chartItem.setDisplayConfigs(configDisplayQueryDtos);

                    allChartItems.add(chartItem);
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage(), e);
                }
            });
        }

        return buildChartService.getChartResult(dto, allChartItems, null);
    }

    @Override
    @Transactional
    public ConfigChartDto copy(Long id) {
        ConfigChartDto old = this.findById(id);
        old.setId(null);
        old.setUpdateTime(new Date());
        old.setUpdateUser(super.getCurrentUsername());

        ConfigChartEntity entity = configChartRepository.save(super.map(old, ConfigChartEntity.class));
        entity.setChartName(entity.getChartName() + " Copy(" + entity.getId() + ")");
        entity.setTitleChart(entity.getTitleChart() + " Copy(" + entity.getId() + ")");
        super.mapData(configChartRepository.save(entity), old);

        logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_CHART, Const.ACTION.INSERT,
                entity.getId(), null, entity));

        old.getConfigChartRoleDtos().forEach(e -> {
            e.setId(null);
            e.setChartId(old.getId());
            e.setUpdateTime(new Date());
            e.setUpdateUser(super.getCurrentUsername());
            ConfigChartRoleEntity newEntity = configChartRoleRepository.save(super.map(e, ConfigChartRoleEntity.class));
            logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_CHART_ROLE, Const.ACTION.INSERT,
                    newEntity.getId(), null, newEntity));
        });

        List<ConfigQueryChartDto> cfQueryChartDtos = configQueryChartRepository.findByChartId(id);
        Map<Long, Long> mapCfQueryChartIds = new HashMap<>();
        cfQueryChartDtos.forEach(dto -> {
            Long oldId = dto.getId();
            dto.setId(null);
            dto.setUpdateTime(new Date());
            dto.setUpdateUser(super.getCurrentUsername());
            ConfigQueryChartEntity newEntity = configQueryChartRepository.save(super.map(dto, ConfigQueryChartEntity.class));

            mapCfQueryChartIds.put(oldId, newEntity.getId());
            logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_QUERY_CHART, Const.ACTION.INSERT,
                    newEntity.getId(), null, newEntity));
        });

        Map<Long, Long> mapCfChartItemIds = new HashMap<>();
        old.getConfigChartItemDtos().forEach(e -> {
            if (Const.STATUS.ACTIVE.equals(e.getStatus())) {
                Long oldId = e.getId();
                e.setId(null);
                e.setChartId(entity.getId());
                e.setQueryId(mapCfQueryChartIds.get(e.getQueryId()));
                e.setUpdateTime(new Date());
                e.setUpdateUser(super.getCurrentUsername());
                ConfigChartItemEntity newEntity = configChartItemRepository.save(super.map(e, ConfigChartItemEntity.class));
                mapCfChartItemIds.put(oldId, newEntity.getId());

                logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_CHART_ITEM, Const.ACTION.INSERT,
                        newEntity.getId(), null, newEntity));
            }
        });

        mapCfChartItemIds.forEach((oldId, newId) -> {
            List<ConfigDisplayQueryDto> dtos = configDisplayQueryRepository.findByItemChartId(oldId).stream()
                    .map(e -> super.map(e, ConfigDisplayQueryDto.class)).collect(Collectors.toList());
            dtos.forEach(dto -> {
                dto.setId(null);
                dto.setItemChartId(newId);
                dto.setUpdateTime(new Date());
                dto.setUpdateUser(super.getCurrentUsername());
                ConfigDisplayQueryEntity newEntity = configDisplayQueryRepository.save(super.map(dto, ConfigDisplayQueryEntity.class));
                logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_DISPLAY_QUERY, Const.ACTION.INSERT,
                        newEntity.getId(), null, newEntity));
            });
        });
        //ghi log
        UserLogDto userLogDto = new UserLogDto("GET", "COPY CAU_HINH_BIEU_DO", MessageUtil.getMessage("code.cau_hinh_bieu_do.copy"), objectToJson(id));
        userLogService.saveLog(userLogDto);
        return old;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ConfigChartEntity entity = configChartRepository.findById(id)
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "configChart"));
        // delete config-chart-role
        configChartRoleRepository.findByChartId(id)
                .forEach(this::deleteConfigChartRole);
        //delete config-query-chart
        super.mapList(configQueryChartRepository.findByChartId(id), ConfigQueryChartEntity.class)
                .forEach(this::deleteConfigQueryChart);
        //delete config-chart-item
        configChartItemRepository.findByChartId(id)
                .forEach(this::deleteConfigChartItem);

        configChartRepository.delete(entity);
        logActionService.saveLogActionInternal(super.createLogDto(
                Const.TABLE.CONFIG_CHART,
                Const.ACTION.DELETE,
                id, entity, null));
        //ghi log
        UserLogDto userLogDto = new UserLogDto("GET", "DELETE CAU_HINH_BIEU_DO", MessageUtil.getMessage("code.cau_hinh_bieu_do.delete"));
        userLogService.saveLog(userLogDto);

    }

    private void deleteConfigQueryChart(ConfigQueryChartEntity entity) {
        configQueryChartRepository.delete(entity);
        logActionService.saveLogActionInternal(super.createLogDto(
                Const.TABLE.CONFIG_QUERY_CHART,
                Const.ACTION.DELETE,
                entity.getId(), entity, null));
    }

    private void deleteConfigChartItem(ConfigChartItemEntity entity) {
        configDisplayQueryRepository.findByItemChartId(entity.getId())
                .forEach(e -> {
                    configDisplayQueryRepository.delete(e);
                    logActionService.saveLogActionInternal(super.createLogDto(
                            Const.TABLE.CONFIG_DISPLAY_QUERY,
                            Const.ACTION.DELETE,
                            e.getId(), e, null));
                });
        configChartItemRepository.delete(entity);
        logActionService.saveLogActionInternal(super.createLogDto(
                Const.TABLE.CONFIG_CHART_ITEM,
                Const.ACTION.DELETE,
                entity.getId(),
                entity, null));
    }

    private void deleteConfigChartRole(ConfigChartRoleEntity entity) {
        configChartRoleRepository.delete(entity);
        logActionService.saveLogActionInternal(super.createLogDto(
                Const.TABLE.CONFIG_CHART_ROLE,
                Const.ACTION.DELETE,
                entity.getId(), entity, null));
    }

    @Override
    public void checkDelete(Long id) {
        List<ConfigMenuItemDto> cfMenuItems = configMenuItemRepository.findByChartId(id).stream()
                .map(e -> super.map(e, ConfigMenuItemDto.class))
                .collect(Collectors.toList());

        if (!cfMenuItems.isEmpty()) {
            throw new ServerException(ErrorCode.ACCESS_DENIED, "Can not delete chart!");
        }

        List<ConfigMapChartAreaDto> cfMapChartAreas = configMapChartAreaRepository.findByChartId(id)
                .stream().map(e -> super.map(e, ConfigMapChartAreaDto.class))
                .collect(Collectors.toList());

        if (!cfMapChartAreas.isEmpty()) {
            throw new ServerException(ErrorCode.ACCESS_DENIED, "Can not delete chart!");
        }
    }

    @Override
    public SaveChartDto getConfigChart(Long id) {
        log.debug("REST request to get ConfigChart : {}", id);
        ConfigChartDto configChartDTO = configChartRepository.findById(id)
                .map(e -> super.map(e, ConfigChartDto.class))
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, Const.TABLE.CONFIG_CHART));
        SaveChartDto rs = new SaveChartDto(configChartDTO);
        if (rs.getChartIdNextto() != null) {
            rs.setChartNextto(configChartRepository.findById(rs.getChartIdNextto())
                    .filter(e -> !Const.STATUS.DISABLED.equals(e.getStatus()))
                    .map(e -> super.map(e, ConfigChartDto.class))
                    .orElseGet(() -> null));
        }
        //get items
        List<ConfigChartItemDto> chartItems = super.mapList(configChartItemRepository.findByChartId(rs.getId()), ConfigChartItemDto.class);
        if (!DataUtil.isNullOrEmpty(chartItems)) {
            List<Long> itemIds = chartItems.stream().map(ConfigChartItemDto::getId).collect(Collectors.toList());
            List<Long> queryIds = chartItems.stream().map(ConfigChartItemDto::getQueryId).filter(Objects::nonNull).collect(Collectors.toList());
            List<ConfigQueryChartDto> queries = new ArrayList<>();
            if (!DataUtil.isNullOrEmpty(queryIds)) {
                queries = super.mapList(configQueryChartRepository.findByIdIn(queryIds), ConfigQueryChartDto.class);
            }
            List<ConfigDisplayQueryDto> displayConfigs = super.mapList(
                    configDisplayQueryRepository.findByItemChartIdInAndStatus(itemIds, Const.STATUS.ACTIVE),
                    ConfigDisplayQueryDto.class
            );
            List<ConfigQueryChartDto> finalQueries = queries;
            List<SaveChartItemDto> items = chartItems.stream()
                    .map(i -> {
                        try {
                            SaveChartItemDto saveItem = new SaveChartItemDto(i);
                            Optional<ConfigQueryChartDto> query = finalQueries.stream().filter(q -> q.getId().equals(i.getQueryId())).findFirst();
                            if (StringUtils.isNotEmpty(i.getInputCondition())) {
                                if (query.isPresent()) saveItem.setQuery(query.get());
                                return saveItem;
                            }
                            if (!query.isPresent()) return saveItem;
                            List<SaveDisplayQueryDto> columns = displayConfigs.stream().filter(c -> i.getId().equals(c.getItemChartId())).map(SaveDisplayQueryDto::new).collect(Collectors.toList());
                            saveItem = iBuildChartServiceTemp.generateInputCondition(saveItem, query.get(), columns);
                            saveItem.setQuery(query.get());
                            return saveItem;
                        } catch (JsonProcessingException e) {
                            log.error(e.getMessage(), e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            rs.setItems(items);
        }
        return rs;
    }

    private void updateConfigChartRole(ConfigChartEntity chart) {
        List<ActionAuditDto> actionLogs = new ArrayList<>();
        // delete config_char_role
        List<ConfigChartRoleEntity> entities = configChartRoleRepository.findByChartId(chart.getId());
        entities.forEach(e -> {
            actionLogs.add(super.deleteLog(Const.TABLE.CONFIG_CHART_ROLE, e.getId(), e));
//            log.oldValue(e).objectId(e.getId());
            configChartRoleRepository.delete(e);
        });

        // add new role for current user is Admin
        ConfigChartRoleEntity configChartRoleEntity = new ConfigChartRoleEntity();
        if (chart.getRoleType().equals(Const.ROLE_TYPE.CA_NHAN)) {
            configChartRoleEntity.setUsernameUsed(super.getCurrentUsername());
        } else if (chart.getRoleType().equals(Const.ROLE_TYPE.THEO_DON_VI)) {
            configChartRoleEntity.setDeptId(super.getCurrentUserDeptId());
        }

        configChartRoleEntity.setChartId(chart.getId());
        configChartRoleEntity.setRoleCode(Const.ROLE_CODE.ADMIN);
        configChartRoleEntity.setUpdateTime(new Date());
        configChartRoleEntity.setUpdateUser(super.getCurrentUsername());
        configChartRoleEntity = configChartRoleRepository.save(configChartRoleEntity);
        logActionService.saveLogActionInternal(super.createLogDto(
                Const.TABLE.CONFIG_CHART_ROLE,
                Const.ACTION.INSERT,
                configChartRoleEntity.getId(), null, configChartRoleEntity));
//        log.newValue(configChartRoleEntity);
        super.saveLog(actionLogs);
    }
}
