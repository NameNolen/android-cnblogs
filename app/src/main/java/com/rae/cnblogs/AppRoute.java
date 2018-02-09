package com.rae.cnblogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.rae.cnblogs.activity.AboutMeActivity;
import com.rae.cnblogs.activity.BlogContentActivity;
import com.rae.cnblogs.activity.BloggerActivity;
import com.rae.cnblogs.activity.CategoryActivity;
import com.rae.cnblogs.activity.CommentActivity;
import com.rae.cnblogs.activity.FavoritesActivity;
import com.rae.cnblogs.activity.FeedbackActivity;
import com.rae.cnblogs.activity.FontSettingActivity;
import com.rae.cnblogs.activity.FriendsActivity;
import com.rae.cnblogs.activity.ImageSelectionActivity;
import com.rae.cnblogs.activity.LoginActivity;
import com.rae.cnblogs.activity.MainActivity;
import com.rae.cnblogs.activity.MomentAtMeActivity;
import com.rae.cnblogs.activity.MomentDetailActivity;
import com.rae.cnblogs.activity.MomentMessageActivity;
import com.rae.cnblogs.activity.PostMomentActivity;
import com.rae.cnblogs.activity.SearchActivity;
import com.rae.cnblogs.activity.SettingActivity;
import com.rae.cnblogs.activity.SystemMessageActivity;
import com.rae.cnblogs.activity.WebActivity;
import com.rae.cnblogs.activity.WebLoginActivity;
import com.rae.cnblogs.image.ImagePreviewActivity;
import com.rae.cnblogs.sdk.bean.BlogBean;
import com.rae.cnblogs.sdk.bean.BlogType;
import com.rae.cnblogs.sdk.bean.MomentBean;
import com.rae.cnblogs.sdk.model.MomentMetaData;

import java.util.ArrayList;

/**
 * 路由
 * Created by ChenRui on 2016/12/6 23:49.
 */
public final class AppRoute {

    // WEB 登录
    public static final int REQ_CODE_WEB_LOGIN = 100;
    /*朋友界面 - 来自粉丝*/
    public static final int ACTIVITY_FRIENDS_TYPE_FANS = 1;
    /*朋友界面 - 来自关注*/
    private static final int ACTIVITY_FRIENDS_TYPE_FOLLOW = 2;
    // 分类
    public static final int REQ_CODE_CATEGORY = 102;
    // 收藏
    public static final int REQ_CODE_FAVORITES = 103;
    // 博主
    public static final int REQ_CODE_BLOGGER = 104;
    // 发布闪存
    public static final int REQ_POST_MOMENT = 105;
    // 图片选择
    public static final int REQ_IMAGE_SELECTION = 106;
    // 图片选择
    public static final int REQ_CODE_IMAGE_SELECTED = 107;

    private static void startActivity(Context context, Intent intent) {
        context.startActivity(intent);
    }

    private static void startActivity(Context context, Class<?> cls) {
        startActivity(context, new Intent(context, cls));
    }

    private static void startActivityForResult(Activity context, Intent intent, int requestCode) {
        context.startActivityForResult(intent, requestCode);
    }

    private static void startActivityForResult(Activity context, Class<?> cls, int requestCode) {
        startActivityForResult(context, new Intent(context, cls), requestCode);
    }

    /**
     * 博客正文界面
     *
     * @param blogId 博客ID
     * @param type   博客类型
     */
    public static void jumpToBlogContent(Context context, String blogId, BlogType type) {
        Intent intent = new Intent(context, BlogContentActivity.class);
        intent.putExtra("blogId", blogId);
        intent.putExtra("type", type.getTypeName());
        startActivity(context, intent);
    }

    /**
     * 博客正文界面
     *
     * @param blog 博客实体
     * @param type 博客类型
     */
    public static void jumpToBlogContent(Context context, BlogBean blog, BlogType type) {
        if (blog == null) return;
        Intent intent = new Intent(context, BlogContentActivity.class);
        // 不传递摘要和正文这些过大的数据。进去博文正文之后再从数据库拉取。已经在BlogBean里面处理大数据问题
        intent.putExtra("blog", blog);
        intent.putExtra("blogId", blog.getBlogId());
        intent.putExtra("type", type.getTypeName());
        startActivity(context, intent);
    }

