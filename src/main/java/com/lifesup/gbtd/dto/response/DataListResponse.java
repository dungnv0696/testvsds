package com.lifesup.gbtd.dto.response;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.base.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class DataListResponse<T> extends BaseResponse {
    private List<T> data;

    public DataListResponse() {
    }

    public DataListResponse(List<T> data) {
        super(ErrorCode.OK);
        this.data = data;
    }

    public DataListResponse(Page<T> data) {
        super(ErrorCode.OK);
        super.setPaging(data);
        this.data = data.getContent();
    }

    public static <T> DataListResponse<T> success(List<T> data) {
        return new DataListResponse<>(data);
    }

    public static <T> DataListResponse<T> success(Page<T> data) {
        return new DataListResponse<>(data);
    }
}
