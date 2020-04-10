package com.miss.http;

import android.util.Log;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 针对普通对象
 * @param <T>
 */
public abstract class BaseObserver<T> implements Observer<T> {
    private final String tag = this.getClass().getSimpleName();
    @Override
    public void onSubscribe(Disposable d) {
        Log.e(tag,"subscribe");
    }

    @Override
    public void onNext(T data) {
        onSuccess(data);
    }

    @Override
    public void onError(Throwable e) {
        onFailure(e);
    }

    @Override
    public void onComplete() {
    }

    public abstract void onSuccess(T data);

    public abstract void onFailure(Throwable e);
}
