package io.esper.roloi;

import static android.provider.CalendarContract.Instances.query;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.time.Duration;
import java.util.Arrays;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.core.app.ActivityCompat;


public class Utility {


    public static HashMap<LocalDate, EventInfo> localDateHashMap = new HashMap<>();

    public static HashMap<LocalDate, EventInfo> readCalendarEvent(Context context, LocalDate mintime, LocalDate maxtime) {

        int f = 1;
        String selection = "(( " + CalendarContract.Instances.SYNC_EVENTS + " = " + f + " ) AND ( " + CalendarContract.Instances.DTSTART + " >= " + mintime.toDateTimeAtStartOfDay().getMillis() + " ) AND ( " + CalendarContract.Instances.DTSTART + " <= " + maxtime.toDateTimeAtStartOfDay().getMillis() + " ))";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        final Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/instances");

//        String[] projection =
//                {
//                        CalendarContract.Instances.EVENT_ID,
//                        CalendarContract.Instances.TITLE,
//                        CalendarContract.Instances.DESCRIPTION,
//                        CalendarContract.Instances.DTSTART,
//                        CalendarContract.Instances.DTEND,
//                        CalendarContract.Instances.EVENT_LOCATION,
//                        CalendarContract.Instances.CALENDAR_DISPLAY_NAME,
//                        CalendarContract.Instances.ALL_DAY,
//                        CalendarContract.Instances.EVENT_COLOR,
//                        CalendarContract.Instances.CALENDAR_COLOR,
//                        CalendarContract.Instances.EVENT_TIMEZONE,
//                        CalendarContract.Instances.DURATION
//                };
//
//        Cursor cursor = null;
//        ContentResolver cr = context.getContentResolver();
//        Uri uri = CalendarContract.Calendars.CONTENT_URI;
//        //String selection = "(( " + CalendarContract.Instances.SYNC_EVENTS + " = " + f + " ) AND ( " + CalendarContract.Instances.DTSTART + " >= " + mintime.toDateTimeAtStartOfDay().getMillis() + " ) AND ( " + CalendarContract.Instances.DTSTART + " <= " + maxtime.toDateTimeAtStartOfDay().getMillis() + " ))";
//        String[] selectionArgs = {"nid.lilly@gmail.com", "nid.lilly@gmail.com", "nid.lilly@gmail.com"};
//
//        cursor = cr.query(uri, projection, selection, selectionArgs, null);





        Cursor cursor = context.getContentResolver().query(
                CalendarContract.Events.CONTENT_URI,
                new String[]{"_id", "title", "description",
                        "dtstart", "dtend", "eventLocation", "calendar_displayName", CalendarContract.Instances.ALL_DAY, CalendarContract.Instances.EVENT_COLOR, CalendarContract.Instances.CALENDAR_COLOR, CalendarContract.Instances.EVENT_TIMEZONE, CalendarContract.Instances.DURATION}, null,
                null, null);
//                projection, null,
//                null, null);



        cursor.moveToFirst();
        // fetching calendars name


        // fetching calendars id
        String syncacc = null;
        while (cursor.moveToNext()) {

            syncacc = cursor.getString(6);

            LocalDate localDate = getDate(Long.parseLong(cursor.getString(3)));

            if (!localDateHashMap.containsKey(localDate)) {
                EventInfo eventInfo = new EventInfo();
                eventInfo.id = cursor.getInt(0);
                //eventInfo.id = 1;
                eventInfo.starttime = cursor.getLong(3);
                eventInfo.endtime = cursor.getLong(4);
                if (cursor.getString(11)!=null) {
                    eventInfo.endtime = eventInfo.starttime+RFC2445ToMilliseconds(cursor.getString(11));
                }
                if (cursor.getString(10) !=null){
                    eventInfo.timezone = cursor.getString(10);
                } else {
                    eventInfo.timezone = "America/Los_Angeles";
                }
                eventInfo.accountname=cursor.getString(6);
                eventInfo.eventtitles = new String[]{cursor.getString(1)};
                eventInfo.isallday = cursor.getInt(7) == 1;
                eventInfo.title = cursor.getString(1);
                eventInfo.eventcolor = cursor.getInt(8)==0? Color.parseColor("#6e52b5"):cursor.getInt(8);
                eventInfo.repeating = CalendarContract.Instances.RRULE;
                long difference=eventInfo.endtime-eventInfo.starttime;
                if (difference>86400000){
                    if (cursor.getInt(7)==0){
                        eventInfo.endtime=eventInfo.endtime+86400000L;
                    }

                    LocalDateTime localDate1=new LocalDateTime( eventInfo.starttime, DateTimeZone.forID(eventInfo.timezone)).withTime(0,0,0,0);
                    LocalDateTime localDate2=new LocalDateTime( eventInfo.endtime, DateTimeZone.forID(eventInfo.timezone)).withTime(23, 59, 59, 999);

                    eventInfo.noofdayevent = Days.daysBetween(localDate1,localDate2).getDays();
                    eventInfo.isallday=true;
                }
                else if (difference<86400000) {
                    eventInfo.noofdayevent=0;
                }
                else {
                    eventInfo.noofdayevent=1;
                }

                localDateHashMap.put(localDate, eventInfo);


            } else {
                EventInfo eventInfo = localDateHashMap.get(localDate);
                EventInfo prev = eventInfo;
                while (prev.nextnode!=null)prev=prev.nextnode;
                String[] s = eventInfo.eventtitles;

                boolean isneed = true;
                for (String value : s) {
                    if (value != null && value.equals(cursor.getString(1))) {

                        isneed = false;
                        break;
                    }
                }

                if (isneed) {

                    String ss[] = Arrays.copyOf(s, s.length + 1);
                    ss[ss.length - 1] = cursor.getString(1);
                    eventInfo.eventtitles = ss;

                    EventInfo nextnode = new EventInfo();
                    nextnode.id = cursor.getInt(0);
                    nextnode.starttime =cursor.getLong(3);
                    nextnode.endtime = cursor.getLong(4);
                    if (cursor.getString(11)!=null) {
                        nextnode.endtime = nextnode.starttime+RFC2445ToMilliseconds(cursor.getString(11));
                    }

                    nextnode.isallday = cursor.getInt(7) == 1 ? true : false;
                    nextnode.timezone = cursor.getString(10);
                    nextnode.title = cursor.getString(1);
                    nextnode.accountname=cursor.getString(6);
                    nextnode.eventcolor = cursor.getInt(8)==0? Color.parseColor("#6e52b5"):cursor.getInt(8);
                    long difference=nextnode.endtime-nextnode.starttime;

                    if (nextnode.endtime-nextnode.starttime>86400000){
                        if (cursor.getInt(7)==0){
                            nextnode.endtime=nextnode.endtime+86400000l;
                        }
                        nextnode.isallday=true;
                        LocalDateTime localDate1=new LocalDateTime( nextnode.starttime, DateTimeZone.forID(nextnode.timezone)).withTime(0,0,0,0);
                        LocalDateTime localDate2=new LocalDateTime( nextnode.endtime, DateTimeZone.forID(nextnode.timezone)).withTime(23, 59, 59, 999);


                        int day = Days.daysBetween(localDate1,localDate2).getDays();

                        nextnode.noofdayevent=day;

                    }
                    else if (difference<86400000)eventInfo.noofdayevent=0;
                    else eventInfo.noofdayevent=1;
                    prev.nextnode = nextnode;


                    localDateHashMap.put(localDate, eventInfo);
                }

            }


        }
        System.out.println(localDateHashMap.toString());
        return localDateHashMap;
    }
    public static long RFC2445ToMilliseconds(String str)
    {

        if(str == null || str.isEmpty())
            throw new IllegalArgumentException("Null or empty RFC string");

        int sign = 1;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        int len = str.length();
        int index = 0;
        char c;

        c = str.charAt(0);

        if (c == '-')
        {
            sign = -1;
            index++;
        }

        else if (c == '+')
            index++;

        if (len < index)
            return 0;

        c = str.charAt(index);

        if (c != 'P')
            throw new IllegalArgumentException("Duration.parse(str='" + str + "') expected 'P' at index="+ index);

        index++;
        c = str.charAt(index);
        if (c == 'T')
            index++;

        int n = 0;
        for (; index < len; index++)
        {
            c = str.charAt(index);

            if (c >= '0' && c <= '9')
            {
                n *= 10;
                n += ((int)(c-'0'));
            }

            else if (c == 'W')
            {
                weeks = n;
                n = 0;
            }

            else if (c == 'H')
            {
                hours = n;
                n = 0;
            }

            else if (c == 'M')
            {
                minutes = n;
                n = 0;
            }

            else if (c == 'S')
            {
                seconds = n;
                n = 0;
            }

            else if (c == 'D')
            {
                days = n;
                n = 0;
            }

            else if (c == 'T')
            {
            }
            else
                throw new IllegalArgumentException ("Duration.parse(str='" + str + "') unexpected char '" + c + "' at index=" + index);
        }

        long factor = 1000 * sign;
        long result = factor * ((7*24*60*60*weeks)
                + (24*60*60*days)
                + (60*60*hours)
                + (60*minutes)
                + seconds);

        return result;
    }
    public static void getDataFromCalendarTable(Context context) {
        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();

        String[] mProjection =
                {
                        CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                        CalendarContract.Calendars.CALENDAR_LOCATION,
                        CalendarContract.Calendars.CALENDAR_TIME_ZONE,
                        CalendarContract.Instances._ID
                };

        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{"conf_room_snowcone@esper.io", "conf_room_snowcone@esper.io",
                "conf_room_snowcone@esper.io"};


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        cur = cr.query(uri, mProjection, null, null, null);

        while (cur.moveToNext()) {
            String displayName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME));
            String accountName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME));


        }

    }

    public static LocalDate getDate(long milliSeconds) {
        Instant instantFromEpochMilli
                = Instant.ofEpochMilli(milliSeconds);
        return instantFromEpochMilli.toDateTime(DateTimeZone.getDefault()).toLocalDate();

    }
}