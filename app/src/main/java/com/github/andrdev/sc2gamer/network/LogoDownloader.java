package com.github.andrdev.sc2gamer.network;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class LogoDownloader extends HandlerThread {
    private static final String TAG = "ThumbDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private static final String IMAGE_URL = "http://www.gosugamers.net/uploads/images/teams/";
    private final File mCacheFolder;
    private Handler mHandler;
    private Map<ImageView, String> requestMap =
            Collections.synchronizedMap(new HashMap<ImageView, String>());
    private Handler mResponseHandler;

    public LogoDownloader(Handler responseHandler, File cacheFolder) {
        super(TAG);
        mResponseHandler = responseHandler;
        mCacheFolder = cacheFolder;
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    ImageView imageView = (ImageView) msg.obj;
                    handleRequest(imageView);
                }
            }
        };
    }

    private void handleRequest(final ImageView imageView) {
        try {

            final String url = requestMap.get(imageView);
            if (url == null) {
                return;
            }
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
                    if (requestMap.get(imageView) != url) {
                        return;
                    }
                    requestMap.remove(imageView);
                    imageView.setImageBitmap(bitmap);
                }
            });
        } catch (IOException ioe) {
            Log.e(TAG, "Error loading image", ioe);
        }
    }

    public void queueThumbnail(ImageView imageView, String url) {
        requestMap.put(imageView, url);
        mHandler.obtainMessage(MESSAGE_DOWNLOAD, imageView).sendToTarget();
    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }
}


