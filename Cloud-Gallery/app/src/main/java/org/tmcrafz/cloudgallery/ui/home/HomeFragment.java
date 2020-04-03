package org.tmcrafz.cloudgallery.ui.home;

import android.content.Intent;
import android.os.Bundle;
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

import org.tmcrafz.cloudgallery.R;
import org.tmcrafz.cloudgallery.adapters.GalleryAdapter;
import org.tmcrafz.cloudgallery.datahandling.DataHandlerSql;
import org.tmcrafz.cloudgallery.datahandling.RemotePath;
import org.tmcrafz.cloudgallery.ui.ShowPicturesActivity;
import org.tmcrafz.cloudgallery.web.nextcloud.NextcloudWrapper;

import java.util.ArrayList;
import java.util.Arrays;

public class HomeFragment extends Fragment {
    private static String TAG = HomeFragment.class.getCanonicalName();

    private EditText mEditTextPath;

    private HomeViewModel homeViewModel;
    private DataHandlerSql mDataHandlerSql;

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
}
