package com.example.encodo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainActivity extends AppCompatActivity {
    private ImageButton previous, next;
    private ImageView imageView;
    private LinearLayout captureLayout, shareLayout, decodeLayout, showLayout;
    private boolean flag;
    private String currentPhotoDirectory;
    private int sizeOFimages, index;
    private ImageProcessor imageProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComp();
        File imageFolder = new File(currentPhotoDirectory);
        File images[] = imageFolder.listFiles();
        sizeOFimages = images.length;

        captureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CaptureImage.class);
                startActivity(intent);
            }
        });
        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShareImage.class);
                startActivity(intent);
            }
        });
        decodeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DecodeImage.class);
                startActivity(intent);
            }
        });
        showLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowImage.class);
                startActivity(intent);
            }
        });

        if (sizeOFimages > 0){
            File image = images[0];
            Uri uri = FileProvider.getUriForFile(MainActivity.this, "com.example.android.fileprovider", image);

            imageView.setImageURI(uri);
        }

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sizeOFimages > 0){
                    index--;
                    if (index < 0){
                        index = sizeOFimages-1;
                    }

                    File image = images[index];
                    Uri uri = FileProvider.getUriForFile(MainActivity.this, "com.example.android.fileprovider", image);

                    imageView.setImageURI(uri);
                    Toast.makeText(MainActivity.this, "Previous image displayed", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "No images here", Toast.LENGTH_LONG).show();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sizeOFimages > 0){
                    index++;
                    if (index == sizeOFimages){
                        index = 0;
                    }

                    File image = images[index];
                    Uri uri = FileProvider.getUriForFile(MainActivity.this, "com.example.android.fileprovider", image);

                    imageView.setImageURI(uri);
                    Toast.makeText(MainActivity.this, "Next image displayed", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "No images here", Toast.LENGTH_LONG).show();
                }
            }
        });

//        File folder = imageProcessor.getDirectoryPath();
//        if (!folder.exists()){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                try {
//                    Files.createDirectory(Paths.get(folder.getAbsolutePath()));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            else {
//                boolean r = folder.mkdir();
//            }
//        }
    }

    private void initComp(){
        flag = true;

        captureLayout = findViewById(R.id.main_captureLayout);
        shareLayout = findViewById(R.id.main_shareLayout);
        decodeLayout = findViewById(R.id.main_decodeLayout);
        showLayout = findViewById(R.id.main_showLayout);

        previous = findViewById(R.id.main_previousButton);
        next = findViewById(R.id.main_nextButton);
        imageView = findViewById(R.id.main_imageView);

        imageProcessor = new ImageProcessor();
        index = 0;
        currentPhotoDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
    }
}