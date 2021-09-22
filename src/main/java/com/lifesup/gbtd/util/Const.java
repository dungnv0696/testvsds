package com.lifesup.gbtd.util;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Const {
//    public static final String TOKEN_CLAIM_USER_ID = "userId";
//    public final static String PATH_IMAGES = "D:/Users/qtdl-os-chc-38/Desktop/image";

    // old code from vbi
    public final static String reportNameExcel = "bao_cao_n2_excel.rptdesign";
    public final static String reportNameHtml = "bao_cao_n2_html.rptdesign";
    public final static String reportNameExcelAll = "bao_cao_n2_excel_all.rptdesign";
    public final static String reportNamePdfAll = "bao_cao_n2_excel_all_pdf.rptdesign";
    public final static String reportNameHtmlAll = "bao_cao_n2_html_all.rptdesign";
    public final static String reportFromDate = "from_date";
    public final static String reportType = "report_type";
    public final static String reportDeptPara = "dept_para_tree";
    public final static String reportShowTable = "show_table_";
    public final static String reportTreeTaget = "tree_target";
    public final static String reportCheckTable = "check_table_";
    public final static String typeReportN = "type";
    public final static String DASHBOARD_PARAM_TREE = "DASHBOARD_PARAM_TREE";

    public final static String SUCCESS = "SUCCESS";
    public final static String OTP_AUTH = "OTP_AUTH";
    public final static String OTP_EXPIRED = "OTP_EXPIRED";
    public final static String OTP_NOT_MATCH = "OTP_NOT_MATCH";
    public final static String OTP_OVER_TIME = "OTP_OVER_TIME";
    public final static String SEND_OTP_OVER_TIME = "SEND_OTP_OVER_TIME";
    public final static String RELOGIN_ERROR = "RELOGIN_ERROR";

    public final static String ERROR = "ERROR";
    public final static String ACCESS_DENIED = "ACCESS_DENIED";
    public final static String TOKEN_EXPIRED = "TOKEN_EXPIRED";

    public static final class ERROR_CODE {
        public static final String SUCCESS = "0";
        public static final String FAIL = "1";
    }

    // ------
    public static final Long ID_TAP_DOAN = 254L;
    public static final String USERS_CONFIG_FILE = "users.json";

    public static final String DATA_PRD_ID = "prd_id";
    public static final String DATA_TIME_TYPE = "time_type";
    public static final String DATA_TABLE_ALIAS = "data";
    public static final String KPI_TABLE_ALIAS = "kpi";
    public static final String OVERVIEW_DOMAIN_CODE = "TONG_QUAN";
    public static final String TYPE_PARAM = "typeParam";
//    public static final String DEFAULT_TABLE_NAME = "RPT_GRAPH_DAY";

    public static final char DEFAULT_ESCAPE_CHAR = '&';

    public static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";
    public static final String DATE_FORMAT_YYYYMM = "yyyyMM";
    public static final String TIME_FORMAT_TO_SECOND = "yyyyMMddhhmmss";
    public static final String TIME_FORMAT_TO_MILISECOND = "yyyyMMddHHmmssSSS";
    public static final Integer DEFAULT_RANGE_TIME = -1;

    public static final String KPI_ID_FIELD = "KPI_ID";
    public static final String UNIT_DISPLAY = "UNIT_DISPLAY";

    public static final Long COMMON_NO = 0L;
    public static final Long COMMON_YES = 1L;

    public static final String IN_OPERATOR = "IN";
    public static final String NOT_IN_OPERATOR = "NOT IN";

    public static final String FIELD_TYPE = "field";
    public static final String TEXT_TYPE = "text";
    public static final String FUNCTION_TYPE = "function";

    public static final String AES_KEY = "v13ttEl_mObil3bI_k@rcut4";

    public static final String DEFAULT_SUBFOLDER_UPLOAD = "/upload";

    public interface STATUS {
        Long INACTIVE = 0L;
        Long ACTIVE = 1L;
        Long SHARED = 2L;
        Long DECLINE = 3L;
        Long DISABLED = 0L;
    }

    public interface CAT_ITEM_CODE {
        String SHEET_BIRT = "SHEET_BIRT";
        String SERVICE_SOURCE = "SERVICE_SOURCE";
        String NDATE_CATITEM = "NDATE";
        String NQUAR_CATITEM = "NQUAR";
        String NMONTH_CATITEM = "NMONTH";
        String NYEAR_CATITEM = "NYEAR";
        String PARAM_CHART_CATITEM = "PARAM_CHART";
        String TYPE_CHART_CATITEM = "TYPE_CHART";
        String MAP_CHART_TYPE = "MAP_CHART";
        String ITEM_MAP_CHART_TYPE = "ITEM_MAP";
        String TIME_TYPE = "TIME_TYPE";
        String DURATION_TIME = "DURATION_TIME";
        String OUTPUT_SEARCH = "OUTPUT_SEARCH";
        String LANGUAGE_CODE = "LANGUAGE_CODE";
        String BUSINESS_TABLE = "BUSINESS_TABLE";
        String BUSINESS_TABLE_COLUMN = "BUSINESS_TABLE_COLUMN";
    }

    public interface SERVICE_TYPE {
        int CT_NGUOIDUNG = 0;
        int CT_KINHDOANH = 1;
    }

    public interface TABLE {
        String SERVICES_GBTD = "SERVICE_GBTD";
        String BI_TD_SERVICES_TREE = "BI_TD_SERVICES_TREE";
        String SERVICES_MAP_DEPT = "SERVICES_MAP_DEPT";
        String SERVICE_GBTD_DEFINE = "SERVICE_GBTD_DEFINE";
        String CAT_ITEM = "CAT_ITEM";
        String TEMP_REPORT = "TEMP_REPORT";
        String CONFIG_PROFILE = "CONFIG_PROFILE";
        String CONFIG_PROFILE_ROLE = "CONFIG_PROFILE_ROLE";
        String CONFIG_AREA = "CONFIG_AREA";
        String CONFIG_MAP_CHART_AREA = "CONFIG_MAP_CHART_AREA";
        String CONFIG_DASHBOARD = "CONFIG_DASHBOARD";
        String CONFIG_MENU = "CONFIG_MENU";
        String CONFIG_MENU_ITEM = "CONFIG_MENU_ITEM";
        String RPT_DATA_NEWEST = "RPT_DATA_NEWEST";
        String RPT_GRAPH_DAY = "RPT_GRAPH_DAY";
        String RPT_GRAPH_MON = "RPT_GRAPH_MON";
        String RPT_GRAPH_YEAR = "RPT_GRAPH_YEAR";
        String RPT_GRAPH_QUAR = "RPT_GRAPH_QUAR";
        String CONFIG_CHART = "CONFIG_CHART";
        String CONFIG_CHART_ROLE = "CONFIG_CHART_ROLE";
        String CONFIG_QUERY_CHART = "CONFIG_QUERY_CHART";
        String CONFIG_CHART_ITEM = "CONFIG_CHART_ITEM";
        String CONFIG_DISPLAY_QUERY = "CONFIG_DISPLAY_QUERY";
        String CHART_COMMENT = "CHART_COMMENT";
        String DASHBOARD_FOLDER = "DASHBOARD_FOLDER";
        String DASHBOARD_REPORT = "DASHBOARD_REPORT";
        String CAT_DEPARTMENT = "CAT_DEPARTMENT";
//        String DASHBOARD_PARAM_TREE = "DASHBOARD_PARAM_TREE";
    }

    public interface ACTION {
        String DELETE = "DELETE";
        String UPDATE = "UPDATE";
        String INSERT = "INSERT";
    }

    public interface FILTER_PARAMS {
        String FROM_DATE_PARAM = "FROMDATE";
        String TO_DATE_PARAM = "TODATE";
        String OBJECT_CODE = "OBJECTCODE";
        String TABLE_NAME = "TABLE_NAME";
        String TIME_TYPE_PARAM = "TIMETYPE";
        String PRD_ID_PARAM = "PRDID";
        String INPUT_LEVEL_PARAM = "INPUTLEVEL";
        String KPI_IDS_PARAM = "KPIIDS";

        String COLUMN = "COLUMN";
        String OBJCODES = "OBJCODES";
        String SERVICE_DEPTS = "SERVICE_DEPTS";
        String DEPT_CODES = "DEPT_CODES";
        String OBJ_OR_PARENT_CODE = "OBJ_OR_PARENT_CODE";
        String BRANCH_CODE = "BRANCH_CODE";
        String ROW_NUM = "rowNum";
        String DRILL_DOWN = "drillDown";
    }

    public interface PARAM_CHART_DEFAULT {
        String CURRENT_DATE = "CURRENT_DATE";
        String MAX_DATE = "MAX_DATE";
        String MAX_DATE_NDAY = "MAX_DATE-NDAY";
        String MAX_DATE_NDATE = "MAX_DATE-NDATE";
        String MAX_DATE_NMONTH = "MAX_DATE-NMONTH";
        String MAX_DATE_NYEAR = "MAX_DATE-NYEAR";
        String MAX_DATE_NQUAR = "MAX_DATE-NQUAR";
        String CURRENT_DATE_RELATIVE_TIME = "CURRENT_DATE-RELATIVE_TIME";
        String CURRENT_DATE_RELATIVE_TIME_NDATE = "CURRENT_DATE-RELATIVE_TIME-NDATE";
        String CURRENT_DATE_RELATIVE_TIME_NMONTH = "CURRENT_DATE-RELATIVE_TIME-NMONTH";
        String CURRENT_DATE_RELATIVE_TIME_NQUAR = "CURRENT_DATE-RELATIVE_TIME-NQUAR";
        String CURRENT_DATE_RELATIVE_TIME_NYEAR = "CURRENT_DATE-RELATIVE_TIME-NYEAR";
        String BYMONTH = "BYMONTH";
        String BYYEAR = "BYYEAR";
        String BYQUARTER = "BYQUARTER";
        String BEGIN_YEAR = "BEGIN_YEAR";
        String END_YEAR = "END_YEAR";
        String BEGIN_MONTH = "BEGIN_MONTH";
        String END_MONTH = "END_MONTH";
        String BEGIN_QUAR ="BEGIN_QUAR";
        String END_QUAR= "END_QUAR";
        String MAX_DATE_NTIME= "MAX_DATE_NTIME";
    }

    public interface TIME_TYPE {
        Long DATE = 1L;
        Long MONTH = 2L;
        Long QUARTER = 3L;
        Long YEAR = 4L;
        String STR_DATE = "1";
        String STR_MONTH = "2";
        String STR_QUARTER = "3";
        String STR_YEAR = "4";
    }

    public interface DEPT_LEVEL {
        Long TAP_DOAN = 1L;
        Long TCT_CTY_PHB = 2L;
        Long TINH_TP = 3L;
        Long QUAN_HUYEN = 4L;
        Long THI_TRUONG = 5L;
        Long NHOM_DON_VI = 6L;
        Long NO_DEPT_LEVEL = 0L;
    }

    public interface ROLE_TYPE {
        Long THEO_DON_VI = 1L;
        Long CA_NHAN = 2L;
    }

    public interface ROLE_CODE {
        String ADMIN = "ADMIN";
        String VIEW = "VIEW";
    }

    public interface DASHBOARD_TYPE {
        Long DETAIL = 0L;
        Long DEFAULT = 1L;
        Long OVERVIEW = 2L;
        Long MAP = 3L;
    }

    public interface TYPE_CHART {
        String INFO_OBJ = "INFO_OBJ";
        String ALARM_CHART = "ALARM_CHART";
        String LINE = "LINE";
        String COLUMN = "COLUMN";
        String PIE = "PIE";
        String AREA = "AREA";
        String GROUP_BAR = "GROUP_BAR";
        String STACK = "STACK";
        String CORRELATE_CHART = "CORRELATE_CHART";
        String RADAR = "RADAR";
        String BAR = "BAR";
        String COMBINE = "COMBINE";
        String OVERVIEW_CHART = "OVERVIEW_CHART";
        String LINE_DASHED = "LINE_DASHED";
        String INFO_MAIN = "INFO_MAIN";
        String SHARE_TIME = "SHARE_TIME";
        String RANK = "RANK";
        String BLOCK_INFO_2 = "BLOCK_INFO_2";
    }

//    public interface DISPLAY_QUERY_TYPE {
//        String FIELD = "field";
//        String TEXT = "text";
//        String FUNCTION = "function";
//    }

    public interface LANGUAGE_EXCHANGE {
        String TITLE_TD = MessageUtil.getMessage("vi.title_td");
        String TITLE_QH = MessageUtil.getMessage("vi.TITLE_QH");
        String TITLE_TN = MessageUtil.getMessage("vi.TITLE_TN");
        String TITLE_BDL = MessageUtil.getMessage("vi.TITLE_BDL");
        String TITLE_DMDNN = MessageUtil.getMessage("vi.TITLE_DMDNN");
        String TABLE_NAME = MessageUtil.getMessage("vi.TABLE_NAME");
        String LANGUAGE = MessageUtil.getMessage("vi.LANGUAGE");
        String RESULT = MessageUtil.getMessage("vi.RESULT");
        String ID_DATA = MessageUtil.getMessage("vi.ID_DATA");
        String MAJOR_TABLE = MessageUtil.getMessage("vi.MAJOR_TABLE");
        String VALUE = MessageUtil.getMessage("vi.VALUE");
        String DEFAULT_VALUE = MessageUtil.getMessage("vi.DEFAULT_VALUE");
    }

    public static final Map<Long, String> TIME_TYPE_TABLE_MAP = ImmutableMap.of(TIME_TYPE.DATE, TABLE.RPT_GRAPH_DAY,
            TIME_TYPE.MONTH, TABLE.RPT_GRAPH_MON,
            TIME_TYPE.YEAR, TABLE.RPT_GRAPH_YEAR,
            TIME_TYPE.QUARTER, TABLE.RPT_GRAPH_QUAR);
//    }).collect(Collectors.toMap(data -> (Long) data[0], data -> (String) data[1], (x, y) -> x, HashMap::new));

    public interface AUTHENTICATION {
        String TOKEN_CLAIM_KEY_USER_ID = "userId";
        String TOKEN_CLAIM_KEY_STAFF_CODE = "staffCode";
        String TOKEN_CLAIM_KEY_USERNAME = "username";
        String TOKEN_CLAIM_KEY_DEPT_ID = "deptId";
    }

    public interface SPECIAL_CHAR {
        String SPACE = " ";
        String UNDERSCORE = "_";
        String DOT = ".";
        String COMMA = ",";
        String COLON = ":";
        String SLASH = "/";
        String BACKSLASH = "\\";
    }

    public interface LOOKUP_FIELD_NAME {
        String VALUE_SHOW = "VALUE_SHOW";
        String CUSTOM_FIELD = "CUSTOM_FIELD";
//        String X_AXIS = "X_AXIS";
//        String Y_AXIS = "X_AXIS";
    }

    public interface FILE_TYPE {
        String EXCEL = "xlxs";
        String BIRT_REPORT = "rptdesign";
    }

    public interface SPLIT_SHEET {
        Long NO_SPLIT = 0L;
        Long SPLIT = 1L;
    }

    public static final Map<Integer, String> DATA_TYPE_MAP = new ImmutableMap.Builder()
            .put(0, "Any")
            .put(1, "String")
            .put(2, "Float")
            .put(3, "Decimal")
            .put(4, "Date Time")
            .put(5, "Boolean")
            .put(6, "Integer")
            .put(7, "Date")
            .put(8, "Time")
            .put(9, "List")
            .put(10, "Tree")
            .build();

    public interface LOCATION_LEVEL {
        Long VN = 1L;
        Long KV = 2L;
        Long TINH = 3L;
        Long HUYEN = 4L;
    }

    public interface COUNTRY_CODE {
        String VN = "VN";
    }

    public static Long calculatorPrdIdBefore(Long prdIdCurrent) {
        String prdId = String.valueOf(prdIdCurrent);
        if ("0101".equals(prdId.substring(4))) {
            return (Long.parseLong(prdId.substring(0, 4)) - 1) * 10000 + 1201;
        } else {
            return prdIdCurrent - 100;
        }
    }

    public static int getMaxDateBefore(Long prdIdCur) {
        int monthBf;
        String prdId = String.valueOf(prdIdCur);
        int monthId = Integer.parseInt(prdId.substring(4, 6));
        if (monthId == 12) {
            monthBf = 11;
        } else if (monthId == 1) {
            monthBf = 12;
        } else {
            monthBf = monthId - 1;
        }
        int yearId = Integer.parseInt(prdId.substring(0, 4));
        int maxDateBf;
        switch (monthBf) {
            case 2:
                if (isNamNhuan(yearId)) {
                    maxDateBf = 29;
                } else
                    maxDateBf = 28;
                break;
            case 4:
                maxDateBf = 30;
                break;
            case 6:
                maxDateBf = 30;
                break;
            case 9:
                maxDateBf = 30;
                break;
            case 11:
                maxDateBf = 30;
                break;
            default:
                maxDateBf = 31;
                break;
        }
        return maxDateBf;
    }
    public static int getMaxDateCur(String prdId){
        String date = java.time.LocalDate.now().toString().replaceAll("-", "");
        int maxDateCur = 0;
        if (prdId.substring(0, 6).equals(date.substring(0, 6))) {
            java.util.Date date1 = new java.util.Date();
            maxDateCur = date1.getDate();
        } else if (Integer.parseInt(prdId.substring(0, 6)) < Integer.parseInt(date.substring(0, 6))) {
//            if (Integer.parseInt(prdId.substring(0, 4)) < Integer.parseInt(date.substring(0, 4))) {
                int month = Integer.parseInt(prdId.substring(4,6));
                switch (month) {
                    case 2:
                        if (isNamNhuan(Integer.parseInt(prdId.substring(0, 4)))) {
                            maxDateCur = 29;
                        } else
                            maxDateCur = 28;
                        break;
                    case 4:
                        maxDateCur = 30;
                        break;
                    case 6:
                        maxDateCur = 30;
                        break;
                    case 9:
                        maxDateCur = 30;
                        break;
                    case 11:
                        maxDateCur = 30;
                        break;
                    default:
                        maxDateCur = 31;
                        break;
                }
//            }
        }
        return maxDateCur;
    }
    private static boolean isNamNhuan(int yearId) {
        boolean check = false;
        if (yearId % 4 == 0) {
            if (yearId % 100 == 0) {
                if (yearId % 400 == 0) {
                    check = true;
                } else {
                    check = false;
                }
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    public static final String ERR_0 = "200";
    public static final String ERROR_04 = "ERROR_04";
    public static final String ERROR_05 = "ERROR_05";
    public static final String ERR_01 = "ERR_01";
    public static final String ERR_02 = "ERR_02";
    public static final String ERR_03 = "ERR_03";
    public static final String DB_CONNECTION_ERROR = "DB_CONNECTION_ERROR";
    public static final String FILE_NOT_FOUND = "FILE_NOT_FOUND";
    public static final String TABLE_DO_NOT_EXIST = "TABLE_DO_NOT_EXIST";
    public static final String MySQLSyntaxErrorException = "table or view does not exist";
    public static final String SQLSyntaxErrorException = "SQLSyntaxErrorException";
    public static final String SQLException = "Cannot open the connection for the driver";
    public static final String FileNotFoundException = "FileNotFoundException";
    public static final String NullPointerException = "NullPointerException";
}
