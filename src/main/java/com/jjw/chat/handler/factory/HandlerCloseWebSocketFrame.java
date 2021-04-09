package com.jjw.chat.handler.factory;

import com.jjw.chat.handler.WebSocketChatHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.springframework.stereotype.Component;

@Component
public class HandlerCloseWebSocketFrame implements HandlerWebSocketFrame {


    @Override
    public void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        new WebSocketChatHandler().getHandshaker().close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
        return;
    }

    @Override
    public boolean support(WebSocketFrame frame) {
        return frame instanceof CloseWebSocketFrame;
    }
}
