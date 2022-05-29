package com.example.encodo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CaptureImage extends AppCompatActivity {
    private ImageView imageView, imageView1;
    private Button capture, decode, send;
    private TextView textView;
    private EditText passwordText;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath, currentPhotoDirectory, nameOFimage;
    private List<Integer> pixel;
    private ImageProcessor imageProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);

        initComp();

        if(ContextCompat.checkSelfPermission(CaptureImage.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CaptureImage.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        decode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordText.getText().toString();

                if (password.equals("6969")){
                    decodeImage();
                }
                else {
                    textView.setText("Hei, you have entered a wrong password");
                }
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPhotoPath != "") {
                    File image = new File(currentPhotoPath);
                    Uri uri = FileProvider.getUriForFile(CaptureImage.this, "com.example.android.fileprovider", image);

                    if (uri != null) {
                        Intent shareFile = new Intent(android.content.Intent.ACTION_SEND);
                        shareFile.setType("*/*");
                        shareFile.putExtra(Intent.EXTRA_STREAM, uri);

                        startActivity(Intent.createChooser(shareFile, "Send File"));
                    }
                }
                else {
                    Toast.makeText(CaptureImage.this, "No image is captured", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private  void initComp(){
        imageView = findViewById(R.id.captureImage_imageView);
        passwordText = findViewById(R.id.captureImage_password);
        //imageView1 = findViewById(R.id.imageView1);

        capture = findViewById(R.id.captureImage_captureButton);
        decode = findViewById(R.id.captureImage_decodeButton);
        send = findViewById(R.id.captureImage_sendButton);

        textView = findViewById(R.id.captureImage_textView);
        pixel = new ArrayList<>();
        currentPhotoPath = "";
        imageProcessor = new ImageProcessor();
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            //create a signature of file
            File image = createImageFile();

            if (image != null){
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", image);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } catch (IOException e) {
            Toast.makeText(CaptureImage.this, "Vejal", Toast.LENGTH_LONG);
            e.printStackTrace();
        }
    }
    private void decodeImage(){
        if (currentPhotoPath.equals("")){
            Toast.makeText(CaptureImage.this, "No image is taken", Toast.LENGTH_LONG).show();
            return;
        }

        File image = new File(currentPhotoPath);
        if (!image.exists()){
            Toast.makeText(CaptureImage.this, "No image is taken", Toast.LENGTH_LONG).show();
            return;
        }

        textView.setText("Processing...");
        Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
        imageBitmap = imageBitmap.copy( Bitmap.Config.ARGB_8888 , true);

        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();

        //decoding starts
        int division = height/8;
        for(int i=0, start=0, end=division; i<8; i++){
            imageBitmap = imageProcessor.encodeImage_height(imageBitmap, width, start, end);
            start += division;
            end += division;
        }
        division = width/8;
        for(int i=0, start=0, end=division; i<8; i++){
            imageBitmap = imageProcessor.encodeImage_width(imageBitmap, start, end, height);
            start += division;
            end += division;
        }
        imageBitmap = imageProcessor.deColor(imageBitmap, width, height);
        //decoding ends

        imageView.setImageBitmap(imageBitmap);

        textView.setText("Decoded");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            File image = new File(currentPhotoPath);
            Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            imageBitmap = imageBitmap.copy( Bitmap.Config.ARGB_8888 , true);

            int height = imageBitmap.getHeight();
            int width = imageBitmap.getWidth();

            //encoding starts
            imageBitmap = imageProcessor.enColor(imageBitmap, width, height);

            int division = width/8;
            for(int i=0, start=0, end=division; i<8; i++){
                imageBitmap = imageProcessor.encodeImage_width(imageBitmap, start, end, height);
                start += division;
                end += division;
            }
            division = height/8;
            for(int i=0, start=0, end=division; i<8; i++){
                imageBitmap = imageProcessor.encodeImage_height(imageBitmap, width, start, end);
                start += division;
                end += division;
            }
            //encoding ends

            imageView.setImageBitmap(imageBitmap);

            //saving encrypted images
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 0 , bos);
            byte[] bitmapData = bos.toByteArray();

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(image);
                fos.write(bitmapData);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //saving to gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(image));
            this.sendBroadcast(mediaScanIntent);
        }
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nameOFimage = "Encoded_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                nameOFimage,
                ".PNG",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}