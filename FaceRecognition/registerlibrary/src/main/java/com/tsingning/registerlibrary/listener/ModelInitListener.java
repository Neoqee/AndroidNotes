package com.tsingning.registerlibrary.listener;

public interface ModelInitListener {

    void initStart();

    void initLicenseSuccess();

    void initLicenseFail(int errorCode, String msg);

    void initModelSuccess();

    void initModelFail(int errorCode, String msg);

}
