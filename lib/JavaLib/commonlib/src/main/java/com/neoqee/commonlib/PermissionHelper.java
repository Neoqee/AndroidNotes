package com.neoqee.commonlib;


import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.List;

/**
 * ProjectName : JavaLib
 * PackageName : com.neoqee.commonlib
 * Create by 小孩 on 2020/6/21
 */
public class PermissionHelper {

    private static String TAG = "InvisibleFragment";

    public void request(FragmentActivity activity, List<String> permissions, PermissionCallback callback){
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment existedFragment = fragmentManager.findFragmentByTag(TAG);
        InvisibleFragment fragment;
        if (null != existedFragment){
            fragment = (InvisibleFragment) existedFragment;
        }else {
            InvisibleFragment invisibleFragment = new InvisibleFragment();
            fragmentManager.beginTransaction().add(invisibleFragment,TAG).commitNow();
            fragment = invisibleFragment;
        }
        fragment.requestNow(permissions,callback);
    }

}
