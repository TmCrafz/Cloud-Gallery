package org.tmcrafz.cloudgallery.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.tmcrafz.cloudgallery.R;
import org.tmcrafz.cloudgallery.ui.showpictures.ShowPicturesFragment;


public class ShowPicturesActivity extends AppCompatActivity {
    private static String TAG = ShowPicturesActivity.class.getCanonicalName();
    public final static String EXTRA_PATH_TO_SHOW = "path_to_show";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String path = getIntent().getExtras().getString(EXTRA_PATH_TO_SHOW);

        setContentView(R.layout.activity_show_pictures);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ShowPicturesFragment.newInstance(path))
                    .commitNow();
        }
    }
}
