package com.jjw.chat.handler.factory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class HandlerWebSocketFrameFactory implements ApplicationContextAware {

    private static List<HandlerWebSocketFrame> list = Collections.synchronizedList(new ArrayList<>());

    public static List<HandlerWebSocketFrame> getList() {
        return list;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, HandlerWebSocketFrame> beansOfType =
                applicationContext.getBeansOfType(HandlerWebSocketFrame.class);
        beansOfType.forEach((key, value) -> list.add(value));
    }
}