    /**
     * 网页
     *
     * @param url 路径
     */
    public static void jumpToWeb(Context context, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.setData(Uri.parse(url));
        startActivity(context, intent);
    }

    /**
     * 用户反馈
     */
    public static void jumpToFeedback(Context context) {
        Intent intent = new Intent(context, FeedbackActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(context, intent);
    }


    /**
     * 网页，新线程
     *
     * @param url 路径
     */
    public static void jumpToWebNewTask(Context context, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.setData(Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(context, intent);
    }

    /**
     * 主界面
     */
    public static void jumpToMain(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


    /**
     * 登录
     */
    public static void jumpToLogin(Context context) {
        startActivity(context, LoginActivity.class);
    }

    /**
     * 登录
     */
    public static void jumpToLogin(Activity context, int req) {
        startActivityForResult(context, LoginActivity.class, req);
    }

    /**
     * 登录，有回调结果
     */
    public static void jumpToWebLogin(Activity context) {
        startActivityForResult(context, WebLoginActivity.class, REQ_CODE_WEB_LOGIN);
    }

    /**
     * 粉丝
     *
     * @param bloggerName 博主昵称
     * @param blogApp     博主ID
     */
    public static void jumpToFans(Context context, String bloggerName, String blogApp) {
        jumpToFriends(context, ACTIVITY_FRIENDS_TYPE_FANS, bloggerName, blogApp);
    }

    /**
     * 关注
     *
     * @param bloggerName 博主昵称
     * @param blogApp     博主ID
     */
    public static void jumpToFollow(Context context, String bloggerName, String blogApp) {
        jumpToFriends(context, ACTIVITY_FRIENDS_TYPE_FOLLOW, bloggerName, blogApp);
    }

    /**
     * 跳转到朋友界面
     *
     * @param type        来源类型，参考该类{@link #ACTIVITY_FRIENDS_TYPE_FANS}
     * @param bloggerName 博主昵称
     * @param blogApp     博主ID
     */
    private static void jumpToFriends(Context context, int type, String bloggerName, String blogApp) {
        Intent intent = new Intent(context, FriendsActivity.class);
        intent.putExtra("blogApp", blogApp);
        intent.putExtra("bloggerName", bloggerName);
        intent.putExtra("fromType", type);
        startActivity(context, intent);
    }

    /**
     * 图片大图预览
     *
     * @param images   图片数组
     * @param position 跳转到低几张图片，默认传0
     */
    public static void jumpToImagePreview(Activity context, @NonNull ArrayList<String> images, int position, ArrayList<String> selectedImages, int maxCount) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putStringArrayListExtra("images", images);
        if (selectedImages != null) {
            intent.putStringArrayListExtra("selectedImages", selectedImages);
        }
        intent.putExtra("position", position);
        intent.putExtra("maxCount", maxCount);
        startActivityForResult(context, intent, REQ_CODE_IMAGE_SELECTED);
    }

    /**
     * 图片大图预览
     *
     * @param images   图片数组
     * @param position 跳转到低几张图片，默认传0
     */
    public static void jumpToImagePreview(Activity context, @NonNull ArrayList<String> images, int position) {
        jumpToImagePreview(context, images, position, null, 0);
    }

    /**
     * 图片大图预览
     *
     * @param images   图片数组
     * @param position 跳转到低几张图片，默认传0
     */
    public static void jumpToImagePreview(Context context, @NonNull ArrayList<String> images, int position) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putStringArrayListExtra("images", images);
        intent.putExtra("position", position);
        startActivity(context, intent);
    }


    /**
     * 图片查看
     *
     * @param imgUrl 图片路径
     */
    public static void jumpToImagePreview(Activity context, @NonNull String imgUrl) {
        ArrayList<String> data = new ArrayList<>();
        data.add(imgUrl);
        jumpToImagePreview(context, data, 0);
    }


    /**
     * 图片查看
     *
     * @param imgUrl 图片路径
     */
    public static void jumpToImagePreview(Context context, @NonNull String imgUrl) {
        ArrayList<String> data = new ArrayList<>();
        data.add(imgUrl);
        jumpToImagePreview(context, data, 0);
    }

    /**
     * 博主界面
     *
     * @param blogApp 博客APP
     */
    public static void jumpToBlogger(Context context, String blogApp) {
        if (TextUtils.isEmpty(blogApp)) {
            AppUI.toast(context, "博主信息为空！");
            return;
        }
        Intent intent = new Intent(context, BloggerActivity.class);
        intent.putExtra("blogApp", blogApp);
        startActivity(context, intent);
    }

    /**
     * 博主界面
     *
     * @param blogApp 博客APP
     */
    public static void jumpToBlogger(Activity context, String blogApp) {
        if (TextUtils.isEmpty(blogApp)) {
            AppUI.toast(context, "博主信息为空！");
            return;
        }
        Intent intent = new Intent(context, BloggerActivity.class);
        intent.putExtra("blogApp", blogApp);
        startActivityForResult(context, intent, REQ_CODE_BLOGGER);
    }


    /**
     * 分类管理
     */
    public static void jumpToCategoryForResult(Activity context) {
        Intent intent = new Intent(context, CategoryActivity.class);
        startActivityForResult(context, intent, REQ_CODE_CATEGORY);
    }

    /**
     * 我的收藏
     */
    public static void jumpToFavorites(Activity context) {
        startActivityForResult(context, FavoritesActivity.class, REQ_CODE_FAVORITES);
    }

    /**
     * 设置
     */
    public static void jumpToSetting(Context context) {
        startActivity(context, SettingActivity.class);
    }

    /**
     * 搜索
     */
    public static void jumpToSearch(Context context) {
        startActivity(context, SearchActivity.class);
    }

    /**
     * 搜索-新闻
     */
    public static void jumpToSearchNews(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("position", 2);
        startActivity(context, intent);
    }

    /**
     * 搜索-知识库
     */
    public static void jumpToSearchKb(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("position", 3);
        startActivity(context, intent);
    }

    /**
     * 系统消息
     */
    public static void jumpToSystemMessage(Context context) {
        startActivity(context, SystemMessageActivity.class);
    }

    /**
     * 字体设置
     */
    public static void jumpToFontSetting(Context context) {
        startActivity(context, FontSettingActivity.class);
    }

    /**
     * 博客评论
     */
    public static void jumpToComment(Context context, BlogBean blog, BlogType type) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra("type", type.getTypeName());
        intent.putExtra("blog", blog);
        startActivity(context, intent);
    }

