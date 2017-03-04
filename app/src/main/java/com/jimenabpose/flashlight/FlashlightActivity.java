package com.jimenabpose.flashlight;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class FlashlightActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static Camera camera = null;
    public static CameraManager cameraManager = null;
    public static String cameraId = null;
    private View mScreenLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashlight);
        mScreenLayout = findViewById(R.id.activity_flashlight);
        setupCamera();
        setupSharedPreferences();

    }

    private void setupCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                cameraId = cameraManager.getCameraIdList()[0];
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), "Exception flashLightOff", Toast.LENGTH_SHORT).show();
            }
        } else if (camera == null) {
            camera = Camera.open();
        }
    }

    private void setupSharedPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        setFlashlighOn(preferences.getBoolean(getString(R.string.pref_light_on_key), getResources().getBoolean(R.bool.pref_light_on_default)));
        setScreenColorFromSharedPreferences(preferences);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void setFlashlighOn(boolean flashlightOn) {
        if (flashlightOn) {
            this.flashLightOn();
        } else {
            this.flashLightOff();
        }
    }

    private void flashLightOn() {
        try {
            if (cameraIsSupported()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cameraManager.setTorchMode(cameraId, true);
                } else if (camera == null) {
                    Parameters p = camera.getParameters();
                    p.setFlashMode(Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(p);
                    camera.startPreview();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception flashLightOn()", Toast.LENGTH_SHORT).show();
        }
    }

    private void flashLightOff() {
        try {
            if (cameraIsSupported()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cameraManager.setTorchMode(cameraId, false);
                } else if (camera != null) {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception flashLightOff", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean cameraIsSupported() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void onBackPressed(){
        flashLightOff();
        super.onBackPressed();
    }

    private void setScreenColorFromSharedPreferences(SharedPreferences sharedPreferences) {
        mScreenLayout.setBackgroundColor(Color.parseColor(sharedPreferences.getString(getString(R.string.pref_screen_color_key), getString(R.string.pref_screen_color_label_white))));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_light_on_key))) {
            setFlashlighOn(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_light_on_default)));
        } else if (key.equals(getString(R.string.pref_screen_color_key))) {
            setScreenColorFromSharedPreferences(sharedPreferences);
        }
    }
}
