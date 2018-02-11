package com.zte.exammvp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zte.exammvp.R;
import com.zte.exammvp.contract.LoginContract;
import com.zte.exammvp.presenter.LoginPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements LoginContract.View, View.OnClickListener {
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etCode)
    EditText etCode;
    @BindView(R.id.bnReqCode)
    Button bnReqCode;
    @BindView(R.id.bnLogin)
    Button bnLogin;

    private LoginPresenter loginPresenter;
    private InputMethodManager intputManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        loginPresenter = new LoginPresenter(this);
        init();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess() {
        Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainActivity);
    }

    @Override
    public String getPhone() {
        return etPhone.getText().toString().trim();
    }

    @Override
    public String getCode() {
        return etCode.getText().toString().trim();
    }

    @Override
    public void timeCounter(int i) {
        bnReqCode.setText(i + " s");
    }

    @Override
    public void timeOut() {
        bnReqCode.setText("重新发送");
        bnReqCode.setClickable(true);
    }

    private void init() {
        intputManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        bnReqCode.setOnClickListener(this);
        bnLogin.setOnClickListener(this);
    }

    private void login() {
        loginPresenter.login();
    }

    private void requestCode() {
        loginPresenter.requestCode();
        etCode.setFocusable(true);
        etCode.setFocusableInTouchMode(true);
        etCode.requestFocus();
        intputManager.toggleSoftInputFromWindow(etCode.getWindowToken(), 1, 0);
        bnReqCode.setClickable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bnReqCode:
                requestCode();
                break;
            case R.id.bnLogin:
                login();
                break;
        }
    }
}
