package com.lcp.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.lcp.test.util.NLog;
import com.lcp.test.util.SPUtils;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by Aislli on 2016/6/27.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText et_phonenum;
    private String userId1 = "12345";
    private String token1 = "VmwMvG5AH6B3S0xFBSqyw8iGymRvHZcxzyzcRvIHWmAyKzbDdbreC2/e/dm+ogfiolNxvOE92i+7kMhPG3+/yA==";
    private String userId2 = "54321";
    private String token2 = "ABi/QscwyZPbO7Dd6Woe9df5RZHOfFTRnEP1ylFvnvM4gP2S6REwjj/eWO4R6NyiB+/CqFLX71IJNt88+hzjEg==";
    private String loginToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        et_phonenum = (EditText) findViewById(R.id.tv_phonenum);
    }

    public void login(View view){
        String phone = et_phonenum.getText().toString().trim();
        if (phone.equals(userId1)) {
            loginToken = token1;
        }else if(phone.equals(userId2)){
            loginToken = token2;
        }
        RongIM.connect(loginToken, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                NLog.e("connect", "onTokenIncorrect");
            }

            @Override
            public void onSuccess(String s) {
                NLog.e("connect", "onSuccess userid:" + s);
                SPUtils.saveString("loginid", s);
                SPUtils.saveString("loginToken", loginToken);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                NLog.e("connect", "onError errorcode:" + errorCode.getValue());
            }
        });
    }
}
