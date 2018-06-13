package com.example.juanm.deteccionbordesjava.Util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Util {
    private static Bitmap bitmap;

    public static void cargarMatEnImageView(Mat mat, final ImageView imageView, Activity activity ){
        bitmap = bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(mat, bitmap);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    public static Mat obtenerBordes(Mat source, int threshold1, int threshold2){
        Mat matBordes = new Mat(source.size(), CvType.CV_8UC1);
        Imgproc.Canny(source, matBordes, threshold1, threshold2);
        return matBordes;
    }

    public static Mat obtenerContornos(Mat matSource, Mat matDstSobreCualDibujar){
        List<MatOfPoint> contornos= new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(matSource, contornos, hierarchy,  Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
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
        Imgproc.drawContours(matDstSobreCualDibujar, contornos, maxIndex, new Scalar(255, 0, 0), 2);
        return matDstSobreCualDibujar;
    }
}
