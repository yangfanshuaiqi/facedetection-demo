package com.cnabcpm.facedetection

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.view.View
import com.cnabcpm.recorder.R
import com.cnabcpm.recorder.R.id.*
import com.cnabcpm.recorder.databinding.ActivityMainBinding


import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_ONE_DETECT = 100
    private val REQUEST_CODE_TWO_DETECT = 101
    private val REQUEST_CODE_THREE_DETECT = 102
    private val KEY_PICTURE_SELECT = "pictureSelecter"

    protected lateinit var mBinding: ActivityMainBinding
    val viewModel by lazy {
        getViewModel(FaceViewMedel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.viewModel = viewModel
        initView()
    }

    fun initView() {
        iv_one_select.setOnClickListener {
            val intent = Intent(this, RegDetectActivity::class.java)
            val bundle = Bundle()
            bundle.putString(KEY_PICTURE_SELECT, "one")
            intent.putExtras(bundle)
            startActivityForResult(intent, REQUEST_CODE_ONE_DETECT)
        }
        iv_two_select.setOnClickListener {
            val intent = Intent(this, RegDetectActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(KEY_PICTURE_SELECT, "two")
            intent.putExtras(bundle)
            startActivityForResult(intent, REQUEST_CODE_TWO_DETECT)
        }
        iv_three_select.setOnClickListener {
            val intent = Intent(this, RegDetectActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(KEY_PICTURE_SELECT, "three")
            intent.putExtras(bundle)
            startActivityForResult(intent, REQUEST_CODE_THREE_DETECT)
        }

        iv_one_close.setOnClickListener {
            viewModel.isHaveOneFace.set(false)
            iv_one_face.setImageDrawable(null)
        }
        iv_two_close.setOnClickListener {
            viewModel.isHaveTwoFace.set(false)
            iv_two_face.setImageDrawable(null)
        }
        iv_three_close.setOnClickListener {
            viewModel.isHaveThreeFace.set(false)
            iv_three_face.setImageDrawable(null)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ONE_DETECT && data != null) {
            val faceImagePath = data.getStringExtra("file_one_path")
            viewModel.oneFace.set(BitmapFactory.decodeFile(faceImagePath))
            viewModel.isHaveOneFace.set(true)

        } else if (requestCode == REQUEST_CODE_TWO_DETECT && data != null) {
            val faceImagePath = data.getStringExtra("file_two_path")
            viewModel.twoFace.set(BitmapFactory.decodeFile(faceImagePath))
            viewModel.isHaveTwoFace.set(true)


        } else if (requestCode == REQUEST_CODE_THREE_DETECT && data != null) {
            val faceImagePath = data.getStringExtra("file_three_path")
            viewModel.threeFace.set(BitmapFactory.decodeFile(faceImagePath))
            viewModel.isHaveThreeFace.set(true)


        }

    }
}
