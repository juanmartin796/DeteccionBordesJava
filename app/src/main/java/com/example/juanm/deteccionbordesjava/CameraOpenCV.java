package com.example.juanm.deteccionbordesjava;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
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
import java.util.List;

public class CameraOpenCV extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private CameraBridgeViewBase mOpenCvCameraView;
    private ImageView ivFotogramaOriginal, ivFotogramaContornos;
    private SeekBar sbCanny, sbCanny2;
    int theshold1, theshold2;
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
        //cargarImageViewFotogramaOrig(inputFrame);
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
        Imgproc.findContours(source, contornos, hierarchy,  Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(dstSobreCualDibujar, contornos, -1, new Scalar(255, 0, 0), 0);
        return dstSobreCualDibujar;
    }

    private void cargarImageViewFotogramaOrig(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        bitmap = bitmap.createBitmap(inputFrame.rgba().cols(), inputFrame.rgba().rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(inputFrame.rgba(), bitmap);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ivFotogramaOriginal.setImageBitmap(bitmap);
            }
        });
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
