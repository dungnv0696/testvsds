package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.ChartCommentDto;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "CHART_COMMENT")
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "chartComment.tableDesc", classes = {
                @ConstructorResult(targetClass = ChartCommentDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "chartId", type = Long.class),
                                @ColumnResult(name = "userName", type = String.class),
                                @ColumnResult(name = "dateTime", type = Date.class),
                                @ColumnResult(name = "content", type = String.class)
                        })
        })
})
public class ChartCommentEntity {
    private Long id;
    private Long chartId;
    private String userName;
    private Date dateTime;
    private String content;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CHART_COMMENT_SEQ")
    @SequenceGenerator(name = "CHART_COMMENT_SEQ", sequenceName = "CHART_COMMENT_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "CHART_ID", nullable = true, precision = 0)
    public Long getChartId() {
        return chartId;
    }

    public void setChartId(Long chartId) {
        this.chartId = chartId;
    }

    @Basic
    @Column(name = "USER_NAME", nullable = true, length = 200)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Basic
    @Column(name = "DATE_TIME", nullable = true)
    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @Basic
    @Column(name = "CONTENT", nullable = true)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChartCommentEntity that = (ChartCommentEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(chartId, that.chartId) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(dateTime, that.dateTime) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chartId, userName, dateTime, content);
    }
}
