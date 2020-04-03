package org.tmcrafz.cloudgallery.web.nextcloud;


import android.os.Handler;

public class NextcloudOperation {
    private static String TAG = NextcloudOperation.class.getCanonicalName();

    protected String mIdentifier;
    protected Handler mHandler;
    protected boolean mIsFinished;

    public NextcloudOperation(String identifier, Handler handler) {
        mIdentifier = identifier;
        mHandler = handler;
        mIsFinished = false;
    }

    public NextcloudOperation(String identifier) {
        mIdentifier = identifier;
        mIsFinished = false;
    }

    protected String getIdentifier() {
        return mIdentifier;
    }

    protected Handler getHandler() {
        return mHandler;
    }

    protected boolean isFinished() {
        return mIsFinished;
    }
}

