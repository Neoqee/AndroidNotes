package com.tsingning.registerlibrary.manager;

import android.content.Context;

import com.baidu.idl.main.facesdk.FaceAuth;
import com.baidu.idl.main.facesdk.FaceCrop;
import com.baidu.idl.main.facesdk.FaceDetect;
import com.baidu.idl.main.facesdk.FaceFeature;
import com.baidu.idl.main.facesdk.ImageIllum;
import com.baidu.idl.main.facesdk.callback.Callback;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceInstance;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.baidu.idl.main.facesdk.model.BDFaceSDKConfig;
import com.tsingning.registerlibrary.api.FaceApi;
import com.tsingning.registerlibrary.callback.FaceFeatureCallBack;
import com.tsingning.registerlibrary.config.GlobalSet;
import com.tsingning.registerlibrary.config.SingleBaseConfig;
import com.tsingning.registerlibrary.db.DBManager;
import com.tsingning.registerlibrary.listener.ModelInitListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 人脸注册管理类
 * */
public class FaceRegisterManager {

    public static final int SDK_MODEL_LOAD_SUCCESS = 0;
    public static final int SDK_UNACTIVATION = 1;
    public static final int SDK_UNINIT = 2;
    public static final int SDK_INITING = 3;
    public static final int SDK_INITED = 4;
    public static final int SDK_INIT_FAIL = 5;
    public static final int SDK_INIT_SUCCESS = 6;

    public static volatile boolean initModelSuccess = false;

    public static volatile int initStatus = SDK_UNACTIVATION;

    private FaceAuth faceAuth;
    private FaceDetect faceDetect;      // 人脸检测
    private FaceFeature faceFeature;    // 人俩特征     用来检验和识别人脸的
    private FaceCrop faceCrop;
    private ImageIllum imageIllum;      // 曝光

    private ExecutorService mRegExecutorService = Executors.newSingleThreadExecutor();
    private Future mRegFuture;

    private FaceRegisterManager() {
        faceAuth = new FaceAuth();
        faceAuth.setActiveLog(BDFaceSDKCommon.BDFaceLogInfo.BDFACE_LOG_TYPE_ALL, 1);
        faceAuth.setCoreConfigure(BDFaceSDKCommon.BDFaceCoreRunMode.BDFACE_LITE_POWER_LOW, 2);
    }

    private static class HolderClass {
        private static final FaceRegisterManager instance = new FaceRegisterManager();
    }

    public static FaceRegisterManager getInstance() {
        return HolderClass.instance;
    }

    public FaceDetect getFaceDetect() {
        return faceDetect;
    }

    public FaceFeature getFaceFeature() {
        return faceFeature;
    }

    public FaceCrop getFaceCrop() {
        return faceCrop;
    }

