package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Find;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ChartCommentDto extends BaseDto {

    private Long id;
    @NotNull(groups = {Add.class, Find.class})
    private Long chartId;
    private String userName;
    private Date dateTime;
    @NotNull(groups = Add.class)
    private String content;

    public ChartCommentDto(Long id, Long chartId, String userName, Date dateTime, String content) {
        this.id = id;
        this.chartId = chartId;
        this.userName = userName;
        this.dateTime = dateTime;
        this.content = content;
    }
}
