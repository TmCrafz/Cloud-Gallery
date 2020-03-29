package org.tmcrafz.cloudgallery.web.nextcloud;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientFactory;
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory;
import com.owncloud.android.lib.common.network.OnDatatransferProgressListener;
import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.files.DownloadFileRemoteOperation;
import com.owncloud.android.lib.resources.files.FileUtils;
import com.owncloud.android.lib.resources.files.ReadFolderRemoteOperation;
import com.owncloud.android.lib.resources.files.model.RemoteFile;

import java.io.File;
import java.util.ArrayList;

public class NextcloudWrapper implements OnRemoteOperationListener, OnDatatransferProgressListener {
    private static String TAG = NextcloudWrapper.class.getCanonicalName();

    private OwnCloudClient mClient;
    private Handler mHandler;

    private String mServerUrl = null;

    public NextcloudWrapper(String serverUrl) {
        mServerUrl = serverUrl;
        mHandler = new Handler();
    }

    public void connect(String username, String password, Context context) {
        Uri serverUri = Uri.parse(mServerUrl);
        mClient = OwnCloudClientFactory.createOwnCloudClient(serverUri, context, true);
        mClient.setCredentials(OwnCloudCredentialsFactory.newBasicCredentials(username, password));
    }

    public void readFolder(String path) {
        ReadFolderRemoteOperation refreshOperation = new ReadFolderRemoteOperation(path);
        refreshOperation.execute(mClient, this, mHandler);
    }

    public void startDownload(String filePath, File targetDirectory) {
        DownloadFileRemoteOperation downloadOperation = new DownloadFileRemoteOperation(filePath, targetDirectory.getAbsolutePath());
        downloadOperation.addDatatransferProgressListener(this);
        downloadOperation.execute( mClient, this, mHandler);

    }

    @Override
    public void onRemoteOperationFinish(RemoteOperation caller, RemoteOperationResult result) {
        if (caller instanceof ReadFolderRemoteOperation) {
            if (result.isSuccess()) {
                Log.d(TAG, "onRemoteOperationFinish ReadFolderRemoteOperation result.isSuccess() TRUE, Code: " + result.getCode());
                ArrayList<Object> files = result.getData();
                for(Object fileTmp: files) {
                    RemoteFile file = (RemoteFile)  fileTmp;
                    String remotePath = file.getRemotePath();
                    Log.d(TAG, "onRemoteOperationFinish ReadFolderRemoteOperation file: " + remotePath);
                }
            }
            else {
                Log.d(TAG, "onRemoteOperationFinish ReadFolderRemoteOperation result.isSuccess() FALSE, Code: " + result.getCode());
            }
        }
        else if (caller instanceof DownloadFileRemoteOperation) {
            if (result.isSuccess()) {
                Log.d(TAG, "onRemoteOperationFinish DownloadFileRemoteOperation result.isSuccess() TRUE, Code: " + result.getCode() + " Addition: " + result.toString());
            }
            else {
                Log.d(TAG, "onRemoteOperationFinish DownloadFileRemoteOperation result.isSuccess() FALSE, Code: " + result.getCode());
            }
        }
        else {
            Log.e(TAG, "onRemoteOperationFinish no matching instance: " + result.getLogMessage(), result.getException());
        }
    }

    @Override
    public void onTransferProgress(long progressRate, long totalTransferredSoFar, long totalToTransfer, String fileAbsoluteName) {

    }
}
