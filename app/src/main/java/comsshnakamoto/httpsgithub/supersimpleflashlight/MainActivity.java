package comsshnakamoto.httpsgithub.supersimpleflashlight;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.github.jorgecastilloprz.FABProgressCircle;

import github.nisrulz.lantern.Lantern;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CAMERA = 77;

    private Lantern lantern;
    private boolean isOn = false;
    boolean hasCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Super Simple Flashlight");

        final FABProgressCircle fabProgressCircle = findViewById(R.id.fab_circle);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                
                /* Check if is started, or Ask Permissions*/
                if (!lantern.initTorch()){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                    Toast.makeText(MainActivity.this, "Allow Permissions to continue", Toast.LENGTH_SHORT).show();
                }

                /* Tell it does not have camera */
                if (hasCamera == false){
                    Toast.makeText(MainActivity.this, "You don't have Led Camera", Toast.LENGTH_SHORT).show();
                }

                /* Flash Interruptor */
                if (isOn == false){
                    fabProgressCircle.show();
                    lantern.enableTorchMode(true);
                    Toast.makeText(MainActivity.this, "ON", Toast.LENGTH_SHORT).show();
                    isOn = true;
                } else {
                    fabProgressCircle.hide();
                    lantern.enableTorchMode(false);
                    Toast.makeText(MainActivity.this, "OFF", Toast.LENGTH_SHORT).show();
                    isOn = false;
                }
            }
        });

        /* Inicialize Lantern */
        lantern = new Lantern(this)
                .checkAndRequestSystemPermission()
                .observeLifecycle(this);
        hasCamera = lantern.initTorch();

        /* If init have not been initialize, Android should be higher than Kitkat.
        *  So, ask permission .*/
        if (!lantern.initTorch()){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            Toast.makeText(this, "Allow Permissions to continue", Toast.LENGTH_SHORT).show();
        } else {
            /* Turn on, on Launch */
            lantern.enableTorchMode(true);
            Toast.makeText(MainActivity.this, "ON", Toast.LENGTH_SHORT).show();
            isOn = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            // Retry initializing the Lantern's torch feature
            lantern.checkAndRequestSystemPermission();
            hasCamera = lantern.initTorch();
            if (!lantern.initTorch()) {
                // Camera Permission Denied! Do something.
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        lantern.cleanup();
        super.onDestroy();
    }
}

