package com.tsingning.registerlibrary.manager;

import android.util.Log;

import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceOcclusion;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.tsingning.registerlibrary.callback.FaceDetectCallBack;
import com.tsingning.registerlibrary.config.SingleBaseConfig;
import com.tsingning.registerlibrary.model.LivenessModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * rgb单目检测管理类
 */
public class FaceTrackManager {

    private static final String TAG = FaceTrackManager.class.getSimpleName();

    private static volatile FaceTrackManager instance = null;

    private boolean isAliving;
    private Future future;
    private volatile int mProcessCount = 0;
    private ExecutorService es;

    public FaceTrackManager() {
        es = Executors.newSingleThreadExecutor();
    }

    public static FaceTrackManager getInstance() {
        synchronized (FaceTrackManager.class) {
            if (instance == null) {
                instance = new FaceTrackManager();
            }
        }
        return instance;
    }

    public void faceTrack(final byte[] argb, final int width, final int height, final FaceDetectCallBack faceDetectCallBack){
        if (future != null && !future.isDone()) {
            return;
        }
        if (mProcessCount > 0) {
            return;
        }
        mProcessCount++;
        future = es.submit(new Runnable() {
            @Override
            public void run() {
                faceDataDetect(argb, width, height, faceDetectCallBack);
                mProcessCount--;
            }
        });
    }

    /**
     * 人脸检测
     *
     * @param argb
     * @param width
     * @param height
     * @param faceDetectCallBack
     */
    private void faceDataDetect(final byte[] argb, int width, int height, FaceDetectCallBack faceDetectCallBack){
        LivenessModel livenessModel = new LivenessModel();

        BDFaceImageInstance rgbInstance;
        rgbInstance = new BDFaceImageInstance(argb, height, width,
                BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21,
                SingleBaseConfig.getBaseConfig().getDetectDirection(),
                SingleBaseConfig.getBaseConfig().getMirrorRGB());

        // getImage() 获取送检图片,如果检测数据有问题，可以通过image view 展示送检图片
        livenessModel.setBdFaceImageInstanceCrop(rgbInstance);

        // 检查函数调用，返回检测结果
        long startTime = System.currentTimeMillis();

        // 快速检测获取人脸信息，仅用于绘制人脸框，详细人脸数据后续获取
        FaceInfo[] faceInfos = FaceRegisterManager.getInstance().getFaceDetect()
                .track(BDFaceSDKCommon.DetectType.DETECT_VIS, rgbInstance);

        livenessModel.setRgbDetectDuration(System.currentTimeMillis() - startTime);

        // getImage() 获取送检图片
        livenessModel.setBdFaceImageInstance(rgbInstance.getImage());

        if (faceInfos != null && faceInfos.length > 0) {
            livenessModel.setTrackFaceInfo(faceInfos);
            FaceInfo faceInfo = faceInfos[0];
            livenessModel.setFaceInfo(faceInfo);
            livenessModel.setLandmarks(faceInfo.landmarks);
            Log.e(TAG, "ldmk = " + faceInfo.landmarks.length);

            // 质量检测，针对模糊度、遮挡、角度   暂时没有开启质量检测
            if (onQualityCheck(livenessModel, faceDetectCallBack)) {
                // todo 活体检测    还未开启活体检测

                // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
                // rgbInstance.destory();
                if (faceDetectCallBack != null) {
                    faceDetectCallBack.onFaceDetectCallback(livenessModel);
                }
            }else {
                // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
                rgbInstance.destory();
            }
        }else {
            // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
            rgbInstance.destory();
            if (faceDetectCallBack != null) {
                faceDetectCallBack.onTip(0, "未检测到人脸");
                faceDetectCallBack.onFaceDetectCallback(null);
            }
        }
    }

    /**
     * 质量检测
     *
     * @param livenessModel
     * @param faceDetectCallBack
     * @return
     */
    public boolean onQualityCheck(final LivenessModel livenessModel, final FaceDetectCallBack faceDetectCallBack){
        if (!SingleBaseConfig.getBaseConfig().isQualityControl()){
            return true;
        }

        if (livenessModel != null && livenessModel.getFaceInfo() != null) {

            // 角度过滤
            if (Math.abs(livenessModel.getFaceInfo().yaw) > SingleBaseConfig.getBaseConfig().getYaw()) {
                faceDetectCallBack.onTip(-1, "人脸左右偏转角超出限制");
                return false;
            } else if (Math.abs(livenessModel.getFaceInfo().roll) > SingleBaseConfig.getBaseConfig().getRoll()) {
                faceDetectCallBack.onTip(-1, "人脸平行平面内的头部旋转角超出限制");
                return false;
            } else if (Math.abs(livenessModel.getFaceInfo().pitch) > SingleBaseConfig.getBaseConfig().getPitch()) {
                faceDetectCallBack.onTip(-1, "人脸上下偏转角超出限制");
                return false;
            }


            // 模糊结果过滤
            float blur = livenessModel.getFaceInfo().bluriness;
            if (blur > SingleBaseConfig.getBaseConfig().getBlur()) {
                faceDetectCallBack.onTip(-1, "图片模糊");
                return false;
            }

            // 光照结果过滤
            float illum = livenessModel.getFaceInfo().illum;
            if (illum < SingleBaseConfig.getBaseConfig().getIllumination()) {
                faceDetectCallBack.onTip(-1, "图片光照不通过");
                return false;
            }

            // 遮挡结果过滤
            if (livenessModel.getFaceInfo().occlusion != null) {
                BDFaceOcclusion occlusion = livenessModel.getFaceInfo().occlusion;

                if (occlusion.leftEye > SingleBaseConfig.getBaseConfig().getLeftEye()) {
                    // 左眼遮挡置信度
                    faceDetectCallBack.onTip(-1, "左眼遮挡");
                } else if (occlusion.rightEye > SingleBaseConfig.getBaseConfig().getRightEye()) {
                    // 右眼遮挡置信度
                    faceDetectCallBack.onTip(-1, "右眼遮挡");
                } else if (occlusion.nose > SingleBaseConfig.getBaseConfig().getNose()) {
                    // 鼻子遮挡置信度
                    faceDetectCallBack.onTip(-1, "鼻子遮挡");
                } else if (occlusion.mouth > SingleBaseConfig.getBaseConfig().getMouth()) {
                    // 嘴巴遮挡置信度
                    faceDetectCallBack.onTip(-1, "嘴巴遮挡");
                } else if (occlusion.leftCheek > SingleBaseConfig.getBaseConfig().getLeftCheek()) {
                    // 左脸遮挡置信度
                    faceDetectCallBack.onTip(-1, "左脸遮挡");
                } else if (occlusion.rightCheek > SingleBaseConfig.getBaseConfig().getRightCheek()) {
                    // 右脸遮挡置信度
                    faceDetectCallBack.onTip(-1, "右脸遮挡");
                } else if (occlusion.chin > SingleBaseConfig.getBaseConfig().getChinContour()) {
                    // 下巴遮挡置信度
                    faceDetectCallBack.onTip(-1, "下巴遮挡");
                } else {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean isAliving() {
        return isAliving;
    }

    public void setAliving(boolean aliving) {
        isAliving = aliving;
    }
}