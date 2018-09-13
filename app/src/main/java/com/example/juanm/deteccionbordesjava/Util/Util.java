package com.example.juanm.deteccionbordesjava.Util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Util {
    static int maxIndex=0;
    static List<MatOfPoint> contornos;
    private static Bitmap bitmap;

    public static void cargarMatEnImageView(Mat mat, final ImageView imageView, Activity activity ){
        bitmap = bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(mat, bitmap);

        /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50, stream);
        byte[] byteArray = stream.toByteArray();
        final Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);*/

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
        contornos= new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(matSource, contornos, hierarchy,  Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
        //Imgproc.drawContours(dstSobreCualDibujar, contornos, -1, new Scalar(255, 0, 0), 0);
        double maxArea=0;
        maxIndex=0;
        double areaContorno;
        for (int i=0; i<contornos.size(); i++){
            areaContorno = Imgproc.contourArea(contornos.get(i));
            if (areaContorno > maxArea){
                maxArea = areaContorno;
                maxIndex = i;
            }
        }
        Mat matTmp= new Mat();
        matDstSobreCualDibujar.copyTo(matTmp);
        Imgproc.drawContours(matTmp, contornos, -1, new Scalar(0, 255, 0), 1);
        Log.e("NUM CONTORNOS", String.valueOf(contornos.size()));
        return matTmp;
    }

    public static String getResultados (){
        String sal="";
        Moments moments = Imgproc.moments(contornos.get(maxIndex));
        double cx = moments.m10 / moments.m00;
        double cy = moments.m01 / moments.m00;

        sal += "\nCentroide: " + cx + " px "+cy+ " px";
        sal += "\nArea: "+Imgproc.contourArea(contornos.get(maxIndex));
        return sal;
    }
}
