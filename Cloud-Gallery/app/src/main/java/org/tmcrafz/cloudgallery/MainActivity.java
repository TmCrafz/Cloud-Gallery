package org.tmcrafz.cloudgallery;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.tmcrafz.cloudgallery.ui.com_interfaces.OnChangeActionBarTitle;
import org.tmcrafz.cloudgallery.web.nextcloud.NextcloudWrapper;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        OnChangeActionBarTitle {

    public interface OnBackPressedListener {
        void onBackPressed();
    }

    private static String TAG = MainActivity.class.getCanonicalName();
    private AppBarConfiguration mAppBarConfiguration;

    private String mNextcloudUrl;
    private String mNextcloudUsername;
    private String mNextcloudPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.key_preference_file_key), 0);
        mNextcloudUrl = prefs.getString(getString(R.string.key_preference_server_url), "");
        mNextcloudUsername = prefs.getString(getString(R.string.key_preference_username), "");
        mNextcloudPassword = prefs.getString(getString(R.string.key_preference_password), "");
        NextcloudWrapper.initializeWrapperWhenNecessary(
                mNextcloudUrl, mNextcloudUsername, mNextcloudPassword, getApplicationContext());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_cloud_folders, R.id.nav_albums, R.id.nav_slideshow, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NextcloudWrapper.initializeWrapperWhenNecessary(
                mNextcloudUrl, mNextcloudUsername, mNextcloudPassword, getApplicationContext());
        Log.d(TAG, "onResume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment).getChildFragmentManager().getFragments();
        for(Fragment fragment: fragments) {
            if (fragment != null && fragment instanceof OnBackPressedListener) {
                ((OnBackPressedListener)fragment).onBackPressed();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public void OnChangActionbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
