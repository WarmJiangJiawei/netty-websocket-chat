package com.jjw.chat.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jjw.chat.config.NettyConfig;
import com.jjw.chat.handler.factory.HandlerWebSocketFrame;
import com.jjw.chat.handler.factory.HandlerWebSocketFrameFactory;
import com.jjw.chat.util.RequestUriUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;

@Slf4j
public class WebSocketChatHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;

    public WebSocketServerHandshaker getHandshaker() {
        return handshaker;
    }

    public static final String WEB_SOCKET_URL = "ws://127.0.0.1:10086/webSocket";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("handlerAdded -> Client:" + channel.remoteAddress() + "加入");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        // 获取在handleHttpRequest方法中写入到channel的userId
        String userId = (String) channel.attr(AttributeKey.valueOf("userId")).get();
        NettyConfig.getUserChannelMap().remove(userId);
        log.info("userId为{}的用户离线，当前在线人数：{}", userId, NettyConfig.getUserChannelMap().size());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        log.info("收到消息：{}", o);
        // websocket第一次建立连接使用的是http协议，握手后将升级为ws
        if (o instanceof FullHttpRequest) {
            handleHttpRequest(channelHandlerContext, (FullHttpRequest) o);
        } else if (o instanceof WebSocketFrame) {
            handlerWebSocketFrame(channelHandlerContext, (WebSocketFrame) o);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    // 唯一的一次http请求，用于创建websocket
    private void handleHttpRequest(ChannelHandlerContext ctx,
                                   FullHttpRequest req) {
        // 要求Upgrade为websocket，过滤掉get/Post
        if (!req.decoderResult().isSuccess()
                || (!"websocket".equals(req.headers().get("Upgrade")))) {
            // 若不是websocket方式，则创建BAD_REQUEST的req，返回给客户端
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        String uri = req.uri();
        Map<String, String> params = RequestUriUtils.getParams(uri);
        log.info("接收到的参数是：{}", JSON.toJSONString(params));

        if (params.containsKey("uid")) {
            String uid = params.get("uid");
            if (!NettyConfig.getUserChannelMap().containsKey(uid)) {
                NettyConfig.getUserChannelMap().put(uid, ctx.channel());

                // 将userId作为自定义属性加入到channel中，方便随时channel中获取userId
                AttributeKey<String> key = AttributeKey.valueOf("userId");
                ctx.channel().attr(key).setIfAbsent(uid);
            }

            log.info("userId为{}的用户上线，当前在线人数：{}", uid, NettyConfig.getUserChannelMap().size());
        }


        // 同客户端握手
        WebSocketServerHandshakerFactory wsshFactory = new WebSocketServerHandshakerFactory(
                WEB_SOCKET_URL, null, false);
        handshaker = wsshFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory
                    .sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    // 拒绝不合法的请求，并返回错误信息
    private static void sendHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        // 如果是非Keep-Alive，关闭连接
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame){
        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }

        List<HandlerWebSocketFrame> list = HandlerWebSocketFrameFactory.getList();
        for (HandlerWebSocketFrame handlerWebSocketFrame : list) {
            if (handlerWebSocketFrame.support(frame))
                handlerWebSocketFrame.handlerWebSocketFrame(ctx, frame);
        }

    }
}
