package com.rae.cnblogs.presenter.impl.blog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.rae.cnblogs.RxObservable;
import com.rae.cnblogs.presenter.IBlogListPresenter;
import com.rae.cnblogs.presenter.impl.BasePresenter;
import com.rae.cnblogs.sdk.ApiDefaultObserver;
import com.rae.cnblogs.sdk.api.IBlogApi;
import com.rae.cnblogs.sdk.bean.BlogBean;
import com.rae.cnblogs.sdk.bean.BlogType;
import com.rae.cnblogs.sdk.bean.CategoryBean;
import com.rae.cnblogs.sdk.db.DbBlog;
import com.rae.cnblogs.sdk.db.DbFactory;
import com.rae.swift.Rx;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

/**
 * 博客列表处理
 * Created by ChenRui on 2016/12/2 00:25.
 */
public class BlogListPresenterImpl extends BasePresenter<IBlogListPresenter.IBlogListView> implements IBlogListPresenter {

    protected IBlogApi mApi;
    private DbBlog mDbBlog;
    protected int mPageIndex = 1;
    private final List<BlogBean> mBlogList = new ArrayList<>();
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mPageIndex++;
            return false;
        }
    });

    public BlogListPresenterImpl(Context context, IBlogListPresenter.IBlogListView view) {
        super(context, view);
        mApi = getApiProvider().getBlogApi();
        mDbBlog = DbFactory.getInstance().getBlog();
    }

    @Override
    public void start() {
        if (mView == null) return;
        mPageIndex = 1;
        mBlogList.clear();
        // 加载列表
        onLoadData(mView.getCategory(), mPageIndex);
    }

    /**
     * 加数据
     */
    protected void onLoadData(final CategoryBean category, final int pageIndex) {
        createObservable(mApi.getBlogList(pageIndex, category.getType(), category.getParentId(), category.getCategoryId())).subscribe(getBlogObserver());
    }

    /**
     * 加载离线数据
     */
    protected void loadOfflineData(final CategoryBean category, final int pageIndex) {
        RxObservable.newThread()
                .map(new Function<Integer, List<BlogBean>>() {
                    @Override
                    public List<BlogBean> apply(@io.reactivex.annotations.NonNull Integer integer) throws Exception {
                        return DbFactory.getInstance().getBlog().getList(category.getCategoryId(), pageIndex - 1, BlogType.BLOG);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getBlogObserver());
    }

    @NonNull
    protected ApiDefaultObserver<List<BlogBean>> getBlogObserver() {
        return new ApiDefaultObserver<List<BlogBean>>() {

            @Override
            public void onError(Throwable e) {
                // fix bug #649
                if (mView == null) return;
                if (isNetworkError()) {
                    loadOfflineData(mView.getCategory(), mPageIndex);
                    return;
                }
                super.onError(e);
            }


            @Override
            protected void onError(String message) {
                if (mView == null) return;
                mView.onLoadFailed(mPageIndex, message);
            }

            @Override
            protected void accept(List<BlogBean> blogBeans) {
                if (mView == null) return;

                if (Rx.isEmpty(blogBeans)) {
                    // 没有更多
                    if (mPageIndex > 1)
                        mView.onLoadMoreEmpty();
                    else
                        mView.onLoadFailed(mPageIndex, "哎呀，没有数据哦");
                    return;
                }
                onApiSuccess(blogBeans);
            }
        };
    }

    private void onApiSuccess(List<BlogBean> data) {
        if (mView == null) return;

        // 保存到数据库
        if (!Rx.isEmpty(data)) {
            mDbBlog.addAll(data, mView.getCategory().getCategoryId());
        }


        // 无重复添加
        data.removeAll(mBlogList);

        if (mPageIndex <= 1) {
            mBlogList.addAll(0, data);
        } else {
            mBlogList.addAll(data);
        }

        mView.onLoadBlogList(mPageIndex, mBlogList);
        // 由于第一次会加载缓存，所以要等待一段时间才处理
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, 500);
    }


    @Override
    public void loadMore() {
        onLoadData(mView.getCategory(), mPageIndex);
    }

    @Override
    public void refreshDataSet() {
    }
}
