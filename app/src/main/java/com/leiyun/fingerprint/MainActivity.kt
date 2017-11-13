package com.leiyun.fingerprint

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {


    private lateinit var mSDKVersionTv: TextView
    private lateinit var mHasPermissionTv: TextView
    private lateinit var mCheckBtn: Button
    private lateinit var mStartBtn: Button
    private var hasPermission = true
    private lateinit var mDialog: FingerprintDialog

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mSDKVersionTv = findViewById(R.id.tv_sdk_version)
        mHasPermissionTv = findViewById(R.id.tv_fingerprint_permission)
        mCheckBtn = findViewById(R.id.btn_requires_permission)
        mStartBtn = findViewById(R.id.btn_fingerprint)

        mSDKVersionTv.text = "系统版本：${Build.VERSION.SDK_INT}"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED
            mHasPermissionTv.text = "是否有指纹权限：$hasPermission"
        } else {
            hasPermission = true
            mHasPermissionTv.text = "是否有指纹权限：M(23-6.0)以下不需要运行时权限"
        }

        mCheckBtn.setOnClickListener {
            if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.USE_FINGERPRINT), 0x99)
            } else {
                Toast.makeText(applicationContext, "已经获取指纹权限", Toast.LENGTH_SHORT).show()
            }
        }
        mDialog = FingerprintDialog()
        FingerprintHelper.init(this)
        mStartBtn.setOnClickListener {
            if (FingerprintHelper.isHardwareDetected()) {
                if (FingerprintHelper.hasEnrolledFingerprints()) {
                    mDialog.show(supportFragmentManager, "dialog")
                } else {
                    Toast.makeText(applicationContext, "您未录制任何指纹", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "您的手机不支持指纹识别", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0x99) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "已经获取指纹权限", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "已拒绝取指纹权限", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
