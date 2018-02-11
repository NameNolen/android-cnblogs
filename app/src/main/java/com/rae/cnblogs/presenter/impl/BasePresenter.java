package com.rae.cnblogs.presenter.impl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.rae.cnblogs.RxObservable;
import com.rae.cnblogs.presenter.IAppPresenter;
import com.rae.cnblogs.sdk.CnblogsApiFactory;
import com.rae.cnblogs.sdk.CnblogsApiProvider;
import com.rae.cnblogs.sdk.UserProvider;

import io.reactivex.Observable;

/**
 * 基类
 * Created by ChenRui on 2016/12/2 00:23.
 */
public abstract class BasePresenter<V> implements IAppPresenter {
    protected Context mContext;
    protected Context mApplicationContext;

    protected V mView;
    private String mTag;


    public BasePresenter(Context context, V view) {
        mTag = this.getClass().getName();
        mView = view;
        mContext = context;
        mApplicationContext = context.getApplicationContext();
    }

    protected boolean isEmpty(String text) {
        return TextUtils.isEmpty(text) || text.isEmpty() || text.trim().isEmpty();
    }

    protected CnblogsApiProvider getApiProvider() {
        return CnblogsApiFactory.getInstance(mContext);
    }

//    protected IRaeCnblogsApiProvider getServerApi() {
//        return RaeCnblogsApiFactory.getInstance(mContext);
//    }

    public String getString(int resId) {
        return mContext.getString(resId);
    }


    @Override
    public void start() {

    }

    public void destroy() {
        cancelRequest();
        mView = null;
        mContext = null;
    }

    /**
     * 释放当前请求
     */
    public void cancelRequest() {
        RxObservable.dispose(mTag);
        RxObservable.dispose();
        RxObservable.dispose("thread");
    }

    protected boolean isLogin() {
        return UserProvider.getInstance().isLogin();
    }

    protected boolean isNotLogin() {
        return !UserProvider.getInstance().isLogin();
    }

    public <T> Observable<T> createObservable(Observable<T> observable) {
        return RxObservable.create(observable, mTag);
    }

    /**
     * 网络是否可用
     */
    protected boolean isNetworkError() {
        if (mContext == null) return true;
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable();
    }

}
