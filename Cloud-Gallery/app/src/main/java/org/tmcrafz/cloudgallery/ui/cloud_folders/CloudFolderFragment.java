package org.tmcrafz.cloudgallery.ui.cloud_folders;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tmcrafz.cloudgallery.R;

public class CloudFolderFragment extends Fragment {

    private CloudFolderViewModel mViewModel;

    public static CloudFolderFragment newInstance() {
        return new CloudFolderFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cloud_folder, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CloudFolderViewModel.class);
        // TODO: Use the ViewModel
    }

}
