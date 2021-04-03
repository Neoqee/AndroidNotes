package com.tsingning.registerlibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.tsingning.registerlibrary.api.FaceApi;
import com.tsingning.registerlibrary.callback.CameraDataCallback;
import com.tsingning.registerlibrary.callback.FaceDetectCallBack;
import com.tsingning.registerlibrary.callback.FaceFeatureCallBack;
import com.tsingning.registerlibrary.camera.AutoTexturePreviewView;
import com.tsingning.registerlibrary.config.SingleBaseConfig;
import com.tsingning.registerlibrary.listener.ModelInitListener;
import com.tsingning.registerlibrary.manager.CameraPreviewManager;
import com.tsingning.registerlibrary.manager.FaceRegisterManager;
import com.tsingning.registerlibrary.manager.FaceTrackManager;
import com.tsingning.registerlibrary.model.LivenessModel;
import com.tsingning.registerlibrary.model.User;
import com.tsingning.registerlibrary.utils.BitmapUtils;
import com.tsingning.registerlibrary.utils.FaceOnDrawTexturViewUtil;
import com.tsingning.registerlibrary.utils.FileUtils;
import com.tsingning.registerlibrary.utils.ToastUtils;
import com.tsingning.registerlibrary.view.FaceRoundProView;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FaceRegisterActivity extends BaseActivity {

    private final static String TAG = "FaceRegisterActivity";

    // RGB摄像头图像宽和高
    private static final int PREFER_WIDTH = SingleBaseConfig.getBaseConfig().getRgbAndNirWidth();
    private static final int PERFER_HEIGHT = SingleBaseConfig.getBaseConfig().getRgbAndNirHeight();

    private Context mContext;

    // 标题栏控件
    private ImageView backBtn;
    private TextView titleTv;
    private TextView tipText;

    // 相机预览
    private AutoTexturePreviewView texturePv;
    private SurfaceView previewSv;
    private boolean mCollectSuccess = false;

    private FaceRoundProView faceRoundProView;

    // 注册人脸相关
    // 采集相关布局
    private RelativeLayout mRelativeCollectSuccess;
    private ImageView mCircleHead;
    private EditText mEditName;
    private TextView mTextError;
    private Button mBtnCollectConfirm;
    private ImageView mImageInputClear;

    // 注册成功相关布局
    private RelativeLayout mRelativeRegisterSuccess;
    private ImageView mCircleRegSucHead;

    // 包含适配屏幕后的人脸的x坐标，y坐标，和width
    private float[] mPointXY = new float[4];
    private Bitmap mCropBitmap;
    private byte[] mFeatures = new byte[512];
    private float previewWidth;
    private float circleX;
    private float circleY;
    private int circleRadius;
    private float leftLimitX;
    private float rightLimitX;
    private float topLimitY;
    private float bottomLimitY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListener();
        setContentView(R.layout.activity_face_register);
        mContext = this;
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭摄像头
        CameraPreviewManager.getInstance().stopPreview();
        if (mCropBitmap != null) {
            if (!mCropBitmap.isRecycled()) {
                mCropBitmap.recycle();
            }
            mCropBitmap = null;
        }
    }

    private void initListener(){
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
                ToastUtils.toast(FaceRegisterActivity.this, "模型加载成功，欢迎使用");
            }

            @Override
            public void initModelFail(int errorCode, String msg) {
                if (errorCode != -12) {
                    ToastUtils.toast(FaceRegisterActivity.this, "模型加载失败，请尝试重启应用");
                }
            }
        });
    }

    private void initView() {
        // 标题栏控件
        backBtn = findViewById(R.id.image_register_back);
        titleTv = findViewById(R.id.text_title);

        // 相机预览
        previewSv = findViewById(R.id.previewSv);
        texturePv = findViewById(R.id.texturePv);
        faceRoundProView = findViewById(R.id.round_view);
        texturePv.setIsRegister(true);
//        circleRadius = previewSv.getWidth() / 3;
//        previewWidth = ((previewSv.getWidth() / 3) * 2);
//        // 圆心的X坐标
//        circleX = (previewSv.getRight() - previewSv.getLeft()) / 2;
//        // 圆心的Y坐标
//        circleY = (previewSv.getBottom() - previewSv.getTop()) / 2;
//
//        leftLimitX = circleX - circleRadius;
//        rightLimitX = circleX + circleRadius;
//        topLimitY = circleY - circleRadius;
//        bottomLimitY = circleY + circleRadius;

        // 提示语
        tipText = findViewById(R.id.tipText);

        // 注册相关
        mRelativeCollectSuccess = findViewById(R.id.relative_collect_success);
        mCircleHead = findViewById(R.id.circle_head);
        mEditName = findViewById(R.id.edit_name);
        mTextError = findViewById(R.id.text_error);
        mBtnCollectConfirm = findViewById(R.id.btn_collect_confirm);
        mImageInputClear = findViewById(R.id.image_input_delete);

        // 注册成功相关
        mRelativeRegisterSuccess = findViewById(R.id.relative_register_success);
        mCircleRegSucHead = findViewById(R.id.circle_reg_suc_head);

        // 绑定点击事件
        bindEvent();
    }

    private void bindEvent(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mEditName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mImageInputClear.setVisibility(View.VISIBLE);
                    mBtnCollectConfirm.setEnabled(true);
                    mBtnCollectConfirm.setTextColor(Color.WHITE);
//                    mBtnCollectConfirm.setBackgroundResource(R.drawable.button_selector);
                    List<User> listUsers = FaceApi.getInstance().getUserListByUserName(s.toString());
                    if (listUsers != null && listUsers.size() > 0) {     // 出现用户名重复
                        mTextError.setVisibility(View.VISIBLE);
                        mBtnCollectConfirm.setEnabled(false);
                    } else {
                        mTextError.setVisibility(View.INVISIBLE);
                        mBtnCollectConfirm.setEnabled(true);
                    }
                } else {
                    mImageInputClear.setVisibility(View.GONE);
                    mBtnCollectConfirm.setEnabled(false);
                    mBtnCollectConfirm.setTextColor(Color.parseColor("#666666"));
                    mBtnCollectConfirm.setBackgroundResource(R.mipmap.btn_all_d);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBtnCollectConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = mEditName.getText().toString();
                // 姓名过滤
                String nameResult = FaceApi.getInstance().isValidName(userName);
                if (!"0".equals(nameResult)) {
                    ToastUtils.toast(getApplicationContext(), nameResult);
                    return;
                }
                String imageName = userName + ".jpg";
                // 注册到人脸库
                boolean isSuccess = FaceApi.getInstance().registerUserIntoDBmanager(null,
                        userName, imageName, null, mFeatures);
                if (isSuccess) {
                    // 保存人脸图片
                    File faceDir = FileUtils.getBatchImportSuccessDirectory();
                    File file = new File(faceDir, imageName);
                    FileUtils.saveBitmap(file, mCropBitmap);
                    // 数据变化，更新内存
                    FaceApi.getInstance().initDatabases(true);
                    // 更新UI
                    mRelativeCollectSuccess.setVisibility(View.GONE);
                    mRelativeRegisterSuccess.setVisibility(View.VISIBLE);
                    mCircleRegSucHead.setImageBitmap(mCropBitmap);
                } else {
                    ToastUtils.toast(getApplicationContext(), "保存数据库失败，" +
                            "可能是用户名格式不正确");
                }
            }
        });

        findViewById(R.id.btn_return_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭摄像头
                CameraPreviewManager.getInstance().stopPreview();
                finish();
            }
        });

        mImageInputClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditName.setText("");
                mTextError.setVisibility(View.INVISIBLE);
            }
        });

    }

    /**
     * 摄像头图像预览
     */
    private void startCameraPreview(){
        CameraPreviewManager.getInstance().startPreview(this, texturePv, PREFER_WIDTH, PERFER_HEIGHT,
                new CameraDataCallback() {
                    @Override
                    public void onGetCameraData(byte[] data, Camera camera, int width, int height) {
                        if (mCollectSuccess) return;
                        // 处理摄像头预览数据
                        faceDetect(data,width,height);
                    }
                });
    }

    /**
     * 摄像头数据处理
     */
    private void faceDetect(byte[] data, final int width, final int height){
        if (mCollectSuccess){
            return;
        }
        // 设置有无活体检测 0 = 无；1 = 有；
        FaceTrackManager.getInstance().setAliving(SingleBaseConfig.getBaseConfig().getType() == 1);
        // 摄像头预览数据进行人脸检测
        FaceTrackManager.getInstance().faceTrack(data, width, height, new FaceDetectCallBack() {
            @Override
            public void onFaceDetectCallback(LivenessModel livenessModel) {
                checkFaceBound(livenessModel);
            }

            @Override
            public void onTip(int code, String msg) {
                setTip(msg);
//                Log.i(TAG,msg);
            }

            @Override
            public void onFaceDetectDarwCallback(LivenessModel livenessModel) {

            }
        });
    }

    /**
     * 检查人脸边界
     *
     * @param livenessModel LivenessModel实体
     */
    private void checkFaceBound(final LivenessModel livenessModel){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCollectSuccess) {
                    return;
                }

                if (livenessModel == null || livenessModel.getFaceInfo() == null) {
                    setTip("暂时没有检测到人脸");
                    return;
                }

                faceRoundProView.setBitmapSource(R.mipmap.ic_loading_grey);
                mPointXY[0] = livenessModel.getFaceInfo().centerX;   // 人脸X坐标
                mPointXY[1] = livenessModel.getFaceInfo().centerY;   // 人脸Y坐标
                mPointXY[2] = livenessModel.getFaceInfo().width;     // 人脸宽度
                mPointXY[3] = livenessModel.getFaceInfo().height;    // 人脸高度
                FaceOnDrawTexturViewUtil.converttPointXY(mPointXY, texturePv,
                        livenessModel.getBdFaceImageInstance(), livenessModel.getFaceInfo().width);
                float leftLimitX = AutoTexturePreviewView.circleX - AutoTexturePreviewView.circleRadius;
                float rightLimitX = AutoTexturePreviewView.circleX + AutoTexturePreviewView.circleRadius;
                float topLimitY = AutoTexturePreviewView.circleY - AutoTexturePreviewView.circleRadius;
                float bottomLimitY = AutoTexturePreviewView.circleY + AutoTexturePreviewView.circleRadius;
                float previewWidth = AutoTexturePreviewView.circleRadius * 2;

                if (mPointXY[2] < 50 || mPointXY[3] < 50) {
                    faceRoundProView.setTipText("请向前靠近镜头");
                    faceRoundProView.setBitmapSource(R.mipmap.ic_loading_red);
                    // 释放内存
                    destroyImageInstance(livenessModel.getBdFaceImageInstanceCrop());
                    return;
                }

                if (mPointXY[2] > previewWidth || mPointXY[3] > previewWidth) {
                    faceRoundProView.setTipText("请向后远离镜头");
                    faceRoundProView.setBitmapSource(R.mipmap.ic_loading_red);
                    // 释放内存
                    destroyImageInstance(livenessModel.getBdFaceImageInstanceCrop());
                    return;
                }

                if (mPointXY[0] - mPointXY[2] / 2 < leftLimitX
                        || mPointXY[0] + mPointXY[2] / 2 > rightLimitX
                        || mPointXY[1] - mPointXY[3] / 2 < topLimitY
                        || mPointXY[1] + mPointXY[3] / 2 > bottomLimitY) {
                    faceRoundProView.setTipText("保持面部在取景框内");
                    faceRoundProView.setBitmapSource(R.mipmap.ic_loading_red);
                    // 释放内存
                    destroyImageInstance(livenessModel.getBdFaceImageInstanceCrop());
                    return;
                }

                faceRoundProView.setTipText("请保持面部在取景框内");
                faceRoundProView.setBitmapSource(R.mipmap.ic_loading_blue);

                setTip("准备检验活体分值");

                // 检验活体分值
                checkLiveScore(livenessModel);

            }
        });
    }

    /**
     * 检验活体分值
     *
     * @param livenessModel LivenessModel实体
     */
    private void checkLiveScore(LivenessModel livenessModel){
        if (livenessModel == null || livenessModel.getFaceInfo() == null) {
            setTip("checkLiveScore model/faceInfo == null");
            return;
        }
        if (SingleBaseConfig.getBaseConfig().getType() == 0){
            getFeatures(livenessModel);
        }
    }

    /**
     * 提取特征值
     *
     * @param model 人脸数据
     */
    private void getFeatures(final LivenessModel model){
        if (model == null) {
            setTip("getFeatures model == null");
            return;
        }
        // 获取选择的特征抽取模型
        int modelType = SingleBaseConfig.getBaseConfig().getActiveModel();
        if (modelType == 1) {
            // 生活照
            FaceRegisterManager.getInstance().onFeatureCheck(
                    model.getBdFaceImageInstance(),
                    model.getLandmarks(),
                    BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_LIVE_PHOTO,
                    new FaceFeatureCallBack() {
                        @Override
                        public void onFaceFeatureCallBack(final float featureSize, final byte[] feature, long time) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mCollectSuccess) {
                                        return;
                                    }
                                    displayCompareResult(featureSize, feature, model);
                                    Log.e(TAG, String.valueOf(feature.length));
                                }
                            });

                        }
                    });
        }
    }

    // 根据特征抽取的结果 注册人脸
    private void displayCompareResult(float ret, byte[] faceFeature, LivenessModel model){
        if (model == null) {
            setTip("displayCompareResult model == null");
            return;
        }
        // 特征提取成功
        if (ret == 128) {
            // 抠图
            BDFaceImageInstance imageInstance = model.getBdFaceImageInstanceCrop();
            AtomicInteger isOutoBoundary = new AtomicInteger();
            BDFaceImageInstance cropInstance = FaceRegisterManager.getInstance().getFaceCrop()
                    .cropFaceByLandmark(imageInstance, model.getLandmarks(),
                            2.0f, false, isOutoBoundary);
            if (cropInstance == null) {
                setTip("抠图失败");
                // 释放内存
                destroyImageInstance(model.getBdFaceImageInstanceCrop());
                return;
            }
            mCropBitmap = BitmapUtils.getInstaceBmp(cropInstance);
            // 获取头像
            if (mCropBitmap != null) {
                mCollectSuccess = true;
                mCircleHead.setImageBitmap(mCropBitmap);
                setTip("识别成功，可以进行下一步注册");
            }
            cropInstance.destory();
            // 释放内存
            destroyImageInstance(model.getBdFaceImageInstanceCrop());

            mRelativeCollectSuccess.setVisibility(View.VISIBLE);
            previewSv.setVisibility(View.GONE);
            texturePv.setVisibility(View.GONE);
            faceRoundProView.setVisibility(View.GONE);

            for (int i = 0; i < faceFeature.length; i++) {
                mFeatures[i] = faceFeature[i];
            }
        }else {
            setTip("特征提取失败");
        }
    }


    /**
     * 释放图像
     *
     * @param imageInstance
     */
    private void destroyImageInstance(BDFaceImageInstance imageInstance) {
        if (imageInstance != null) {
            imageInstance.destory();
        }
    }

    /**
     * 设置提示消息
     * */
    private void setTip(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tipText.setText(msg);
                Log.i(TAG,msg);
            }
        });
    }

}
