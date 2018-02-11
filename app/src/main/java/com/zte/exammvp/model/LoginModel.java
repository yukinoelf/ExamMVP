package com.zte.exammvp.model;

import android.content.Context;

import com.zte.exammvp.contract.LoginContract;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by LL on 2018/2/5.
 */

public class LoginModel implements LoginContract.Model {
    private String countryCode="86";

    public void init(Context ctx, EventHandler eventHandler) {
        SMSSDK.initSDK(ctx, "2431ccd0f37ea", "85dfad1a7ee45741c7250dbb1619b29b");
        //注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
    }

    @Override
    public void requestCode(String phone) {
        SMSSDK.getVerificationCode(countryCode, phone);
    }

    @Override
    public void login(String phone, String code) {
        SMSSDK.submitVerificationCode(countryCode, phone, code);
    }
}
