package com.example.dipper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.client.utils.DateUtils;
import org.joda.time.Period;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

public class ViewHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(Constants.Action, Constants.GetCheckins);

        client.get(Constants.ApiUrl, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    TextView[] textViews = {
                            (TextView)findViewById(R.id.checkin1),
                            (TextView)findViewById(R.id.checkin2),
                            (TextView)findViewById(R.id.checkin3),
                            (TextView)findViewById(R.id.checkin4),
                            (TextView)findViewById(R.id.checkin5),
                    };

                    SimpleDateFormat inputFormat = new SimpleDateFormat(Constants.LongDateFormat);
                    SimpleDateFormat outputFormat = new SimpleDateFormat(Constants.ShortDateFormat);
                    Calendar cal = Calendar.getInstance();
                    Date currentTime = cal.getTime();
                    TimeZone tz = cal.getTimeZone();
                    int timeDiffMs = tz.getOffset(cal.getTimeInMillis());

                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    String dataStr = jsonObject.getString(Constants.Data);
                    JSONArray dataArr = new JSONArray(dataStr);

                    for (int i = 0; i < dataArr.length(); i++) {
                        JSONObject checkinTimeJson = dataArr.getJSONObject(i);
                        String checkinTimeStr = checkinTimeJson.getString(Constants.DateStr);
                        Date checkinTime = inputFormat.parse(checkinTimeStr);

                        Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        utcCal.setTime(checkinTime);
                        utcCal.add(Calendar.MILLISECOND, timeDiffMs);

                        long secs = (currentTime.getTime() - utcCal.getTime().getTime()) / 1000;
                        long hours = secs / 3600;
                        String formattedCheckin = outputFormat.format(utcCal.getTime());

                        String checkinStr = formattedCheckin + " (" + hours + " hrs ago)";
                        textViews[i].append(checkinStr);
                    }
                }
                catch (Exception ex)
                {
                }

                LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
                linlaHeaderProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
                linlaHeaderProgress.setVisibility(View.GONE);
            }
        });
    }
}
