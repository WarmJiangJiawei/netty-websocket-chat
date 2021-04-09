package com.jjw.chat.handler.factory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.springframework.stereotype.Component;

@Component
public class HandlerPingWebSocketFrame implements HandlerWebSocketFrame {

    @Override
    public void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        ctx.channel().write(
                new PongWebSocketFrame(frame.content().retain()));
        return;
    }

    @Override
    public boolean support(WebSocketFrame frame) {
        return frame instanceof PingWebSocketFrame;
    }
}
