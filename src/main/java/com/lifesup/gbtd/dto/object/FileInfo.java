package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.FileUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
public class FileInfo {
    private String fileName;
    private String physicalPath;
    private String relativePath;
    private String host;

    public FileInfo(String fileName, String physicalPath, String relativePath) {
        this.fileName = fileName;
        this.physicalPath = physicalPath;
        this.relativePath = relativePath;
        this.host = FileUtil.getInstance().getServerInfo().getHost();
    }

    public String getFullPath() {
        return "http://" + host + Const.SPECIAL_CHAR.SLASH + StringUtils.replace(relativePath, Const.SPECIAL_CHAR.BACKSLASH, Const.SPECIAL_CHAR.SLASH);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
