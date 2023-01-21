package com.mirai;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import net.mamoe.mirai.utils.ExternalResource;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public final class HttpUtils {

    /**
     * 用于返回图片资源
     *
     * @return Image
     */
    public static ExternalResource getExResByURL(URL url) throws IOException {
        URLConnection uc = url.openConnection();
        InputStream in = uc.getInputStream();
        byte[] bytes = in.readAllBytes();
        in.close();
        return ExternalResource.create(bytes);
    }

    public static String QrDecode(String filepath) throws IOException, NotFoundException {
        BufferedImage bufferedImage = ImageIO.read(new FileInputStream(filepath));
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        Binarizer binarizer = new HybridBinarizer(source);
        BinaryBitmap bitmap = new BinaryBitmap(binarizer);
        HashMap<DecodeHintType, Object> decodeHints = new HashMap<>();
        decodeHints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
        Result result = new MultiFormatReader().decode(bitmap, decodeHints);
        return result.getText();
    }

    @Nullable
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

    @Nullable
    public static Response httpApi(String url, String requestBody) {

        OkHttpClient client=new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(requestBody, mediaType);
        Request request = new Request.Builder().url(url).post(body).build();

        try {
            return client.newCall(request).execute();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getLocationInfo(String region) {
        Response response = httpApi("https://restapi.amap.com/v3/geocode/geo?address=" + region.strip() + "&output=json&key=b1bbd99c8a1a9117227498975da1f5a4");
        if(response==null) return null;
        String result;
        try {
            result = response.body().string();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        response.close();
        return result;
    }
}
