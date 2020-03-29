package org.tmcrafz.cloudgallery.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.tmcrafz.cloudgallery.R;
import org.tmcrafz.cloudgallery.web.nextcloud.NextcloudWrapper;

import java.io.File;

public class HomeFragment extends Fragment {
    private static String TAG = HomeFragment.class.getCanonicalName();

    private HomeViewModel homeViewModel;
    private NextcloudWrapper mNextCloudWrapper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText("TEST");
            }
        });
        Log.d(TAG, "CACHE DIR: " + getActivity().getCacheDir());
        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.key_preference_file_key), 0);
        String serverUrl = prefs.getString(getString(R.string.key_preference_server_url), "");
        String username = prefs.getString(getString(R.string.key_preference_username), "");
        String password = prefs.getString(getString(R.string.key_preference_password), "");
        if (serverUrl != "" && username != "" && password != "") {
            mNextCloudWrapper = new NextcloudWrapper(serverUrl);
            mNextCloudWrapper.connect(username, password, getActivity());
            mNextCloudWrapper.readFolder("/Test1/");
            mNextCloudWrapper.readFolder("/Test2/");
            mNextCloudWrapper.startDownload("/Test1/20200226_143040.jpg", new File(getActivity().getCacheDir() + "/tmpfile123.jpg"));
            mNextCloudWrapper.startDownload("/Test1/20200304_181429.jpg", new File(getActivity().getCacheDir() + "/tmpfile543.jpg"));
            mNextCloudWrapper.startDownload("/Test2/Bjorn_S04E20_promo600.jpg", new File(getActivity().getCacheDir() + "/tmpfile2352.jpg"));
        }
        else {
            Log.d(TAG, "mNextCloudWrapper Cannot connect to Nextcloud. Server, username or password is not set");
        }

        return root;
    }
}
