package com.jjw.chat.config;

import com.jjw.chat.handler.WebSocketChatHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class WebSocketChatServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        // 心跳检测第一个设置，如果超时将调用userEventTriggered(), 并告知超时类型
        pipeline.addLast(new IdleStateHandler(30, 30, 30,
                TimeUnit.MINUTES));

        //  webSocket协议是基于http协议，所以使用http编解码器
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ObjectEncoder());

        // http数据在传输过程中是分段的，HttpObjectAggregator可以将多个段聚合
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
        pipeline.addLast(new ChunkedWriteHandler());

        // websocket协议
        //pipeline.addLast(new WebSocketServerProtocolHandler(PATH,
                //null, true, 65536 * 10));

        // 自定义处理器用以处理业务逻辑
        pipeline.addLast(new WebSocketChatHandler());
    }
}
