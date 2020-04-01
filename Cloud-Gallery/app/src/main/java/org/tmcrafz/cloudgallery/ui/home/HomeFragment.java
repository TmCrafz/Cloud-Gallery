package org.tmcrafz.cloudgallery.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.owncloud.android.lib.resources.files.model.RemoteFile;

import org.tmcrafz.cloudgallery.R;
import org.tmcrafz.cloudgallery.adapters.GalleryAdapter;
import org.tmcrafz.cloudgallery.datahandling.DataHandlerSql;
import org.tmcrafz.cloudgallery.ui.ShowPicturesActivity;
import org.tmcrafz.cloudgallery.web.nextcloud.NextcloudOperationDownloadFile;
import org.tmcrafz.cloudgallery.web.nextcloud.NextcloudOperationReadFolder;
import org.tmcrafz.cloudgallery.web.nextcloud.NextcloudWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class HomeFragment extends Fragment implements NextcloudOperationReadFolder.onReadFolderFinishedListener , NextcloudOperationDownloadFile.onDownloadFileFinishedListener {
    private static String TAG = HomeFragment.class.getCanonicalName();

    private EditText mEditTextPath;
    private RecyclerView mRecyclerViewGallery;
    private GridLayoutManager mGridLayoutManagerGallery;
    private GalleryAdapter mGalleryAdapter;
    private ArrayList<String> mImagePaths;

    private HomeViewModel homeViewModel;
    private NextcloudWrapper mNextCloudWrapper;
    private DataHandlerSql mDataHandlerSql;
    private ArrayList<String> mSupportedExtensions = new ArrayList<String>(Arrays.asList(".jpg", ".jpeg", ".png"));
    private ArrayList<String> mMediaPaths;
    // private Handler mHandlerTmp = new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        mEditTextPath = root.findViewById(R.id.editText_path);
        mEditTextPath.setText("/Test1/");


        Button buttonShow = root.findViewById(R.id.button_show);
        buttonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String path = mEditTextPath.getText().toString();
//                //downloadAndShowPicture(path);
//                loadContentOfFolder(path);
                String path = mEditTextPath.getText().toString();
                Intent intent = new Intent(getActivity(), ShowPicturesActivity.class);
                intent.putExtra(ShowPicturesActivity.EXTRA_PATH_TO_SHOW, path);
                startActivity(intent);
            }
        });

        mRecyclerViewGallery = root.findViewById(R.id.recyclerView_gallery);
        mGridLayoutManagerGallery = new GridLayoutManager(getActivity(), 2);
        mRecyclerViewGallery.setLayoutManager(mGridLayoutManagerGallery);
        mImagePaths = new ArrayList<String>();
        mGalleryAdapter = new GalleryAdapter(getActivity(), mImagePaths);
        mRecyclerViewGallery.setAdapter(mGalleryAdapter);


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

        mDataHandlerSql = new DataHandlerSql(getActivity());
        mDataHandlerSql.insertMediaPath("/Test1/");
        mDataHandlerSql.insertMediaPath("/Test2/");
        mMediaPaths = mDataHandlerSql.getAllPaths();
        for (String path : mMediaPaths) {
            Log.d(TAG, "->Path: " + path);
        }


        return root;
    }

    public void downloadAndShowPicture(String serverPath) {
        Log.d(TAG, "Download path : " + serverPath);
        // Check if file is valid image
        if (serverPath.lastIndexOf(".") == -1) {
            return;
        }
        String fileExtension = serverPath.substring(serverPath.lastIndexOf("."));
        if (!mSupportedExtensions.contains(fileExtension)) {
            return;
        }
        //String filename = path.substring(path.lastIndexOf("/") + 1);
        // Store in cache
        //String downloadedFilePath = getContext().getCacheDir().toString();
        // Store in internal storage
        //String downloadedFilePath = getContext().getFilesDir().toString();
        // Store in external storage
        String downloadedFilePath = getContext().getExternalFilesDir(null).toString() + "/CloudGallery";

        String downloadedFile = downloadedFilePath + serverPath;
        Log.d(TAG, "downloadAndShowPicture serverPath: " + serverPath + " downloadedFilePath: " + downloadedFilePath + " downloadedFile: " + downloadedFile);
        mNextCloudWrapper.startDownload(serverPath, new File(downloadedFilePath), downloadedFile , new Handler(),this);
    }

    public void loadContentOfFolder(String serverPath) {
        mNextCloudWrapper.startReadFolder(serverPath, serverPath, new Handler(), this);
    }

    @Override
    public void onReadFolderFinished(String identifier, boolean isSuccesfull, ArrayList<Object> files) {
        if (isSuccesfull) {
            Log.d(TAG, "HomeFragment onReadFolderFinished Succesfull Identifier: " + identifier);
            for(Object fileTmp: files) {
                RemoteFile file = (RemoteFile)  fileTmp;
                String remotePath = file.getRemotePath();
                downloadAndShowPicture(remotePath);
                Log.d(TAG, "HomeFragment onReadFolderFinished file: " + remotePath);
            }
        }
        else {
            Log.d(TAG, "HomeFragment onReadFolderFinishedListener FAILED");
        }
        mNextCloudWrapper.cleanOperations();
    }

    @Override
    public void onDownloadFileFinished(String identifier, boolean isSuccesfull) {
        if (isSuccesfull) {
            Log.d(TAG, "HomeFragment onDownloadFileFinished Succesfull Identifier: " + identifier);
            File file = new File(identifier);
            if (file.isFile()) {
                Log.d(TAG, "FILE_NOTE: isFile.");
            }
            if (file.isDirectory()) {
                Log.d(TAG, "FILE_NOTE: isDirectory.");
            }
            if (file.exists()) {
                Log.d(TAG, "FILE_NOTE: exists.");
            }
            //mImageViewShow.setImageBitmap(BitmapFactory.decodeFile(identifier));
            mImagePaths.add(identifier);
            mGalleryAdapter.notifyDataSetChanged();
            //mGalleryAdapter.notifyItemInserted(position);
        }
        else {
            Log.d(TAG, "HomeFragment onDownloadFileFinished FAILED");
        }
        mNextCloudWrapper.cleanOperations();
    }
}
