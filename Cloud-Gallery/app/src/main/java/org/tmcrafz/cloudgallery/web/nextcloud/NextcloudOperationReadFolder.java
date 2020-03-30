package org.tmcrafz.cloudgallery.web.nextcloud;

import android.os.Handler;
import android.util.Log;

import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.files.DownloadFileRemoteOperation;
import com.owncloud.android.lib.resources.files.ReadFolderRemoteOperation;
import com.owncloud.android.lib.resources.files.model.RemoteFile;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class NextcloudOperationReadFolder extends NextcloudOperation implements OnRemoteOperationListener {
    public interface onReadFolderFinishedListener {
        void onReadFolderFinished(String identifier, boolean isSuccesfull, ArrayList<Object> files);
    }

    private static String TAG = NextcloudOperationReadFolder.class.getCanonicalName();
    private onReadFolderFinishedListener mListener;

    public NextcloudOperationReadFolder(String identifier, Handler handler, onReadFolderFinishedListener listener) {
        super(identifier, handler);
        mListener = listener;
    }

    public void readFolder(String path, OwnCloudClient client) {
        ReadFolderRemoteOperation refreshOperation = new ReadFolderRemoteOperation(path);
        refreshOperation.execute(client, this, mHandler);
    }

    @Override
    public void onRemoteOperationFinish(RemoteOperation caller, RemoteOperationResult result) {
        if (caller instanceof ReadFolderRemoteOperation) {
            mListener.onReadFolderFinished(mIdentifier, result.isSuccess(), result.getData());
        }
        mIsFinished = true;
    }
}
