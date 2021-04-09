package com.jjw.chat.handler.factory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public interface HandlerWebSocketFrame {

    /**
     * 处理对应类型
     * @param ctx
     * @param frame
     */
    void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame);

    /**
     * 是否支持对此种帧类型的处理
     * @param frame
     * @return
     */
    boolean support(WebSocketFrame frame);
}
