/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.cnabcpm.facedetection;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.aip.ImageFrame;
import com.baidu.aip.face.CameraImageSource;
import com.baidu.aip.face.DetectRegionProcessor;
import com.baidu.aip.face.FaceCropper;
import com.baidu.aip.face.FaceDetectManager;
import com.baidu.aip.face.LivenessDetector;
import com.baidu.aip.face.PreviewView;
import com.baidu.aip.face.camera.CameraView;
import com.baidu.aip.face.camera.ICameraControl;
import com.baidu.aip.face.camera.PermissionCallback;
import com.baidu.idl.facesdk.FaceInfo;
import com.cnabcpm.recorder.R;


import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 自动检测获取人脸
 */
public class RegDetectActivity extends AppCompatActivity {

    private PreviewView previewView;
    private View        rectView;
    private ImageView   displayAvatar;
    private TextView    hintTextView;
    private int recLen = 3;
    private int lastTipResId;
    private boolean success = false;

    private FaceDetectManager faceDetectManager;

    private DetectRegionProcessor cropProcessor = new DetectRegionProcessor();
    private String   mPictureSelecter;
    private Button   mBt_cancel;
    private TextView mTv_time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        faceDetectManager = new FaceDetectManager(getApplicationContext());
        setContentView(R.layout.activity_reg_detected);
        previewView = (PreviewView) findViewById(R.id.texture_view);
        displayAvatar = (ImageView) findViewById(R.id.display_avatar);
        mBt_cancel = (Button) findViewById(R.id.bt_cancel);
        rectView = findViewById(R.id.rect_view);
        mTv_time = (TextView) findViewById(R.id.tv_time);
        rectView.setKeepScreenOn(true);
        hintTextView = (TextView) findViewById(R.id.hint_text_view);
        handler.postDelayed(myRunnable, 1000);
        init();
        //   timer.schedule(task, 1000, 1000);
        hintTextView.setText(R.string.hint_move_into_frame);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPictureSelecter = bundle.getString("pictureSelecter");
        }
        mBt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private Handler handler = new Handler();

    private void init() {

        final CameraImageSource cameraImageSource = new CameraImageSource(this);
        cameraImageSource.getCameraControl().setPreferredPreviewSize(1366, 768);
        // 从相机获取图片
        faceDetectManager.setImageSource(cameraImageSource);
        // 设置预览View
        cameraImageSource.setPreviewView(previewView);
        // 添加PreProcessor对图片进行裁剪。
        faceDetectManager.addPreProcessor(cropProcessor);

        rectView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rectView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // 打开相机，映射View到相机之间的坐标
                start();//或者倒计时结束才start
            }
        });


        // 安卓6.0+的系统相机权限是动态获取的，当没有该权限时会回调。
        cameraImageSource.getCameraControl().setPermissionCallback(new PermissionCallback() {
            @Override
            public boolean onRequestPermission() {
                ActivityCompat
                        .requestPermissions(RegDetectActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                return true;
            }
        });

        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        if (isPortrait) {
            previewView.setScaleType(PreviewView.ScaleType.FIT_WIDTH);
            cameraImageSource.getCameraControl().setDisplayOrientation(CameraView.ORIENTATION_PORTRAIT);
        } else {
            previewView.setScaleType(PreviewView.ScaleType.FIT_HEIGHT);
            cameraImageSource.getCameraControl().setDisplayOrientation(CameraView.ORIENTATION_HORIZONTAL);
        }

        setCameraType(cameraImageSource);
    }

    private void setCameraType(CameraImageSource cameraImageSource) {
        // TODO 选择使用前置摄像头
//        cameraImageSource.getCameraControl().setCameraFacing(ICameraControl.CAMERA_FACING_FRONT);

        // TODO 选择使用后置摄像头
//         cameraImageSource.getCameraControl().setCameraFacing(ICameraControl.CAMERA_FACING_BACK);
//         previewView.setMirrored(false);

        // TODO 选择使用usb摄像头 如果不设置，人脸框会镜像，显示不准
        cameraImageSource.getCameraControl().setCameraFacing(ICameraControl.CAMERA_USB);
        previewView.setMirrored(false);

    }

    @Override
    protected void onStop() {
        super.onStop();
        faceDetectManager.stop();
    }

    private void start() {
        Rect detectedRect = new Rect();
        rectView.getGlobalVisibleRect(detectedRect);
        RectF newDetectedRect = new RectF(detectedRect);
        cropProcessor.setDetectedRect(newDetectedRect);
        faceDetectManager.start();
    }

    private long lastTipTime;

    private Rect  rect  = new Rect();
    private RectF rectF = new RectF(rect);

    private void handleFace(int retCode, FaceInfo[] faces, int[] argb, int width, int height) {
        if (success) {
            return;
        }
        if (faces == null || faces.length == 0) {
            // 获取状态码
            int hint = LivenessDetector.getInstance().getHintCode(retCode, null, 0, 0);
            // 根据状态码获取相应的资源ID
            final int resId = LivenessDetector.getInstance().getTip(hint);
            displayTip(resId);
            lastTipResId = resId;
            return;
        }

        rectView.getGlobalVisibleRect(rect);
        // 屏幕显示坐标对应到，实际图片坐标。
        rectF.set(rect);
        previewView.mapToOriginalRect(rectF);
        LivenessDetector.getInstance().setDetectRect(rectF);

        // 获取状态码
        final int hint = LivenessDetector.getInstance().getHintCode(retCode, faces[0], width, height);
        // 根据状态码获取相应的资源ID
        final int resId = LivenessDetector.getInstance().getTip(hint);

        final Bitmap bitmap = FaceCropper.getFace(argb, faces[0], width);

        handler.post(new Runnable() {
            @Override
            public void run() {
                displayAvatar.setImageBitmap(bitmap);
                // 在主线程，显示。
                displayTip(resId);
                lastTipResId = resId;
            }
        });

        if (hint != LivenessDetector.HINT_OK) {
            return;
        }

        try {

            if (mPictureSelecter.equals("one")) {
                final File file = File.createTempFile(UUID.randomUUID().toString() + "", "one.jpg");
                Intent intent = new Intent();
                ImageUtil.resize(bitmap, file, 300, 300);
                //Log.e("--------", "---face1---" + file.getAbsolutePath());
                intent.putExtra("file_one_path", file.getAbsolutePath());
                setResult(Activity.RESULT_OK, intent);
            } else if (mPictureSelecter.equals("two")) {
                final File file = File.createTempFile(UUID.randomUUID().toString() + "", "two.jpg");
                Intent intent = new Intent();
                ImageUtil.resize(bitmap, file, 300, 300);
                intent.putExtra("file_two_path", file.getAbsolutePath());
                setResult(Activity.RESULT_OK, intent);
            } else if (mPictureSelecter.equals("three")) {
                final File file = File.createTempFile(UUID.randomUUID().toString() + "", "three.jpg");
                Intent intent = new Intent();
                ImageUtil.resize(bitmap, file, 300, 300);
                intent.putExtra("file_three_path", file.getAbsolutePath());
                setResult(Activity.RESULT_OK, intent);
            }


            success = true;
            finish();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DisplayTipRunnable displayTipRunnable;

    private void displayTip(final int resId) {
        if (lastTipTime != 0 && (System.currentTimeMillis() - lastTipTime < 1500) && lastTipResId == resId) {
            lastTipTime = System.currentTimeMillis();
            return;
        }

        if (resId != 0) {
            handler.removeCallbacks(displayTipRunnable);
            displayTipRunnable = new DisplayTipRunnable(resId);
            handler.postDelayed(displayTipRunnable, 500);
        }
        lastTipTime = System.currentTimeMillis();
    }

    private class DisplayTipRunnable implements Runnable {

        private int resId;

        DisplayTipRunnable(int resId) {
            this.resId = resId;
        }

        @Override
        public void run() {
            hintTextView.setText(resId);
        }
    }

    private Runnable myRunnable = new Runnable() {
        public void run() {

            handler.postDelayed(this, 1000);
            recLen--;
            mTv_time.setText("" + recLen);
            if (recLen < 1) {
                mTv_time.setText("");
                faceDetectManager.setOnFaceDetectListener(new FaceDetectManager.OnFaceDetectListener() {
                    @Override
                    public void onDetectFace(int status, FaceInfo[] infos, ImageFrame frame) {
                        handleFace(status, infos, frame.getArgb(), frame.getWidth(), frame.getHeight());
                    }
                });

            }
        }
    };

}
