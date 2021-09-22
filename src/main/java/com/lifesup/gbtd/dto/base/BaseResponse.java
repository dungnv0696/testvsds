package com.lifesup.gbtd.dto.base;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class BaseResponse extends BaseDto {
    private String code;
    private String message;
    private String debugMessage;
    private long timestamp;

    public BaseResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public BaseResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = MessageUtil.getMessage(errorCode.getMessage());
        this.timestamp = System.currentTimeMillis();
    }

    public BaseResponse(Exception ex) {
        this.timestamp = System.currentTimeMillis();
        if (ex instanceof ServerException) {
            ServerException e = (ServerException) ex;
            if (e.getErrorCode() != null) {
                this.code = e.getErrorCode().getCode();
                this.message = MessageUtil.getMessage(e.getErrorCode().getMessage(), e.getArgs());
            } else {
                this.code = ErrorCode.SERVER_ERROR.getCode();
                this.message = MessageUtil.getMessage(ErrorCode.SERVER_ERROR.getMessage());
            }
        } else {
            this.code = ErrorCode.SERVER_ERROR.getCode();
            this.message = MessageUtil.getMessage(ErrorCode.SERVER_ERROR.getMessage());
        }
        this.debugMessage = ex.getMessage();
    }

    public void success() {
        this.code = ErrorCode.OK.getCode();
        this.message = MessageUtil.getMessage(ErrorCode.OK.getMessage());
    }

    public void withErrorCode(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = MessageUtil.getMessage(errorCode.getMessage());
    }

    public void exception(Exception ex) {
        this.code = ErrorCode.SERVER_ERROR.getCode();
        this.message = MessageUtil.getMessage(ErrorCode.SERVER_ERROR.getMessage());
        this.debugMessage = ex.getMessage();
    }

    public void exception(ServerException ex) {
        if (ex.getErrorCode() != null) {
            this.code = ex.getErrorCode().getCode();
            this.message = MessageUtil.getMessage(ex.getErrorCode().getMessage(), ex.getArgs());
        } else {
            this.code = ErrorCode.SERVER_ERROR.getCode();
            this.message = MessageUtil.getMessage(ErrorCode.SERVER_ERROR.getMessage());
        }
        this.debugMessage = ex.getMessage();
    }

    public <T extends BaseDto> void setPaging(T dto) {
        this.setTotalPage(dto.getTotalPage());
        this.setTotalRow(dto.getTotalRow());
    }

    public <T> void setPaging(Page<T> paging) {
        this.setTotalPage((long) paging.getTotalPages());
        this.setTotalRow(paging.getTotalElements());
    }

    public static BaseResponse build(Exception ex) {
        return new BaseResponse(ex);
    }

    public static BaseResponse build(ErrorCode errorCode) {
        return new BaseResponse(errorCode);
    }
}
