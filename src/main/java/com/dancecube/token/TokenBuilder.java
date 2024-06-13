package com.dancecube.token;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mirai.task.RefreshTokenJob;
import com.tools.HttpUtil;
import okhttp3.Call;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mirai.config.AbstractConfig.configPath;

public final class TokenBuilder {
    //公用 ids
    private static ArrayList<String> ids = initIds();

    //公用 pointer
    private static int pointer = 0;

    //临时 index
    private final int index;

    //临时个人 id,多线程不会阻塞
    private final String id;


    public static ArrayList<String> initIds() {
        try {
            FileReader reader = new FileReader(configPath + "TokenIds.json");
            String[] strings = new Gson().fromJson(reader, String[].class);
            System.out.println(Arrays.toString(strings));
            return new ArrayList<>(Arrays.asList(strings));
        } catch(FileNotFoundException e) {
            throw new RuntimeException("TokenIds.json 文件未找到，请重新配置");
        }
    }

    public TokenBuilder() {
        this.index = pointer;
        this.id = getId();
    }

    public static ArrayList<String> updateIds() {
        TokenBuilder.ids = initIds();
        return ids;
    }

    public static int getSize() {
        return ids.size();
    }

    private static String getId() {
        //ID会一段时间释放，需要换用ID
        if(pointer>getSize() - 1) pointer = 0;
        return ids.get(pointer++);
    }

    public String getQrcodeUrl() {
        return getQrcodeUrl(id);
    }

    private String getQrcodeUrl(String id) {
        //对于含有'+' '/'等符号的TokenId会自动url编码
        id = URLEncoder.encode(id, StandardCharsets.UTF_8);

        String string = "";
        try {
            Response response = HttpUtil.httpApi("https://dancedemo.shenghuayule.com/Dance/api/Common/GetQrCode?id=" + id);
            if(response!=null && response.body()!=null) {
                string = response.body().string();
                response.close(); // 释放
                return JsonParser.parseString(string).getAsJsonObject().get("QrcodeUrl").getAsString();
            }
            return "";
        } catch(IOException e) {
            throw new RuntimeException(e);
        } catch(NullPointerException e) {
            System.out.println(string);
            ids.remove(index);
            System.out.println("# ID:" + id + " 不可用已删除，还剩下" + getSize() + "条");
            throw new RuntimeException(e);
        }
    }

    public String getNewID() {
        try {
            Response response = HttpUtil.httpApi("https://dancedemo.shenghuayule.com/Dance/api/Common/GetQrCode");

            String string;
            if(response!=null && response.body()!=null) {
                string = response.body().string();
                if(response.code()==400) return null;
                response.close(); // 释放
                JsonObject jsonObject = JsonParser.parseString(string).getAsJsonObject();
                System.out.println(jsonObject);
                return jsonObject.get("ID").getAsString();
            }
            return "";
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test() throws IOException {
        URL url = new URL("https://dancedemo.shenghuayule.com/Dance/api/Common/GetQrCode");
        byte[] allBytes = url.openStream().readAllBytes();
        String json = new String(allBytes);
        Pattern compile = Pattern.compile("\"ID\":\"(\\S+)\",\"Q");
        Matcher matcher = compile.matcher(json);
        matcher.find();
        System.out.println(matcher.group(1));
    }

    public Token getToken() {
        return getToken(id);
    }

    public Token getToken(String id) {
        long curTime = System.currentTimeMillis();
        Call call = HttpUtil.httpApiCall("https://dancedemo.shenghuayule.com/Dance/token",
                Map.of("content-type", "application/x-www-form-urlencoded"),
                Map.of("client_type", "qrcode",
                        "grant_type", "client_credentials",
                        "client_id", URLEncoder.encode(id, Charset.defaultCharset())));
        Response response;
        //五分钟计时
        long wait = System.currentTimeMillis();
        while(System.currentTimeMillis() - curTime<300_000) {  // 5min超时
            if(System.currentTimeMillis() - wait<4000) continue;  //等待4s时间（防止高频）
            wait = System.currentTimeMillis(); //重新赋值等待时间

            try {
                //call不能重复请求
                response = call.clone().execute();
                //未登录为 400 登录为 200
                if(response.body()!=null && response.code()==200) {
                    JsonObject json = JsonParser.parseString(response.body().string()).getAsJsonObject();
                    return new Token(json.get("userId").getAsInt(),
                            json.get("access_token").getAsString(),
                            json.get("refresh_token").getAsString(),
                            curTime);
                }
                response.close();  // 关闭释放
            } catch(IOException e) {
                System.out.println("# TokenHttp执行bug辣！");
                e.printStackTrace();
            }
        }
        return null;
    }


    // HashMap写入json文件 不用数组是为了覆盖原key
    public static void tokensToFile(HashMap<Long, Token> tokenMap, String filePath) {
        Type type = new TypeToken<HashMap<Long, Token>>() {
        }.getType();
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(tokenMap, type);

        try {
            FileOutputStream stream = new FileOutputStream(filePath);
            stream.write(json.getBytes(StandardCharsets.UTF_8));
            stream.close();
        } catch(IOException e) {
            System.out.println("# TokenToFile执行bug辣！");
            throw new RuntimeException(e);
        }
    }

    // 读取json文件Token Map
    public static HashMap<Long, Token> tokensFromFile(String filePath, boolean refreshing) {
        Type type = new TypeToken<HashMap<Long, Token>>() {
        }.getType();
        HashMap<Long, Token> userMap;
        try {
            userMap = new Gson().fromJson(new FileReader(filePath), type);
            // 读取并refresh()
            if(refreshing & userMap!=null) userMap.forEach((key, token) -> token.refresh());
        } catch(FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return userMap;
    }

    public static HashMap<Long, Token> tokensFromFile(String filePath) {
        return tokensFromFile(filePath, false);
    }


    @Test
    public void main() {
        for(; ; ) {
            System.out.println("start?");
            Scanner scanner = new Scanner(System.in);
            String reply = scanner.nextLine();
            if(reply.equals("n")) {
                break;
            } else if(reply.equals("y")) {
                TokenBuilder builder = new TokenBuilder();
                System.out.println("url:" + builder.getQrcodeUrl());
                System.out.println("loading...");
                System.out.println(builder.getToken());
            } else {
                System.out.println("#your reply:" + reply);
            }
        }
    }

    public void autoRefreshToken() {
        Scheduler scheduler;
        try {
            scheduler = new StdSchedulerFactory().getScheduler();
        } catch(SchedulerException e) {
            throw new RuntimeException(e);
        }
        JobDetail jobDetail = JobBuilder.newJob(RefreshTokenJob.class).build();
        Trigger trigger = TriggerBuilder.newTrigger().startNow().withSchedule(
                        SimpleScheduleBuilder.repeatSecondlyForever(3))
//                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(13, 36))
                .build();
        try {
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        } catch(SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}
