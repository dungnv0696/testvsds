package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.ActionAuditDto;
import com.lifesup.gbtd.dto.response.ActionAuditResponse;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.service.inteface.ILogActionService;
import com.lifesup.gbtd.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logAction")
@Slf4j
public class LogActionController {
    private ILogActionService iLogActionService;

    @Autowired
    public LogActionController(ILogActionService iLogActionService) {
        this.iLogActionService = iLogActionService;
    }

    @PostMapping("")
    public ActionAuditResponse saveLogAction(@RequestHeader(value = "Authorization") String authorization,
                                             @Validated @RequestBody ActionAuditDto dto) {
        ActionAuditResponse res = new ActionAuditResponse();
        try {
            iLogActionService.saveLogAction(dto, authorization);
            res.setErrorCode(Const.ERROR_CODE.SUCCESS);
            res.setDescription(Const.SUCCESS);
        } catch (Exception e) {
            res.setErrorCode(Const.ERROR_CODE.FAIL);
            if (e instanceof ServerException) {
                res.setDescription(((ServerException) e).getErrorCode().getMessage());
            } else {
                res.setDescription(e.getMessage());
            }
        }

        return res;
    }
}
