package com.lcp.test;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import io.rong.imkit.RongIM;
import io.rong.imlib.ipc.RongExceptionHandler;
import io.rong.message.GroupNotificationMessage;
import io.rong.push.RongPushClient;


/**
 * Created by bob on 2015/1/30.
 */
public class App extends Application {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        context = this;
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {

            RongPushClient.registerHWPush(this);
            RongPushClient.registerMiPush(this, "2882303761517473625", "5451747338625");
            /*try {
                RongPushClient.registerGCM(this);
            } catch (RongException e) {
                e.printStackTrace();
            }*/
        }
        /**
         * 注意：
         *
         * IMKit SDK调用第一步 初始化
         *
         * context上下文
         *
         * 只有两个进程需要初始化，主进程和 push 进程
         */
        RongIM.init(this);

        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
            Thread.setDefaultUncaughtExceptionHandler(new RongExceptionHandler(this));

            try {
//                RongIM.registerMessageType(AgreedFriendRequestMessage.class);
                RongIM.registerMessageType(GroupNotificationMessage.class);
//                RongIM.registerMessageTemplate(new ContactNotificationMessageProvider());
//                RongIM.registerMessageTemplate(new RealTimeLocationMessageProvider());
//                RongIM.registerMessageTemplate(new GroupNotificationMessageProvider());
                //@ 消息模板展示
//                RongContext.getInstance().registerConversationTemplate(new NewDiscussionConversationProvider());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

}
