package com.leiyun.fingerprint

import android.app.Service
import android.content.DialogInterface
import android.os.Bundle
import android.os.Vibrator
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/**
 * 类名：FingerprintDialog
 * 作者：Yun.Lei
 * 功能：
 * 创建日期：2017-11-13 15:05
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
class FingerprintDialog : BottomSheetDialogFragment() {
    private lateinit var mVibrator: Vibrator
    private lateinit var fingerImageView: ImageView
    private lateinit var hintTv: TextView
    private lateinit var btnCancel: TextView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mVibrator = context!!.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        return inflater.inflate(R.layout.dialog_fingerprint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fingerImageView = view.findViewById(R.id.fingerprint_image)
        hintTv = view.findViewById(R.id.hint_tv)
        btnCancel = view.findViewById(R.id.btn_cancel)
        btnCancel.setOnClickListener { dismiss() }
        FingerprintHelper.authenticate(object : FingerprintManagerCompat.AuthenticationCallback() {

            //在识别指纹成功时调用。
            override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
                startVibrate()
                hintTv.text = "指纹识别成功"
                fingerImageView.setImageResource(R.mipmap.ic_finger_success)
                hintTv.setTextColor(context!!.resources.getColor(R.color.colorPrimary))
                hintTv.postDelayed({ dismiss() }, 1000)
            }

            //当指纹有效但未被识别时调用。
            override fun onAuthenticationFailed() {
                hintTv.text = "识别失败，请重试"
                startVibrate()
            }

            //当遇到不可恢复的错误并且操作完成时调用。
            override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
                hintTv.text = errString
                if (errMsgId != 5) {   //取消不震动
                    startVibrate()
                }
            }

            //在认证期间遇到可恢复的错误时调用。
            override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) {
                hintTv.text = helpString
                startVibrate()
            }
        })
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        FingerprintHelper.cancel()
    }

    fun startVibrate() {
        mVibrator.vibrate(500)
    }
}