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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DecodeImage extends AppCompatActivity {
    private Button decode, select, save;
    private EditText password;
    private ImageView imageView;
    private Uri imageUri;
    private Bitmap imageBitmap;
    private ImageProcessor imageProcessor;
    private boolean flag, saveFlag;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode_image);

        initComp();

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(DecodeImage.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DecodeImage.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 100);
                } else if (ContextCompat.checkSelfPermission(DecodeImage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DecodeImage.this, new String[]{
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
        decode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri == null){
                    Toast.makeText(DecodeImage.this, "Please select an image", Toast.LENGTH_LONG).show();
                }
                else{
                    if (flag){
                        Toast.makeText(DecodeImage.this, "This image is decoded", Toast.LENGTH_LONG).show();
                    }
                    else{
                        if (password.getText().toString().equals("6969")){
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
                            flag = true;
                        }
                        else{
                            Toast.makeText(DecodeImage.this, "Enter a valid password", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri == null){
                    Toast.makeText(DecodeImage.this, "Please select an image", Toast.LENGTH_LONG).show();
                }
                else if (!flag){
                    Toast.makeText(DecodeImage.this, "Please deocde an image", Toast.LENGTH_LONG).show();
                }
                else if (saveFlag){
                    Toast.makeText(DecodeImage.this, "This image is saved", Toast.LENGTH_LONG).show();
                }
                else {
                    saveDecodedImage();
                    saveFlag=true;
                    Toast.makeText(DecodeImage.this, "Image saved", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initComp(){
        decode = findViewById(R.id.decodeImage_decodeButton);
        select = findViewById(R.id.decodeImage_selectButton);
        save = findViewById(R.id.decodeImage_saveButton);

        password = findViewById(R.id.decodeImage_passwordEditText);
        imageView = findViewById(R.id.decodeImage_imageView);

        imageUri = null;
        imageBitmap = null;
        currentPhotoPath = "";
        imageProcessor = new ImageProcessor();
        flag = false;
        saveFlag = false;
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
            saveFlag=false;
        }
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nameOFimage = "JPEG_" + timeStamp + "_";
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
    private void saveDecodedImage(){
        try {
            File image = createImageFile();

            if (image != null){
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}