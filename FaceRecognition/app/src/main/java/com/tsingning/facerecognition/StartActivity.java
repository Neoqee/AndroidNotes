package com.tsingning.facerecognition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends BaseActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mContext = this;
        initLicense();
    }

    private void initLicense(){
        FaceSDKManager.getInstance().init(this, new SdkInitListener() {
            @Override
            public void initStart() {

            }

            public void initLicenseSuccess() {

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        /** 要执行的操作 */
                        startActivity(new Intent(mContext, HomeActivity.class));
                        finish();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 2000);
            }

            @Override
            public void initLicenseFail(int errorCode, String msg) {
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        /** 要执行的操作 */
                        startActivity(new Intent(mContext, ActivitionActivity.class));
                        finish();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 2000);
            }

            @Override
            public void initModelSuccess() {
            }

            @Override
            public void initModelFail(int errorCode, String msg) {

            }
        });
    }

}
