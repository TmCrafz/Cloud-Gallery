package org.tmcrafz.cloudgallery.web.nextcloud;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.nextcloud.common.NextcloudClient;
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
import java.util.Iterator;

public class NextcloudWrapper {
    private static String TAG = NextcloudWrapper.class.getCanonicalName();

    private OwnCloudClient mClient;
    private String mServerUrl;

    private ArrayList<NextcloudOperation> mRunningOperations;

    public NextcloudWrapper(String serverUrl) {
        mServerUrl = serverUrl;
        mRunningOperations = new ArrayList<>();
    }

    public void connect(String username, String password, Context context) {
        Uri serverUri = Uri.parse(mServerUrl);
        mClient = OwnCloudClientFactory.createOwnCloudClient(serverUri, context, true);
        mClient.setCredentials(OwnCloudCredentialsFactory.newBasicCredentials(username, password));
    }

    public void startReadFolder(String path, String identifier, Handler handler, NextcloudOperationReadFolder.onReadFolderFinishedListener listener) {
        NextcloudOperationReadFolder operation = new NextcloudOperationReadFolder(identifier, handler, listener);
        mRunningOperations.add(operation);
        operation.readFolder(path, mClient);
    }
    
    public void startDownload(String filePath, File targetDirectory, String identifier, Handler handler, NextcloudOperationDownloadFile.onDownloadFileFinishedListener listener) {
        NextcloudOperationDownloadFile operation = new NextcloudOperationDownloadFile(identifier, handler, listener);
        mRunningOperations.add(operation);
        operation.downloadFile(filePath, targetDirectory, mClient);
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