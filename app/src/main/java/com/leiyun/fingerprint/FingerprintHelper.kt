package com.leiyun.fingerprint

import android.content.Context
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.support.v4.os.CancellationSignal
import java.lang.ref.WeakReference

/**
 * 类名：FingerprintHelper
 * 作者：Yun.Lei
 * 功能：
 * 创建日期：2017-11-13 10:47
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
object FingerprintHelper {

    private lateinit var fingerprintManager: FingerprintManagerCompat
    private var mCancellationSignal: CancellationSignal? = null
    fun init(ctx: Context) {
        fingerprintManager = FingerprintManagerCompat.from(ctx)
    }

    /**
     * 确定是否至少有一个指纹登记过
     *
     * @return 如果至少有一个指纹登记，则为true，否则为false
     */
    fun hasEnrolledFingerprints(): Boolean {
        return fingerprintManager.hasEnrolledFingerprints()
    }

    /**
     * 确定指纹硬件是否存在并且功能正常。
     *
     * @return 如果硬件存在且功能正确，则为true，否则为false。
     */
    fun isHardwareDetected(): Boolean {
        return fingerprintManager.isHardwareDetected
    }

    /**
     * 开始进行指纹识别
     * @param callback 指纹识别回调函数
     */
    fun authenticate(callback: FingerprintManagerCompat.AuthenticationCallback) {
        mCancellationSignal = CancellationSignal()
        fingerprintManager.authenticate(null, 0, mCancellationSignal, callback, null)
    }

    /**
     * 提供取消操作能力
     */
    fun cancel(){
        mCancellationSignal?.cancel()
    }
}