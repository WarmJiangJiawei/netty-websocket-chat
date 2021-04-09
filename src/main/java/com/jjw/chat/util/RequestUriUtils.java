package com.jjw.chat.util;

import java.util.HashMap;
import java.util.Map;

public class RequestUriUtils {

    /**
     * 将路径参数转为map
     * @param uri
     * @return
     */
    public static Map<String, String> getParams(String uri) {
        Map<String, String> params = new HashMap<>(10);

        int idx = uri.indexOf("?");
        if (idx != -1) {
            String[] paramsArr = uri.substring(idx + 1).split("&");
            for (String param : paramsArr) {
                idx = param.indexOf("=");
                params.put(param.substring(0, idx), param.substring(idx + 1));
            }
        }
        return params;
    }


    /**
     * 获取路径中参数以外部分的路径
     * @param uri
     * @return
     */
    public static String getBasePath(String uri) {
        if (uri == null || uri.isEmpty())
            return null;

        int idx = uri.indexOf("?");
        if (idx == -1)
            return uri;

        return uri.substring(0, idx);
    }


}
