package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Update;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DashboardReportDto extends BaseDto {
    private Long reportId;
    @NotEmpty(groups = {Add.class, Update.class})
    private String reportCode;
    @NotEmpty(groups = {Add.class, Update.class})
    private String reportName;
    private String description;
    @NotNull(groups = {Add.class, Update.class})
    private Long folderId;
    private Long status;
    private String fileName;
    private Date modifiedDate;
    @NotNull(groups = {Add.class, Update.class})
    private Long splitSheet;
    private String updateUser;
    private String ipServer;
    @NotNull(groups = {Add.class})
    private MultipartFile file;

    List<ParamsReportDto> paramsReportDtos;
    private String folderName;

    public DashboardReportDto(Long reportId, String reportCode, String reportName, String description, Long folderId,
                              Long status, String fileName, Date modifiedDate, Long splitSheet, String updateUser,
                              String ipServer, String folderName) {
        this.reportId = reportId;
        this.reportCode = reportCode;
        this.reportName = reportName;
        this.description = description;
        this.folderId = folderId;
        this.status = status;
        this.fileName = fileName;
        this.modifiedDate = modifiedDate;
        this.splitSheet = splitSheet;
        this.updateUser = updateUser;
        this.ipServer = ipServer;
        this.folderName = folderName;
    }

    public DashboardReportDto(Long reportId, @NotEmpty(groups = {Add.class, Update.class}) String reportCode, @NotEmpty(groups = {Add.class, Update.class}) String reportName, @NotNull(groups = {Add.class, Update.class}) Long folderId, String fileName, @NotNull(groups = {Add.class, Update.class}) Long splitSheet) {
        this.reportId = reportId;
        this.reportCode = reportCode;
        this.reportName = reportName;
        this.folderId = folderId;
        this.fileName = fileName;
        this.splitSheet = splitSheet;
    }
}
