package com.neoqee.commonlib.pickphoto;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.neoqee.commonlib.InvisibleFragment;


public class PickPhotoHelper {

    private static String TAG = "InvisibleFragment";

    public final static int CAMERAREQUESTCODE = 101;
    public final static int ALBUMREQUESTCODE = 102;

    private PickPhotoHelper(){}

    public volatile static PickPhotoHelper instance = null;

    public static PickPhotoHelper getInstance(){
        if (null == instance){
            synchronized (PickPhotoHelper.class){
                if (null == instance){
                    instance = new PickPhotoHelper();
                }
            }
        }
        return instance;
    }

    public void pick(FragmentActivity activity, int type, PickResultCallback callback){
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
//        fragment.requestNow(permissions,callback);
        fragment.pickPhoto(type,callback);
    }

}
