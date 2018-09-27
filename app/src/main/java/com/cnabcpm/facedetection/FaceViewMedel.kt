package com.cnabcpm.facedetection

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.graphics.Bitmap

/**
 *  @创建者      Administrator
 *  @创建时间    2018/9/27 12:37
 *  @描述        ${TODO}
 *
 *  @更新者      $Author$
 *  @更新时间    $Date$
 *  @更新描述    ${TODO}
 */
class FaceViewMedel : ViewModel() {


    var groupBackText = ObservableField<String>()
    //第一張
    var oneFace = ObservableField<Bitmap>()
    var isHaveOneFace = ObservableField<Boolean>(false)
    //第二张
    var twoFace = ObservableField<Bitmap>()
    var isHaveTwoFace = ObservableField<Boolean>(false)
    //第三张
    var threeFace = ObservableField<Bitmap>()
    var isHaveThreeFace = ObservableField<Boolean>(false)

}