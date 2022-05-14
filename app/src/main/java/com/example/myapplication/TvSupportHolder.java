package com.example.myapplication;

import android.media.tv.TvView;

import com.example.myapplication.MainActivity;

import java.lang.ref.WeakReference;

public class TvSupportHolder {

    private static WeakReference<TvView> sTvPlayer;
    private static WeakReference<MainActivity> sMainActivity;

    static void configPlayer(TvView tvPlayer){ sTvPlayer = new WeakReference<>(tvPlayer);}

    static void releaseTvPlayer(){
        if (sTvPlayer != null){
            sTvPlayer = null;
        }
    }
    static void releaseTifActivity() {
        if (sMainActivity != null) {
            sMainActivity = null;
        }
    }

    static void configTifActivity(MainActivity activity){
        sMainActivity = new WeakReference<>(activity);
    }

    public static TvView getTvPlayer(){
        return sTvPlayer == null ? null : sTvPlayer.get();
    }

    public static MainActivity getTifActivity(){
        return sMainActivity == null ? null : sMainActivity.get();
    }

}
