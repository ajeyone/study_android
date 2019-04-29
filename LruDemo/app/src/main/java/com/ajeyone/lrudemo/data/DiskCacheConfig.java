package com.ajeyone.lrudemo.data;

import android.content.Context;
import android.util.Log;

import java.io.File;

import androidx.annotation.NonNull;

public class DiskCacheConfig {
    private static final String TAG = "ajeyonelru";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File getImageCacheDir(@NonNull Context context) {
        File cacheDir = context.getCacheDir();
        cacheDir = new File(cacheDir, "images");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        Log.d(TAG, "getImageCacheDir: " + cacheDir);
        return cacheDir;
    }
}
