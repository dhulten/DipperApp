package com.example.dipper;

/**
 * Created by davidh on 4/2/2017.
 */

public class Constants {
    public static final String ApiUrl = "http://52.41.79.4/api/Dipper";
    public static final String Checkin = "Checkin";
    public static final String GetCheckins = "GetCheckins";
    public static final String Action = "Action";
    public static final String Result = "Result";
    public static final String UploadImage = "UploadImage";
    public static final String ImageBytes = "ImageBytes";
    public static final String ImageUrl = "http://52.41.79.4/image.jpg";
    public static final String LocalImageName = "/image.jpg";
    public static final String Data = "Data";
    public static final String DateStr = "DateStr";
    public static final String CheckinMode = "CheckinMode";
    public static final String AdminMode = "AdminMode";
    public static final String UserMode = "UserMode";
    public static final String ShortDateFormat = "MM-dd HH:mm";
    public static final String LongDateFormat = "yyyy-MM-dd HH:mm";
    public static final int AdminAlertTimeMinutes = 1920;// 32 hours allow some wiggle room on user checkin in before push alert
    public static final int UserCheckinWindowMinutes = 1680;// 28 hours checkin should be daily but allow for small changes in time
    public static final String Preferences = "Preferences";
    public static final String UserPushNotificationsKey = "UserPushNotificationsKey";
    public static final String AdminPushNotificationsKey = "AdminPushNotificationsKey";
    public static final String AdminModeKey = "AdminModeKey";
    public static final String UserAlertTimeKey = "UserAlertTimeKey";
    public static final String LastLocalCheckinKey = "LastLocalCheckinKey";
}
