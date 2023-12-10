package com.mirai.task;

import com.dancecube.token.Token;
import com.dancecube.token.TokenBuilder;
import com.mirai.config.AbstractConfig;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mirai.config.AbstractConfig.configPath;

/*
为什么设计成JobDetail + Job，不直接使用Job？

JobDetail 定义的是任务数据，而真正的执行逻辑是在Job中。
这是因为任务是有可能并发执行，如果Scheduler直接使用Job，就会存在对同一个Job实例并发访问的问题。
而JobDetail & Job 方式，Scheduler每次执行，都会根据JobDetail创建一个新的Job实例，这样就可以 规避并发访问 的问题
 */

/**
 * 刷新本地Token任务
 *
 * @author Lin
 */
public class RefreshTokenJob implements Job {
    private static final String dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    private static final HashMap<Long, Token> userTokensMap = AbstractConfig.userTokensMap;

    public void execute(JobExecutionContext context) throws JobExecutionException {
        AtomicInteger validNum = new AtomicInteger();
        userTokensMap.forEach(
                (qq, token) -> {
                    if(token.refresh()) validNum.getAndIncrement();
                }
        );
        TokenBuilder.tokensToFile(userTokensMap, configPath + "UserTokens.json");
        System.out.printf("#%s 读取共%d个token，有效token共%d个%n", dateFormat, userTokensMap.size(), validNum.get());
    }
}
