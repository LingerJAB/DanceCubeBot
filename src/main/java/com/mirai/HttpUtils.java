package com.mirai;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public final class HttpUtils {

    public static Image getImageFromURL(String strUrl, Contact contact) {
        Image image = null;
        try {
            URL url = new URL(strUrl);
            URLConnection uc = url.openConnection();
            InputStream in = uc.getInputStream();
            byte[] bytes = in.readAllBytes();
            in.close();
            ExternalResource ex = ExternalResource.create(bytes);
            image = ExternalResource.uploadAsImage(ex, contact);
            ex.close();
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("# getImageFromURL出bug啦！");
        }
        return image;
    }

    public static String QrDecode(String url) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new URL(url));
        } catch(IOException e) {
            e.printStackTrace();
        }
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        Binarizer binarizer = new HybridBinarizer(source);
        BinaryBitmap bitmap = new BinaryBitmap(binarizer);
        HashMap<DecodeHintType, Object> decodeHints = new HashMap<>();
        decodeHints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
        Result result = null;
        try {
            result = new MultiFormatReader().decode(bitmap, decodeHints);
        } catch(NotFoundException e) {
            e.printStackTrace();
        }
        return result.getText();
    }

    @Nullable
    public static String getLocationInfo(String region) {
        Response response = httpApi("https://restapi.amap.com/v3/geocode/geo?address=" + region.strip() + "&output=json&key=b1bbd99c8a1a9117227498975da1f5a4");
        if(response==null) return null;
        String result = null;
        try {
            result = response.body().string();
        } catch(IOException e) {
            e.printStackTrace();
        }
        response.close();
        return result;
    }

    @Nullable  // 用于获取HTTP API资源
    public static Response httpApi(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();

        try {
            return client.newCall(request).execute();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable  // GET
    public static Response httpApi(String url, Map<String, String> headerMap) {
        OkHttpClient client = new OkHttpClient();
        Request.Builder post = new Request.Builder().url(url).get();
        headerMap.forEach(post::addHeader);
        Request request = post.build();

        try {
            return client.newCall(request).execute();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable  // POST
    public static Response httpApi(String url, Map<String, String> headerMap, Map<String, String> bodyMap) {

        StringBuilder bodySb = new StringBuilder();
        bodyMap.forEach((k, v) -> bodySb.append('&').append(k).append('=').append(v));
        bodySb.deleteCharAt(0);
        String body = bodySb.toString();

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody requestBody = RequestBody.create(body, mediaType);
        Request.Builder post = new Request.Builder()
                .url(url)
                .post(requestBody);
        if(headerMap!=null) headerMap.forEach(post::addHeader);
        Request request = post.build();

        try {
            return client.newCall(request).execute();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
