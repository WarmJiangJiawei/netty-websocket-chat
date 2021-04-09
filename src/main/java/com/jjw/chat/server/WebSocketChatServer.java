package com.jjw.chat.server;

import com.jjw.chat.config.WebSocketChatServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

@Slf4j
@Component
public class WebSocketChatServer {

    public static final Integer PORT = 10086;

    private NioEventLoopGroup boss;
    private NioEventLoopGroup worker;

    public void start() throws InterruptedException {

        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        // boss负责处理请求，worker负责同客户端之间的读写处理
        bootstrap.group(boss, worker)
                .localAddress(new InetSocketAddress(PORT))
                .channel(NioServerSocketChannel.class)  // NIO类型
                .childHandler(new WebSocketChatServerInitializer()) // 连接到达时创建channel
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        log.info("WebSocketChatServer在 {} 端口启动了", PORT);
        ChannelFuture sync = bootstrap.bind().sync();

        // 对关闭通道进行监听
        sync.channel().closeFuture().sync();
        boss.shutdownGracefully();
        worker.shutdownGracefully();
        log.info("WebSocketChatServer关闭了");
    }

    @PostConstruct()
    public void init() {
        new Thread(() -> {
            try {
                start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @PreDestroy()
    public void destory() {
        try {
            if (null != boss) {
                boss.shutdownGracefully().sync();
            }
            if (null != worker) {
                worker.shutdownGracefully().sync();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
