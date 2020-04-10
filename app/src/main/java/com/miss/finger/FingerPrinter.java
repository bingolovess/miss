package com.miss.finger;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;

import androidx.core.app.ActivityCompat;

import java.util.HashMap;

import static com.miss.finger.CodeException.FINGERPRINTERS_FAILED_ERROR;
import static com.miss.finger.CodeException.HARDWARE_MISSIING_ERROR;
import static com.miss.finger.CodeException.KEYGUARDSECURE_MISSIING_ERROR;
import static com.miss.finger.CodeException.NO_FINGERPRINTERS_ENROOLED_ERROR;
import static com.miss.finger.CodeException.PERMISSION_DENIED_ERROE;
import static com.miss.finger.CodeException.SYSTEM_API_ERROR;

/**
 * 指纹校验
 */
public class FingerPrinter {
    private FingerPrinter(){}
    private FingerPrinterListener fingerPrinterListener;
    private FingerprintManager manager;
    private KeyguardManager mKeyManager;
    private Context context;
    @SuppressLint("NewApi")
    CancellationSignal mCancellationSignal;
    @SuppressLint("NewApi")
    FingerprintManager.AuthenticationCallback mSelfCancelled;
    public static FingerPrinter onCreate(){
        return new FingerPrinter();
    }

    /**
     *  设置手印监听
     * @param context
     * @param listener
     */
    public void setFingerPrinterListener(Context context,FingerPrinterListener listener){
        this.context = context;
        this.fingerPrinterListener = listener;
        if (Build.VERSION.SDK_INT < 23){
            listener.onError(new FPerException(SYSTEM_API_ERROR));
        }else {
            initManager();
            confirmFinger();
            startListening(null);
        }
    }
    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.M)
    public void startListening(FingerprintManager.CryptoObject cryptoObject) {
        //android studio 上，没有这个会报错
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT)
                != PackageManager.PERMISSION_GRANTED) {
            throw new FPerException(PERMISSION_DENIED_ERROE);
        }
        manager.authenticate(cryptoObject, null, 0, mSelfCancelled, null);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initManager() {
        mCancellationSignal = new CancellationSignal();
        manager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        mSelfCancelled = new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                //多次指纹密码验证错误后，进入此方法；并且，不能短时间内调用指纹验证
                if (fingerPrinterListener!=null){
                    fingerPrinterListener.onError(new FPerException(FINGERPRINTERS_FAILED_ERROR));
                }
                mCancellationSignal.cancel();
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                if (fingerPrinterListener != null){
                    fingerPrinterListener.onNext(true);
                }
            }

            @Override
            public void onAuthenticationFailed() {
                if (fingerPrinterListener != null){
                    fingerPrinterListener.onNext(false);
                }
            }
        };
    }

    @SuppressLint({"NewApi", "MissingPermission"})
    @TargetApi(23)
    public void confirmFinger() {
        //android studio 上，没有这个会报错
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT)
                != PackageManager.PERMISSION_GRANTED) {
            fingerPrinterListener.onError(new FPerException(PERMISSION_DENIED_ERROE));
        }
        //判断硬件是否支持指纹识别
        if (!manager.isHardwareDetected()) {
            fingerPrinterListener.onError(new FPerException(HARDWARE_MISSIING_ERROR));
        }
        //判断 是否开启锁屏密码
        if (!mKeyManager.isKeyguardSecure()) {
            fingerPrinterListener.onError(new FPerException(KEYGUARDSECURE_MISSIING_ERROR));
        }
        //判断是否有指纹录入
        if (!manager.hasEnrolledFingerprints()) {
            fingerPrinterListener.onError(new FPerException(NO_FINGERPRINTERS_ENROOLED_ERROR));
        }

    }
    public interface FingerPrinterListener{
        void onNext(boolean next);
        void onError(FPerException e);
    }
}
