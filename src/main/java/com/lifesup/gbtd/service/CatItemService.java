package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.CatItemDto;
import com.lifesup.gbtd.dto.object.UserLogDto;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.CatItemEntity;
import com.lifesup.gbtd.repository.CatItemRepository;
import com.lifesup.gbtd.service.inteface.ICatItemService;
import com.lifesup.gbtd.service.inteface.ILogActionService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.MessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class CatItemService extends BaseService implements ICatItemService {

    private final CatItemRepository catItemRepository;
    private final ILogActionService logActionService;
    private final UserLogService userLogService;

    @Autowired
    public CatItemService(CatItemRepository catItemRepository, ILogActionService logActionService, UserLogService userLogService) {
        this.catItemRepository = catItemRepository;
        this.logActionService = logActionService;
        this.userLogService = userLogService;
    }

    @Override
    public List<CatItemDto> getCatItems() {
        return catItemRepository.getCatItems();
    }

    @Override
    public void add(CatItemDto dto) {
        this.validateKeysAddAndUpdate(dto);
        CatItemEntity catItemEntity = map(dto, CatItemEntity.class);
        //catItemEntity.setStatus(Const.STATUS.ACTIVE);
        catItemEntity.setUpdateTime(new Date(System.currentTimeMillis()));
        catItemEntity.setUpdateUser(super.getCurrentUsername());
        catItemEntity = catItemRepository.save(catItemEntity);
        UserLogDto userLogDto = new UserLogDto("POST", "CREATE DANH_MUC_CHUNG", MessageUtil.getMessage("code.danh_muc_chung.create"), objectToJson(dto));
        userLogService.saveLog(userLogDto);
        logActionService.saveLogActionInternal(
                super.createLogDto(Const.TABLE.CAT_ITEM, Const.ACTION.INSERT, catItemEntity.getItemId(), null, catItemEntity));
    }

    @Override
    public void update(CatItemDto dto) {
        this.validateKeysAddAndUpdate(dto);
        AtomicReference<String> oldValue = new AtomicReference<>();

        CatItemEntity entity = catItemRepository.findById(dto.getItemId())
                .map(e -> {
                    oldValue.set(super.toJson(e));
                    e.setCategoryCode(dto.getCategoryCode());
                    e.setCategoryId(dto.getCategoryId());
                    e.setParentItemId(dto.getParentItemId());
                    e.setItemName(dto.getItemName());
                    e.setItemCode(dto.getItemCode());
                    e.setItemValue(dto.getItemValue());
                    e.setPosition(dto.getPosition());
                    e.setEditable(dto.getEditable());
                    e.setStatus(dto.getStatus());
                    e.setDescription(dto.getDescription());
                    e.setUpdateTime(new Date());
                    e.setUpdateUser(super.getCurrentUsername());

                    return e;
                })
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "catItem"));
        catItemRepository.save(entity);
        UserLogDto userLogDto = new UserLogDto("POST", "UPDATE DANH_MUC_CHUNG", MessageUtil.getMessage("code.danh_muc_chung.update"), objectToJson(dto));
        userLogService.saveLog(userLogDto);

        logActionService.saveLogActionInternal(
                super.createLogDto(Const.TABLE.CAT_ITEM, Const.ACTION.UPDATE, entity.getItemId(), oldValue.get(), entity));
    }

    @Override
    public List<CatItemDto> findCatItems(CatItemDto dto) {
//        UserLogDto userLogDto = new UserLogDto("POST", "SEARCH DANH_MUC_CHUNG", "Tìm kiếm danh mục chung", objectToJson(dto));
//        userLogService.saveLog(userLogDto);
        List<CatItemDto> catItemDtos = catItemRepository.findCatItems(dto);

        return catItemDtos;
    }

    @Override
    public List<CatItemDto> findByIds(List<Long> itemIds) {
        return catItemRepository.findByItemIdIn(itemIds)
                .stream()
                .map(e -> super.map(e, CatItemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCatItemById(Long id) {
        CatItemEntity catItem = catItemRepository.findById(id)
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "catItem"));
        int result = catItemRepository.deleteCatItemById(id);
        if (result == 0) {
            throw new ServerException(ErrorCode.FAILED, "DELETE");
        }
        UserLogDto userLogDto = new UserLogDto("POST", "DELETE DANH_MUC_CHUNG", MessageUtil.getMessage("code.danh_muc_chung.delete"), objectToJson(id));
        userLogService.saveLog(userLogDto);

        logActionService.saveLogActionInternal(
                super.createLogDto(Const.TABLE.CAT_ITEM, Const.ACTION.DELETE, id, catItem, null));
    }

    private void validateKeysAddAndUpdate(CatItemDto dto) {
//        if (!catItemRepository.validateKeysAddAndUpdate(dto.getCategoryCode(), dto.getItemCode(), dto.getParentItemId())) {
//            throw new ServerException(ErrorCode.ALREADY_EXIST);
//        }
        CatItemDto result = catItemRepository.findByCatCodeAndItemCodeAndParentItemId(dto.getCategoryCode(),
                dto.getItemCode(), dto.getParentItemId());

        if (Objects.nonNull(result)) {
            // Truong hop add
            if (Objects.isNull(dto.getItemId())) {
                throw new ServerException(ErrorCode.ALREADY_EXIST);
            }

            if (dto.getItemId().intValue() != result.getItemId().intValue()) {
                throw new ServerException(ErrorCode.ALREADY_EXIST);
            }
        }
    }

    @Override
    public List<CatItemDto> findByCategoryCode(String categoryCode) {
        return catItemRepository.findAllByCategoryCode(categoryCode)
                .stream()
                .map(e -> super.map(e, CatItemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CatItemDto> findByCategoryCodeAndStatus(String categoryCode, Long status) {
        return catItemRepository.findAllByCategoryCodeAndStatus(categoryCode, status)
                .stream()
                .map(e -> super.map(e, CatItemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findServiceSourceByDeptCode(String deptCode) {
        if (StringUtils.isEmpty(deptCode)) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "deptCode");
        }
        List<String> list = catItemRepository.findByCatCodeAndItemCode(Const.CAT_ITEM_CODE.SERVICE_SOURCE, deptCode);
//        UserLogDto userLogDto = new UserLogDto("POST", "RECIPE DANH_MUC_CHI_TIEU", "Thêm mới công thức chỉ tiêu", objectToJson(deptCode));
//        userLogService.saveLog(userLogDto);
        return list;
    }

    @Override
    public Optional<CatItemDto> findByCode(String code) {
        CatItemEntity entity = catItemRepository.findFirstByItemCodeAndStatus(code, Const.STATUS.ACTIVE);
        if (entity == null)
            return Optional.empty();
        return Optional.of(super.map(entity, CatItemDto.class));
    }

    @Override
    public List<CatItemDto> findAll(CatItemDto catItemDto) {
        if (StringUtils.isNotEmpty(catItemDto.getCategoryCode())) {
            if (DataUtil.isNullOrEmpty(catItemDto.getCategoryCodes())) {
                catItemDto.setCategoryCodes(Collections.singletonList(catItemDto.getCategoryCode()));
            } else {
                catItemDto.getCategoryCodes().add(catItemDto.getCategoryCode());
            }
        }
        return super.mapList(catItemRepository.findAll(catItemDto), CatItemDto.class);
    }
}
