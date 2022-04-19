package com.frf.app.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

public class ImageGallery {
    public static ArrayList<String> listOfImGE(Context context){
        Uri uri;
        Cursor cursor;
        int maxSize = 0;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        String ablosutepathOfImage;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        String oreBy = MediaStore.Video.Media.DATE_TAKEN;
        cursor = context.getContentResolver().query(uri, projection, null, null, oreBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        //get folder home
        while (cursor.moveToNext() && maxSize < 100){
            ablosutepathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(ablosutepathOfImage);
           maxSize++;
        }

        maxSize = 0;

        return listOfAllImages;
    }
}
