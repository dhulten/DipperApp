package com.example.dipper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
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

                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MM-dd HH:mm");
                    Calendar cal = Calendar.getInstance();
                    Date currentTime = cal.getTime();
                    TimeZone tz = cal.getTimeZone();
                    int timeDiffMs = tz.getOffset(cal.getTimeInMillis());

                    // to do: actually format as JSON for easier parsing
                    String responsePlaintext = new String(responseBody); // for UTF-8 encoding
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    String checkinData = jsonObject.getString("Data");
                    JSONObject checkinObject = new JSONObject(new String(responseBody));
                    JSONArray dateArray = checkinObject.getJSONArray("CheckinDates");

                    // to do: actually use this dateArray instead of clean text
                    
                    String cleanResponseText = responsePlaintext.replace("[", "");
                    cleanResponseText = cleanResponseText.replace("]", "");
                    cleanResponseText = cleanResponseText.replace("\\", "");
                    cleanResponseText = cleanResponseText.replace(",","");
                    String[] checkinTimes = cleanResponseText.split("\"");

                    int textViewCounter = -1;
                    for (int i = 0; i < checkinTimes.length; i++) {
                        if (checkinTimes[i].length() > 0) {
                            textViewCounter++;
                            Date checkinTime = inputFormat.parse(checkinTimes[i]);

                            Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                            utcCal.setTime(checkinTime);
                            utcCal.add(Calendar.MILLISECOND, timeDiffMs);

                            long secs = (currentTime.getTime() - utcCal.getTime().getTime()) / 1000;
                            long hours = secs / 3600;
                            String formattedCheckin = outputFormat.format(utcCal.getTime());

                            String checkinStr = formattedCheckin + " (" + hours + " hrs ago)";
                            textViews[textViewCounter].append(checkinStr);
                        }
                    }
                }
                catch (Exception ex)
                {
                    String a = "a";
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }
}
