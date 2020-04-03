package org.tmcrafz.cloudgallery.datahandling;

import android.content.Context;

public class StorageHandler {

    public static String getCacheDir(Context context) {
        return context.getExternalFilesDir(null).toString() + "/cache";
    }

    public static String getThumbnailDir(Context context) {
        return getCacheDir(context) + "/thumbnails";
    }

    public static String getMediaDir(Context context) {
        // /Android/data/org.tmcrafz.cloudgallery/files
        return context.getExternalFilesDir(null).toString();
    }

}
