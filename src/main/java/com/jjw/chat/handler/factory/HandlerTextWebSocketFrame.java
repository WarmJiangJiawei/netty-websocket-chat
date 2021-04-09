package com.jjw.chat.handler.factory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jjw.chat.config.NettyConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class HandlerTextWebSocketFrame implements HandlerWebSocketFrame {

    @Override
    public void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        String msg = ((TextWebSocketFrame) frame).text();
        JSONObject msgObject = JSON.parseObject(msg);
        log.info("服务端收到：{}", msgObject);

        String receiver = msgObject.getString("receiver");
        // 用户在线
        if (NettyConfig.getUserChannelMap().containsKey(receiver)) {

            // 获取接收方的channel
            Channel channel = NettyConfig.getUserChannelMap().get(receiver);

            // 发送者userId
            String userId = (String) channel.attr(AttributeKey.valueOf("userId")).get();

            TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString()
                    + "发送者为：" + userId + "：" + msgObject.getString("message"));
            channel.writeAndFlush(tws);

        } else {
            // 用户不在线处理逻辑
        }
    }

    @Override
    public boolean support(WebSocketFrame frame) {
        return frame instanceof TextWebSocketFrame;
    }
}
