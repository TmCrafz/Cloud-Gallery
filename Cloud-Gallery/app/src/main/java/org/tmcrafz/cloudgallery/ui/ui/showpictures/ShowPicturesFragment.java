package org.tmcrafz.cloudgallery.ui.ui.showpictures;

import androidx.lifecycle.ViewModelProviders;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.owncloud.android.lib.resources.files.model.RemoteFile;

import org.tmcrafz.cloudgallery.R;
import org.tmcrafz.cloudgallery.adapters.GalleryAdapter;
import org.tmcrafz.cloudgallery.web.CloudFunctions;
import org.tmcrafz.cloudgallery.web.nextcloud.NextcloudOperationDownloadFile;
import org.tmcrafz.cloudgallery.web.nextcloud.NextcloudOperationDownloadThumbnail;
import org.tmcrafz.cloudgallery.web.nextcloud.NextcloudOperationReadFolder;
import org.tmcrafz.cloudgallery.web.nextcloud.NextcloudWrapper;

import java.io.File;
import java.util.ArrayList;


public class ShowPicturesFragment extends Fragment implements
        NextcloudOperationReadFolder.onReadFolderFinishedListener,
        NextcloudOperationDownloadFile.onDownloadFileFinishedListener,
        NextcloudOperationDownloadThumbnail.OnDownloadThumbnailFinishedListener {
    private static String TAG = ShowPicturesFragment.class.getCanonicalName();

    public final static String EXTRA_PATH_TO_SHOW = "path_to_show";
    private ShowPicturesViewModel mViewModel;
    private String mPath;

    private NextcloudWrapper mNextCloudWrapper;
    private RecyclerView mRecyclerViewGallery;
    private GridLayoutManager mGridLayoutManagerGallery;
    private GalleryAdapter mGalleryAdapter;

    private GalleryAdapter.AdapterItems mMediaPaths;

    public static ShowPicturesFragment newInstance(String path) {
        ShowPicturesFragment fragment = new ShowPicturesFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_PATH_TO_SHOW, path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPath = getArguments().getString(EXTRA_PATH_TO_SHOW);
        }

        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.key_preference_file_key), 0);
        String serverUrl = prefs.getString(getString(R.string.key_preference_server_url), "");
        String username = prefs.getString(getString(R.string.key_preference_username), "");
        String password = prefs.getString(getString(R.string.key_preference_password), "");
        if (serverUrl != "" && username != "" && password != "") {
            mNextCloudWrapper = new NextcloudWrapper(serverUrl);
            mNextCloudWrapper.connect(username, password, getActivity());
        }
        else {
            Log.d(TAG, "mNextCloudWrapper Cannot connect to Nextcloud. Server, username or password is not set");
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_show_pictures, container, false);

        mRecyclerViewGallery = root.findViewById(R.id.recyclerView_gallery);
        mGridLayoutManagerGallery = new GridLayoutManager(getActivity(), 2);
        mRecyclerViewGallery.setLayoutManager(mGridLayoutManagerGallery);
        mMediaPaths = new GalleryAdapter.AdapterItems();
        mGalleryAdapter = new GalleryAdapter(getActivity(), mMediaPaths);
        mRecyclerViewGallery.setAdapter(mGalleryAdapter);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ShowPicturesViewModel.class);

        String remoteFilePath = "/Test1/file4.jpg";
        String targetDirectory = getContext().getExternalFilesDir(null).toString() + "/CloudGallery/cache/thumbnails";
        // identifier = localFilePath
        String identifier = targetDirectory + remoteFilePath;
        int thumbnailSizePixel = 128;
        //mNextCloudWrapper.downloadThumbnail("/Test1/file4.jpg", getContext().getExternalFilesDir(null).toString() + "/CloudGallery/cache/thumbnails/Test1/file4.jpg");
        mNextCloudWrapper.startThumbnailDownload(remoteFilePath, targetDirectory, thumbnailSizePixel, identifier, this);


        mNextCloudWrapper.startReadFolder(mPath, mPath, new Handler(), this);

        // TODO: Use the ViewModel
    }

    @Override
    public void onReadFolderFinished(String identifier, boolean isSuccesfull, ArrayList<Object> files) {
        if (isSuccesfull) {
            String localDirectoryPath = getContext().getExternalFilesDir(null).toString() + "/CloudGallery";
            for(Object fileTmp: files) {
                RemoteFile file = (RemoteFile)  fileTmp;
                String remoteFilePath = file.getRemotePath();
                if (CloudFunctions.isFileSupportedPicture(remoteFilePath)) {
                    // Download Picture
                    // Create local file path (location on disk + absolute path
                    // For example:
                    // remote path: /Test1/test.png, location on disk: /storage/external/CloudGallery
                    // Local file path: /storage/external/CloudGallery/Test1/test.png
                    // The final path
                    String localFilePath = localDirectoryPath + remoteFilePath;
                    Log.d(TAG, "->localFilePath before Add: " + localFilePath);
                    mMediaPaths.add(localDirectoryPath, localFilePath, remoteFilePath, false);;
                }
            }
            // Start download files
            for(GalleryAdapter.AdapterItems.Item item : mMediaPaths.getArrayList()) {
                String localFilePath = item.localFilePath;
                String remoteFilePath = item.remotePath;
                String identifierDownload = localFilePath;
                Log.d(TAG, "->Start Downloads Local File Path: " + localFilePath + " remoteFilePath: " + remoteFilePath + " identifierDownload " + identifierDownload);
                // Start downloading file
                mNextCloudWrapper.startDownload(remoteFilePath, localDirectoryPath, identifierDownload , new Handler(),this);
            }
            mGalleryAdapter.notifyDataSetChanged();
        }
        else {
            Log.e(TAG, "Could not read remote folder with identifier: " + identifier);
        }
        mNextCloudWrapper.cleanOperations();
    }

    @Override
    public void onDownloadFileFinished(String identifier, boolean isSuccesfull) {
        if (isSuccesfull) {
            String localFilePath = identifier;
            File file = new File(localFilePath);
            if (file.exists() && file.isFile()) {
                // We note that the file is available now
                mMediaPaths.updateDownloadStatusByLocalFilePath(localFilePath, true);
                //mGalleryAdapter.notifyDataSetChanged();
                int updatePosition = mMediaPaths.getPositionByLocalFilePath(localFilePath);
                Log.d(TAG, "->Local File Path: " + localFilePath + " position: " + updatePosition);
                mGalleryAdapter.notifyItemChanged(updatePosition);
                //mGalleryAdapter.notifyDataSetChanged();
            }
            else {
                Log.e(TAG, "Showing downloaded file with identifier '" + identifier +"' failed. File is not existing or directory");
                if (!file.exists()) {
                    Log.e(TAG, "-->File is not existing");
                }
                if (!file.isFile()) {
                    Log.e(TAG, "-->Not a file");
                }
            }
        }
        else {
            Log.e(TAG, "Download of file with identifier failed: " + identifier);
        }
        mNextCloudWrapper.cleanOperations();
    }

    @Override
    public void onDownloadThumbnailFinished(String identifier, boolean isSuccesfull) {
        if (isSuccesfull) {
            Log.d(TAG, "Succesdully downloaded thumbnail with identifier: " + identifier);
        }
        else {
            Log.e(TAG, "Error downloading thumbnail with identifier: " + identifier);
        }
    }
}
