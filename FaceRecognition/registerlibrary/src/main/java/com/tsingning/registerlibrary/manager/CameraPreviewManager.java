package com.tsingning.registerlibrary.manager;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import com.tsingning.registerlibrary.callback.CameraDataCallback;
import com.tsingning.registerlibrary.camera.AutoTexturePreviewView;
import com.tsingning.registerlibrary.config.SingleBaseConfig;

import java.io.IOException;
import java.util.List;

public class CameraPreviewManager implements SurfaceHolder.Callback, TextureView.SurfaceTextureListener {

    private static volatile CameraPreviewManager instance = null;

    private static boolean isTexture = true;

    private Camera mCamera;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    /** 当前相机的ID。*/
    private int cameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private int previewWidth;
    private int previewHeight;

    private int videoWidth;
    private int videoHeight;

    AutoTexturePreviewView mTextureView;
    private SurfaceTexture mSurfaceTexture;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private boolean mSurfaceCreated = false;

    private CameraDataCallback mCameraDataCallback;

    public static CameraPreviewManager getInstance() {
        synchronized (CameraPreviewManager.class) {
            if (instance == null) {
                instance = new CameraPreviewManager();
            }
        }
        return instance;
    }

    public void startPreview(Context context, SurfaceView surfaceView, int width,
                             int height, CameraDataCallback cameraDataCallback){
        this.mCameraDataCallback = cameraDataCallback;
        mSurfaceView = surfaceView;
        this.previewWidth = width;
        this.previewHeight = height;
//        mSurfaceTexture = mSurfaceView.
        mSurfaceView.getHolder().addCallback(this);
        mSurfaceHolder = mSurfaceView.getHolder();
    }

    public void startPreview(Context context, AutoTexturePreviewView textureView, int width,
                             int height, CameraDataCallback cameraDataCallback) {
        Context mContext = context;
        this.mCameraDataCallback = cameraDataCallback;
        mTextureView = textureView;
        this.previewWidth = width;
        this.previewHeight = height;
        mSurfaceTexture = mTextureView.getTextureView().getSurfaceTexture();
        mTextureView.getTextureView().setSurfaceTextureListener(this);
    }

    public void stopPreview(){
        if (mCamera != null){
            try {
                mCamera.setPreviewTexture(null);
                mSurfaceCreated = false;
                mTextureView = null;
                mSurfaceView = null;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openCamera(){
        try {
            if (mCamera == null){
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                for (int i = 0; i < Camera.getNumberOfCameras(); i++){
                    Camera.getCameraInfo(i,cameraInfo);
                    if (cameraInfo.facing == cameraFacing){
                        mCameraId = i;
                    }
                }
                mCamera = Camera.open(mCameraId);
                // todo 打开相机
            }
            // 摄像头图像预览角度
            int cameraRotation = SingleBaseConfig.getBaseConfig().getVideoDirection();
            mCamera.setDisplayOrientation(cameraRotation);

//            if (cameraRotation == 90 || cameraRotation == 270) {
//                mTextureView.setPreviewSize(previewHeight, previewWidth);
//            }else {
//                mTextureView.setPreviewSize(previewWidth, previewHeight);
//            }

            Camera.Parameters params = mCamera.getParameters();
            List<Camera.Size> sizeList = params.getSupportedPreviewSizes();
            Camera.Size optionSize = getOptimalPreviewSize(sizeList, previewWidth, previewHeight);
            if (optionSize.width == previewWidth && optionSize.height == previewHeight){
                videoWidth = previewWidth;
                videoHeight = previewHeight;
            }else {
                videoWidth = optionSize.width;
                videoHeight = optionSize.height;
            }
            params.setPreviewSize(videoWidth,videoHeight);
//            params.setPreviewFormat(ImageFormat.NV21);
            mCamera.setParameters(params);

            try {
                if (isTexture){
                    mCamera.setPreviewTexture(mSurfaceTexture);
                }else {
                    mCamera.setPreviewDisplay(mSurfaceHolder);
                }
                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        if (mCameraDataCallback != null){
                            mCameraDataCallback.onGetCameraData(data,camera,videoWidth,videoHeight);
                        }
                    }
                });
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }catch (RuntimeException e){
            e.printStackTrace();
        }
    }

    private void closeCamera(){
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 解决预览变形问题
     *
     * @param sizes
     * @param w
     * @param h
     * @return
     */
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double aspectTolerance = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) {
            return null;
        }
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > aspectTolerance) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceCreated = true;
        openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceCreated = false;
        closeCamera();
    }

    // TextureView.SurfaceTextureListener
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurfaceTexture = surface;
        mSurfaceCreated = true;
        openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mSurfaceCreated = false;
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
