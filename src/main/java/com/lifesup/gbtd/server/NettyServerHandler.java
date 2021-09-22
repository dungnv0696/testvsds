package com.lifesup.gbtd.server;

import com.lifesup.gbtd.controller.LoginController;
import com.lifesup.gbtd.repository.UserLogRepository;
import com.lifesup.gbtd.repository.repositoryImpl.UserLogRepositoryCustomImpl;
import com.lifesup.gbtd.service.inteface.IUserLogService;
import com.lifesup.gbtd.util.DataUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private UserLogRepository userLogRepository;
    private IUserLogService userLogService;

    public NettyServerHandler (UserLogRepository userLogRepository, IUserLogService userLogService) {
        this.userLogRepository = userLogRepository;
        this.userLogService = userLogService;
    }

    private static final ChannelGroup channels = new DefaultChannelGroup(null);

    private static CopyOnWriteArrayList<UserOnline> lstUserOnline = new CopyOnWriteArrayList<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame text) throws Exception {
        String userId = text.text();
        log.info("text message" + text.text());
        int count = 0;
        for(UserOnline uson : lstUserOnline){
            if(uson.getUserId().longValue() == Long.parseLong(userId)){
                if(!uson.getLstChannel().contains(ctx.channel())) {
                    uson.getLstChannel().add(ctx.channel());
                }
                count++;
                break;
            }
        }
        if(count == 0){
            UserOnline user = new UserOnline(Long.parseLong(userId), ctx.channel());
            lstUserOnline.add(user);
        }
        updateCountUserOnline();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        log.info("connect[ip:" + clientIp + "]success");
        channels.add(ctx.channel());

//        Thread thread = new Thread() {
//            public void run() {
//                while(true) {
                    updateCountUserOnline();
//                    try {
//                        Thread.sleep(10000);
//                    }catch(Exception e){
//
//                    }
//                }
//            }
//        };
//        thread.start();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        log.info("connected[ip:" + clientIp + "]fail");
        for(UserOnline uson : lstUserOnline){
            log.info(uson.getLstChannel().toString());
            if(uson.getLstChannel().contains(ctx.channel())){
                uson.getLstChannel().remove(ctx.channel());
                if(uson.getLstChannel().size() == 0){
                    lstUserOnline.remove(uson);
                }
            }
        }
        log.info("updateCountUserOnline " + lstUserOnline.size());
        updateCountUserOnline();

//        channels.remove(ctx.channel());
//        channels.forEach(e -> {
//            e.writeAndFlush(new TextWebSocketFrame(this.getNumberOfUser() + "," + getNumberOfUserLK()));
//        });
    }

    private void updateCountUserOnline(){
        log.info("updateCountUserOnline " + lstUserOnline.size());
        for(UserOnline uson : lstUserOnline){
            for (Channel cha : uson.getLstChannel()){
                cha.writeAndFlush(new TextWebSocketFrame(lstUserOnline.size() + "," + getNumberOfUserLK()));
            }
        }
    }

    private Long getNumberOfUser() {
        Long count = userLogRepository.getNumberOfUser();
        log.info("Counting ...." + count);
        return count;
    }

    private Long getNumberOfUserLK() {
        Long count = userLogRepository.getNumberOfUserLK(Long.parseLong(userLogService.getTime("yyyyMMdd")));
        log.info("Counting ....2" + count);
        return count;
    }
}

