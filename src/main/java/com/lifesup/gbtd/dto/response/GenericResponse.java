package com.lifesup.gbtd.dto.response;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.base.BaseResponse;
import com.lifesup.gbtd.exception.ServerException;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class GenericResponse<T> extends BaseResponse {
    private T data;

    public GenericResponse() {
    }

    public GenericResponse(T data) {
        super(ErrorCode.OK);
        this.data = data;
    }
    public static <T> GenericResponse<T> success(T data) {
        return new GenericResponse<>(data);
    }
}
