package com.dancecube.image;

import com.dancecube.api.AccountInfo;
import com.dancecube.api.UserInfo;

public class allUserInfos {
    private UserInfo userInfo;
    private AccountInfo accountInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public allUserInfos setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public allUserInfos setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
        return this;
    }
}
