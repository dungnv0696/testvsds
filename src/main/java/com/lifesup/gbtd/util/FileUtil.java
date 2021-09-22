package com.lifesup.gbtd.util;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.FileInfo;
import com.lifesup.gbtd.dto.object.ServerInfo;
import com.lifesup.gbtd.exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.StringJoiner;

@Component
@Slf4j
public class FileUtil {

    private static final String WEBAPPS_FOLDER = "webapps";
    private static String CONTENT_FOLDER = "content";
    private static String FOLDER_DEFAULT = "temp";
    private static String FOLDER_REPORT = "report";
    private static String HTTP = "http://";
    private static String BASE_FOLDER;
    private static FileUtil instance;
    private static final ServerInfo SERVER_INFO = new ServerInfo();

    @Autowired
    public FileUtil() {
        log.info("init fileUtils");

        String baseFolder = System.getProperty("catalina.base");
        if (StringUtils.isEmpty(baseFolder)) {
            baseFolder = System.getProperty("user.dir").replace("/bin", "");
        }
        BASE_FOLDER = baseFolder +
                Const.SPECIAL_CHAR.SLASH + WEBAPPS_FOLDER +
                Const.SPECIAL_CHAR.SLASH + CONTENT_FOLDER;

        instance = this;
    }

    public static FileUtil getInstance() {
        return instance;
    }

    public ServerInfo getServerInfo() {
        if (!SERVER_INFO.isInit()) {
            try {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                int port = request.getLocalPort();
                String address = InetAddress.getLocalHost().getHostAddress();
                SERVER_INFO.setIp(address);
                SERVER_INFO.setPort(port);
                log.info(SERVER_INFO.getHost());
            } catch (Exception e) {
                log.error("loi", e);
            }
        }
        return SERVER_INFO;
    }

    private void createFolderIfNotExist(String uploadFolder) {
        Path uploadPath = Paths.get(uploadFolder);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                log.error("cant create directory", e);
                throw new ServerException(ErrorCode.FAILED);
            }
        }
    }

    public FileInfo writeToFileOnServer(Workbook workbook, String fileName) {
        Calendar cal = Calendar.getInstance();
        String uploadFolder = new StringJoiner(Const.SPECIAL_CHAR.SLASH)
                .add(FOLDER_DEFAULT)
                .add("" + cal.get(Calendar.YEAR) + (cal.get(Calendar.MONTH) + 1) + cal.get(Calendar.DATE))
                .add("" + cal.get(Calendar.HOUR_OF_DAY))
                .add("" + cal.get(Calendar.MINUTE))
                .add("" + cal.get(Calendar.SECOND))
                .add("" + cal.get(Calendar.MILLISECOND)).toString();
        String relativePath = CONTENT_FOLDER + Const.SPECIAL_CHAR.SLASH + uploadFolder
                + Const.SPECIAL_CHAR.SLASH + fileName;
        uploadFolder = BASE_FOLDER + Const.SPECIAL_CHAR.SLASH + uploadFolder;
        this.createFolderIfNotExist(uploadFolder);

        uploadFolder = uploadFolder + Const.SPECIAL_CHAR.SLASH + fileName;
        try (OutputStream os = new FileOutputStream(uploadFolder)) {
            workbook.write(os);
        } catch (Exception e) {
            log.error("cant write", e);
            throw new ServerException(ErrorCode.FAILED);
        }

        FileInfo result = new FileInfo(fileName, uploadFolder, relativePath);
        log.info("fileInfo: {}", result.toString());
        return result;
    }

    public FileInfo writeFileReport(MultipartFile file, String fileName) {
        String uploadFolder = FOLDER_REPORT;
        String relativePath = CONTENT_FOLDER + Const.SPECIAL_CHAR.SLASH + uploadFolder
                + Const.SPECIAL_CHAR.SLASH + fileName;
        uploadFolder = BASE_FOLDER + Const.SPECIAL_CHAR.SLASH + uploadFolder;
        this.createFolderIfNotExist(uploadFolder);

        uploadFolder = uploadFolder + Const.SPECIAL_CHAR.SLASH + fileName;
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, Paths.get(uploadFolder), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.error("cant write", e);
            throw new ServerException(ErrorCode.FAILED);
        }

        FileInfo result = new FileInfo(fileName, uploadFolder, relativePath);
        log.info("fileInfo: {}", result.toString());
        return result;
    }

    public Path getFolderReport(String fileName) {
        Path path = StringUtils.isNotEmpty(fileName)
                ? Paths.get(BASE_FOLDER + Const.SPECIAL_CHAR.SLASH + FOLDER_REPORT + Const.SPECIAL_CHAR.SLASH + fileName)
                : Paths.get(BASE_FOLDER + Const.SPECIAL_CHAR.SLASH + FOLDER_REPORT);
        log.info("PATH: " + path.toString());
        return path;
    }

    public String getPathUrlFIle(String ipServer, String fileName){
        return HTTP + ipServer +Const.SPECIAL_CHAR.SLASH+ CONTENT_FOLDER +Const.SPECIAL_CHAR.SLASH + FOLDER_REPORT + Const.SPECIAL_CHAR.SLASH + fileName;
    }
}
