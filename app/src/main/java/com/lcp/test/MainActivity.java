package com.lcp.test;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.lcp.test.util.SPUtils;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ContactNotificationMessage;

public class MainActivity extends AppCompatActivity {

    private TextView mUnreadNumView;
    private Button btn_connect;
    private CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        mUnreadNumView = (TextView) findViewById(R.id.de_num);
        btn_connect = (Button) findViewById(R.id.btn_connect);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        final String loginid = SPUtils.getString("loginid");
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginid.equals("12345")) {
                    if (RongIM.getInstance() != null)
                        RongIM.getInstance().startPrivateChat(MainActivity.this, "54321", "123456");
                } else if (loginid.equals("54321")) {
                    if (RongIM.getInstance() != null)
                        RongIM.getInstance().startPrivateChat(MainActivity.this, "12345", "54321");
                }
            }
        });
    }

    private void initData() {
        final Conversation.ConversationType[] conversationTypes = {Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM,
                Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE};

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RongIM.getInstance().setOnReceiveUnreadCountChangedListener(mCountListener, conversationTypes);
            }
        }, 500);

//        getConversationPush();// 获取 push 的 id 和 target
//
//        getPushMessage();

        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {

            @Override
            public UserInfo getUserInfo(String userId) {
                UserInfo userInfo = null;
                if ("12345".equals(userId)) {
                    userInfo = new UserInfo(userId, "联通", Uri.parse("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png"));
                } else if ("54321".equals(userId)) {
                    userInfo = new UserInfo(userId, "电信", Uri.parse("https://ss0.baidu.com/73F1bjeh1BF3odCf/it/u=1330997865,4009510567&fm=96&s=7D20AD1F5B9344D0404C67FF0300C024"));
                }
                return userInfo;//根据 userId 去你的用户系统里查询对应的用户信息返回给融云 SDK。
            }

        }, true);
    }

    public RongIM.OnReceiveUnreadCountChangedListener mCountListener = new RongIM.OnReceiveUnreadCountChangedListener() {
        @Override
        public void onMessageIncreased(int count) {
            if (count == 0) {
                mUnreadNumView.setVisibility(View.GONE);
            } else if (count > 0 && count < 100) {
                mUnreadNumView.setVisibility(View.VISIBLE);
                mUnreadNumView.setText(count + "");
            } else {
                mUnreadNumView.setVisibility(View.VISIBLE);
                mUnreadNumView.setText(R.string.no_read_message);
            }
        }
    };


    /**
     *
     */
    private void getConversationPush() {
        if (getIntent() != null && getIntent().hasExtra("PUSH_CONVERSATIONTYPE") && getIntent().hasExtra("PUSH_TARGETID")) {

            final String conversationType = getIntent().getStringExtra("PUSH_CONVERSATIONTYPE");
            final String targetId = getIntent().getStringExtra("PUSH_TARGETID");


            RongIM.getInstance().getConversation(Conversation.ConversationType.valueOf(conversationType), targetId, new RongIMClient.ResultCallback<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {

                    if (conversation != null) {

                        if (conversation.getLatestMessage() instanceof ContactNotificationMessage) { //好友消息的push
//                            startActivity(new Intent(MainActivity.this, NewFriendListActivity.class));
                        } else {
                            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation")
                                    .appendPath(conversationType).appendQueryParameter("targetId", targetId).build();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {

                }
            });
        }
    }

    /**
     * 得到不落地 push 消息
     */
    private void getPushMessage() {

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {
            String content = intent.getData().getQueryParameter("pushContent");
            String data = intent.getData().getQueryParameter("pushData");
            String id = intent.getData().getQueryParameter("pushId");
            String token = SPUtils.getString("DEMO_TOKEN", "default");
            if (token.equals("default")) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            } else {
                RongIMClient.ConnectionStatusListener.ConnectionStatus status = RongIM.getInstance().getCurrentConnectionStatus();
                if (RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED.equals(status)) {
                    return;
                } else if (RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING.equals(status)) {
                    return;
                } else {
                    Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                    intent1.putExtra("PUSH_MESSAGE", true);
                    startActivity(intent1);
                    finish();
                }

            }
        }
    }

    private final Handler mHandler = new Handler();
    private boolean mBackKeyFlag = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!this.mBackKeyFlag) {
                this.mBackKeyFlag = true;
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                this.mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.mBackKeyFlag = false;
                    }
                }, 2000);
            } else {
                SPUtils.saveBoolean("isquite", true);
                try {
                    boolean allowRongPush = checkbox.isChecked();
                    if (allowRongPush && null != RongIM.getInstance()) {
                        RongIM.getInstance().disconnect();
                    }
                    if (!allowRongPush && null != RongIM.getInstance()) {
                        RongIM.getInstance().logout();
                    }
                } catch (Exception e) {
                }
                finish();
                System.exit(0);
            }
        }
        return false;
    }
}
