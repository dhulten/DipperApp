package com.example.dipper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class UploadImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Uri data = (Uri) bundle.get(Intent.EXTRA_STREAM);
        Bitmap myBitmap = null;
        try {
            myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        int maxWidth = 333;
        int maxHeight = 400;

        int heightDiff = myBitmap.getHeight() / maxHeight;
        int widthDiff = myBitmap.getWidth() / maxWidth;

        Bitmap scaledImage = null;

        if (heightDiff > widthDiff)
        {
            scaledImage = Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth() / heightDiff,
                    myBitmap.getHeight() / heightDiff, false);
        }
        else
        {
            scaledImage = Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth() / widthDiff,
                    myBitmap.getHeight() / widthDiff, false);
        }

        imageView.setImageBitmap(scaledImage);
    }
}
