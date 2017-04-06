package com.example.dipper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

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
        ImageView imageView = (ImageView) findViewById(R.id.uploadImage);

        int maxWidth = 999;
        int maxHeight = 1200;

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

    public void Upload(View view){
        findViewById(R.id.pbHeaderProgress).setVisibility(View.VISIBLE);

        ImageView imageView = (ImageView) findViewById(R.id.uploadImage);
        Bitmap bm = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();

        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(Constants.Action, Constants.UploadImage);

        RequestParams params = new RequestParams(Constants.ImageBytes, encodedImage);

        findViewById(R.id.resultMessage).setVisibility(View.VISIBLE);
        findViewById(R.id.uploadImage).setVisibility(View.INVISIBLE);

        client.post(Constants.ApiUrl, params, new AsyncHttpResponseHandler(){
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ((TextView)findViewById(R.id.resultMessage)).append("Upload success!");
                findViewById(R.id.pbHeaderProgress).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ((TextView)findViewById(R.id.resultMessage)).append(error.getMessage());
                findViewById(R.id.pbHeaderProgress).setVisibility(View.INVISIBLE);
            }
        });
    }

    public void viewSettings(View view){
        Intent i = new Intent(this, AdminSettingsActivity.class);
        startActivity(i);
    }
}
