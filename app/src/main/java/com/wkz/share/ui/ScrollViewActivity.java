package com.wkz.share.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sunfusheng.glideimageview.GlideImageView;
import com.wkz.share.R;
import com.wkz.share.blur.GlideBlurTransformation;
import com.wkz.share.immersionbar.BarHide;
import com.wkz.share.immersionbar.ImmersionBar;
import com.wkz.share.share.OnShareListener;
import com.wkz.share.share.ShareDialog;
import com.wkz.share.share.SharePlatformAdapter;
import com.wkz.share.utils.AnimationUtils;
import com.wkz.share.utils.ScreenShotUtils;
import com.wkz.share.zxing.QRCode;

/**
 * 滚动视图的长截图分享界面
 *
 * @author Administrator
 * @date 2018/5/16
 */
public class ScrollViewActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final float ZOOM_BEFORE = 1.0F;
    private static final float ZOOM_AFTER = 1.05F;
    private static final long DURATION = 200L;


    private ShareDialog mShareDialog;
    private GlideImageView mGameImageIv;
    private GlideImageView mGameIconIv;
    private TextView mGameNameTv;
    private TextView mGameDescriptionTv;
    private RelativeLayout mGameInfoRl;
    private NestedScrollView mScrollNsv;
    private ImageView mQrCodeIv;
    private LinearLayout mChildLl;

    private Activity mContext;
    /**
     * 二维码中心图片地址
     */
    private String mCenterImageUrl = "http://gss0.baidu.com/-fo3dSag_xI4khGko9WTAnF6hhy/zhidao/pic/item/5366d0160924ab1861c9326236fae6cd7a890b8c.jpg";
    /**
     * 二维码顶角颜色
     */
    @ColorInt
    private int mVertexColor = Color.parseColor("#ff000000");
    /**
     * 下载游戏地址
     */
    private String mDownloadUrl = "http://files.rastargame.com/andriodapk/RSClient.apk";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.activity_scroll_view);

        initView();

        //隐藏状态栏
        ImmersionBar.with(this)
                .fitsSystemWindows(false)
                .statusBarDarkFont(false)
                .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
                .init();

        //分享弹窗
        mShareDialog = new ShareDialog(this)
                .setShareTitle("图片分享到")
                .setOnShareListener(new OnShareListener() {
                    @Override
                    public void onClickCloseBtn(ShareDialog shareDialog) {
                        finish();
                    }

                    @Override
                    public void onTouchOutSide(ShareDialog shareDialog) {
                        zoomIn();
                    }

                    @Override
                    public void onClickSharePlatform(ShareDialog shareDialog, SharePlatformAdapter.ViewHolder holder, int sharePlatform) {
                        //长截图
                        Bitmap bitmap = ScreenShotUtils.shotNestedScrollView(mScrollNsv);
                    }
                });
        mShareDialog.show();
    }

    private void initView() {
        mGameImageIv = (GlideImageView) findViewById(R.id.iv_game_image);
        mGameImageIv.setOnClickListener(this);
        mGameIconIv = (GlideImageView) findViewById(R.id.iv_game_icon);
        mGameIconIv.setOnClickListener(this);
        mGameNameTv = (TextView) findViewById(R.id.tv_game_name);
        mGameDescriptionTv = (TextView) findViewById(R.id.tv_game_description);
        mGameInfoRl = (RelativeLayout) findViewById(R.id.rl_game_info);
        mGameInfoRl.setOnClickListener(this);
        mScrollNsv = (NestedScrollView) findViewById(R.id.nsv_scroll);
        mQrCodeIv = (ImageView) findViewById(R.id.iv_qr_code);
        mQrCodeIv.setOnLongClickListener(this);
        mChildLl = (LinearLayout) findViewById(R.id.ll_child);

        //背景图片
        Glide.with(this)
                .load(R.mipmap.pic_image)
                .apply(new RequestOptions().transform(new GlideBlurTransformation(this)))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        mChildLl.setBackground(resource);
                    }
                });

        //二维码中心图片
        Glide.with(mContext)
                .asBitmap()
                .load(R.mipmap.ic_game_icon)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        mQrCodeIv.setImageBitmap(QRCode.createQRCodeWithLogo6(mDownloadUrl, 500, resource, mVertexColor));
                    }
                });

    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_game_image:
                if (!mShareDialog.isShowing()) {
                    mShareDialog.show();
                }
                zoomOut();
                break;
            case R.id.iv_game_icon:
                if (!mShareDialog.isShowing()) {
                    mShareDialog.show();
                }
                zoomOut();
                break;
            case R.id.rl_game_info:
                if (!mShareDialog.isShowing()) {
                    mShareDialog.show();
                }
                zoomOut();
                break;
            default:
                break;
        }
    }

    /**
     * 缩小
     */
    private void zoomIn() {
        AnimationUtils.zoomIn(mGameInfoRl, ZOOM_BEFORE, ZOOM_BEFORE, ZOOM_AFTER, ZOOM_AFTER, DURATION);
    }

    /**
     * 放大
     */
    private void zoomOut() {
        AnimationUtils.zoomOut(mGameInfoRl, ZOOM_AFTER, ZOOM_AFTER, ZOOM_BEFORE, ZOOM_BEFORE, DURATION);
    }

    @Override
    public boolean onLongClick(View v) {
        //打开浏览器下载游戏
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(mDownloadUrl));
        mContext.startActivity(intent);
        return false;
    }
}
