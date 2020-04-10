package com.miss.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.miss.R;
import com.miss.base.BaseActivity;
import com.miss.base.utils.LogUtils;
import com.miss.view.CommonVideoView;

import cn.com.superLei.aoparms.annotation.Delay;
import cn.com.superLei.aoparms.annotation.DelayAway;

import static android.provider.MediaStore.Video.Thumbnails.MINI_KIND;

public class GuideActivity extends BaseActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private CommonVideoView videoView;
    private int curPage;
    private boolean mHasPaused;

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        videoView = findViewById(R.id.video_view);
        videoView.setOnPreparedListener(this);
        int videoRes = R.raw.splash;
        String videoPath = "android.resource://" + getPackageName() + "/" + videoRes;
        Bitmap videoThumbnail = getVideoThumbnail(videoPath, 512, 384, MINI_KIND);
        if (videoThumbnail!=null){
            Drawable drawable = new BitmapDrawable(videoThumbnail);
            videoView.setBackground(drawable);
        }
        LogUtils.getInstance().e("开始执行");
        play(videoPath);
    }
    //开启延迟任务（5s后执行该方法）
    @Delay(key = "splash", delay = 3000L)
    public void play(String path){
        LogUtils.getInstance().e("執行到了.....");
        videoView.setVideoPath(path);
    }
    //移除延迟任务
    @DelayAway(key = "splash")
    public void cancle() {
        LogUtils.getInstance().e("cancle: >>>>>");
    }

    public void navMain(View view) {
        cancle();
        if (videoView != null) {
            videoView.stopPlayback();
        }
        Intent intent = new Intent(GuideActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @param width 指定输出视频缩略图的宽度
     * @param height 指定输出视频缩略图的高度度
     * @param kind 参照MediaStore.Images(Video).Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind); //調用ThumbnailUtils類的靜態方法createVideoThumbnail獲取視頻的截圖；
        if(bitmap!= null){
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);//調用ThumbnailUtils類的靜態方法extractThumbnail將原圖片（即上方截取的圖片）轉化為指定大小；
        }
        return bitmap;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    // video 视屏播放的时候把背景设置为透明
                    videoView.setBackgroundColor(Color.TRANSPARENT);
                    return true;
                }
                return false;
            }
        });
        if (videoView != null) {
            videoView.requestFocus();
            videoView.seekTo(0);
            videoView.start();
            videoView.setOnCompletionListener(this);
        }
        return;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Toast.makeText(this, "播放结束", Toast.LENGTH_SHORT).show();
        navMain(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHasPaused) {
            if (videoView != null) {
                videoView.seekTo(curPage);
                videoView.resume();
            }
        }
        return;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView != null) {
            curPage = videoView.getCurrentPosition();
        }
        mHasPaused = true;
    }

    public void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}
