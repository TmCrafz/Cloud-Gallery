package org.tmcrafz.cloudgallery.ui.show_photo;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import org.tmcrafz.cloudgallery.R;
import org.tmcrafz.cloudgallery.adapters.GalleryItem;
import org.tmcrafz.cloudgallery.datahandling.StorageHandler;
import org.tmcrafz.cloudgallery.web.nextcloud.NextcloudOperationDownloadFile;
import org.tmcrafz.cloudgallery.web.nextcloud.NextcloudWrapper;

import java.io.File;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ShowPhotoActivity extends AppCompatActivity implements
        NextcloudOperationDownloadFile.OnDownloadFileFinishedListener {
    private static String TAG = ShowPhotoActivity.class.getCanonicalName();

    public final static String EXTRA_REMOTE_PATH_TO_IMAGE = "remote_path_to_image";

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            //mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    private String mNextcloudUrl;
    private String mNextcloudUsername;
    private String mNextcloudPassword;

    private ImageView mImageView;
    private String mRemotePath;
    // ToDo: Remote Folder, to swipe to next picture (pre loading next and before last picture?


    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.key_preference_file_key), 0);
        mNextcloudUrl = prefs.getString(getString(R.string.key_preference_server_url), "");
        mNextcloudUsername = prefs.getString(getString(R.string.key_preference_username), "");
        mNextcloudPassword = prefs.getString(getString(R.string.key_preference_password), "");
        NextcloudWrapper.initializeWrapperWhenNecessary(
                mNextcloudUrl, mNextcloudUsername, mNextcloudPassword, getApplicationContext());

        setContentView(R.layout.activity_show_photo);
        //supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        getWindow().setStatusBarColor(Color.TRANSPARENT);


        mVisible = true;
        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);



        mImageView = findViewById(R.id.imageView_picture);
        Intent intent = getIntent();
        if (intent != null) {
            mRemotePath = intent.getStringExtra(EXTRA_REMOTE_PATH_TO_IMAGE);
            Log.d(TAG, "RemotePath: " + mRemotePath);
            // ToDo: Use real pricture Data (online this one when nothing else is available until better resolution is loaded)
            String previewTmpPath = StorageHandler.getThumbnailDir(this) + mRemotePath;
            Log.d(TAG, "PreviewPath: " + previewTmpPath);
            // ToDo: Thumbnail resoultion seem to differ from full picture, find way to make loading more smooth
            mImageView.setImageBitmap(BitmapFactory.decodeFile(previewTmpPath));
            // Load full picture to show
            String localFileDir = StorageHandler.getMediaDir(getApplicationContext());
            String identifier = localFileDir + mRemotePath;
            NextcloudWrapper.wrapper.startDownload(
                    mRemotePath, localFileDir, identifier, new Handler(), this);
            // ToDo: load smaller pictures first when big (and dont load full size picture when in Datamode and too big?)
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        NextcloudWrapper.initializeWrapperWhenNecessary(
                mNextcloudUrl, mNextcloudUsername, mNextcloudPassword, getApplicationContext());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        //mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_photo_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDownloadFileFinished(String identifier, boolean isSuccessful) {
        if (isSuccessful) {
            Log.d(TAG, "Identifier: " + identifier);
            String localFilePath = identifier;
            Log.d(TAG, "onDownloadFileFinished successful, local path: " + localFilePath);
            File file = new File(localFilePath);
            if (file.exists() && file.isFile()) {
                // ToDo: handle picture orientation
                mImageView.setImageBitmap(BitmapFactory.decodeFile(localFilePath));
            }
        }
    }
}