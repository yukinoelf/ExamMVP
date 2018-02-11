package com.zte.exammvp.presenter;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.zte.exammvp.contract.LoginContract;
import com.zte.exammvp.model.LoginModel;
import com.zte.exammvp.view.LoginActivity;

import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by LL on 2018/2/5.
 */

public class LoginPresenter implements LoginContract.Presenter {
    private LoginActivity loginActivity;
    private LoginModel loginModel;
    private String countryCode="86";
    private int i = 60;//倒计时

    public LoginPresenter(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
        loginModel = new LoginModel();
        init();
    }

    private void init() {
        loginModel.init(loginActivity, eventHandler);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -1) {
                loginActivity.timeCounter(i);
            } else if (msg.what == -2) {
                loginActivity.timeOut();
                i = 60;
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 短信注册成功后，返回MainActivity,然后提示
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        // 提交验证码成功,调用注册接口，之后直接登录
                        //当号码来自短信注册页面时调用登录注册接口
                        //当号码来自绑定页面时调用绑定手机号码接口
                        loginActivity.showToast("短信验证成功");
                        loginActivity.onSuccess();
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        loginActivity.showToast("验证码已经发送");
                    } else {
                        ((Throwable) data).printStackTrace();
                    }
                } else if (result == SMSSDK.RESULT_ERROR) {
                    try {
                        Throwable throwable = (Throwable) data;
                        throwable.printStackTrace();
                        JSONObject object = new JSONObject(throwable.getMessage());
                        String des = object.optString("detail");//错误描述
                        int status = object.optInt("status");//错误代码
                        if (status > 0 && !TextUtils.isEmpty(des)) {
                            Log.e("asd", "des: " + des);
                            loginActivity.showToast(des);
                            return;
                        }
                    } catch (Exception e) {
                        //do something
                    }
                }
            }
        }
    };

    //initSDK方法是短信SDK的入口，需要传递您从MOB应用管理后台中注册的SMSSDK的应用AppKey和AppSecrete，如果填写错误，后续的操作都将不能进行
    EventHandler eventHandler = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            Message msg = new Message();
            msg.what = -3;
            msg.arg1 = event;
            msg.arg2 = result;
            msg.obj = data;
            handler.sendMessage(msg);
        }
    };

    @Override
    public void requestCode() {
        String phone = loginActivity.getPhone();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(loginActivity, "手机号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        loginModel.requestCode(phone);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (; i > 0; i--) {
                    handler.sendEmptyMessage(-1);
                    if (i <= 0) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendEmptyMessage(-2);
            }
        }).start();
    }

    @Override
    public void login() {
        String phone = loginActivity.getPhone();
        String code = loginActivity.getCode();
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(code)) {
            Toast.makeText(loginActivity, "手机号码或验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        loginModel.login(phone, code);
    }
}
