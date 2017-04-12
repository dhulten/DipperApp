package com.example.dipper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

/**
 * Created by davidh on 4/7/2017.
 */

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    private static String CheckinReminder = "Check-in Reminder";

    public void onReceive(final Context context, final Intent intent) {

        final NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        SharedPreferences settings = context.getSharedPreferences(Constants.Preferences, 0);
        boolean adminMode = settings.getBoolean(Constants.AdminModeKey, false);

        boolean pushNotifications = adminMode ? settings.getBoolean(Constants.AdminPushNotificationsKey, true)
                : settings.getBoolean(Constants.UserPushNotificationsKey, true);

        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);

        // only send notifications during waking hours
        if (pushNotifications && hour >= 6 && hour < 22) {

            if (adminMode && isNetworkAvailable(context)) {
                AsyncHttpClient client = new AsyncHttpClient();
                client.addHeader(Constants.Action, Constants.GetCheckins);

                client.get(Constants.ApiUrl, null, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {

                            SimpleDateFormat inputFormat = new SimpleDateFormat(Constants.LongDateFormat);
                            SimpleDateFormat outputFormat = new SimpleDateFormat(Constants.ShortDateFormat);
                            Calendar cal = Calendar.getInstance();
                            Date currentTime = cal.getTime();
                            TimeZone tz = cal.getTimeZone();
                            int timeDiffMs = tz.getOffset(cal.getTimeInMillis());

                            JSONObject jsonObject = new JSONObject(new String(responseBody));
                            String dataStr = jsonObject.getString(Constants.Data);
                            JSONArray dataArr = new JSONArray(dataStr);
                            JSONObject mostRecentCheckin = dataArr.getJSONObject(0);

                            String checkinTimeStr = mostRecentCheckin.getString(Constants.DateStr);
                            Date checkinTime = inputFormat.parse(checkinTimeStr);

                            Calendar comparisonCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                            comparisonCalendar.setTime(checkinTime);
                            comparisonCalendar.add(Calendar.MILLISECOND, timeDiffMs);

                            long secsSinceCheckin = (currentTime.getTime() - comparisonCalendar.getTime().getTime()) / 1000;
                            long minutesSinceCheckin = secsSinceCheckin / 60;

                            String formattedCheckin = outputFormat.format(comparisonCalendar.getTime());

                            if (minutesSinceCheckin > Constants.AdminAlertTimeMinutes) {
                                Notification notification = getNotification("Check-in alert", "Last checkin: " + formattedCheckin, context, ViewHistoryActivity.class);
                                int id = intent.getIntExtra(NOTIFICATION_ID, 0);
                                notificationManager.notify(id, notification);
                            }

                            // reset current number of consecutive admin failures to 0
                            SharedPreferences settings = context.getSharedPreferences(Constants.Preferences, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt(Constants.AdminFailuresKey, 0);
                            editor.apply();
                        }
                        catch (Exception ex)
                        {
                            SharedPreferences settings = context.getSharedPreferences(Constants.Preferences, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            int currentFailureCount = settings.getInt(Constants.AdminFailuresKey, 0);

                            if (currentFailureCount > Constants.MaxConsecutiveAdminFailures) {
                                Notification notification = getNotification(currentFailureCount + " consequtive alert failures", ex.getMessage(), context, ViewHistoryActivity.class);
                                int id = intent.getIntExtra(NOTIFICATION_ID, 0);
                                notificationManager.notify(id, notification);
                            }

                            editor.putInt(Constants.AdminFailuresKey, currentFailureCount + 1);
                            editor.apply();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Notification notification = getNotification("Check-in Alert Error", error.getMessage(), context, ViewHistoryActivity.class);
                        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
                        notificationManager.notify(id, notification);
                    }
                });

            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.LongDateFormat);
                Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                Notification notification;
                String lastCheckinStr = settings.getString(Constants.LastLocalCheckinKey, "");

                boolean notificationNeeded = true;

                if (lastCheckinStr.length() == 0) {
                    notification = getNotification(CheckinReminder, "Error getting check-in time, let David know", context, MainActivity.class);
                }
                else {

                    try {
                        Date checkinTime = dateFormat.parse(lastCheckinStr);
                        long secsSinceCheckin = (utcCal.getTime().getTime() - checkinTime.getTime()) / 1000;
                        long minutesSinceCheckin = secsSinceCheckin / 60;

                        int alertWarning = settings.getInt(Constants.UserAlertTimeKey, 60);

                        notificationNeeded = minutesSinceCheckin + alertWarning > Constants.UserCheckinWindowMinutes;
                        long minutesTillCheckinNeeded = Constants.UserCheckinWindowMinutes - minutesSinceCheckin;

                        String checkinMessage = minutesTillCheckinNeeded > 0 ? "Next check-in expected in "
                                + minutesTillCheckinNeeded + " minutes." : "Check-in expected "
                                + -1 * minutesTillCheckinNeeded + " minutes ago.";

                        notification = getNotification(CheckinReminder, checkinMessage, context, MainActivity.class);
                    } catch (Exception ex) {
                        notification = getNotification(CheckinReminder, "Error getting check-in time, let David know", context, MainActivity.class);
                        notificationNeeded = true;
                    }
                }

                // prevents notifications if the time requirements haven't been met
                if (notificationNeeded) {
                    int id = intent.getIntExtra(NOTIFICATION_ID, 0);
                    notificationManager.notify(id, notification);
                }
            }
        }
    }

    private Notification getNotification(String title, String content, Context context, Class targetClass) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.notification);
        Intent notificationIntent = new Intent(context, targetClass);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        builder.setContentIntent(pendingIntent);
        return builder.build();
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
