package com.cnabcpm.facedetection

import android.accounts.AccountManager
import android.app.Application
import android.content.Context
import com.baidu.aip.FaceEnvironment
import com.baidu.aip.FaceSDKManager
import com.cnabcpm.recorder.BuildConfig

/**
 *  @创建者      Administrator
 *  @创建时间    2018/9/27 11:30
 *  @描述        ${TODO}
 *
 *  @更新者      $Author$
 *  @更新时间    $Date$
 *  @更新描述    ${TODO}
 */
class App : Application() {


    override fun onCreate() {

        super.onCreate()
        ResourceSettings.init()
        initLib()


    }


    /**
     * 初始化SDK
     */
    private fun initLib() {
        // 为了android和ios 区分授权，appId=appname_face_android ,其中appname为申请sdk时的应用名
        // 应用上下文
        // 申请License取得的APPID
        // assets目录下License文件名
        FaceSDKManager.getInstance().init(this, Config.licenseID, Config.licenseFileName)
        setFaceConfig()
    }

    private fun setFaceConfig() {
        val tracker = FaceSDKManager.getInstance().getFaceTracker(this)
        // SDK初始化已经设置完默认参数（推荐参数），您也根据实际需求进行数值调整

        // 模糊度范围 (0-1) 推荐小于0.7
        tracker.set_blur_thr(FaceEnvironment.VALUE_BLURNESS)
        // 光照范围 (0-1) 推荐大于40
        tracker.set_illum_thr(FaceEnvironment.VALUE_BRIGHTNESS)
        // 裁剪人脸大小
        tracker.set_cropFaceSize(FaceEnvironment.VALUE_CROP_FACE_SIZE)
        // 人脸yaw,pitch,row 角度，范围（-45，45），推荐-15-15
        tracker.set_eulur_angle_thr(FaceEnvironment.VALUE_HEAD_PITCH, FaceEnvironment.VALUE_HEAD_ROLL,
                FaceEnvironment.VALUE_HEAD_YAW)

        // 最小检测人脸（在图片人脸能够被检测到最小值）80-200， 越小越耗性能，推荐120-200
        tracker.set_min_face_size(FaceEnvironment.VALUE_MIN_FACE_SIZE)
        //
        tracker.set_notFace_thr(FaceEnvironment.VALUE_NOT_FACE_THRESHOLD)
        // 人脸遮挡范围 （0-1） 推荐小于0.5
        tracker.set_occlu_thr(FaceEnvironment.VALUE_OCCLUSION)
        // 是否进行质量检测
        tracker.set_isCheckQuality(true)
        // 是否进行活体校验
        tracker.set_isVerifyLive(false)
        // AccountManager.token = ""
    }



}