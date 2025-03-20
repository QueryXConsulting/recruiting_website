package com.queryx.recruiting_website.netty;

import com.queryx.recruiting_website.common.VideoMsgEncoder;
import com.queryx.recruiting_website.common.WebsocketEncoder;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.common.VideoMsgDecoder;
import com.queryx.recruiting_website.handler.netty.CoordinationSocketHandler;
import com.queryx.recruiting_website.handler.netty.ws.WsSharkHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettySever implements InitializingBean {


    @Resource
    private CoordinationSocketHandler coordinationSocketHandler;
    @Resource
    private WsSharkHandler wsSharkHandler;
    private static Logger LOG = LoggerFactory.getLogger(NettySever.class);


    public void startNettyServer() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup group = new NioEventLoopGroup();

        ServerBootstrap sb = new ServerBootstrap();
        // 连接请求数量的最大值
        sb.option(ChannelOption.SO_BACKLOG, 1024);
        // 设置为非阻塞类型
        sb.group(group, bossGroup) // 绑定线程池
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        LOG.info("初始化连接服务");
                        // 使用 HTTP 解编码器
                        ch.pipeline().addLast(new HttpServerCodec());
                        // 以块的方式写处理器
                        ch.pipeline().addLast(new ChunkedWriteHandler());
                        // 聚合 HTTP 消息
                        ch.pipeline().addLast(new HttpObjectAggregator(8192));
                        // WebSocket 握手处理
                        ch.pipeline().addLast(wsSharkHandler);
                        // 自定义编解码器
                        ch.pipeline().addLast(new VideoMsgDecoder());
                        ch.pipeline().addLast(new WebsocketEncoder());
                        ch.pipeline().addLast(new VideoMsgEncoder());
                        // 业务逻辑处理器
                        ch.pipeline().addLast(coordinationSocketHandler);
                    }
                });

        // 关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            group.shutdownGracefully();
        }));
        // 绑定端口
        ChannelFuture channelFuture = sb.bind(Integer.parseInt(Common.port)).syncUninterruptibly();
        System.out.println(NettySever.class + "已启动，正在监听： " + channelFuture.channel().localAddress());
        // 阻塞主线程，实现服务长期开启的效果
        channelFuture.channel().closeFuture().sync();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread nettyServerThread = new Thread(() -> {
            try {
                startNettyServer();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        nettyServerThread.setName("video-server");
        nettyServerThread.start();
    }

}
