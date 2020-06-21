package com.neoqee.commonlib;

import java.util.List;

/**
 * ProjectName : JavaLib
 * PackageName : com.neoqee.commonlib
 * Create by 小孩 on 2020/6/21
 */
public interface PermissionCallback {

    void callback(boolean allGranted, List<String> deniedPermissions);

}
