package org.tmcrafz.cloudgallery.web.nextcloud;

import android.os.Handler;
import android.util.Log;

import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.network.OnDatatransferProgressListener;
import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.files.DownloadFileRemoteOperation;

import java.io.File;
import java.util.ArrayList;

public class NextcloudOperationDownloadFile extends NextcloudOperation implements OnRemoteOperationListener, OnDatatransferProgressListener {
    private static String TAG = NextcloudOperationDownloadFile.class.getCanonicalName();

    public interface onDownloadFileFinishedListener {
        void onDownloadFileFinished(String identifier, boolean isSuccesfull);
    }

    private onDownloadFileFinishedListener mListener;

    public NextcloudOperationDownloadFile(String identifier, Handler handler, onDownloadFileFinishedListener listener) {
        super(identifier, handler);
        mListener = listener;
    }

    // File is saved in targetDirectory + filePath (location on Server)
    public void downloadFile(String remoteFilePath, String targetDirectory, OwnCloudClient client) {
        File targetDirFile = new File(targetDirectory);
        DownloadFileRemoteOperation downloadOperation = new DownloadFileRemoteOperation(remoteFilePath, targetDirFile.getAbsolutePath());
        downloadOperation.addDatatransferProgressListener(this);
        downloadOperation.execute(client, this, mHandler);

    }

    @Override
    public void onRemoteOperationFinish(RemoteOperation caller, RemoteOperationResult result) {
        if (caller instanceof DownloadFileRemoteOperation) {
            mListener.onDownloadFileFinished(mIdentifier, result.isSuccess());
        }
        mIsFinished = true;
    }

    @Override
    public void onTransferProgress(long progressRate, long totalTransferredSoFar, long totalToTransfer, String fileAbsoluteName) {

    }
}
