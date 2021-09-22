package com.lifesup.gbtd.server;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserOnline {

    private Long userId;
    private CopyOnWriteArrayList<Channel> lstChannel = new CopyOnWriteArrayList();

    public UserOnline(long parseLong, Channel channel) {
        userId = parseLong;
        lstChannel.add(channel);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public CopyOnWriteArrayList<Channel> getLstChannel() {
        return lstChannel;
    }

    public void setLstChannel(CopyOnWriteArrayList<Channel> lstChannel) {
        this.lstChannel = lstChannel;
    }
}