    /**
     * 发布闪存
     */
    public static void jumpToPostMoment(Activity context) {
        Intent intent = new Intent(context, PostMomentActivity.class);
        startActivityForResult(context, intent, REQ_POST_MOMENT);
    }

    /**
     * 发布闪存
     */
    public static void jumpToPostMoment(Activity context, MomentMetaData data) {
        Intent intent = new Intent(context, PostMomentActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, data);
        startActivityForResult(context, intent, REQ_POST_MOMENT);
    }

    /**
     * 闪存详情
     */
    public static void jumpToMomentDetail(Context context, MomentBean data) {
        Intent intent = new Intent(context, MomentDetailActivity.class);
        intent.putExtra("data", data);
        startActivity(context, intent);
    }

    /**
     * 闪存详情
     */
    public static void jumpToMomentDetail(Context context, String userAlias, String ingId) {
        Intent intent = new Intent(context, MomentDetailActivity.class);
        intent.putExtra("ingId", ingId);
        intent.putExtra("userId", userAlias);
        startActivity(context, intent);
    }


    /**
     * 闪存消息
     */
    public static void jumpToMomentMessage(Context context) {
        Intent intent = new Intent(context, MomentMessageActivity.class);
        startActivity(context, intent);
    }

    /**
     * 提到我的闪存
     */
    public static void jumpToMomentAtMe(Context context) {
        Intent intent = new Intent(context, MomentAtMeActivity.class);
        startActivity(context, intent);
    }

    /**
     * 跳转到图片选择
     */
    public static void jumpToImageSelection(Activity context, ArrayList<String> selectedImages) {
        Intent intent = new Intent(context, ImageSelectionActivity.class);
        if (selectedImages != null)
            intent.putStringArrayListExtra("selectedImages", selectedImages);
        startActivityForResult(context, intent, REQ_IMAGE_SELECTION);
    }

    /**
     * 关于我们
     */
    public static void jumpToAboutMe(Context context) {
        startActivity(context, AboutMeActivity.class);
    }
}
