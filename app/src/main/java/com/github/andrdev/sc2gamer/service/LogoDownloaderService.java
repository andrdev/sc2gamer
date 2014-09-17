package com.github.andrdev.sc2gamer.service;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.github.andrdev.sc2gamer.network.NetHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LogoDownloaderService extends IntentService{
    private static final String TAG = "LogoDownloaderService";
    private static final String IMAGE_URL = "http://www.gosugamers.net/uploads/images/teams/";
    private final File mCacheFolder = getCacheDir();
    private Map<ImageView, String> requestMap =
            Collections.synchronizedMap(new HashMap<ImageView, String>());
    private Handler mResponseHandler;

    public LogoDownloaderService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mResponseHandler = new Handler(getMainLooper());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ImageView imageView = (ImageView) intent.getParcelableExtra("image");
        String url = intent.getStringExtra("url");
        if (imageView == null || url == null) {
            return;
        }
        try {
            File file = new File(mCacheFolder + "/" + url);
            if (file.createNewFile()) {
                byte[] bitmapBytes = NetHelper.getTeamLogo(IMAGE_URL + "/" + url);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bitmapBytes);
                fileOutputStream.flush();
                fileOutputStream.getFD().sync();
                fileOutputStream.close();
            }
            final Bitmap bitmap = BitmapFactory
                    .decodeFile(file.toString());
            mResponseHandler.post(new Runnable() {
                public void run() {
                    imageView.setImageBitmap(bitmap);
                }
            });
        } catch (IOException ioe) {
            Log.e(TAG, "Error loading image", ioe);
        }
    }
}
