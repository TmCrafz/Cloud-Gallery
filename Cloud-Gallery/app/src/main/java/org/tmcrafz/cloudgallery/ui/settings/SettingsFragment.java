package org.tmcrafz.cloudgallery.ui.settings;

import androidx.lifecycle.ViewModelProviders;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.tmcrafz.cloudgallery.R;

public class SettingsFragment extends Fragment {

    private static String TAG = SettingsFragment.class.getCanonicalName();

    private SettingsViewModel mViewModel;
    private EditText mEditTextServer;
    private EditText mEditTextUsername;
    private EditText mEditTextPassword;

    private SharedPreferences mSavedSettings;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mSavedSettings = getActivity().getSharedPreferences(getString(R.string.key_preference_file_key), 0);

        mViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel.serverUrl = mSavedSettings.getString(getString(R.string.key_preference_server_url), "");
        mViewModel.username = mSavedSettings.getString(getString(R.string.key_preference_username), "");
        mViewModel.password = mSavedSettings.getString(getString(R.string.key_preference_password), "");

        mEditTextServer = (EditText) getActivity().findViewById(R.id.edit_text_server);
        mEditTextServer.setText(mViewModel.serverUrl);
        mEditTextUsername = (EditText) getActivity().findViewById(R.id.edit_text_username);
        mEditTextUsername.setText(mViewModel.username);
        mEditTextPassword = (EditText) getActivity().findViewById(R.id.edit_text_password);
        mEditTextPassword.setText(mViewModel.password);

        Button btnSave = getActivity().findViewById(R.id.button_save_settings);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverUrl = mEditTextServer.getText().toString();
                String username = mEditTextUsername.getText().toString();
                String password = mEditTextPassword.getText().toString();
                SharedPreferences.Editor editor = mSavedSettings.edit();
                editor.putString(getString(R.string.key_preference_server_url), serverUrl);
                editor.putString(getString(R.string.key_preference_username), username);
                editor.putString(getString(R.string.key_preference_password), password);
                editor.commit();

                Toast toast = Toast.makeText(getActivity(), R.string.settings_toast_saved ,Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

}
