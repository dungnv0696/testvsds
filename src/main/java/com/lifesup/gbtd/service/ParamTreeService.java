package com.lifesup.gbtd.service;

import com.lifesup.gbtd.dto.object.ParamTreeDto;
import com.lifesup.gbtd.dto.response.ResponseCommon;
import com.lifesup.gbtd.repository.ParamTreeRepository;
import com.lifesup.gbtd.service.inteface.IParamTreeService;
import com.lifesup.gbtd.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;


@Service
@Slf4j
public class ParamTreeService extends BaseService implements IParamTreeService {

    private final ParamTreeRepository paramTreeRepository;
    private static final int Max_length_Name = 100;
    private static final int Max_length_Code = 50;
    private static final int Min_length = 0;

    @Autowired
    public ParamTreeService(ParamTreeRepository paramTreeRepository) {
        this.paramTreeRepository = paramTreeRepository;
    }

    public List processData1(List<ParamTreeDto> abc) {
        List<ParamTreeDto> array1 = new ArrayList<>();
        for (int i = 0; i < abc.size(); i++) {
            array1.get(i).setId(abc.get(i).getId());

        }
        return array1;
    }


    /**
     * Kiem tra ma don vi co ton tai hay k
     * True : Ton tai
     * False: khong ton tai
     */
    public boolean doSearchCode(ParamTreeDto obj) {
        List<ParamTreeDto> ls = paramTreeRepository.getParamTreeByCode(obj);
        if (ls.size() > 0) {
            return true;
        }
        return false;
    }

