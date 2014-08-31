package com.github.andrdev.sc2gamer;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.networkstate.NetworkStateChecker;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;

/**
 * Created by taiyokaze on 8/31/14.
 */
public class SpiceServi extends UncachedSpiceService {
    @Override
    protected NetworkStateChecker getNetworkStateChecker() {
        return new NetworkStateChecker() {
            @Override
            public boolean isNetworkAvailable (Context context ) {
                ConnectivityManager cm = (ConnectivityManager) getApplication()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    return true;
                }
                return false;
            }
            @Override
            public void checkPermissions (Context context ) {
            }
        };
    }

}
