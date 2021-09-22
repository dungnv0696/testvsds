package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import com.lifesup.gbtd.validator.FieldValue;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Delete;
import com.lifesup.gbtd.validator.group.Find;
import com.lifesup.gbtd.validator.group.Share;
import com.lifesup.gbtd.validator.group.Update;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class TempReportDto extends BaseDto {

    @NotNull(message = "reportId is required", groups = {Update.class, Delete.class, Find.class})
    private Long reportId;
    private String customerId;
    @NotNull(message = "reportName is required", groups = {Add.class, Update.class})
    private String reportName;
    private String createUser;
    @NotNull(message = "reportType is required", groups = {Add.class, Update.class})
    private String reportType;
    @NotNull(message = "type is required", groups = {Add.class, Update.class})
    private String type;
    @NotNull(message = "deptParam is required", groups = {Add.class, Update.class})
    private String deptParam;
    private Date fromDate;
    private String checked;
    @NotNull(message = "ownerBy is required", groups = {Share.class})
    private String ownerBy;
    @FieldValue(numbers = {1, 2, 3})
    private Long status;

    public TempReportDto(Long reportId, String customerId, String reportName, String createUser, String reportType,
                         String type, String deptParam, Date fromDate, String checked, String ownerBy, Long status) {
        this.reportId = reportId;
        this.customerId = customerId;
        this.reportName = reportName;
        this.createUser = createUser;
        this.reportType = reportType;
        this.type = type;
        this.deptParam = deptParam;
        this.fromDate = fromDate;
        this.checked = checked;
        this.ownerBy = ownerBy;
        this.status = status;
    }
}
