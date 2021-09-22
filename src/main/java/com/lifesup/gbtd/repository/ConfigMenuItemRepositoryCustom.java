package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ConfigMenuItemEntity;

import java.util.List;

public interface ConfigMenuItemRepositoryCustom {
    List<ConfigMenuItemEntity> findAll(String keyword, Long[] menuIds, Long isDefault, Long status);
}
