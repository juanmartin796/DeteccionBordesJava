package com.example.juanm.deteccionbordesjava;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.juanm.deteccionbordesjava.Util.Util;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ImageOpenCVActivity extends AppCompatActivity {
    private int RESULT_LOAD_IMG = 1;
    private String imgDecodableString;
    private ImageView imgOriginal, imgBordes, imgContornos, imgThreshold;
    private SeekBar seekBarThreshold1, seekBarThreshold2, seekBarThresholdProc;
    public int threshold1 = 200, threshold2 = 200, thresholdProc = 45;
    private Bitmap bitmapOriginal;
    private TextView tvResultados;
    FloatingActionButton fabProcesar;
    Bitmap bitmap= null;
    Mat matOriginal;
    Long startTime, endTime;
    private String textoResultados= "";
    private Mat matBordes;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("OpenCVMartin", "OpenCV loaded successfully");
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
        setContentView(R.layout.activity_image_open_cv);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        seekBarThreshold1 = findViewById(R.id.seekBarThreshold1);
        seekBarThreshold2 = findViewById(R.id.seekBarThreshold2);
        seekBarThresholdProc = findViewById(R.id.seekBarThresholdProc);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fabProcesar = findViewById(R.id.fabProcesar);
        imgBordes = findViewById(R.id.imgBordes);
        imgContornos = findViewById(R.id.imgContornos);
        imgThreshold= findViewById(R.id.imgThreshold);
        tvResultados = findViewById(R.id.tvResultados);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });

        fabProcesar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textoResultados="";
                crearBordes();
                crearContornos();
                tvResultados.setText(textoResultados);
                //escribirArchivo(getApplicationContext(), "tiempo.txt", textoResultados);
            }
        });

        seekBarThreshold1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textoResultados ="";
                threshold1 = progress;
                textoResultados+= "\n\n" + imgDecodableString;
                textoResultados+="\nThreshold1: "+ threshold1;
                textoResultados+="\nThresholdProc: "+ thresholdProc;
                textoResultados+="\nThreshold2: "+ threshold2;
                crearBordes();
                crearContornos();
                tvResultados.setText(textoResultados);
                //escribirArchivo(getApplicationContext(), "tiempo.txt", textoResultados);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        seekBarThreshold2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textoResultados="";
                threshold2 = progress;
                textoResultados+= "\n\n" + imgDecodableString;
                textoResultados+="\nThreshold1: "+ threshold1;
                textoResultados+="\nThresholdProc: "+ thresholdProc;
                textoResultados+="\nThreshold2: "+ threshold2;
                crearBordes();
                crearContornos();
                tvResultados.setText(textoResultados);
                //escribirArchivo(getApplicationContext(), "tiempo.txt", textoResultados);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarThresholdProc.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textoResultados="";
                thresholdProc = progress;
                textoResultados+= "\n\n" + imgDecodableString;
                textoResultados+="\nThreshold1: "+ threshold1;
                textoResultados+="\nThresholdProc: "+ thresholdProc;
                textoResultados+="\nThreshold2: "+ threshold2;
                crearBordes();
                crearContornos();
                tvResultados.setText(textoResultados);
                //escribirArchivo(getApplicationContext(), "tiempo.txt", textoResultados);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
    }

    private void crearBordes() {
        startTime();
        Mat matImage = new Mat();
        Utils.bitmapToMat(BitmapFactory.decodeFile(imgDecodableString), matImage);
        matBordes = Util.obtenerBordes(matImage, threshold1, threshold2);
        endTime();
        textoResultados += "\nProc bordes: "+ getTiempoRecorrido() +" mm";

        startTime();
        Util.cargarMatEnImageView(matBordes, imgBordes, ImageOpenCVActivity.this);
        endTime();
        textoResultados+= "\nDibujar bordes: "+ getTiempoRecorrido() + " mm";
    }

    private void crearContornos() {
        startTime();
        Mat tmpGray = new Mat (matOriginal.width(), matOriginal.height(), CvType.CV_8UC1);
        Imgproc.cvtColor(matOriginal, tmpGray, Imgproc.COLOR_RGB2GRAY);

        //Imgproc.threshold(tmpGray, tmpGray, thresholdProc, 255, Imgproc.THRESH_BINARY);
        Imgproc.threshold(tmpGray, tmpGray, thresholdProc, threshold1, Imgproc.THRESH_BINARY);

        Util.cargarMatEnImageView(tmpGray, imgThreshold, ImageOpenCVActivity.this); //HABILITAR SI SE QUIERE VER EL FRAME COMO QUEDA

        Mat matContornos = Util.obtenerContornos(tmpGray, matOriginal); //HABILITAR SI SE QUIERE OBTENER LOS CORTORNOS UTILIZANDO EL THRESHOLD
        //Mat matContornos = Util.obtenerContornos(matBordes, matOriginal);

        endTime();
        textoResultados+= "\nProc contornos: "+ getTiempoRecorrido() +" mm";

        startTime();
        Util.cargarMatEnImageView(matContornos, imgContornos, ImageOpenCVActivity.this);
        endTime();
        textoResultados += "\nDibubar contornos: " + getTiempoRecorrido() + " mm";
        textoResultados+= Util.getResultados();
        escribirArchivo(getApplicationContext(), "tiempo.txt", textoResultados);
    }

    public void escribirArchivo(Context mcoContext,String sFileName, String sBody){
        /*String filename = sFileName;
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(sBody.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        File file = new File(mcoContext.getFilesDir(),"ArchivosGisia");
        if(!file.exists()){
            file.mkdir();
        }

        try{
            File gpxfile = new File(file, sFileName);
            //FileWriter writer = new FileWriter(gpxfile);
            FileOutputStream writer = new FileOutputStream(gpxfile, true);

            writer.write(sBody.getBytes());
            writer.flush();
            writer.close();

        }catch (Exception e){
            e.printStackTrace();

        }
    }

    private void startTime(){
        startTime = System.currentTimeMillis();
    }

    private void endTime(){
        endTime= System.currentTimeMillis();
    }

    private long getTiempoRecorrido(){
        return startTime - endTime;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                imgOriginal = (ImageView) findViewById(R.id.imgOriginal);
                // Set the Image in ImageView after decoding the String
                bitmapOriginal = BitmapFactory.decodeFile(imgDecodableString);
                matOriginal = new Mat();
                Utils.bitmapToMat(bitmapOriginal, matOriginal);
                imgOriginal.setImageBitmap(bitmapOriginal);
                fabProcesar.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }
}
