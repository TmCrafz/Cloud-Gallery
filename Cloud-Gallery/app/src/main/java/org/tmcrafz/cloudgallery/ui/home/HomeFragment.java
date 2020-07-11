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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.owncloud.android.lib.resources.files.model.RemoteFile;

import org.tmcrafz.cloudgallery.R;
import org.tmcrafz.cloudgallery.datahandling.DataHandlerSql;
import org.tmcrafz.cloudgallery.web.nextcloud.NextcloudOperationReadFolder;
import org.tmcrafz.cloudgallery.web.nextcloud.NextcloudWrapper;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements
        NextcloudOperationReadFolder.OnReadFolderFinishedListener {
    private static String TAG = HomeFragment.class.getCanonicalName();

    private EditText mEditTextPath;

    private HomeViewModel homeViewModel;
    private DataHandlerSql mDataHandlerSql;

    private NextcloudWrapper mNextCloudWrapper;

    // Store the folder structure as with path as key and another map with the same data as value
    //private HashMap<String, Object> m_FolderStructure = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        mEditTextPath = root.findViewById(R.id.editText_path);
        mEditTextPath.setText("/Test1/");


        Button buttonShow = root.findViewById(R.id.button_show);
        /*
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
*/
//        mDataHandlerSql = new DataHandlerSql(getActivity());
//        mDataHandlerSql.deleteDatabase(getActivity());
        mDataHandlerSql = new DataHandlerSql(getActivity());
        mDataHandlerSql.insertMediaPath("/Test1/", true);
        mDataHandlerSql.insertMediaPath("/Test2/", false);
//        mMediaPaths = mDataHandlerSql.getAllPaths();
//        for (RemotePath remotePath : mMediaPaths) {
//            Log.d(TAG, "->Path: " + remotePath.path + " show: " + remotePath.show);
//        }

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // ToDO: TMP.
        // Load folder structure
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
        loadCompleteFolderStructure();
    }

    // ToDo: TMP.
    private void loadCompleteFolderStructure() {
        if (mNextCloudWrapper == null) {
            return;
        }
        mNextCloudWrapper.startReadFolder("/", "/", new Handler(), this);
    }

    @Override
    public void onReadFolderFinished(String identifier, boolean isSuccesfull, ArrayList<Object> files) {
        // Load dir and sub dirs recursively
        if (isSuccesfull) {
            Log.d(TAG, "--------------Identifier: " + identifier + "----------------");
            for(Object fileTmp: files) {
                RemoteFile file = (RemoteFile)  fileTmp;
                String mimetype = file.getMimeType();
                //Log.d(TAG, remotePath + ": " + mimetype);
                if (mimetype.equals("DIR")) {
                    String remotePath = file.getRemotePath();
                    //Log.d(TAG, "---DIR");
                    Log.d(TAG, remotePath + ": " + mimetype);
                    // Dont proceed with current path to avoid unlimited recursion
                    if (!remotePath.equals(identifier)) {
                        mNextCloudWrapper.startReadFolder(remotePath, remotePath, new Handler(), this);
                    }
                }
            }
        }
        else {
            Log.e(TAG, "Could not read remote folder with identifier: " + identifier);
        }
        mNextCloudWrapper.cleanOperations();
    }

}
