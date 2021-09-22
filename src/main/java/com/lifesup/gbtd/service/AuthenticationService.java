package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.*;
import com.lifesup.gbtd.dto.request.UserInfoRequest;
import com.lifesup.gbtd.dto.response.LoginResponseDto;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.*;
import com.lifesup.gbtd.repository.CatDepartmentRepository;
import com.lifesup.gbtd.repository.SsoMapDeptRepository;
import com.lifesup.gbtd.repository.UsersRepository;
import com.lifesup.gbtd.service.inteface.IAuthenticationService;
import com.lifesup.gbtd.service.inteface.IJWTService;
import com.lifesup.gbtd.service.inteface.INotShowIntroductionService;
import com.lifesup.gbtd.util.*;
import com.viettel.vps.webservice.AuthorizedData;
import com.viettel.vps.webservice.AuthorizedDataVAC;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.JoseException;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.cert.CertificateException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class AuthenticationService extends BaseService implements IAuthenticationService {

    private final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    private final UsersRepository usersRepository;
    private final IJWTService jwtService;
    private final CatDepartmentRepository catDepartmentRepository;
    private final SsoMapDeptRepository ssoMapDeptRepository;
    private final UserLogService userLogService;
    private final INotShowIntroductionService notShowIntroductionService;

    @Value("${chatbot.url.authenticate}")
    private String CHATBOT_URL_AUTHEN;

    @Value("${chatbot.url.logout}")
    private String CHATBOT_URL_LOGOUT;

    @Value("${chatbot.user}")
    private String CHATBOT_USER;

    @Value("${chatbot.password}")
    private String CHATBOT_PASSWORD;

    @Value("${vac.signing_key}")
    private String SIGNING_KEY;

    @Value("${vac.encryption_key}")
    private String ENCRYPTION_KEY;
    @Value("${images.folder}")
    private String pathImagesFolder;
    @Value("${sso}")
    private String ssoUrl;
    @Value("${request.otp}")
    private String urlRequestOtp;
    private final static String MBI = "mbi";


    @Autowired
    public AuthenticationService(UsersRepository usersRepository,
                                 IJWTService jwtService,
                                 CatDepartmentRepository catDepartmentRepository,
                                 SsoMapDeptRepository ssoMapDeptRepository, UserLogService userLogService, INotShowIntroductionService notShowIntroductionService) {
        this.usersRepository = usersRepository;
        this.jwtService = jwtService;
        this.catDepartmentRepository = catDepartmentRepository;
        this.ssoMapDeptRepository = ssoMapDeptRepository;
        this.userLogService = userLogService;
        this.notShowIntroductionService = notShowIntroductionService;
    }

    @Override
    public UserInfoDto getUserInfo(UserInfoRequest userInfoRequest) {
        if (null == userInfoRequest.getUserId()
                && StringUtils.isEmpty(userInfoRequest.getUsername())
                && StringUtils.isEmpty(userInfoRequest.getStaffCode())) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "must have at least one parameter");
        }
        List<UsersEntity> exists = usersRepository.findUserInfo(userInfoRequest);
        if (DataUtil.isNullOrEmpty(exists)) {
            throw new ServerException(ErrorCode.NOT_FOUND, "user");
        }

        UserInfoDto user = super.map(exists.get(0), UserInfoDto.class);
        catDepartmentRepository.findById(user.getDeptId())
                .ifPresent(e -> user.setDeptName(e.getName()));
        List<CatDepartmentDto> depts = super.mapList(usersRepository.getUserDepartmentInfo(user.getId()), CatDepartmentDto.class);
        depts = new ArrayList<>(depts);
        CatDepartmentEntity currentDept = catDepartmentRepository.findById(user.getDeptId())
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "Department"));
        depts.add(super.map(currentDept, CatDepartmentDto.class));
        user.setDepts(depts);
        return user;
    }

    public UsersDto getUserInfo(String username) {
        UserInfoDto userInfoDto = this.getUserInfo(new UserInfoRequest(username));
        return super.map(userInfoDto, UsersDto.class);
    }

    @Override
    public JSONObject callApiAuthenticateChatbot() {
        String username = super.getCurrentUsername();
        UsersDto userData = this.getUserInfo(username);
        JSONObject jsonObject = null;
        try {
            log.info(ReflectionToStringBuilder.toString(userData, ToStringStyle.JSON_STYLE));
            URL obj = new URL(CHATBOT_URL_AUTHEN);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            this.setChatBotConnectionProperties(con, HttpMethod.POST.toString());

            Map<String, Object> body = new HashMap<>();
            body.put("domain", MBI);
            body.put("username", null == userData.getEmail() ? "" : userData.getEmail().split("@")[0]);
            body.put("fullname", userData.getName());
            body.put("mobile", userData.getPhone());
            body.put("device", "BROWSER");
            body.put("isLogin", 0);
            body.put("listComponents", null);
            body.put("deptLevel", userData.getDepts().stream()
                    .filter(d -> d.getId().equals(userData.getDeptId()))
                    .map(CatDepartmentDto::getDeptLevel)
                    .findAny().orElse(null));
            body.put("deptId", userData.getDeptId());
            body.put("position", null);
            body.put("staffCode", userData.getStaffCode());
            body.put("unit", null);
            String xml = JsonUtil.writeAsString(body);
            log.info("--- body data ---: {}", xml);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8));
            bw.write(xml);
            bw.flush();
            bw.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            log.info("--- response ---: {}", response.toString());
            jsonObject = new JSONObject(response.toString());
            con.disconnect();

        } catch (Exception e) {
            log.error("Exception : ", e);
        }
        return jsonObject;
    }

    private void setChatBotConnectionProperties(HttpURLConnection con, String method) throws ProtocolException {
        String encoded = Base64.getEncoder()
                .encodeToString(
                        (CHATBOT_USER + ":" + Crypto.getInstance().decrypt(CHATBOT_PASSWORD))
                                .getBytes(StandardCharsets.UTF_8)
                );  //Java 8
        con.setRequestMethod(method);
        con.setRequestProperty("content-type", "application/json; charset=utf-8");
        con.setDoOutput(true);
        con.setRequestProperty("Authorization", "Basic " + encoded);
    }

    @Override
    public void logoutChatBot(String token) throws Exception {
        HttpURLConnection con = null;
        try {
            URL obj = new URL(CHATBOT_URL_LOGOUT);
            con = (HttpURLConnection) obj.openConnection();
            this.setChatBotConnectionProperties(con, HttpMethod.POST.toString());
            DataOutputStream wrHsSv = new DataOutputStream(con.getOutputStream());
            wrHsSv.writeBytes(token);
            wrHsSv.flush();
            wrHsSv.close();

            int responseCode = con.getResponseCode();
            log.info("--- response ---: {}", responseCode);
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new Exception("Logout error!");
            }
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    @Override
    public LoginResponseDto login(String ticket) {
        UserLogDto userLogDto = new UserLogDto("GET","LOGIN", MessageUtil.getMessage("code.login"));
        LoginResponseDto res = new LoginResponseDto();
        log.info("Ticket: " + ticket);
        log.info("SSO: " + ssoUrl);
        String userInfo = this.translateJwt(ticket);
        log.info("UserInfo: " + userInfo);
        Map<String, Object> parsed = null;
        try {
            parsed = org.jose4j.json.JsonUtil.parseJson(userInfo);
        } catch (JoseException ex) {
            log.error("error", ex);
            throw new ServerException(ErrorCode.JSON_WRONG_FORMAT);
        }
        Long userId = null;
        if (null != parsed) {
            // tach user name;
            String username = parsed.get("userName").toString();
            String usernameArray = parsed.get("userName").toString().replace("[","").replace("]","");

            username = username.contains("[") ? usernameArray : username;
            AuthorizedData authorizedData = AuthorizedDataVAC.getAuthorizedData(username, null, null);

            if (Objects.isNull(authorizedData) || authorizedData.getBusinessUserPermissions().isEmpty()) {
                log.error("Not found Permissions from VAC. Access Denied for user: " + username);
                // Hoa tat phan quyen
                throw new ServerException(ErrorCode.PERMISSION_DENIED);
            } else {
                this.mappingUserDepartment(parsed);
            }

            UsersDto usersDto = this.getUserInfo(username);
            res.setUsersInfo(usersDto);
            res.setPermission(authorizedData);

            String token = jwtService.expiring(this.createUserInfoAttributes(usersDto));
            res.setToken(token);
            userLogDto.setToken(token);
            userLogDto.setUserId(usersDto.getId());
            userId = usersDto.getId();
        }
        log.info(ReflectionToStringBuilder.toString(res, ToStringStyle.JSON_STYLE));
//        userLogService.saveLog(userLogDto);
        //xu ly login lan dau
        if(checkLoginFirstTime(userId) || (!checkLoginFirstTime(userId) && !checkClickPopUpShow(userId))){
            res.setShowIntroduction(true);
        }
        res.setListImages(getListImages());
        userLogService.saveLog(userLogDto);
        return res;
    }

    @Override
    public LoginResponseDto loginMobile(AccountEndCode accountEndCode) {
        try {
            String params = "username=" + URLEncoder.encode(accountEndCode.getUsername(),java.nio.charset.StandardCharsets.UTF_8.toString())
                    + "&password=" + URLEncoder.encode(accountEndCode.getPassword(), java.nio.charset.StandardCharsets.UTF_8.toString())
                    + "&token=true";

            String s = sendHtpps(params, ssoUrl);
            return login(s);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private String sendHtpps(String params, String url) {
        String result = "";
        OutputStreamWriter out = null;
        BufferedReader in = null;
        HttpURLConnection conn = null;

        try {
            trustAllHosts();
            URL realUrl = new URL(null, url, new sun.net.www.protocol.https.Handler());
            if ("https".toLowerCase().equals(realUrl.getProtocol())) {
                HttpsURLConnection https = (HttpsURLConnection) realUrl.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                conn = https;
            } else {
                conn = (HttpURLConnection) realUrl.openConnection();
            }

            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(params);
            out.flush();

            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            result = "sendHtpps error";
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;


    }

    private void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {
                // TODO Auto-generated method stub
            }
            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {
                // TODO Auto-generated method stub
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getListImages(){
//        String PATH = "C:/Users/Admin/Desktop/thaiph";
        List<String> listImages = new ArrayList<>();
        try {
            File folder = new File(pathImagesFolder);
            File[] listOfFiles = folder.listFiles();


            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    listImages.add(listOfFiles[i].getName());
                }
            }
            Collections.sort(listImages);
        } catch (Exception e){
            log.error("Cau hinh lai file duong dan trong file propertie",e.getMessage());
        }

        return listImages;
    }
    private boolean checkLoginFirstTime(Long userId){
        List<UserLogEntity> userLogEntityList= userLogService.checkLoginFirstTime(userId);
        if (!userLogEntityList.isEmpty()){
            return false;
        }else {
            return true;
        }
    }

    private boolean checkClickPopUpShow(Long userId){
        List<NotShowIntroductionEntity> result = notShowIntroductionService.findByUserId(userId);
        if (result.isEmpty()){
            return false;
        } else {
            return true;
        }
    }

    private void mappingUserDepartment(Map<String, Object> map) {
        String username = map.get("userName").toString();
        String usernameArray = map.get("userName").toString().replace("[","").replace("]","");
        username = username.contains("[") ? usernameArray : username;

        String deptIdStrArr = map.get("deptId").toString().replace("[","").replace("]","");
        String deptIdStr = map.get("deptId").toString().contains("[") ? deptIdStrArr : map.get("deptId").toString() ;

        SsoMapDeptEntity ssoMapDeptEntity = ssoMapDeptRepository.findBySsoDeptId(Long.parseLong(deptIdStr))
                .stream()
                .findAny()
                .orElseThrow(() -> new ServerException(ErrorCode.NO_CONFIG, "SSO_MAP_DEPT-" + deptIdStr));
        Long deptId = ssoMapDeptEntity.getMapDeptId();
        List<UsersEntity> entities = usersRepository.findByUsernameOrIdOrStaffCode(username, null, null);
        if (entities.isEmpty()) {
            UsersEntity usersEntity = new UsersEntity();
            usersEntity.setUsername(username);
            usersEntity.setDeptId(deptId);
            usersEntity.setName(map.get("fullName").toString());
            usersEntity.setStaffCode(map.get("staffCode").toString());
            usersEntity.setStaffCode(map.get("staffCode").toString());
            usersEntity.setPhone(map.get("phoneNumber").toString());
            usersEntity.setEmail(map.get("email").toString());
            usersEntity.setStatus(Integer.parseInt(map.get("status").toString()));

            usersRepository.save(usersEntity);
        } else {
            if (!deptId.equals(entities.get(0).getDeptId())) {
                entities.get(0).setDeptId(deptId);
                usersRepository.save(entities.get(0));
            }
        }
    }

    private Map<String, String> createUserInfoAttributes(UsersDto usersDto) {
        return Stream.of(new String[][]{
                {Const.AUTHENTICATION.TOKEN_CLAIM_KEY_USER_ID, usersDto.getId().toString()},
                {Const.AUTHENTICATION.TOKEN_CLAIM_KEY_STAFF_CODE, usersDto.getStaffCode()},
                {Const.AUTHENTICATION.TOKEN_CLAIM_KEY_USERNAME, usersDto.getUsername()},
                {Const.AUTHENTICATION.TOKEN_CLAIM_KEY_DEPT_ID, usersDto.getDeptId().toString()}
        }).collect(Collectors.toMap(
                data -> data[0],
                data -> data[1],
                (x, y) -> x));
    }

    private String translateJwt(String secureJwt) {
        try {
            final String signingKey = SIGNING_KEY;
            final String encryptionKey = ENCRYPTION_KEY;
            String result = URLDecoder.decode(secureJwt, StandardCharsets.UTF_8.name());
            final Key key = new AesKey(signingKey.getBytes(StandardCharsets.UTF_8));

            final JsonWebSignature jws = new JsonWebSignature();
            jws.setCompactSerialization(result);
            jws.setKey(key);

            if (!jws.verifySignature()) {
                throw new Exception("JWT verification failed");
            }

            final byte[] decodedBytes = org.apache.commons.codec.binary.Base64.decodeBase64(jws.getEncodedPayload().getBytes(StandardCharsets.UTF_8));
            final String decodedPayload = new String(decodedBytes, StandardCharsets.UTF_8);
            final JsonWebEncryption jwe = new JsonWebEncryption();
            final JsonWebKey jsonWebKey = JsonWebKey.Factory
                    .newJwk("\n" + "{\"kty\":\"oct\",\n" + " \"k\":\"" + encryptionKey + "\"\n" + "}");
            jwe.setCompactSerialization(decodedPayload);
            jwe.setKey(new AesKey(jsonWebKey.getKey().getEncoded()));

            return jwe.getPlaintextString();
        } catch (Exception ex) {
            log.error("error", ex);
            throw new ServerException(ErrorCode.TICKET_NOT_VALID);
        }
    }

    @Override
    public Optional<UsersEntity> findByToken(String token) {
        return Optional
                .of(jwtService.verify(token))
                .map(map -> {
                    try {
                        return Arrays.asList(Long.parseLong(map.get(Const.AUTHENTICATION.TOKEN_CLAIM_KEY_USER_ID)),
                                Long.parseLong(map.get(Const.AUTHENTICATION.TOKEN_CLAIM_KEY_DEPT_ID)));
                    } catch (NumberFormatException numberFormatException) {
                        log.error("id not valid", numberFormatException);
                        return null;
                    }
                })
                .flatMap(arr -> {
                    Optional<UsersEntity> usersEntity = usersRepository.findById(arr.get(0));
                    usersEntity.ifPresent(entity -> entity.setDeptId(arr.get(1)));
                    return usersEntity;
                });
    }

    @Override
    public void logout() {

    }

    @Override
    public LoginResponseDto changeDept(UserInfoRequest userInfoRequest) {
        if (Objects.isNull(userInfoRequest.getDeptId()))
            throw new ServerException(ErrorCode.MISSING_PARAMS, "Department");

        UsersDto usersDto = this.getUserInfo(super.getCurrentUsername());
        usersDto.setDeptId(userInfoRequest.getDeptId());
        catDepartmentRepository.findById(userInfoRequest.getDeptId())
                .ifPresent(e -> usersDto.setDeptName(e.getName()));

        LoginResponseDto res = new LoginResponseDto();

        String token = jwtService.expiring(this.createUserInfoAttributes(usersDto));
        res.setToken(token);
        res.setUsersInfo(usersDto);
        return res;
    }

    @Override
    public LoginResponseDto getImages(Long userId) {
        LoginResponseDto res = new LoginResponseDto();
        if(checkLoginFirstTime(userId) || (!checkLoginFirstTime(userId) && !checkClickPopUpShow(userId))){
            res.setShowIntroduction(true);
        }
        res.setListImages(getListImages());
        return res;
    }

    @Override
    public Map<String, Object> getOtp(AccountEndCode accountEndCode) {
        String urlParameters  = "username="+accountEndCode.getUsername()+"&locale=vi";
        byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
        String result= "";
        try {
            String urlReq= urlRequestOtp;
            URL url = new URL(urlReq);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("username", "241173");
            connection.setRequestProperty("password", "b50c9e7a0099b4a800aecf089d2ee9dc");
            connection.connect();
            try( DataOutputStream wr = new DataOutputStream( connection.getOutputStream())) {
                wr.write( postData );
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            Map<String, Object> parsed=    org.jose4j.json.JsonUtil.parseJson(result);
            return  parsed;
        }catch (Exception e){
            log.info(e.getMessage());
        }
        return null;
    }
}
