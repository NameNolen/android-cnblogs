package com.rae.cnblogs.presenter;

import android.content.Context;

import com.rae.cnblogs.presenter.impl.BlogCommentPresenterImpl;
import com.rae.cnblogs.presenter.impl.BlogContentPresenterImpl;
import com.rae.cnblogs.presenter.impl.BlogListPresenterImpl;
import com.rae.cnblogs.presenter.impl.HomePresenterImpl;
import com.rae.cnblogs.presenter.impl.LauncherPresenterImpl;
import com.rae.cnblogs.presenter.impl.LoginPresenterImpl;

/**
 * Created by ChenRui on 2016/12/2 00:23.
 */
public final class CnblogsPresenterFactory {

    public static IHomePresenter getHomePresenter(Context context, IHomePresenter.IHomeView view) {
        return new HomePresenterImpl(context, view);
    }

    public static IBlogListPresenter getBlogListPresenter(Context context, IBlogListPresenter.IBlogListView view) {
        return new BlogListPresenterImpl(context, view);
    }

    public static IBlogContentPresenter getBlogContentPresenter(Context context, IBlogContentPresenter.IBlogContentView view) {
        return new BlogContentPresenterImpl(context, view);
    }

    public static ILauncherPresenter getLauncherPresenter(Context context, ILauncherPresenter.ILauncherView view) {
        return new LauncherPresenterImpl(context, view);
    }

    /**
     * 评论
     *
     * @return
     */
    public static IBlogCommentPresenter getBlogCommentPresenter(Context context, IBlogCommentPresenter.IBlogCommentView view) {
        return new BlogCommentPresenterImpl(context, view);
    }

    public static ILoginPresenter getLoginPresenter(Context context, ILoginPresenter.ILoginView view) {
        return new LoginPresenterImpl(context, view);
    }
}
