package com.jimenabpose.flashlight;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class FlashlightActivity extends AppCompatActivity {

    public static Camera camera = null;
    public static CameraManager cameraManager = null;
    public static String cameraId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashlight);
        this.flashLightOn();
    }

    private void flashLightOn() {
        try {
            if (cameraIsSupported()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                    cameraId = cameraManager.getCameraIdList()[0];
                    cameraManager.setTorchMode(cameraId, true);
                } else if (camera == null) {
                    camera = Camera.open();
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
}