    public static String convertDateToPrettyString(Date date) throws Exception {
        if (date == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return dateFormat.format(date);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * xem do dai chuoi co qua so ky tu dc nhap
     * dai hon max ky tu : bao loi
     * ngan: true
     */
    public boolean maxLength(String obj, int max) {
        String obj1 = obj.trim();
        if (obj1.length() <= max) {
            return true;
        }
        return false;
    }

    /**
     * So sanh ngay bat dau vs ngay het han
     * date1: ngay bat dau     date2: ngay het han
     * ngay het han =< ngay bat dau : false
     * ngay het han > ngay bay dau : true
     */
    public boolean compateDate(Date date1, Date date2) {
        if (date1.compareTo(date2) <= 0) {
            return true;
        }
        return false;
    }

    /**
     * kiem tra the html
     * true' la the html
     * false: ko phai the html
     */
    public boolean checkHtml(String obj) {
        String regex = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
        if (Pattern.matches(regex, obj)) {
            return true;
        }
        return false;
    }

    private boolean checkDate(ParamTreeDto obj) {
        if (obj.getStartTime() != null && obj.getEndTime() != null) {
            if (!compateDate(obj.getStartTime(), obj.getEndTime())) {
                return true;
            }
        }
        return false;
    }

    private ResponseCommon checkAreas(ParamTreeDto obj, boolean isCreateNew) {
        obj.setName(obj.getName().trim());
        obj.setCode(obj.getCode().trim());
        ResponseCommon rs = new ResponseCommon();
        if (StringUtils.isEmpty(obj.getCode())) {
            rs.setErrorCode(Const.ERROR_CODE.FAIL);
//            rs.setErrorMessage("Mã đơn vị không được để trống");
            rs.setErrorMessage("Dept code must not be empty");
            return rs;
        }
        if (StringUtils.isEmpty(obj.getName())) {
            rs.setErrorCode(Const.ERROR_CODE.FAIL);
//            rs.setErrorMessage("Tên đơn vị không được để trống");
            rs.setErrorMessage("Dept name must not be empty");
            return rs;
        }
        if (!maxLength(obj.getCode(), 50)) {
            rs.setErrorCode(Const.ERROR_CODE.FAIL);
//            rs.setErrorMessage("Mã đơn vị không được quá 50 ký tự");
            rs.setErrorMessage("Dept code must not be over 50 characters");
            return rs;
        }
        if (!maxLength(obj.getName(), 100)) {
            rs.setErrorCode(Const.ERROR_CODE.FAIL);
//            rs.setErrorMessage("Tên đơn vị không được quá 100 ký tự");
            rs.setErrorMessage("Dept name must not be over 100 characters");
            return rs;
        }
        if (checkHtml(obj.getCode())) {
            rs.setErrorCode(Const.ERROR_CODE.FAIL);
//            rs.setErrorMessage("Mã đơn vị không được trùng định dạng Html");
            rs.setErrorMessage("Dept code must not in html format");
            return rs;
        }
        if (checkHtml(obj.getName())) {
            rs.setErrorCode(Const.ERROR_CODE.FAIL);
//            rs.setErrorMessage("Tên đơn vị không được trùng định dạng Html");
            rs.setErrorMessage("Dept name must not in html format");
            return rs;
        }
        if (checkDate(obj)) {
            rs.setErrorCode(Const.ERROR_CODE.FAIL);
//            rs.setErrorMessage("Sai định dạng ngày");
            rs.setErrorMessage("Date invalid");
            return rs;
        }
        if (isCreateNew) {
            if (doSearchCode(obj)) {
                rs.setErrorCode(Const.ERROR_CODE.FAIL);
//                rs.setErrorMessage("Trùng mã đơn vị");
                rs.setErrorMessage("Dept code duplicate");
                return rs;
            }
        } else {
            List<ParamTreeDto> list = paramTreeRepository.getParamTreeById(obj);
            if (list.size() == 0) {
                rs.setErrorCode(Const.ERROR_CODE.FAIL);
//                rs.setErrorMessage("Không tồn tại đơn vị này");
                rs.setErrorMessage("Dept not found");
                return rs;
            }
            ParamTreeDto ParamTreeDto = list.get(0);
            if (!(obj.getCode().equals(ParamTreeDto.getCode()) && obj.getTypeParam().equals(ParamTreeDto.getTypeParam()))) {
                if (doSearchCode(obj)) {
                    rs.setErrorCode(Const.ERROR_CODE.FAIL);
//                    rs.setErrorMessage("Trùng mã đơn vị");
                    rs.setErrorMessage("Dept code duplicate");
                    return rs;
                }
            }
        }
        return rs;
    }

    @Override
    public List<ParamTreeDto> getParamTree(ParamTreeDto obj) {
        return paramTreeRepository.getParent(obj);
    }

    public List<ParamTreeDto> getType(ParamTreeDto obj) {
        return paramTreeRepository.getType();
    }

    @Override
    public List<ParamTreeDto> doSearch(ParamTreeDto obj) {
        return paramTreeRepository.getListParamTree(obj);
    }

    public boolean doSearchId(ParamTreeDto obj) {
        List<ParamTreeDto> list = paramTreeRepository.getParamTreeById(obj);
        return list.size() > 0;
    }


    @Override
    public ResponseCommon createParamTree(ParamTreeDto obj, String userId) {
//        Long uId = super.getCurrentUserId();
        ResponseCommon rs = this.checkAreas(obj, true);
        if (StringUtils.isEmpty(rs.getErrorCode())) {
            // todo action audit
//            ActionAuditDto actionAuditDto = new ActionAuditDto();
//            actionAuditDto.setTableName("DASHBOARD_PARAM_TREE");
//            actionAuditDto.setAction("INSERT");
            int a = paramTreeRepository.createParamTree(obj);

            if (a > 0) {
//                List<ParamTreeDto> list = paramTreeRepository.getIdMax(obj.getTypeParam());
//                actionAuditDto.setObjectId(list.get(0).getId());
                // todo add api audit
                rs.setErrorCode(Const.ERROR_CODE.SUCCESS);
//                rs.setErrorMessage("Thêm thành công");
                rs.setErrorMessage(Const.SUCCESS);
            } else {
                rs.setErrorCode(Const.ERROR_CODE.FAIL);
//                rs.setErrorMessage("Có lỗi xảy ra vui lòng thử lại");
                rs.setErrorMessage(Const.ERROR);
            }
        }
        return rs;
    }

    @Override
    public ResponseCommon editParamTree(ParamTreeDto obj, String userId) {
        ResponseCommon rs = this.checkAreas(obj, false);
        if (StringUtils.isEmpty(rs.getErrorCode())) {
            // todo add audit
//            actionAuditDTO = new ActionAuditDTO();
//            actionAuditDTO.setTableName("DASHBOARD_PARAM_TREE");
//            actionAuditDTO.setAction("UPDATE");
//            actionAuditDTO.setObjectId(obj.getId());
//            getActionAuditService().createActionAudit(actionAuditDTO, userId);
            int a = paramTreeRepository.editParamTree(obj);
            int b = paramTreeRepository.editParamTreeChi(obj);

            if (a > 0 && b >= 0) {
                rs.setErrorCode(Const.ERROR_CODE.SUCCESS);
//                rs.setErrorMessage("Sửa thành công");
                rs.setErrorMessage(Const.SUCCESS);
            } else {
                rs.setErrorCode(Const.ERROR_CODE.FAIL);
//                rs.setErrorMessage("Có lỗi xảy ra vui lòng thử lại");
                rs.setErrorMessage(Const.ERROR);
            }
        }
        return rs;
    }

    @Override
    public ResponseCommon deleteParamTree(ParamTreeDto obj, String userId) {
        ResponseCommon rs = new ResponseCommon();
        if (!doSearchId(obj)) {
            rs.setErrorCode(Const.ERROR_CODE.FAIL);
//            rs.setErrorMessage(Const.LOST_PARAMTREE_ID());
            return rs;
        } else {
            //todo action audit
//            actionAuditDTO = new ActionAuditDTO();
//            actionAuditDTO.setTableName("DASHBOARD_PARAM_TREE");
//            actionAuditDTO.setAction("DELETE");
//            actionAuditDTO.setObjectId(obj.getId());
//            getActionAuditService().createActionAudit(actionAuditDTO, userId);
            return paramTreeRepository.deleteParamTree(obj);
        }
    }
}

