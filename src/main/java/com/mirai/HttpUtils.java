package com.mirai;

import net.mamoe.mirai.utils.ExternalResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public final class HttpUtils {
    // 用于返回图片资源
    public static ExternalResource getExResByURL(URL url) throws IOException {
        URLConnection uc = url.openConnection();
        InputStream in = uc.getInputStream();
        byte[] bytes = in.readAllBytes();
        in.close();
        return ExternalResource.create(bytes);
    }

}
