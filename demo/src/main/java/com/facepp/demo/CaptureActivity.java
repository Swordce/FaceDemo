package com.facepp.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.megvii.awesomedemo.facepp.R;

/**
 * Created by zwj on 2017/9/21.
 */

public class CaptureActivity extends AppCompatActivity {

    private CameraView cameraView;
    private Button btnCapture;
    private ImageView iv_capture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_picture);
        cameraView = (CameraView)findViewById(R.id.camera);
        btnCapture = (Button)findViewById(R.id.btn_capture);
        iv_capture = (ImageView)findViewById(R.id.iv_capture);
        cameraView.start();
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePicture();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        capturePicture();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    private void capturePicture() {
        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);
                Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg,0,jpeg.length);
             //   Glide.with(CaptureActivity.this).load(jpeg).into(iv_capture);
            }
        });
        if(cameraView != null) {
            cameraView.captureImage();
        }
    }
}
