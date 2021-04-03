package com.tsingning.facerecognition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tsingning.attendancelibrary.FaceAttendanceActivity;
import com.tsingning.registerlibrary.FaceRegisterActivity;
import com.tsingning.registerlibrary.UserManageActivity;

public class HomeActivity extends BaseActivity {

    private Context mContext;

    private Button registerBtn,managerBtn,attendanceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = this;
        initView();
    }

    private void initView() {
        registerBtn = findViewById(R.id.registerBtn);
        managerBtn = findViewById(R.id.managerBtn);
        attendanceBtn = findViewById(R.id.attendanceBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpTo(FaceRegisterActivity.class);
            }
        });
        managerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpTo(UserManageActivity.class);
            }
        });
        attendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpTo(FaceAttendanceActivity.class);
            }
        });
    }

    private void jumpTo(Class<?> clz){
        Intent intent = new Intent(this, clz);
        startActivity(intent);
    }

}
