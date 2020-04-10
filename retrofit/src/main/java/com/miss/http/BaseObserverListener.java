package com.miss.http;

import com.miss.http.bean.BaseResponse;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

/**
 * 针对普通对象
 * @param <T>
 */
public abstract class BaseObserverListener<T> extends DisposableObserver<T> {

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onNext(T t) {
        try {
            BaseResponse baseResponse = (BaseResponse) t;
            if (baseResponse.getCode()==200){
                onSuccess(t);
            }else {
                onError(baseResponse.getMsg());
            }
        } catch (Exception e) {
            onError(e);
            onError(e.getMessage());
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        String message;
        if (e instanceof SocketTimeoutException){
            message="SocketTimeoutException:网络连接超时！";
        }else if (e instanceof ConnectException){
            message="ConnectException:网络无法连接！";
        } else if (e instanceof HttpException) {
            message="HttpException:网络中断，请检查您的网络状态！";
        } else if (e instanceof UnknownHostException) {
            message="UnknownHostException:网络错误，请检查您的网络状态！";
        }else {
            message = e.getMessage();
        }
        onError(message);
    }

    @Override
    public void onComplete() {}
    protected abstract void onSuccess(T t);
    /**
     * 很多异常不需要反馈给用户，所以不必抽象，如果需要异常信息，重写即可
     * @param msg
     */
    protected  void onError(String msg){}
}
