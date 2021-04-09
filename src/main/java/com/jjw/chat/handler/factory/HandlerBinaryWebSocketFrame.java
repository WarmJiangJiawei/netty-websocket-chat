package com.jjw.chat.handler.factory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HandlerBinaryWebSocketFrame implements HandlerWebSocketFrame{

    @Override
    public void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // TODO 在线不在线逻辑，且图片如何存储的问题
        log.info("服务器接收到二进制消息,消息长度:[{}]", frame.content().capacity());
        ByteBuf byteBuf = Unpooled.directBuffer(frame.content().capacity());
        byteBuf.writeBytes(frame.content());
        ctx.writeAndFlush(new BinaryWebSocketFrame(byteBuf));
    }

    @Override
    public boolean support(WebSocketFrame frame) {
        return frame instanceof BinaryWebSocketFrame;
    }
}
