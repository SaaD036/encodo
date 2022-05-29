package com.example.encodo;

import androidx.annotation.Nullable;
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

public class ShareImage extends AppCompatActivity {
    private Button select, send, encode, delete;
    private TextView pathText;
    private ImageView imageView;
    private Uri imageUri;
    private ImageProcessor imageProcessor;
    private Bitmap imageBitmap;
    private boolean flag;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_image);

        initComp();

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ShareImage.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShareImage.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 100);
                } else if (ContextCompat.checkSelfPermission(ShareImage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShareImage.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 100);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(Intent.createChooser(intent, "Select an image"), 10);
                }
            }
        });
        encode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri == null){
                    Toast.makeText(ShareImage.this, "No image is chosen", Toast.LENGTH_LONG).show();
                }
                else if (flag){
                    Toast.makeText(ShareImage.this, "Image is already encoded", Toast.LENGTH_LONG).show();
                }
                else{
                    int width = imageBitmap.getWidth();
                    int height = imageBitmap.getHeight();

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
                    flag = true;

                    saveEncodedImage();
                }
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag){
                    if (imageUri != null) {
                        Intent shareFile = new Intent(android.content.Intent.ACTION_SEND);
                        shareFile.setType("*/*");
                        shareFile.putExtra(Intent.EXTRA_STREAM, imageUri);

                        startActivity(Intent.createChooser(shareFile, "Send File"));
                        delete.setVisibility(View.VISIBLE);
                    }
                    else {
                        Toast.makeText(ShareImage.this, "No image is chosen", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(ShareImage.this, "Please encode image first", Toast.LENGTH_LONG).show();
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File image = new File(currentPhotoPath);
                image.delete();
                delete.setVisibility(View.GONE);
            }
        });
    }

    private void initComp() {
        select = findViewById(R.id.shareImage_selectButton);
        send = findViewById(R.id.shareImage_sendButton);
        encode = findViewById(R.id.shareImage_encodeButton);
        delete = findViewById(R.id.shareImage_deleteButton);
        pathText = findViewById(R.id.shareImage_pathTextview);
        imageView = findViewById(R.id.shareImage_imageView);

        imageUri = null;
        imageBitmap = null;
        currentPhotoPath = "";
        imageProcessor = new ImageProcessor();
        flag = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10 && resultCode == RESULT_OK) {
            imageUri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageBitmap = imageBitmap.copy( Bitmap.Config.ARGB_8888 , true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageView.setImageBitmap(imageBitmap);
            flag = false;
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
    private void saveEncodedImage(){
        try {
            File image = createImageFile();

            if (image != null){
                //saving encrypted images
                image = new File(currentPhotoPath);
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

                imageUri = FileProvider.getUriForFile(ShareImage.this, "com.example.android.fileprovider", new File(currentPhotoPath));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}