package com.lifesup.gbtd.server;

import com.lifesup.gbtd.repository.UserLogRepository;
import com.lifesup.gbtd.service.inteface.IUserLogService;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private UserLogRepository userLogRepository;
    private IUserLogService userLogService;

    public NettyServerInitializer (UserLogRepository userLogRepository, IUserLogService userLogService) {
        this.userLogRepository = userLogRepository;
        this.userLogService = userLogService;
    }

    /**
     * @param ch
     * @throws java.lang.Exception
     */
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        pipeline.addLast(new WebSocketServerProtocolHandler("/websocket"));
        pipeline.addLast(new NettyServerHandler(userLogRepository, userLogService));
    }
}

