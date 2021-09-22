package com.lifesup.gbtd.exception;

import com.lifesup.gbtd.dto.response.ResponseCommon;
import com.lifesup.gbtd.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.birt.report.engine.api.IEngineTask;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;

import java.util.List;

@Slf4j
public class BirtHandleException {
    public ResponseCommon handleException(IRunAndRenderTask task) {
        ResponseCommon birtResponse = new ResponseCommon();
        birtResponse.setErrorCode(Const.ERR_0);
        birtResponse.setErrorMessage(Const.SUCCESS);
        if (task.getStatus() == IEngineTask.STATUS_CANCELLED) {
            List<Throwable> errors = task.getErrors();
            if (!errors.isEmpty()) {
                log.error(Const.ERROR + ": " + errors.get(errors.size() - 1));
                String mess_response = String.valueOf(errors.get(errors.size() - 1));
                if (mess_response.contains(Const.SQLException)) {
                    birtResponse.setErrorCode(Const.ERR_01);
                    birtResponse.setErrorMessage(Const.DB_CONNECTION_ERROR);
                } else if (mess_response.contains(Const.FileNotFoundException) || mess_response.contains(Const.NullPointerException)) {
                    birtResponse.setErrorCode(Const.ERR_02);
                    birtResponse.setErrorMessage(Const.FILE_NOT_FOUND);
                } else if (mess_response.contains(Const.MySQLSyntaxErrorException)) {
                    birtResponse.setErrorCode(Const.ERR_03);
                    birtResponse.setErrorMessage(Const.TABLE_DO_NOT_EXIST);
                } else if(mess_response.contains(Const.SQLSyntaxErrorException)) {
                    birtResponse.setErrorCode(Const.ERROR_05);
                    birtResponse.setErrorMessage(Const.SQLSyntaxErrorException);
                } else {
                    birtResponse.setErrorCode(Const.ERROR);
                    birtResponse.setErrorMessage(mess_response);
                }
            }
        }
        return birtResponse;
    }

}