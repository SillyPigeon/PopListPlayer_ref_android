package com.example.myapplication;

import android.media.tv.TvContract;
import android.media.tv.TvView;
import android.net.Uri;

public class TvPlayerController {

    public static boolean tune(int channelId){
        MainActivity tifActivity = TvSupportHolder.getTifActivity();
        TvView tvPlayer = TvSupportHolder.getTvPlayer();
        if ((tifActivity == null) || (tvPlayer == null)){
            return false;
        }
        String inputSource = "com.package/.tuner.TunerInputService/HW0";
        Uri resultUri = TvContract.buildChannelUri(channelId);
        tvPlayer.tune(inputSource, resultUri);
        return false;
    }
}
