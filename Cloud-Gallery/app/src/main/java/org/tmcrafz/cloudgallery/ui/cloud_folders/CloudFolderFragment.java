package org.tmcrafz.cloudgallery.ui.cloud_folders;

import androidx.lifecycle.ViewModelProviders;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.owncloud.android.lib.resources.files.model.RemoteFile;

import org.tmcrafz.cloudgallery.MainActivity;
import org.tmcrafz.cloudgallery.R;
import org.tmcrafz.cloudgallery.adapters.GalleryItem;
import org.tmcrafz.cloudgallery.adapters.RecyclerviewGalleryBrowserAdapter;
import org.tmcrafz.cloudgallery.datahandling.StorageHandler;
import org.tmcrafz.cloudgallery.web.CloudFunctions;
import org.tmcrafz.cloudgallery.web.nextcloud.NextcloudOperationDownloadThumbnail;
import org.tmcrafz.cloudgallery.web.nextcloud.NextcloudOperationReadFolder;
import org.tmcrafz.cloudgallery.web.nextcloud.NextcloudWrapper;

import java.io.File;
import java.util.ArrayList;

public class CloudFolderFragment extends Fragment implements
        NextcloudOperationReadFolder.OnReadFolderFinishedListener,
        NextcloudOperationDownloadThumbnail.OnDownloadThumbnailFinishedListener,
        RecyclerviewGalleryBrowserAdapter.OnLoadFolderData,
        MainActivity.OnBackPressedListener {
    private static String TAG = CloudFolderFragment.class.getCanonicalName();

    private ArrayList<GalleryItem> mItemData = new ArrayList<GalleryItem>();
    private final String ABSOLUTE_ROOT_PATH = "/";
    private String mCurrentPath = ABSOLUTE_ROOT_PATH;

    private CloudFolderViewModel mViewModel;
    private RecyclerView mRecyclerViewFolderBrowser;
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;
    private RecyclerviewGalleryBrowserAdapter mAdapter;

    private Menu mMenu;

    private NextcloudWrapper mNextCloudWrapper;


    public static CloudFolderFragment newInstance() {
        return new CloudFolderFragment();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.gallery_view_menu, menu);
        mMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_view:
                // Switch from linear to grid layout
                if (mAdapter.getLayoutMode() == RecyclerviewGalleryBrowserAdapter.LAYOUT_MODE_LINEAR) {
                    mMenu.findItem(R.id.action_change_view).setIcon(R.drawable.ic_baseline_view_list_24);
                    mAdapter.setLayoutMode(RecyclerviewGalleryBrowserAdapter.LAYOUT_MODE_GRID);
                    mRecyclerViewFolderBrowser.setLayoutManager(mGridLayoutManager);
                    mRecyclerViewFolderBrowser.setAdapter(mAdapter);
                }
                // Switch from grid layout to linear
                else if (mAdapter.getLayoutMode() == RecyclerviewGalleryBrowserAdapter.LAYOUT_MODE_GRID) {
                    mMenu.findItem(R.id.action_change_view).setIcon(R.drawable.ic_baseline_view_module_grid_24);
                    mAdapter.setLayoutMode(RecyclerviewGalleryBrowserAdapter.LAYOUT_MODE_LINEAR);
                    mRecyclerViewFolderBrowser.setLayoutManager(mLinearLayoutManager);
                    mRecyclerViewFolderBrowser.setAdapter(mAdapter);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use own custom Menu for fragment
        setHasOptionsMenu(true);

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cloud_folder, container, false);

        mRecyclerViewFolderBrowser = root.findViewById(R.id.recyclerView_gallery);
        int layoutMode = RecyclerviewGalleryBrowserAdapter.LAYOUT_MODE_LINEAR;

        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        if (layoutMode == RecyclerviewGalleryBrowserAdapter.LAYOUT_MODE_LINEAR) {
            mRecyclerViewFolderBrowser.setLayoutManager(mLinearLayoutManager);
        }
        else {
            mRecyclerViewFolderBrowser.setLayoutManager(mGridLayoutManager);
        }


        mAdapter = new RecyclerviewGalleryBrowserAdapter(
                (RecyclerviewGalleryBrowserAdapter.OnLoadFolderData) this, mItemData, layoutMode);
        mRecyclerViewFolderBrowser.setAdapter(mAdapter);
        // Turn off animation when item change
        //((SimpleItemAnimator) mRecyclerViewGallery.getItemAnimator()).setSupportsChangeAnimations(false);
        onLoadPathData(mCurrentPath);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CloudFolderViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onReadFolderFinished(String identifier, boolean isSuccesfull, ArrayList<Object> files) {
        if (isSuccesfull) {
            // We replace the new pathes with the old ones
            mItemData.clear();
            // ToDo: thumbnail size in settings and elsewhere (not hardcoded here)
            int thumbnailSizePixel = 1280;
            String localDirectoryPath = StorageHandler.getThumbnailDir(getContext());

            //mPathData.add(new RecyclerviewFolderBrowserAdapter.AdapterItem("Back", ""));
            // ToDo: Temporary add entry to come back. Add functionality with androids "back button" instead
            if (!mCurrentPath.equals(ABSOLUTE_ROOT_PATH)) {
                String parent = (new File(mCurrentPath)).getParent();
                mItemData.add(
                        new GalleryItem.FolderItem(
                                GalleryItem.TYPE_FOLDER, getString(R.string.text_folder_browser_back), parent));
            }
            for(Object fileTmp: files) {
                RemoteFile file = (RemoteFile)  fileTmp;
                String mimetype = file.getMimeType();
                String remotePath = file.getRemotePath();
                //Log.d(TAG, remotePath + ": " + mimetype);
                if (mimetype.equals("DIR")) {
                    String name = remotePath;
                    //Log.d(TAG, "remotePath Path: " + remotePath);
                    mItemData.add(
                            new GalleryItem.FolderItem(
                                    GalleryItem.TYPE_FOLDER, name, remotePath));
                    // ToDo: Ausnahme für aktuellen Ordner (bpen Eintrag, außerhalb von Adapter?)
                    //if (!remotePath.equals(identifier)) {
                    //    mNextCloudWrapper.startReadFolder(remotePath, remotePath, new Handler(), this);
                    //}
                }
                else if (CloudFunctions.isFileSupportedPicture(remotePath)) {
                    // Download Picture
                    // Create local file path (location on disk + absolute path
                    // For example:
                    // remote path: /Test1/test.png, location on disk: /storage/external/CloudGallery
                    // Local file path: /storage/external/CloudGallery/Test1/test.png
                    // The final path
                    String localFilePath = localDirectoryPath + remotePath;
                    Log.d(TAG, "->localFilePath before Add: " + localFilePath);
                    mItemData.add(
                            new GalleryItem.ImageItem(
                                GalleryItem.TYPE_IMAGE, localDirectoryPath, localFilePath, remotePath, false));
                }
            }
            GalleryItem.sortByName(mItemData);
            // Show loaded path in recyclerview adapter
            mAdapter.notifyDataSetChanged();
        }
        else {
            Log.e(TAG, "Could not read remote folder with identifier: " + identifier);
        }
        mNextCloudWrapper.cleanOperations();
    }

    @Override
    public void onDownloadThumbnailFinished(String identifier, boolean isSuccessful) {
        if (isSuccessful) {
            String localFilePath = identifier;
            File file = new File(localFilePath);
            if (file.exists() && file.isFile()) {
                // We note that the file is available now
                GalleryItem.ImageItem.updateDownloadStatusByLocalFilePath(mItemData, localFilePath, true);
                //mGalleryAdapter.notifyDataSetChanged();
                int updatePosition = GalleryItem.ImageItem.getPositionByLocalFilePath(mItemData, localFilePath);
                mAdapter.notifyItemChanged(updatePosition, null);
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
            Log.e(TAG, "Download Thumbnail with identifier failed: " + identifier);
        }
        mNextCloudWrapper.cleanOperations();
    }

    @Override
    public void onLoadPathData(String path) {
        if (mNextCloudWrapper == null) {
            Log.e(TAG, "Can't load cloud path. Nextcloud Wrapper is null");
            return;
        }
        // The new current path will be the one we load
        mCurrentPath = path;
        mNextCloudWrapper.startReadFolder(path, path, new Handler(), this);
    }


    @Override
    public void onBackPressed() {
        // Load parent folder
        // Dont load anything when we are in root path
        if (mCurrentPath != null && !mCurrentPath.equals(ABSOLUTE_ROOT_PATH)) {
            File file = new File(mCurrentPath);
            String parentPath = file.getParent();
            Log.d(TAG, "onBackPressedO Curpath:" + mCurrentPath + " Parent path: " + parentPath);
            onLoadPathData(parentPath);
        }
    }
}
