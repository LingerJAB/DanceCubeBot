package com.dancecube.image;

import com.dancecube.info.AccountInfo;
import com.dancecube.info.UserInfo;

public class AllUserInfos {
    private UserInfo userInfo;
    private AccountInfo accountInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public AllUserInfos setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public AllUserInfos setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
        return this;
    }


//    @NotNull
//    public static AllUserInfos getAllInfo(Token token) throws ExecutionException, InterruptedException {
//        JavaPluginScheduler scheduler = MiraiBot.INSTANCE.getScheduler();
//        AllUserInfos allInfo = new AllUserInfos();
////        CompletableFuture<AllUserInfos> userInfoFuture = CompletableFuture.supplyAsync(() -> allInfo.setUserInfo(UserInfo.get(token)));
////        CompletableFuture<AllUserInfos> accountInfoFuture = CompletableFuture.supplyAsync(() -> allInfo.setAccountInfo(AccountInfo.get(token)));
////        CompletableFuture<Void> allFutures = CompletableFuture.allOf(accountInfoFuture, userInfoFuture);
////
////        allFutures.join();
//        Future<UserInfo> userInfoFuture = scheduler.async(() -> UserInfo.get(token));
//        Future<AccountInfo> accountInfoFuture = scheduler.async(() -> AccountInfo.get(token));
//        UserInfo userInfo = userInfoFuture.get();
//        AccountInfo accountInfo=accountInfoFuture.get();
//        allInfo.setAccountInfo(accountInfo);
//        allInfo.setUserInfo(userInfo);
//        return allInfo;
//    }
}
