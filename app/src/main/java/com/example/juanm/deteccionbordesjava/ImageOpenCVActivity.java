package com.example.juanm.deteccionbordesjava;

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
import android.widget.Toast;

import com.example.juanm.deteccionbordesjava.Util.Util;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ImageOpenCVActivity extends AppCompatActivity {
    private int RESULT_LOAD_IMG = 1;
    private String imgDecodableString;
    private ImageView imgOriginal, imgBordes, imgContornos;
    private SeekBar seekBarThreshold1, seekBarThreshold2;
    public int threshold1, threshold2;
    FloatingActionButton fabProcesar;
    Bitmap bitmap= null;

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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fabProcesar = findViewById(R.id.fabProcesar);
        imgBordes = findViewById(R.id.imgBordes);
        imgContornos = findViewById(R.id.imgContornos);


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
                Mat matImage = new Mat();
                Utils.bitmapToMat(BitmapFactory.decodeFile(imgDecodableString), matImage);
                //cargarMatEnImageView(obtenerBordes(matImage), imgView);
                Mat matBordes = Util.obtenerBordes(matImage, threshold1, threshold2);
                Util.cargarMatEnImageView(matBordes, imgBordes, ImageOpenCVActivity.this);
            }
        });

        seekBarThreshold1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                threshold1 = progress;
                Mat matImage = new Mat();
                Utils.bitmapToMat(BitmapFactory.decodeFile(imgDecodableString), matImage);
                Mat matBordes = Util.obtenerBordes(matImage, threshold1, threshold2);
                Util.cargarMatEnImageView(matBordes, imgBordes, ImageOpenCVActivity.this);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        seekBarThreshold2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                threshold2 = progress;
                Mat matImage = new Mat();
                Utils.bitmapToMat(BitmapFactory.decodeFile(imgDecodableString), matImage);
                Mat matBordes = Util.obtenerBordes(matImage, threshold1, threshold2);
                Util.cargarMatEnImageView(matBordes, imgBordes, ImageOpenCVActivity.this);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
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
                imgOriginal.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));
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
