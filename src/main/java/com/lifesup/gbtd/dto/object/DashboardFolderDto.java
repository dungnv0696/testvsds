package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Update;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class DashboardFolderDto extends BaseDto {
    private Long folderId;
    private Long folderParentId;
    @NotEmpty(groups = {Add.class, Update.class})
    private String folderCode;
    @NotEmpty(groups = {Add.class, Update.class})
    private String folderName;
    private String description;
    private Long status;
    private Date modifiedDate;
    private String updateUser;

    private String folderNameParent;

    // reportId in dashboard_report
    private Long reportid;

    public DashboardFolderDto(Long folderId, Long folderParentId, String folderCode, String folderName, String description,
                              Long status, Date modifiedDate, String folderNameParent, Long reportid, String updateUser) {
        this.folderId = folderId;
        this.folderParentId = folderParentId;
        this.folderCode = folderCode;
        this.folderName = folderName;
        this.description = description;
        this.status = status;
        this.modifiedDate = modifiedDate;
        this.folderNameParent = folderNameParent;
        this.reportid = reportid;
        this.updateUser = updateUser;
    }

    public DashboardFolderDto(Long folderId, Long folderParentId, String folderCode, String folderName) {
        this.folderId = folderId;
        this.folderParentId = folderParentId;
        this.folderCode = folderCode;
        this.folderName = folderName;
    }

    public DashboardFolderDto(Long folderId, String folderCode, String folderName, Long folderParentId, String description, Long status, Date modifiedDate) {
        this.folderId = folderId;
        this.folderCode = folderCode;
        this.folderName = folderName;
        this.folderParentId = folderParentId;
        this.description = description;
        this.status = status;
        this.modifiedDate = modifiedDate;
    }
}
