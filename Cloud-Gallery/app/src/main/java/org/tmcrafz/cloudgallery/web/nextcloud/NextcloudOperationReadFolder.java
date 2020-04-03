package org.tmcrafz.cloudgallery.web.nextcloud;

import android.os.Handler;

import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.files.ReadFolderRemoteOperation;

import java.util.ArrayList;


public class NextcloudOperationReadFolder extends NextcloudOperation implements OnRemoteOperationListener {
    public interface OnReadFolderFinishedListener {
        void onReadFolderFinished(String identifier, boolean isSuccesfull, ArrayList<Object> files);
    }

    private static String TAG = NextcloudOperationReadFolder.class.getCanonicalName();
    private OnReadFolderFinishedListener mListener;

    public NextcloudOperationReadFolder(String identifier, Handler handler, OnReadFolderFinishedListener listener) {
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
