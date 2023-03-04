package com.mirai;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.mirai.event.AbstractHandler;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ocr.v20181119.OcrClient;
import com.tencentcloudapi.ocr.v20181119.models.QrcodeOCRRequest;
import com.tencentcloudapi.ocr.v20181119.models.QrcodeOCRResponse;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public final class HttpUtils extends AbstractHandler {
    static String gaodeApiKey;
    static String tencentSecretId;
    static String tencentSecretKey;
    static {
        try {
            // Authorization错误时查看控制台ip白名单
            Map<String, Map<String, String>> map = new Yaml().load(new FileReader(configPath + "ApiKeys.yml"));
            Map<String, String> tencentScannerKeys = map.get("tencentScannerKeys");
            Map<String, String> gaodeMapKeys = map.get("gaodeMapKeys");

            gaodeApiKey = gaodeMapKeys.get("apiKey");
            tencentSecretId = tencentScannerKeys.get("secretId");
            tencentSecretKey = tencentScannerKeys.get("secretKey");

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }


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

    public static Image getImageFromStream(InputStream inputStream, Contact contact) {
        Image image = null;
        try {
            byte[] bytes = inputStream.readAllBytes();
            inputStream.close();
            ExternalResource ex = ExternalResource.create(bytes);
            image = ExternalResource.uploadAsImage(ex, contact);
            ex.close();
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("# getImageFromIS出bug啦！");
        }
        return image;
    }


    /**
     * @param url 使用URL格式，即“ http:// ”和“ file:/// ”
     */
    public static String qrDecodeZXing(String url) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new URL(url));
        } catch(IOException e) {
            e.printStackTrace();
        }
        LuminanceSource source = null;
        if(bufferedImage!=null)
            source = new BufferedImageLuminanceSource(bufferedImage);
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
        return result==null ? null : result.getText();
    }

    public static String qrDecodeTencent(String imgUrl) {
        String url = "";
        try {
            Credential cred = new Credential(tencentSecretId, tencentSecretKey);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("ocr.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            OcrClient client = new OcrClient(cred, "ap-shanghai", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            QrcodeOCRRequest req = new QrcodeOCRRequest();
            req.setImageUrl(imgUrl);
            // 返回的resp是一个QrcodeOCRResponse的实例，与请求对象对应
            QrcodeOCRResponse resp = client.QrcodeOCR(req);
            // 输出json格式的字符串回包
            url = resp.getCodeResults()[0].getUrl();
        } catch(TencentCloudSDKException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getLocationInfo(String region) {
        Response response = httpApi("https://restapi.amap.com/v3/geocode/geo?address=" + region.strip() + "&output=json&key=" + gaodeApiKey);
        String result = "";
        try {
            if(response!=null && response.body()!=null) {
                result = response.body().string();
                response.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
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
    public static Response httpApi(String url, Map<String, String> headersMap) {
        OkHttpClient client = new OkHttpClient();
        Request.Builder post = new Request.Builder().url(url).get();
        headersMap.forEach(post::addHeader);
        Request request = post.build();

        try {
            return client.newCall(request).execute();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param bodyMap MediaType 默认为 application/x-www-form-urlencoded
     */
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

    public static Response httpApiPut(String url, Map<String, String> headerMap, Map<String, String> bodyMap) {
        StringBuilder bodySb = new StringBuilder();
        bodyMap.forEach((k, v) -> bodySb.append('&').append(k).append('=').append(v));
        bodySb.deleteCharAt(0);
        String body = bodySb.toString();

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody requestBody = RequestBody.create(body, mediaType);
        Request.Builder post = new Request.Builder()
                .url(url)
                .put(requestBody);
        if(headerMap!=null) headerMap.forEach(post::addHeader);
        Request request = post.build();

        try {
            return client.newCall(request).execute();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Call httpApiCall(String url, Map<String, String> headersMap, Map<String, String> bodyMap) {

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
        if(headersMap!=null) headersMap.forEach(post::addHeader);
        Request request = post.build();

        return client.newCall(request);
    }

    public static Call httpApiCall(String url, Map<String, String> headersMap) {
        OkHttpClient client = new OkHttpClient();
        Request.Builder post = new Request.Builder().url(url).get();
        headersMap.forEach(post::addHeader);
        Request request = post.build();
        return client.newCall(request);
    }

}