    /**
     * 初始化模型；因为初始化是顺序执行，可以在最好初始化回掉中返回状态结果
     * */
    public void initModel(final Context context, final ModelInitListener listener){
        // 默认检测
        BDFaceInstance bdFaceInstance = new BDFaceInstance();
        bdFaceInstance.creatInstance();
        faceDetect = new FaceDetect(bdFaceInstance);
        // 默认识别
        faceFeature = new FaceFeature();

        faceCrop = new FaceCrop();

        // 曝光
        imageIllum = new ImageIllum();

        initConfig();

        faceDetect.initModel(
                context,
                GlobalSet.DETECT_VIS_MODEL,
                null,
                GlobalSet.ALIGN_RGB_MODEL,
                new Callback() {
                    @Override
                    public void onResponse(int i, String s) {
                        if (i != 0 && listener != null) {
                            listener.initModelFail(i, s);
                        }
                    }
                });
        faceDetect.initModel(
                context,
                GlobalSet.DETECT_VIS_MODEL,
                GlobalSet.ALIGN_TRACK_MODEL,
                BDFaceSDKCommon.DetectType.DETECT_VIS,
                BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_FAST,
                new Callback() {
                    @Override
                    public void onResponse(int code, String response) {
                        if (code != 0 && listener != null) {
                            listener.initModelFail(code, response);
                        }
                    }
                });
        faceDetect.initModel(
                context,
                GlobalSet.DETECT_VIS_MODEL,
                GlobalSet.ALIGN_RGB_MODEL,
                BDFaceSDKCommon.DetectType.DETECT_VIS,
                BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_ACCURATE,
                new Callback() {
                    @Override
                    public void onResponse(int code, String response) {
                        //  ToastUtils.toast(context, code + "  " + response);
                        if (code != 0 && listener != null) {
                            listener.initModelFail(code, response);
                        }
                    }
                });
        faceCrop.initFaceCrop(new Callback() {
            @Override
            public void onResponse(int code, String response) {
                if (code != 0 && listener != null) {
                    listener.initModelFail(code, response);
                }
            }
        });
        faceFeature.initModel(
                context,
                GlobalSet.RECOGNIZE_IDPHOTO_MODEL,
                GlobalSet.RECOGNIZE_VIS_MODEL,
                "",
                new Callback() {
                    @Override
                    public void onResponse(int code, String response) {
                        long endInitModelTime = System.currentTimeMillis();
                        //                        LogUtils.e(TIME_TAG, "init model time = " + (endInitModelTime - startInitModelTime));
                        if (code != 0) {
                            //                            ToastUtils.toast(context, "模型加载失败,尝试重启试试");
                            if (listener != null) {
                                listener.initModelFail(code, response);
                            }
                        } else {
                            initStatus = SDK_MODEL_LOAD_SUCCESS;
                            // 模型初始化成功，加载人脸数据
                            initDataBases(context);
                            //                            ToastUtils.toast(context, "模型加载完毕，欢迎使用");
                            if (listener != null) {
                                listener.initModelSuccess();
                            }
                        }
                    }
                });
    }

    /**
     * 单独调用 特征提取
     *
     * @param imageInstance       可见光底层送检对象
     * @param landmark            检测眼睛，嘴巴，鼻子，72个关键点
     * @param featureCheckMode    特征提取模式
     * @param faceFeatureCallBack 回掉方法
     */
    public void onFeatureCheck(
            final BDFaceImageInstance imageInstance,
            final float[] landmark,
            final BDFaceSDKCommon.FeatureType featureCheckMode,
            final FaceFeatureCallBack faceFeatureCallBack
    ){
        final long startFeatureTime = System.currentTimeMillis();
        if (mRegFuture != null && !mRegFuture.isDone()) {
            return;
        }

        mRegFuture = mRegExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                BDFaceImageInstance rgbInstance = new BDFaceImageInstance(
                        imageInstance.data,
                        imageInstance.height,
                        imageInstance.width,
                        imageInstance.imageType,
                        0,
                        0
                );

                byte[] feature = new byte[512];
                float featureSize = FaceRegisterManager.getInstance().getFaceFeature()
                        .feature(featureCheckMode, rgbInstance, landmark, feature);
                if (featureSize == GlobalSet.FEATURE_SIZE / 4) {
                    // 特征提取成功
                    if (faceFeatureCallBack != null) {
                        long endFeatureTime = System.currentTimeMillis() - startFeatureTime;
                        faceFeatureCallBack.onFaceFeatureCallBack(featureSize, feature, endFeatureTime);
                    }

                }
                // 流程结束销毁图片
                rgbInstance.destory();
            }
        });
    }

    /**
     * 初始化配置
     * */
    private void initConfig(){
        if (faceDetect != null){
            BDFaceSDKConfig config = new BDFaceSDKConfig();
            // 最大人脸个数检查，默认为1
            config.maxDetectNum = 1;
            // 最小可检测人脸的大小，默认为80px，最小30px
            config.minFaceSize = SingleBaseConfig.getBaseConfig().getMinimumFace();
            // 是否进行属性检测，默认关闭
            config.isAttribute = SingleBaseConfig.getBaseConfig().isAttribute();
            // 模糊，遮挡，光照三个质量检测和姿态角查默认关闭，如果要开启，设置页启动
            config.isCheckBlur = config.isOcclusion
                    = config.isIllumination = config.isHeadPose
                    = SingleBaseConfig.getBaseConfig().isQualityControl();

            faceDetect.loadConfig(config);
        }
    }

    /**
     * 初始化数据库
     * */
    public void initDataBases(Context context) {
        // 初始化数据库
        DBManager.getInstance().init(context);
        // 数据变化，更新内存
        FaceApi.getInstance().initDatabases(true);
    }

}
