package com.jndv.avajxr2;

import android.accounts.NetworkErrorException;
import android.content.Context;

import com.jndv.avajxr2.bean.BaseEntity;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class BaseObserver<T> implements Observer<BaseEntity<T>> {

    private static final String TAG = BaseObserver.class.getSimpleName();
    private Context mContext;

    protected BaseObserver(){ }

    protected BaseObserver(Context context) { this.mContext = context; }

    @Override
    public void onNext(BaseEntity<T> tBaseEntity) {
        if (tBaseEntity.isSuccess()) {
            try {
                onSuccees(tBaseEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                onCodeError(tBaseEntity.getError_code());
                onError(new Throwable(tBaseEntity.getReason()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        try {
            if (e instanceof ConnectException
                    || e instanceof TimeoutException
                    || e instanceof NetworkErrorException
                    || e instanceof UnknownHostException) {
                onFailure(e, true);
            } else {
                onFailure(e, false);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onComplete() { }

    @Override
    public void onSubscribe(Disposable d) { }

    /**
     * 返回成功
     *
     * @param data
     * @throws Exception
     */
    protected abstract void onSuccees(BaseEntity<T> data) throws Exception;

    /**
     * 返回失败
     *
     * @param e
     * @param isNetWorkError 是否是网络错误
     * @throws Exception
     */
    protected abstract void onFailure(Throwable e, boolean isNetWorkError) throws Exception;

    /**
     * 返回成功了,但是code错误
     *
     * @param error
     * @throws Exception
     */
    protected void onCodeError(int error) throws Exception{ }
}

