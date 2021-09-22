package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.ParamTreeDto;
import com.lifesup.gbtd.dto.response.ResponseCommon;

import java.util.List;

/**
 * Created by pmvt-os-chc-35 on 6/26/2020.
 */
public interface IParamTreeService {
    List<ParamTreeDto> getParamTree(ParamTreeDto obj);

    List<ParamTreeDto> doSearch(ParamTreeDto obj);

    List<ParamTreeDto> getType(ParamTreeDto obj);

    boolean doSearchCode(ParamTreeDto obj);

    ResponseCommon createParamTree(ParamTreeDto obj, String userId);

    ResponseCommon editParamTree(ParamTreeDto obj, String userId);

    ResponseCommon deleteParamTree(ParamTreeDto obj, String userId);
}
