package com.github.andrdev.sc2gamer;

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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by taiyokaze on 8/22/14.
 */
public class LogosDownloader extends HandlerThread {
    private static final String TAG = "ThumbDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private static final String IMAGE_URL="http://www.gosugamers.net/uploads/images/teams/";
    private final File mCacheFolder;
    Handler mHandler;
    Map<ImageView,String> requestMap =
            Collections.synchronizedMap(new HashMap<ImageView, String>());
    Handler mResponseHandler;

    public LogosDownloader(Handler responseHandler, File cacheFolder) {
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
                    ImageView imageView = (ImageView)msg.obj;
                    Log.i(TAG, "Got a request for url: " + requestMap.get(imageView));
                    handleRequest(imageView);
                }
            }
        };
    }

    private void handleRequest(final ImageView imageView) {
        try {

            final String url = requestMap.get(imageView);
            if (url == null)
                return;
            File file = new File(mCacheFolder + "/" + url);
            Log.i(TAG, "file " + file.toString());
            if(file.createNewFile()){
            byte[] bitmapBytes = JsoupHelper.getPhoto(IMAGE_URL + "/" + url);
            FileOutputStream fileout = new FileOutputStream(file);
            fileout.write(bitmapBytes);
            fileout.flush();
            fileout.getFD().sync();
            fileout.close();}
            Log.i("file1", "file1 " + Arrays.toString(mCacheFolder.listFiles()));
            final Bitmap bitmap = BitmapFactory
                      .decodeFile(file.toString());
            mResponseHandler.post(new Runnable() {
                public void run() {
                    if (requestMap.get(imageView) != url)
                        return;

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


