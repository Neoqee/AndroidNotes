package com.tsingning.registerlibrary;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.tsingning.registerlibrary.api.FaceApi;
import com.tsingning.registerlibrary.listener.ModelInitListener;
import com.tsingning.registerlibrary.manager.FaceRegisterManager;
import com.tsingning.registerlibrary.manager.UserInfoManager;
import com.tsingning.registerlibrary.model.User;
import com.tsingning.registerlibrary.utils.ToastUtils;

import java.util.List;

public class UserManageActivity extends BaseActivity {

    private ImageView backBtn;
    private RecyclerView userListRv;
    private FaceUserAdapter faceUserAdapter;
    private Context mContext;

    private List<User> mUserInfoList;
    private UserListListener mUserListListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListener();
        FaceRegisterManager.getInstance().initDataBases(this);
        setContentView(R.layout.activity_user_manage);
        mContext = this;
        initView();
        initData();
        bindEvent();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // 读取数据库，获取用户信息
        UserInfoManager.getInstance().getUserListInfo(null, mUserListListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 数据变化，更新内存
        FaceApi.getInstance().initDatabases(true);
    }

    private void initListener() {
        if (FaceRegisterManager.initStatus != FaceRegisterManager.SDK_MODEL_LOAD_SUCCESS) {
            FaceRegisterManager.getInstance().initModel(this, new ModelInitListener() {
                @Override
                public void initStart() {
                }

                @Override
                public void initLicenseSuccess() {
                }

                @Override
                public void initLicenseFail(int errorCode, String msg) {
                }

                @Override
                public void initModelSuccess() {
                    FaceRegisterManager.initModelSuccess = true;
                    ToastUtils.toast(UserManageActivity.this, "模型加载成功，欢迎使用");
                }

                @Override
                public void initModelFail(int errorCode, String msg) {
                    FaceRegisterManager.initModelSuccess = false;
                    if (errorCode != -12) {
                        ToastUtils.toast(UserManageActivity.this, "模型加载失败，请尝试重启应用");
                    }
                }
            });
        }
    }

    private void initView(){
        backBtn = findViewById(R.id.image_register_back);
        userListRv = findViewById(R.id.recycler_user_manager);

        faceUserAdapter = new FaceUserAdapter();
        userListRv.setAdapter(faceUserAdapter);


    }

    private void initData(){
        mUserListListener = new UserListListener();
        // 读取数据库，获取用户信息
        UserInfoManager.getInstance().getUserListInfo(null, mUserListListener);
    }

    private void bindEvent(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    // 更新删除相关的UI
    private void updateDeleteUI(boolean isShowDeleteUI) {

    }

    // 用于返回读取用户的结果
    private class UserListListener extends UserInfoManager.UserInfoListener {
        // 读取用户列表成功
        @Override
        public void userListQuerySuccess(final String userName, final List<User> listUserInfo) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mUserInfoList = listUserInfo;
                    if (listUserInfo == null || listUserInfo.size() == 0) {
                        // 显示无内容判断
//                        if (userName == null) {
//                            mTextEmpty.setText("暂无内容");
//                            updateDeleteUI(false);
//                        } else {
//                            mTextEmpty.setText("暂无搜索结果");
//                            mRelativeDelete.setVisibility(View.GONE);
//                        }
                        return;
                    }

                    // 恢复默认状态
                    if (userName == null || userName.length() == 0) {
                        updateDeleteUI(false);
                    } else {
                        updateDeleteUI(true);
                    }
                    faceUserAdapter.submitList(listUserInfo);
                    faceUserAdapter.notifyDataSetChanged();
                }
            });
        }

        // 读取用户列表失败
        @Override
        public void userListQueryFailure(final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mContext == null) {
                        return;
                    }
                    ToastUtils.toast(mContext, message);
                }
            });
        }

        // 删除用户列表成功
        @Override
        public void userListDeleteSuccess() {
            UserInfoManager.getInstance().getUserListInfo(null, mUserListListener);
        }

        // 删除用户列表失败
        @Override
        public void userListDeleteFailure(final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mContext == null) {
                        return;
                    }
                    ToastUtils.toast(mContext, message);
                }
            });
        }
    }

}
