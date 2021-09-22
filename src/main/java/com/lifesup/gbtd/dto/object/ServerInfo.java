package com.lifesup.gbtd.dto.object;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@ToString
public class ServerInfo {
    private String ip;
    private int port;

    public ServerInfo() {
    }

    public ServerInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public boolean isInit() {
        return StringUtils.isNotEmpty(ip) && port > 0;
    }

    public String getHost() {
        return ip + ":" + port;
    }
}
