package org.tmcrafz.cloudgallery.web.nextcloud;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientFactory;
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory;

import org.tmcrafz.cloudgallery.R;

import java.util.ArrayList;
import java.util.Iterator;

public class NextcloudWrapper {

    private static String TAG = NextcloudWrapper.class.getCanonicalName();

    public static NextcloudWrapper wrapper;

    private OwnCloudClient mClient;
    private String mServerUrl;

    private ArrayList<NextcloudOperation> mRunningOperations;

    public NextcloudWrapper(String serverUrl) {
        mServerUrl = serverUrl;
        mRunningOperations = new ArrayList<>();
    }

    public static boolean initializeWrapperWhenNecessary(String serverUrl, String username, String password, Context context) {
        if (wrapper == null) {
            if (serverUrl != "" && username != "" && password != "") {
                wrapper = new NextcloudWrapper(serverUrl);
                wrapper.connect(username, password, context);
                return true;
            }
            else {
                Log.d(TAG, "mNextCloudWrapper Cannot connect to Nextcloud. Server, username or password is not set");
                return false;
            }
        }
        return true;
    }


    public void connect(String username, String password, Context context) {
        Uri serverUri = Uri.parse(mServerUrl);
        mClient = OwnCloudClientFactory.createOwnCloudClient(serverUri, context, true);
        mClient.setCredentials(OwnCloudCredentialsFactory.newBasicCredentials(username, password));
    }

    public void startReadFolder(String path, String identifier, Handler handler, NextcloudOperationReadFolder.OnReadFolderFinishedListener listener) {
        NextcloudOperationReadFolder operation = new NextcloudOperationReadFolder(identifier, handler, listener);
        mRunningOperations.add(operation);
        operation.readFolder(path, mClient);
    }

    public void startDownload(String remoteFilePath, String targetDirectory, String identifier, Handler handler, NextcloudOperationDownloadFile.OnDownloadFileFinishedListener listener) {
        NextcloudOperationDownloadFile operation = new NextcloudOperationDownloadFile(identifier, handler, listener);
        mRunningOperations.add(operation);
        operation.downloadFile(remoteFilePath, targetDirectory, mClient);
    }

    public void startThumbnailDownload(Activity context, String remoteFilePath, String targetDirectory, int size, String identifier, NextcloudOperationDownloadThumbnail.OnDownloadThumbnailFinishedListener listener) {
        NextcloudOperationDownloadThumbnail operation = new NextcloudOperationDownloadThumbnail(identifier, listener);
        mRunningOperations.add(operation);
        operation.downloadThumbnail(context, remoteFilePath, targetDirectory, size, mClient);
    }

    // Delete all finished operations
    public void cleanOperations() {
        Iterator<NextcloudOperation> operationIterator = mRunningOperations.iterator();
        while (operationIterator.hasNext()) {
            NextcloudOperation operation = operationIterator.next();
            if (operation.isFinished()) {
                operationIterator.remove();
            }
        }
    }

}