# 指纹识别

### 申请权限
使用指纹识别需要使用到**android.permission.USE_FINGERPRINT**,这是一个PROTECTION_NORMAL类权限，所以只需要在Manifest.xml中注册即可
```xml
<uses-permission android:name="android.permission.USE_FINGERPRINT"/>
```
当然使用时最好去检查一下是否具有指纹权限，避免被手动关闭
```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED
    mHasPermissionTv.text = "是否有指纹权限：$hasPermission"
} else {
    hasPermission = true
    mHasPermissionTv.text = "是否有指纹权限：M(23-6.0)以下不需要运行时权限"
}
```

### 指纹管理类
在SDK中提供了两种方式来使用指纹识别，**FingerprintManager**和V4包中的**FingerprintManagerCompat**

官方推荐是用**FingerprintManagerCompat**，因为FingerprintManager只能在SDK>23时使用，
而FingerprintManagerCompat做了兼容处理,不用做版本判断（实际上该类中判断了版本，23以下依然不能用）

附加说明：指纹这种东西再任何APP中应该都是非必要的东西，只是说可以让使用者更方便，所以尽量使用官方的Api去完成该功能
国内的某些Android版本在低于6.0的系统中加入了指纹就不要去适配了，这种指纹识别就留给他们官方的应用吧，免得有更大的坑。非要适配可以研究他们自己提供的Api进行适配

这里我创建了一个帮助类
```kotlin
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
```
### 开始指纹识别
指纹识别调用authenticate即可开始识别，此时应该给出用户提示，此Demo中使用的是Dialog
```kotlin
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
        if (errMsgId != 5) {   //取消识别时不震动提示
            startVibrate()
        }
    }

    //在认证期间遇到可恢复的错误时调用。
    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) {
        hintTv.text = helpString
        startVibrate()
    }
})
```
不需要继续指纹认证时请取消
```kotlin
override fun onDismiss(dialog: DialogInterface?) {
    super.onDismiss(dialog)
    FingerprintHelper.cancel()
}
``

### Demo展示
开始指纹验证
 ![1.jpg](https://github.com/leiyun1993/Fingerprint/raw/master/screenshot/1.jpg)

指纹验证成功

 ![2.jpg](https://github.com/leiyun1993/Fingerprint/raw/master/screenshot/2.jpg)
