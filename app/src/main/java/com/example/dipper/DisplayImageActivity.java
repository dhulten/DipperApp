package com.example.dipper;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import cz.msebera.android.httpclient.Header;

public class DisplayImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);

        SharedPreferences settings = getApplicationContext().getSharedPreferences(Constants.Preferences, 0);
        Boolean adminMode = settings.getBoolean(Constants.AdminModeKey, false);
        String checkinMode = adminMode ? Constants.AdminMode : Constants.UserMode;

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(Constants.Action, Constants.Checkin);
        client.addHeader(Constants.CheckinMode, checkinMode);

        client.post(Constants.ApiUrl, null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    findViewById(R.id.displayImage).setVisibility(View.VISIBLE);

                    File cacheDir = getApplicationContext().getCacheDir();
                    File cachedImage = new File(cacheDir.getPath() + Constants.LocalImageName);

                    boolean newImage = false;

                    for (Header h : headers)
                    {
                        if (h.getName().equals(Constants.Result) && h.getValue().equals("True"))
                        {
                            newImage = true;
                            break;
                        }
                    }

                    if (!cachedImage.exists() || newImage)
                    {
                        new DownloadImageTask((ImageView) findViewById(R.id.displayImage))
                                .execute(Constants.ImageUrl);
                    }
                    else
                    {
                        LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
                        linlaHeaderProgress.setVisibility(View.GONE);

                        Bitmap image = BitmapFactory.decodeFile(cachedImage.getAbsolutePath());
                        ImageView imageView = (ImageView)findViewById(R.id.displayImage);
                        imageView.setImageBitmap(image);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    findViewById(R.id.errorMessage1).setVisibility(View.VISIBLE);
                    findViewById(R.id.errorMessage2).setVisibility(View.VISIBLE);
                    findViewById(R.id.displayImage).setVisibility(View.INVISIBLE);
                }
        });
    }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        // CAST THE LINEARLAYOUT HOLDING THE MAIN PROGRESS (SPINNER)
        LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }


        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap resultImage = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                resultImage = BitmapFactory.decodeStream(in);
            } catch (Exception ex) {
                Log.e("Error", ex.getMessage());
                ex.printStackTrace();
            }
            return resultImage;
        }

        protected void onPostExecute(Bitmap result) {
            // HIDE THE SPINNER AFTER LOADING FEEDS
            linlaHeaderProgress.setVisibility(View.GONE);

            // set image url
            bmImage.setImageBitmap(result);

            File cacheDir = getApplicationContext().getCacheDir();
            File newImage = new File(cacheDir.getPath() + Constants.LocalImageName);
            if (newImage.exists()){
                newImage.delete();
            }

            try {
                FileOutputStream out = new FileOutputStream(newImage);
                result.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


