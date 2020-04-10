package com.miss.base.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;

public class MediaHelper {
    /**
     *  player.prepare();
     *  player.start();	//开始或恢复
     * 	player.stop();	//停止播放
     * 	player.pause();	//暂停播放
     *
     * @param context
     * @param resId   如：R.raw.d
     */
    public MediaPlayer createMedia(Context context,@IdRes int resId){
        MediaPlayer player = MediaPlayer.create(context,resId);
        return player;
    }
    public MediaPlayer createMedia(Context context, Uri uri){
        MediaPlayer player = MediaPlayer.create(context,uri);
        return player;
    }

    /**
     * 使用SoundPool类播放音频
     * 可以同时播放多个短小的音频，而且占用资源较少.
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static SoundPool createSoundPool(){
        AudioAttributes attr = new AudioAttributes.Builder()           //设置音效相关属性
                .setUsage(AudioAttributes.USAGE_GAME)                 // 设置音效使用场景
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)  // 设置音效的类型
                .build();
        SoundPool soundpool = new SoundPool.Builder()           // 创建SoundPool对象
                .setAudioAttributes(attr) // 设置音效池的属性
                .setMaxStreams(10) // 设置最多可容纳10个音频流，
                .build();
        return soundpool;
    }

    /**
     * 将对应音频加载进来
     * @param soundPool
     * @param context
     * @param resId     如：R.raw.d
     * @param priority  优先级 默认：1
     */
    public static void load(SoundPool soundPool,Context context,int resId,int priority){
       if (soundPool != null){
           soundPool.load(context,resId,priority);
       }
    }
    /**
     *
     * @param soundPool
     * @param soundId     播放的音频,一般是一个soundpool对象，通过load加载
     * @param leftVolume  左音量
     * @param rightVolume 右音量
     * @param priority    优先级
     * @param loop        循环次数
     * @param rate        指定速率 正常为1
     */
    public static void play(SoundPool soundPool,int soundId,float leftVolume,float rightVolume,int priority,int loop,float rate){
        if (soundPool != null){
            soundPool.play(soundId,leftVolume,rightVolume,priority,loop,rate);
        }
    }
}
