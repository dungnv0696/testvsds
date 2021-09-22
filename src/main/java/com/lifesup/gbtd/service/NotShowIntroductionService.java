package com.lifesup.gbtd.service;

import com.lifesup.gbtd.model.NotShowIntroductionEntity;
import com.lifesup.gbtd.repository.NotShowIntroductionRepository;
import com.lifesup.gbtd.service.inteface.INotShowIntroductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotShowIntroductionService implements INotShowIntroductionService {
    @Autowired
    private NotShowIntroductionRepository notShowIntroductionRepository;

    @Override
    public void creat(NotShowIntroductionEntity notShowIntroductionEntity) {
        notShowIntroductionRepository.save(notShowIntroductionEntity);
    }

    @Override
    public List<NotShowIntroductionEntity> findByUserId(Long userId) {
        return notShowIntroductionRepository.findByUserId(userId);
    }
}
