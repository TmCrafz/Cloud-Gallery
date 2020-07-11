package org.tmcrafz.cloudgallery.ui.cloud_folders;

import androidx.lifecycle.ViewModelProviders;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.owncloud.android.lib.resources.files.model.RemoteFile;

import org.tmcrafz.cloudgallery.R;
import org.tmcrafz.cloudgallery.adapters.RecyclerviewFolderBrowserAdapter;
import org.tmcrafz.cloudgallery.web.nextcloud.NextcloudOperationReadFolder;
import org.tmcrafz.cloudgallery.web.nextcloud.NextcloudWrapper;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CloudFolderFragment extends Fragment implements
        NextcloudOperationReadFolder.OnReadFolderFinishedListener,
        RecyclerviewFolderBrowserAdapter.OnLoadFolderData {
    private static String TAG = CloudFolderFragment.class.getCanonicalName();

    private ArrayList<RecyclerviewFolderBrowserAdapter.AdapterItem> mPathData = new ArrayList<RecyclerviewFolderBrowserAdapter.AdapterItem>();
    private final String ABSOLUTE_ROOT_PATH = "/";
    private String mCurrentPath = ABSOLUTE_ROOT_PATH;

    private CloudFolderViewModel mViewModel;
    private RecyclerView mRecyclerViewFolderBrowser;
    private LinearLayoutManager mLayoutManager;
    private RecyclerviewFolderBrowserAdapter mRecyclerViewFolderBrowserAdapter;

    private NextcloudWrapper mNextCloudWrapper;

    public static CloudFolderFragment newInstance() {
        return new CloudFolderFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerViewFolderBrowser.setLayoutManager(mLayoutManager);

        mRecyclerViewFolderBrowserAdapter = new RecyclerviewFolderBrowserAdapter((RecyclerviewFolderBrowserAdapter.OnLoadFolderData) this, mPathData);
        mRecyclerViewFolderBrowser.setAdapter(mRecyclerViewFolderBrowserAdapter);
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
            mPathData.clear();
            //mPathData.add(new RecyclerviewFolderBrowserAdapter.AdapterItem("Back", ""));
            // ToDo: Temporary add entry to come back. Add functionality with androids "back button" instead
            if (!mCurrentPath.equals(ABSOLUTE_ROOT_PATH)) {
                String parent = (new File(mCurrentPath)).getParent();
                mPathData.add(
                        new RecyclerviewFolderBrowserAdapter.AdapterItem.FolderItem(
                                RecyclerviewFolderBrowserAdapter.AdapterItem.TYPE_FOLDER, getString(R.string.text_folder_browser_back), parent));
            }
            for(Object fileTmp: files) {
                RemoteFile file = (RemoteFile)  fileTmp;
                String mimetype = file.getMimeType();
                //Log.d(TAG, remotePath + ": " + mimetype);
                if (mimetype.equals("DIR")) {
                    String remotePath = file.getRemotePath();
                    String name = remotePath;
                    //Log.d(TAG, "remotePath Path: " + remotePath);
                    mPathData.add(
                            new RecyclerviewFolderBrowserAdapter.AdapterItem.FolderItem(
                                    RecyclerviewFolderBrowserAdapter.AdapterItem.TYPE_FOLDER, name, remotePath));
                    // ToDo: Ausnahme für aktuellen Ordner (bpen Eintrag, außerhalb von Adapter?)
                    //if (!remotePath.equals(identifier)) {
                    //    mNextCloudWrapper.startReadFolder(remotePath, remotePath, new Handler(), this);
                    //}
                }
            }
            // Show loaded path in recyclerview adapter
            mRecyclerViewFolderBrowserAdapter.notifyDataSetChanged();
        }
        else {
            Log.e(TAG, "Could not read remote folder with identifier: " + identifier);
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
}
