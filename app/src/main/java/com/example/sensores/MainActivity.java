package com.example.sensores;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton vibracion, linterna;
    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private boolean linternaon = false;

    private boolean permiso = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        vibracion = findViewById(R.id.floatingActionButton);
        linterna = findViewById(R.id.floatingActionButton2);

        checkAndRequestPermissions();
        linterna.setEnabled(Boolean.FALSE);


        //Obtenemos las instancias de vibración y camara
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        String cameraId = null;
        try {
            String[] cameraIds = cameraManager.getCameraIdList();
            for (String id : cameraIds) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
                Integer lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    cameraId = id;
                    break;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


        //Configuramos listeners para click

        vibracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(500);


            }
        });

        String finalCameraId = cameraId;

        linterna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!linternaon){
                    try {
                        cameraManager.setTorchMode(finalCameraId, true); // Activar linterna
                        linterna.setImageResource(R.drawable.baseline_flashlight_off_24);
                        linternaon = true;

                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }



                }else{
                    try {
                        cameraManager.setTorchMode(finalCameraId, false); // Desactivar linterna
                        linterna.setImageResource(R.drawable.baseline_flashlight_on_24);
                        linternaon = false;
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

    }


    private void checkAndRequestPermissions() {
        String[] permissions = {Manifest.permission.CAMERA};
        List<String> permissionList = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[0]), REQUEST_CAMERA_PERMISSION);
        } else {
            // Ambos permisos ya están concedidos
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0) {
                boolean cameraPermissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (cameraPermissionGranted) {
                    permiso = true;
                    linterna.setEnabled(Boolean.TRUE);


                }
            }
        }
    }
}