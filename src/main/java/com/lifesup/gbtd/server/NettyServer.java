package com.lifesup.gbtd.server;

import com.lifesup.gbtd.repository.UserLogRepository;
import com.lifesup.gbtd.service.inteface.IUserLogService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NettyServer {

//    @Autowired
    private final UserLogRepository userLogRepository;
    private IUserLogService userLogService;

    /**
     *
     */
    private Logger log = LoggerFactory.getLogger(NettyServer.class);

    /**
     *
     */
    @Value("${netty.port}")
    private int port;

    private boolean init;

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public NettyServer(UserLogRepository userLogRepository, IUserLogService userLogService) {
        this.userLogRepository = userLogRepository;
        this.userLogService = userLogService;

//        Thread thread = new Thread() {
//            @Override
//            public void start() {
//                try {
//                    Thread.sleep(3000);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//
//            }
//        };
//        thread.start();
    }

    /**
     * @param
     */
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            final ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(final SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new HttpRequestDecoder(),
                                    new HttpObjectAggregator(65536),
                                    new HttpResponseEncoder(),
                                    new WebSocketServerProtocolHandler("/websocket"),
                                    new NettyServerInitializer(userLogRepository, userLogService));
                        }
                    });
            ChannelFuture channelFuture = sb.bind(port).sync();
            log.info("netty: [port:" + port + "]");

            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("netty-", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}



