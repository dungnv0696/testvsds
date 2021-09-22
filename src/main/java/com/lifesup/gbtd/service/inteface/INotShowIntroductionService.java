package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.model.NotShowIntroductionEntity;

import java.util.List;

public interface INotShowIntroductionService {
    void creat(NotShowIntroductionEntity notShowIntroductionEntity);
    List<NotShowIntroductionEntity> findByUserId(Long userId);
}
