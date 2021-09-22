package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.ParamTreeDto;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.dto.response.ResponseCommon;
import com.lifesup.gbtd.service.inteface.IParamTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/paramTree")
public class ParamTreeController {

    private final IParamTreeService iParamTreeService;

    @Autowired
    public ParamTreeController(IParamTreeService iParamTreeService) {
        this.iParamTreeService = iParamTreeService;
    }

    @PostMapping("/getList")
    public GenericResponse<List<ParamTreeDto>> getListTarget(@RequestBody ParamTreeDto obj) {
        List<ParamTreeDto> result = iParamTreeService.getParamTree(obj);
        GenericResponse<List<ParamTreeDto>> res = new GenericResponse<>();
        res.setData(result);
        return res;
    }

    @PostMapping("/doSearch")
    public List<ParamTreeDto> doSearch(@RequestBody ParamTreeDto obj) {
        return iParamTreeService.doSearch(obj);
    }

    @PostMapping("/getType")
    public List<ParamTreeDto> getType(@RequestBody ParamTreeDto obj) {
        return iParamTreeService.getType(obj);
    }

    @PostMapping("/add")
    public ResponseCommon createServiceCustomer(@RequestBody ParamTreeDto obj) {
//        return iParamTreeService.createParamTree(obj, userId);
        return iParamTreeService.createParamTree(obj, "");
    }


    //Edit
    @PostMapping("/update")
    public ResponseCommon EditServiceCustomer(@RequestBody ParamTreeDto obj) {
//        ResponseCommon rs = new ResponseCommon();
//        String userId;
//        if (obj == null) {
//            rs.setErrorCode(Const.ERROR_CODE.FAIL);
//            rs.setErrorMessage("Object null");
//            return rs;
//        } else {
//            try {
//                userId = ((UserToken) request.getSession().getAttribute("vsaUserToken")).getUserName();
//                return iParamTreeService.editParamTree(obj, userId);
//            } catch (Exception e) {
//                logger.error(e.getMessage(), e);
//                rs.setErrorCode(Const.ERROR_CODE.FAIL);
//                rs.setErrorMessage(e.getMessage());
//                return rs;
//            }
//        }
        return iParamTreeService.editParamTree(obj, "");
    }


    @PostMapping("/remote")
    public ResponseCommon deleteServiceCustomer(@RequestBody ParamTreeDto obj) {
//        ResponseCommon rs = new ResponseCommon();
//        String userId;
//        try {
//            userId = ((UserToken) request.getSession().getAttribute("vsaUserToken")).getUserName();
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            rs.setErrorCode(Const.ERROR);
//            rs.setErrorMessage(Const.TOKEN_REQUIRE());
//            return rs;
//        }
//        rs = getParamTreeService().deleteParamTree(obj, userId);
//        return rs;
        return iParamTreeService.deleteParamTree(obj, "");
    }

}
