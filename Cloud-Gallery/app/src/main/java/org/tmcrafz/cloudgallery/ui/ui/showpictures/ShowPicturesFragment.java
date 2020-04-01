package org.tmcrafz.cloudgallery.ui.ui.showpictures;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tmcrafz.cloudgallery.R;


public class ShowPicturesFragment extends Fragment {
    private static String TAG = ShowPicturesFragment.class.getCanonicalName();

    public final static String EXTRA_PATH_TO_SHOW = "path_to_show";
    private ShowPicturesViewModel mViewModel;
    private String mPath;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_show_pictures, container, false);
        TextView textViewTxt = root.findViewById(R.id.message);
        textViewTxt.setText(mPath);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ShowPicturesViewModel.class);
        // TODO: Use the ViewModel
    }

}
