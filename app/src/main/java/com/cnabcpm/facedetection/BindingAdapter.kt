package com.cnabcpm.facedetection

import android.databinding.BindingAdapter
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.ImageView

/**
 *  @创建者      Administrator
 *  @创建时间    2018/9/27 11:19
 *  @描述        ${TODO}
 *
 *  @更新者      $Author$
 *  @更新时间    $Date$
 *  @更新描述    ${TODO}
 */
@BindingAdapter("setBitmaps")
fun showInBitmap(imageView: ImageView, bitmap: Bitmap?) {
    var isEmpty = if (bitmap == null) {
        "null"
    } else {
        "not null"
    }
    Log.d("BitmapBinding", isEmpty)
    imageView.setImageBitmap(bitmap)
}

@BindingAdapter("visibleGone")
fun showHide(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.GONE
}