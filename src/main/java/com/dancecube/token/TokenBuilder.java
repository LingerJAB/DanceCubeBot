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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.mirai.config.AbstractConfig.configPath;

public final class TokenBuilder {

    private static int index = 0;
    private final String id;
    private static final ArrayList<String> ids = initIds();

    private static ArrayList<String> initIds() {
        try {
            FileReader reader = new FileReader(configPath + "TokenIds.json");
            String[] strings = new Gson().fromJson(reader, String[].class);
            return new ArrayList<>(Arrays.asList(strings));
        } catch(FileNotFoundException e) {
            throw new RuntimeException("TokenIds.json 文件未找到，请重新配置");
        }
    }

    public TokenBuilder() {
        this.id = getId();
    }

    private static String getId() {
        //ID会一段时间释放，需要换用ID
        if(index>ids.size() - 1) index = 0;
        return ids.get(index++);
    }

    public String getQrcodeUrl() {
        return getQrcodeUrl(id);
    }

    private String getQrcodeUrl(String id) {
        try {
            Response response = HttpUtil.httpApi("https://dancedemo.shenghuayule.com/Dance/api/Common/GetQrCode?id=" + id);
            String string;
            if(response!=null && response.body()!=null) {
                string = response.body().string();
                response.close(); // 释放
                return JsonParser.parseString(string).getAsJsonObject().get("QrcodeUrl").getAsString();
            }
            return "";
        } catch(IOException e) {
            throw new RuntimeException(e);
        } catch(NullPointerException e) {
            System.out.println("# ID:" + id + " 不可用已删除，还剩下" + ids.size() + "条");
            throw new RuntimeException(e);
        }
    }

    public Token getToken() {
        long curTime = System.currentTimeMillis();
        Call call = HttpUtil.httpApiCall("https://dancedemo.shenghuayule.com/Dance/token", Map.of("content-type", "application/x-www-form-urlencoded"), Map.of("client_type", "qrcode", "grant_type", "client_credentials", "client_id", id));
        Response response;
        //五分钟计时
        long wait = System.currentTimeMillis();
        while(System.currentTimeMillis() - curTime<300_000) {
            if(System.currentTimeMillis() - wait<2000) continue;  //等待2s时间（防止高频）
//            System.out.println(System.currentTimeMillis()+" minus "+ wait);
            wait = System.currentTimeMillis(); //重新赋值等待时间

            try {
                //call不能重复请求
                response = call.clone().execute();
                //未登录为 400 登录为 200
                if(response.body()!=null && response.code()==200) {
                    JsonObject json = JsonParser.parseString(response.body().string()).getAsJsonObject();
                    return new Token(json.get("userId").getAsInt(), json.get("access_token").getAsString(), json.get("refresh_token").getAsString(), 0);
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
    public void test() {
//        String accessToken ="Bj1iYAA5z4bemJ0gn-QKgGiw1xE9sh21FP6kcm8Iy2sXcRx_i2tLeAiqrvxfdi53BtHVY_3M4N5yip0w4FpcH1RM-GsqHaWQL9LnAl5BpdZQUblqOjb-t_2XFstbvxMS1FQ8BF3j65XrwSpOjIx9QOg_-igNomTpfx-31Itz7S-s7ua1KGcUBT9ippgg3p3S8Os_jFxs7wJPYLzn_tbj8fD5VgtjFZJhIH2-NIM_XpGhDfSaxXWyFmDbTEIY1iM6p24Lyswk1U_fJ4US93ozD7JS8c1gJQ_mijrmgVwlDDrrmacEtLvaFSaOSBEfFtstqi7alZFcOgWlzwn-JtP1gmu-1ToOoqubjLpOqBmeOl5QN7xejkB-oC5DzQ9ES4XS";
//        String refreshToken ="a8ada5b46b4044af8315506e03d35335";
//        Token token=new Token(939088,accessToken,refreshToken,0);
//        System.out.println(token);
//        System.out.println(token.checkAvailable());
//
//        token.refresh();
//        System.out.println(token.checkAvailable());
//        System.out.println(token);
        autoRefreshToken();
        try {
            TimeUnit.MINUTES.sleep(1);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
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
