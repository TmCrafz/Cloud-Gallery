package org.tmcrafz.cloudgallery.web.nextcloud;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.operations.RemoteOperation;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class NextcloudOperationDownloadThumbnail extends NextcloudOperation {
    private static String TAG = NextcloudOperationDownloadThumbnail.class.getCanonicalName();

    public interface OnDownloadThumbnailFinishedListener {
        void onDownloadThumbnailFinished(String identifier, boolean isSuccessful);
    }

    OnDownloadThumbnailFinishedListener mListener;

    public NextcloudOperationDownloadThumbnail(String identifier, OnDownloadThumbnailFinishedListener listener) {
        super(identifier);
        mListener = listener;
    }

    public void downloadThumbnail(String remoteFilePath, String targetDirectory, int size, OwnCloudClient client) {
        DownloadThumbnailTask asyncTask = new DownloadThumbnailTask(mIdentifier, mListener);
        String localFilePath = targetDirectory;
        Log.d(TAG, "downloadThumbnail: remoteFilePath: " + remoteFilePath + " targetDirectory: " + targetDirectory + " size: " + size);
        asyncTask.execute(new TaskParams(remoteFilePath, localFilePath, size, client));
    }

    private class TaskParams {
        public String remoteFilePath;
        public String localFilePath;
        public int sizePixels;
        public OwnCloudClient client;

        public TaskParams(String remoteFilePath, String localFilePath, int sizePixels, OwnCloudClient client) {
            this.remoteFilePath = remoteFilePath;
            this.localFilePath = localFilePath;
            this.sizePixels = sizePixels;
            this.client = client;
        }
    }

    private class DownloadThumbnailTask extends AsyncTask<TaskParams, Void, Boolean> {
        private OnDownloadThumbnailFinishedListener mListener;
        private String mIdentifier;

        public DownloadThumbnailTask(String identifier, OnDownloadThumbnailFinishedListener listener) {
            mListener = listener;
            mIdentifier = identifier;
        }

        @Override
        protected Boolean doInBackground(TaskParams... taskParams) {
            TaskParams params = taskParams[0];
            String remoteFilePath = params.remoteFilePath;
            String localFilePath = params.localFilePath;
            int sizePixels = params.sizePixels;
            OwnCloudClient client = params.client;

            // Uri:  "/index.php/apps/files/api/v1/thumbnail/" + pxW + "/" + pxH + Uri.encode(remoteFilePath, "/");
            String uri = client.getBaseUri() + "/index.php/apps/files/api/v1/thumbnail/" + sizePixels + "/" + sizePixels + Uri.encode(remoteFilePath, "/");
            Log.d(TAG, "downloadThumbnail: generate thumbnail: " + remoteFilePath + " URI: " + uri);
            GetMethod gMethod = new GetMethod(uri);
            gMethod.setRequestHeader("Cookie", "nc_sameSiteCookielax=true;nc_sameSiteCookiestrict=true");
            gMethod.setRequestHeader(RemoteOperation.OCS_API_HEADER, RemoteOperation.OCS_API_HEADER_VALUE);
            try {
                int status = client.executeMethod(gMethod);
                if (status == HttpStatus.SC_OK) {
                    File file = new File(localFilePath + remoteFilePath);
                    InputStream inputStream = gMethod.getResponseBodyAsStream();
                    FileUtils.copyInputStreamToFile(inputStream, file);
                }
                else {
                    client.exhaustResponse(gMethod.getResponseBodyAsStream());
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            boolean isSuccessful = aBoolean.booleanValue();
            mListener.onDownloadThumbnailFinished(mIdentifier, isSuccessful);
        }

    }
}
