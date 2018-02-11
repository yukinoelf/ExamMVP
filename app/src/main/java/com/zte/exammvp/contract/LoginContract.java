package com.zte.exammvp.contract;

/**
 * Created by LL on 2018/2/5.
 */

public interface LoginContract {
    interface Model {
        void requestCode(String phone);
        void login(String phone, String code);
    }

    interface View {
        void showToast(String msg);
        void onSuccess();
        String getPhone();
        String getCode();
        void timeCounter(int i);
        void timeOut();
    }

    interface Presenter {
        void requestCode();
        void login();
    }
}
