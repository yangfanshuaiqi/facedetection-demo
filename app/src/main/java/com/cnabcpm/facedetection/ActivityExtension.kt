package com.cnabcpm.facedetection

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity

/**
 *  @创建者      Administrator
 *  @创建时间    2018/9/27 12:44
 *  @描述        ${TODO}
 *
 *  @更新者      $Author$
 *  @更新时间    $Date$
 *  @更新描述    ${TODO}
 */
fun <T : ViewModel> AppCompatActivity.getViewModel(c: Class<T>): T {
    return ViewModelProviders.of(this).get(c)
}