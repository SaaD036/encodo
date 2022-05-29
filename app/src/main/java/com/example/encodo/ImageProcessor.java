package com.example.encodo;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;

import java.io.File;

public class ImageProcessor {
    public Bitmap encodeImage_width(Bitmap imageBitmap, int start_width, int end_width, int height){
        int middle_width = (start_width+end_width)/2;

        for (int i=start_width, k=middle_width; k<end_width; i++, k++){
            for (int j=0; j<height; j++){
                int tmp = imageBitmap.getPixel(i, j);
                imageBitmap.setPixel(i, j, imageBitmap.getPixel(k, j));
                imageBitmap.setPixel(k, j, tmp);
            }
        }

        return imageBitmap;
    }
    public Bitmap encodeImage_height(Bitmap imageBitmap, int width, int start_height, int end_height){
        int middle_height = (start_height+end_height)/2;

        for (int i=0; i<width; i++){
            for (int j=start_height, k=middle_height; k<end_height; j++, k++){
                int tmp = imageBitmap.getPixel(i, j);
                imageBitmap.setPixel(i, j, imageBitmap.getPixel(i, k));
                imageBitmap.setPixel(i, k, tmp);
            }
        }

        return imageBitmap;
    }
    public Bitmap enColor(Bitmap imageBitmap, int width, int height){
        for (int i=0; i<width; i++){
            for (int j=0; j<height; j++){
                if(i%7 == 0){
                    imageBitmap.setPixel(i, j, (imageBitmap.getPixel(i, j)+ Color.WHITE)/2);
                }
                else if(i%7 == 1){
                    imageBitmap.setPixel(i, j, (imageBitmap.getPixel(i, j)+Color.RED)/2);
                }
                else if(i%7 == 2){
                    imageBitmap.setPixel(i, j, (imageBitmap.getPixel(i, j)+Color.GRAY)/2);
                }
                else if(i%7 == 3){
                    imageBitmap.setPixel(i, j, (imageBitmap.getPixel(i, j)+Color.BLUE)/2);
                }
                else if(i%7 == 4){
                    imageBitmap.setPixel(i, j, (imageBitmap.getPixel(i, j)+Color.GREEN)/2);
                }
                else if(i%7 == 5){
                    imageBitmap.setPixel(i, j, (imageBitmap.getPixel(i, j)+Color.DKGRAY)/2);
                }
                else{
                    imageBitmap.setPixel(i, j, (imageBitmap.getPixel(i, j)+Color.LTGRAY)/2);
                }
            }
        }

        return imageBitmap;
    }
    public Bitmap deColor(Bitmap imageBitmap, int width, int height){
        for (int i=0; i<width; i++){
            for (int j=0; j<height; j++){
                if(i%7 == 0){
                    imageBitmap.setPixel(i, j, imageBitmap.getPixel(i, j)*2-Color.WHITE);
                }
                else if(i%7 == 1){
                    imageBitmap.setPixel(i, j, imageBitmap.getPixel(i, j)*2-Color.RED);
                }
                else if(i%7 == 2){
                    imageBitmap.setPixel(i, j, imageBitmap.getPixel(i, j)*2-Color.GRAY);
                }
                else if(i%7 == 3){
                    imageBitmap.setPixel(i, j, imageBitmap.getPixel(i, j)*2-Color.BLUE);
                }
                else if(i%7 == 4){
                    imageBitmap.setPixel(i, j, imageBitmap.getPixel(i, j)*2-Color.GREEN);
                }
                else if(i%7 == 5){
                    imageBitmap.setPixel(i, j, imageBitmap.getPixel(i, j)*2-Color.DKGRAY);
                }
                else{
                    imageBitmap.setPixel(i, j, imageBitmap.getPixel(i, j)*2-Color.LTGRAY);
                }
            }
        }

        return imageBitmap;
    }

    public File getDirectoryPath(){
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+"Bor_and_Bou");
        return folder;
    }
}
