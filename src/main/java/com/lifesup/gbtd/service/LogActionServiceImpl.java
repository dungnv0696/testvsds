package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.ActionAuditDto;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.ActionAuditEntity;
import com.lifesup.gbtd.repository.ActionAuditRepository;
import com.lifesup.gbtd.service.inteface.ILogActionService;
import com.lifesup.gbtd.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class LogActionServiceImpl extends BaseService implements ILogActionService {

    private ActionAuditRepository actionAuditRepository;

    @Autowired
    public LogActionServiceImpl(ActionAuditRepository actionAuditRepository) {
        this.actionAuditRepository = actionAuditRepository;
    }

    @Override
    public void saveLogAction(ActionAuditDto dto, String auth) {
        this.checkUserAndPassword(auth);
        dto.setAction(dto.getAction().toUpperCase());
        Optional.of(super.map(dto, ActionAuditEntity.class))
                .map(actionAuditRepository::save);
    }

    @Override
    public void saveLogActionInternal(ActionAuditDto dto) {
        Optional.of(super.map(dto, ActionAuditEntity.class))
                .map(actionAuditRepository::save);
        log.info("save done");
    }

    private void checkUserAndPassword(String auth) {
        if (auth.length() <= 6) {
            throw new ServerException(ErrorCode.AUTHEN_ERROR);
        }

        String decoded = new String(Base64.decodeBase64(auth.substring(6).getBytes()));
        if (!decoded.contains(":")) {
            throw new ServerException(ErrorCode.AUTHEN_ERROR);
        }

        String[] account = decoded.split(":");
        HashMap<String, String> mapUser = JsonUtil.getListUser();
        if (mapUser.containsKey(account[0])) {
            if (!mapUser.get(account[0]).equals(account[1])) {
                throw new ServerException(ErrorCode.AUTHEN_ERROR);
            }
        }
    }
}
