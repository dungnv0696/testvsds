package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.request.DataLookupRequest;
import com.lifesup.gbtd.dto.response.DataLookupResponse;

import java.text.ParseException;

public interface IDataLookupService {
    Long getLatestDate(Long[] serviceIds, String[] deptCodes, Long timeType);

    DataLookupResponse doLookup(DataLookupRequest rq);
}
