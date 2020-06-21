package com.neoqee.commonlib;

import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * ProjectName : JavaLib
 * PackageName : com.neoqee.commonlib
 * Create by 小孩 on 2020/6/21
 */
public class InvisibleFragment extends Fragment {

    private PermissionCallback callback;

    public void requestNow(List<String> permissions,PermissionCallback callback){
        this.callback = callback;
        requestPermissions(permissions.toArray(new String[permissions.size()]),998);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 998){
            List<String> deniedList = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    deniedList.add(permissions[i]);
                }
            }
            boolean allGranted = deniedList.isEmpty();
            callback.callback(allGranted,deniedList);
        }
    }

}
