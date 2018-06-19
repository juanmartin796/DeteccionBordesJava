package com.example.juanm.deteccionbordesjava;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class CameraOpenCV extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private CameraBridgeViewBase mOpenCvCameraView;
    private ImageView ivFotogramaOriginal, ivFotogramaContornos;
    private SeekBar sbCanny, sbCanny2;
    int theshold1 = 200, theshold2 = 200;
    private TextView tvArea;
    private FloatingActionButton fabPause;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("OpencvBorde", "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    private double area;
    private boolean pause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_open_cv);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        ivFotogramaOriginal = findViewById(R.id.ivFotogramaOriginal);
        ivFotogramaContornos = findViewById(R.id.ivFotogramaContornos);
        sbCanny = findViewById(R.id.seekBarThreshold1);
        sbCanny2 = findViewById(R.id.seekBarThreshold2);
        tvArea= findViewById(R.id.tvArea);
        fabPause = findViewById(R.id.fbPause);

        fabPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pause == true){
                    pause = false;
                    fabPause.setImageResource(android.R.drawable.ic_media_pause);
                    mOpenCvCameraView.enableView();
                } else {
                    pause = true;
                    fabPause.setImageResource(android.R.drawable.ic_media_play);
                    mOpenCvCameraView.disableView();
                }
            }
        });

        sbCanny.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                theshold1 = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sbCanny2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                theshold2 = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {
    }

    Bitmap bitmap= null;
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        cargarMatEnImageView(inputFrame.rgba(), ivFotogramaOriginal);
        Mat matBordes = obtenerBordes(inputFrame.rgba());
        Mat matContornos= obtenerContornos(matBordes, inputFrame.rgba());
        cargarMatEnImageView(matContornos, ivFotogramaContornos);
        return matBordes;
    }

    private Mat obtenerBordes(Mat source){
        Mat matBordes = new Mat(source.size(), CvType.CV_8UC1);
        Imgproc.Canny(source, matBordes, theshold1, theshold2);
        return matBordes;
    }

    private Mat obtenerContornos(Mat source, Mat dstSobreCualDibujar){
        List<MatOfPoint> contornos= new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(source, contornos, hierarchy,  Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
        //Imgproc.drawContours(dstSobreCualDibujar, contornos, -1, new Scalar(255, 0, 0), 0);
        double maxArea=0;
        int maxIndex=0;
        double areaContorno;
        for (int i=0; i<contornos.size(); i++){
            areaContorno = Imgproc.contourArea(contornos.get(i));
            if (areaContorno > maxArea){
                maxArea = areaContorno;
                maxIndex = i;
            }
        }
        Imgproc.drawContours(dstSobreCualDibujar, contornos, maxIndex, new Scalar(255, 0, 0), 2);
        if (contornos.size() >0){
            area = Imgproc.contourArea(contornos.get(0));
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvArea.setText(String.valueOf(area));
            }
        });
        return dstSobreCualDibujar;
    }

    private void cargarMatEnImageView(Mat mat, final ImageView imageView ){
        bitmap = bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });
    }
}
