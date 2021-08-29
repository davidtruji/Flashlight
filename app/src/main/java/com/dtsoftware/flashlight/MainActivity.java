package com.dtsoftware.flashlight;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;


public class MainActivity extends AppCompatActivity {

    private final String ERROR_TAG = "FLASHLIGHT_ERROR";
    private final String INFO_TAG = "FLASHLIGHT";

    private final int SDK_VERSION = Build.VERSION.SDK_INT;
    private AdView mAdView;
    private ToggleButton tbSwitchFlashLight;
    private CameraManager cameraManager;
    private String cameraId;
    private boolean hasFlash;
    private Camera camera;
    private android.hardware.Camera.Parameters params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, initializationStatus -> {
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        // Uses OLD camera API in old android SDKs
        if (SDK_VERSION < Build.VERSION_CODES.LOLLIPOP) {

            // First check if device is supporting flashlight or not
            hasFlash = getApplicationContext().getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

            if (!hasFlash)
                showErrorDialog();

            // get the camera
            getCamera();

        } else {
            // Uses CameraX API in new androids SDKs
            hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);


        }


        tbSwitchFlashLight = findViewById(R.id.tbSwitchFlashLight);
        tbSwitchFlashLight.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchFlashLight(isChecked);
        });


    }

    /**
     * Switch the flash light state
     *
     * @param enabled new flash light state
     */
    private void switchFlashLight(boolean enabled) {


        // Uses OLD camera API in old android SDKs
        if (SDK_VERSION < Build.VERSION_CODES.LOLLIPOP) {

            if (enabled)
                turnOnFlash();
            else
                turnOffFlash();


        } else {// Uses CameraX API in new androids SDKs


        }


    }

    // Get the camera
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e(ERROR_TAG, e.getMessage());
            }
        }
    }


    // Turning On flash
    private void turnOnFlash() {
        Log.d(INFO_TAG, "Flash ON");


        if (camera == null || params == null)
            return;

        params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);
        camera.startPreview();

    }


    // Turning Off flash
    private void turnOffFlash() {
        Log.d(INFO_TAG, "Flash OFF");

        if (camera == null || params == null)
            return;

        params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);
        camera.stopPreview();

    }


    private void showErrorDialog() {
        AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                .create();
        alert.setTitle("Error");
        alert.setMessage("Sorry, your device doesn't support flash light!");
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
            // closing the application
            finish();
        });
        alert.show();
    }


}