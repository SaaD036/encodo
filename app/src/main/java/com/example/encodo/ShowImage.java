package com.example.encodo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShowImage extends AppCompatActivity {
    private ImageButton previous, next;
    private Button share, delete, decode;
    private ImageView imageView;
    private String currentPhotoDirectory;
    private List<File> fileList;
    private int sizeOFimages, index;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        initComp();

        File imageFolder = new File(currentPhotoDirectory);
        File images[] = imageFolder.listFiles();
        sizeOFimages = images.length;

        for (int i=0; i<sizeOFimages; i++){
            if (images[i].toString().contains("Encoded_")){
                fileList.add(images[i]);
            }
        }

        sizeOFimages = fileList.size();
        if (sizeOFimages > 0){
            File image = fileList.get(0);
            imageUri = FileProvider.getUriForFile(ShowImage.this, "com.example.android.fileprovider", image);

            imageView.setImageURI(imageUri);
        }

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sizeOFimages > 0){
                    index--;
                    if (index < 0){
                        index = sizeOFimages-1;
                    }

                    File image = fileList.get(index);
                    imageUri = FileProvider.getUriForFile(ShowImage.this, "com.example.android.fileprovider", image);

                    imageView.setImageURI(imageUri);
                }
                else {
                    Toast.makeText(ShowImage.this, "No images here", Toast.LENGTH_LONG).show();
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

                    File image = fileList.get(index);
                    imageUri = FileProvider.getUriForFile(ShowImage.this, "com.example.android.fileprovider", image);

                    imageView.setImageURI(imageUri);
                }
                else{
                    Toast.makeText(ShowImage.this, "No images here", Toast.LENGTH_LONG).show();
                }
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    Intent shareFile = new Intent(android.content.Intent.ACTION_SEND);
                    shareFile.setType("*/*");
                    shareFile.putExtra(Intent.EXTRA_STREAM, imageUri);

                    startActivity(Intent.createChooser(shareFile, "Send File"));
                }
                else {
                    Toast.makeText(ShowImage.this, "Please select an image", Toast.LENGTH_LONG).show();
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri == null){
                    Toast.makeText(ShowImage.this, "No images here", Toast.LENGTH_LONG).show();
                }
                else{
                    File file = fileList.get(index);
                    file.delete();
                    fileList.remove(index);
                    sizeOFimages = fileList.size();

                    if (sizeOFimages == 0){
                        imageUri=null;
                        imageView.setImageURI(null);
                        Toast.makeText(ShowImage.this, "No images here", Toast.LENGTH_LONG).show();
                    }
                    else{
                        if (index == sizeOFimages){
                            index=0;
                        }

                        file = fileList.get(index);
                        imageUri = FileProvider.getUriForFile(ShowImage.this, "com.example.android.fileprovider", file);
                        imageView.setImageURI(imageUri);
                    }
                }
            }
        });
    }

    private void initComp(){
        previous = findViewById(R.id.showImage_previousButton);
        next = findViewById(R.id.showImage_nextButton);
        share = findViewById(R.id.showImage_shareButton);
        delete = findViewById(R.id.showImage_deleteButton);
        decode = findViewById(R.id.showImage_decodeButton);

        imageView = findViewById(R.id.showImage_imageView);

        currentPhotoDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        fileList = new ArrayList<>();
        index=0;
        imageUri = null;
    }
}