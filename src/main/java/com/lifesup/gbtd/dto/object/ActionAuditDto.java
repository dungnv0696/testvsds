package com.lifesup.gbtd.dto.object;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lifesup.gbtd.util.JsonDateSerializer;
import com.lifesup.gbtd.validator.FieldValue;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
public class ActionAuditDto {

    @Size(max = 100)
    @NotEmpty(message = "tableName not empty")
    @NotNull(message = "tableName is required")
    private String tableName;
    @Size(max = 100)
    @NotNull(message = "objectId is required")
    @NotEmpty(message = "objectId not empty")
    private String objectId;
    @Size(max = 100)
    @NotEmpty(message = "action not empty")
    @NotNull(message = "action is required")
    @FieldValue(strings = {"DELETE", "UPDATE", "INSERT"}, message = "Action can only be DELETE, UPDATE, INSERT")
    private String action;
    @NotEmpty(message = "oldValue not empty")
    @NotNull(message = "oldValue is required")
    private String oldValue;
    @NotEmpty(message = "newValue not empty")
    @NotNull(message = "newValue is required")
    private String newValue;
    @Size(max = 100)
    @NotEmpty(message = "user not empty")
    @NotNull(message = "user is required")
    private String user;
    @Size(max = 100)
    @NotNull(message = "userIP is required")
    @Pattern(regexp = "^([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])" +
            "\\.([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])" +
            "\\.([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])" +
            "\\.([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])$",
            message = "UserIp wrong format")
    private String userIP;

    @NotNull(message = " createDate is required")
    @JsonDeserialize(using = JsonDateSerializer.class)
    private Date createDate;

    public ActionAuditDto() {
    }

    public ActionAuditDto(String tableName, String objectId, String action, String oldValue,
                          String newValue, String user, String userIP, Date createDate) {
        this.tableName = tableName;
        this.objectId = objectId;
        this.action = action;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.user = user;
        this.userIP = userIP;
        this.createDate = createDate;
    }

    public static class Builder {
        private String tableName;
        private String objectId;
        private String action;
        private String oldValue;
        private String newValue;
        private String user;
        private String userIP;
        private Date createDate;

        public Builder() {
            this.createDate = new Date();
        }

        public Builder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder objectId(String objectId) {
            this.objectId = objectId;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder oldValue(String oldValue) {
            this.oldValue = oldValue;
            return this;
        }

        public Builder newValue(String newValue) {
            this.newValue = newValue;
            return this;
        }

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public Builder userIP(String userIP) {
            this.userIP = userIP;
            return this;
        }

        public Builder createDate(Date createDate) {
            this.createDate = createDate;
            return this;
        }

        public Builder createDate() {
            this.createDate = new Date();
            return this;
        }

        public Builder oldValue(Object oldValue) {
            if (oldValue == null) {
                this.oldValue = null;
                return this;
            }
            this.oldValue = oldValue instanceof String
                    ? oldValue.toString()
                    : ReflectionToStringBuilder.toString(oldValue, ToStringStyle.JSON_STYLE);
            return this;
        }

        public Builder newValue(Object newValue) {
            if (newValue == null) {
                this.newValue = null;
                return this;
            }
            this.newValue = newValue instanceof String
                    ? newValue.toString()
                    : ReflectionToStringBuilder.toString(newValue, ToStringStyle.JSON_STYLE);
            return this;
        }

        public Builder oldValueNull() {
            this.oldValue = null;
            return this;
        }

        public Builder newValueNull() {
            this.newValue = null;
            return this;
        }

        public Builder objectId(Long objectId) {
            this.objectId = objectId != null ? String.valueOf(objectId) : null;
            return this;
        }

        public ActionAuditDto build() {
            return new ActionAuditDto(tableName, objectId, action, oldValue, newValue, user, userIP, createDate);
        }
    }
}
