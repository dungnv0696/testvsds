package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.ActionAuditDto;

public interface ILogActionService {
    void saveLogAction(ActionAuditDto dto, String aut);

    void saveLogActionInternal(ActionAuditDto dto);
}
