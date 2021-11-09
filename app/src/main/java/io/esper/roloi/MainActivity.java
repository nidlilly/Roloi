package io.esper.roloi;

import android.Manifest;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import io.esper.roloi.weekview.DateTimeInterpreter;
import io.esper.roloi.weekview.MonthLoader;
import io.esper.roloi.weekview.WeekView;
import io.esper.roloi.weekview.WeekViewEvent;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import io.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import io.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import io.esper.devicesdk.EsperDeviceSDK;
import io.esper.devicesdk.models.EsperDeviceInfo;
import io.esper.devicesdk.utils.EsperSDKVersions;

public class MainActivity extends AppCompatActivity
        implements MyRecyclerView.AppBarTracking, WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener,WeekView.ScrollListener {

    private static final String TAG = "MainActivity";
    public static int timeElapsedBeforeReboot = 0;
    public static LocalDate lastdate = LocalDate.now();
    private String daysList[] = {"", "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday", "Sunday"};
    public static int topspace = 0;
    private View myshadow;
    long lasttime;
    int mycolor;
     MyRecyclerView mNestedView;
    private ViewPager monthviewpager;
    private HashMap<LocalDate, EventInfo> alleventlist;
    private DrawerLayout drawerLayout;
    private int mAppBarOffset = 0;
    private boolean mAppBarIdle = true;
    private int mAppBarMaxOffset = 0;
    private View shadow;
    private AppBarLayout mAppBar;
    private boolean mIsExpanded = false;
    private View redlay;
    private ImageView mArrowImageView;
    private TextView monthname;
    private Toolbar toolbar;
    private static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 1000;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 1001;
    private static final int DOWNLOAD_JOB_KEY = 101;
    private int lastchangeindex = -1;
    private boolean isappbarclosed = true;
    private int month;
    private int expandedfirst;
    private View roundrect;
    private TextView eventnametextview, eventrangetextview, holidaytextview,eventfixstextview;
    private Button delEvent;
    private Button updateEvent;
    //private ImageView calendaricon;
    private View eventview, fullview;
    private GooglecalenderView calendarView;
    private ArrayList<EventModel> eventalllist;
    private boolean isgivepermission;
    private HashMap<LocalDate, Integer> indextrack;
    private ImageButton closebtn;
    private HashMap<LocalDate, Integer> dupindextrack;
    EditText title;
    EditText location;
    EditText description;
    Button addEvent;
    Button cancel;
    View weekviewcontainer;
    EditText chooseTime;
    EditText chooseEndTime;
    TimePickerDialog timePickerDialog;
    Calendar calendar;
    int currentHour;
    int currentMinute;
    Calendar calendar2 = Calendar.getInstance();
    final int year = calendar2.get(Calendar.YEAR);
    final int monthOfYear = calendar2.get(Calendar.MONTH);
    final int day = calendar2.get(Calendar.DAY_OF_MONTH);
    EditText dateChooser;

    EditText updatedTitle;
    EditText updatedLocation;
    EditText updatedDescription;
    Button finishUpdate;
    Button cancelUpdate;
    EditText updateTime;
    EditText updateEndTime;
    TimePickerDialog updatedTimePicker;
    Calendar updateCalendar;
    EditText updateDate;

    Handler handler;
    Runnable r;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private String[] var = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN",};

     WeekView mWeekView;

    public static void setTransparent(Activity activity) {
        transparentStatusBar(activity);
        setRootView(activity);
    }

    private static void setRootView(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(false);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }
    }

    private static void transparentStatusBar(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        activity.getWindow().setNavigationBarColor(Color.parseColor("#f1f3f5"));
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==android.R.id.home){

            drawerLayout.openDrawer(Gravity.LEFT);
                return true;
        }
        if (item.getItemId() == R.id.action_favorite) {
            final LocalDate localDate = LocalDate.now();
            if (monthviewpager.getVisibility() == View.VISIBLE && monthviewpager.getAdapter() != null) {
                monthviewpager.setCurrentItem(calendarView.calculateCurrentMonth(localDate), false);
            }
            if (weekviewcontainer.getVisibility()==View.VISIBLE){
                Calendar todaydate=Calendar.getInstance();
                todaydate.set(Calendar.DAY_OF_MONTH,localDate.getDayOfMonth());
                todaydate.set(Calendar.MONTH,localDate.getMonthOfYear()-1);
                todaydate.set(Calendar.YEAR,localDate.getYear());
                mWeekView.goToDate(todaydate);

            }
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mNestedView.getLayoutManager();
            mNestedView.stopScroll();
            if (indextrack.containsKey(new LocalDate(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth()))) {

                final Integer val = indextrack.get(new LocalDate(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth()));

                if (isAppBarExpanded()) {
                    calendarView.setCurrentmonth(new LocalDate());

                    expandedfirst = val;
                    topspace = 20;
                    linearLayoutManager.scrollToPositionWithOffset(val, 20);
                    EventBus.getDefault().post(new MonthChange(localDate, 0));
                    month = localDate.getDayOfMonth();
                    lastdate = localDate;
                } else {

                    expandedfirst = val;
                    topspace = 20;
                    linearLayoutManager.scrollToPositionWithOffset(val, 20);
                    EventBus.getDefault().post(new MonthChange(localDate, 0));
                    month = localDate.getDayOfMonth();
                    lastdate = localDate;

                }


            }

        }
        if (item.getItemId() == R.id.action_refresh) {
            refresh(item.getActionView());
        }
        if (item.getItemId() == R.id.action_schedule) {
            dialogBuilder = new AlertDialog.Builder(this);
            final View contactPopupView = getLayoutInflater().inflate(R.layout.create_event, null);

            title = (EditText) contactPopupView.findViewById(R.id.etTitle);
            location = (EditText) contactPopupView.findViewById(R.id.etLocation);
            description = (EditText) contactPopupView.findViewById(R.id.etDescription);
            addEvent = (Button) contactPopupView.findViewById(R.id.btnAdd);
            cancel = (Button) contactPopupView.findViewById(R.id.btnCancel);
            chooseTime = (EditText) contactPopupView.findViewById(R.id.etChooseTime);
            chooseEndTime = (EditText) contactPopupView.findViewById(R.id.etChooseEndTime);
            dateChooser = (EditText) contactPopupView.findViewById(R.id.chooseDate);

            dialogBuilder.setView(contactPopupView);
            dialog = dialogBuilder.create();
            dialog.show();

            dateChooser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            view.setMaxDate(System.currentTimeMillis());
                            Calendar c = Calendar.getInstance();
                            c.set(Calendar.YEAR, year);
                            c.set(Calendar.MONTH, month);
                            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
                            int commaIndex = currentDateString.indexOf(',');
                            currentDateString = currentDateString.substring(commaIndex + 2);
                            dateChooser.setText(currentDateString);
                        }
                    }, year, monthOfYear, day);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    Calendar maxTime = Calendar.getInstance();
                    maxTime.set(Calendar.getInstance().get(Calendar.YEAR), 11, 31, 23, 59);
                    long totalMaxTime = maxTime.getTimeInMillis();
                    datePickerDialog.getDatePicker().setMaxDate(totalMaxTime);
                    datePickerDialog.show();
                }
            });

            chooseTime.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    calendar = Calendar.getInstance();
                    currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                    currentMinute = calendar.get(Calendar.MINUTE);

                    timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            chooseTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                            //chooseTime.setText(hourOfDay + ":" + minute);
                        }
                    }, currentHour, currentMinute, false);

                    timePickerDialog.show();
                }
            });
            chooseEndTime.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    calendar = Calendar.getInstance();
                    currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                    currentMinute = calendar.get(Calendar.MINUTE);

                    timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            chooseEndTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                        }
                    }, currentHour, currentMinute, false);

                    timePickerDialog.show();
                }
            });

            addEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!title.getText().toString().isEmpty() && !location.getText().toString().isEmpty() &&
                            !description.getText().toString().isEmpty() && !chooseTime.getText().toString().isEmpty() && !chooseEndTime.getText().toString().isEmpty() && !dateChooser.getText().toString().isEmpty()) {
                        try {
                            String eventTitle = title.getText().toString();
                            if (isEventAlreadyExist(eventTitle, v)) {
                                Snackbar.make(v, eventTitle + " already exists", Snackbar.LENGTH_SHORT).show();
                                return;
                            }
                            long calID = 4;
                            long startMillis = 0;
                            long endMillis = 0;
                            Calendar beginTime = Calendar.getInstance();
                            String date = dateChooser.getText().toString();
                            int spaceIndex = date.indexOf(' ');
                            String month = date.substring(0, spaceIndex);
                            int commIndex = date.indexOf(',');
                            String dayOfMonth = date.substring(spaceIndex + 1, commIndex);
                            int dom = Integer.parseInt(dayOfMonth);
                            String year = date.substring(commIndex + 2);
                            int yearOfEvent = Integer.parseInt(year);

                            String startingTime = chooseTime.getText().toString();
                            String startingTimeHour = startingTime.substring(0, 2);
                            int sth = Integer.parseInt(startingTimeHour);
                            String startingTimeMinute = startingTime.substring(3, 5);
                            int stm = Integer.parseInt(startingTimeMinute);
                            String endingTime = chooseEndTime.getText().toString();
                            String endingTimeHour = endingTime.substring(0, 2);
                            int eth = Integer.parseInt(endingTimeHour);
                            String endingTimeMinute = startingTime.substring(3, 5);
                            int etm = Integer.parseInt(endingTimeMinute);

                            int monthConverted = 0;

                            switch (month) {
                                case "January":
                                    monthConverted = 0;
                                    break;
                                case "February":
                                    monthConverted = 1;
                                    break;
                                case "March":
                                    monthConverted = 2;
                                    break;
                                case "April":
                                    monthConverted = 3;
                                    break;
                                case "May":
                                    monthConverted = 4;
                                    break;
                                case "June":
                                    monthConverted = 5;
                                    break;
                                case "July":
                                    monthConverted = 6;
                                    break;
                                case "August":
                                    monthConverted = 7;
                                    break;
                                case "September":
                                    monthConverted = 8;
                                    break;
                                case "October":
                                    monthConverted = 9;
                                    break;
                                case "November":
                                    monthConverted = 10;
                                    break;
                                case "December":
                                    monthConverted = 11;
                                    break;
                            }
                            System.out.println("Date----" + date + "-------");
                            System.out.println("Starting Time Hour: ----" + startingTimeHour + "-------");
                            System.out.println("Starting Time Minute: ----" + startingTimeMinute + "-------");
                            System.out.println("Ending Time Hour----" + endingTimeHour + "-------");
                            System.out.println("Ending Time Minute----" + endingTimeMinute + "-------");
                            System.out.println("Month----" + month + "-------");
                            System.out.println("DayOfMonth----" + dayOfMonth + "-------");
                            System.out.println("Year----" + yearOfEvent + "-------");
                            System.out.println("Month in integer ------------" + monthConverted);

                            if ((eth < sth) || ((eth == sth) && (etm < stm))) {
                                Snackbar.make(v, "Invalid times, please try again", Snackbar.LENGTH_SHORT).show();
                            } else {
                                beginTime.set(yearOfEvent, monthConverted, dom, sth, stm);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(yearOfEvent, monthConverted, dom, eth, etm);
                                endMillis = endTime.getTimeInMillis();

                                ContentResolver cr = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.TITLE, eventTitle);
                                values.put(CalendarContract.Events.DESCRIPTION, description.getText().toString());
                                values.put(CalendarContract.Events.EVENT_LOCATION, location.getText().toString());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");
                                values.put(CalendarContract.Events.ORGANIZER, "google_calendar@gmail.com");
                                checkPermissionTester(cr, values, v, eventTitle, dialog);
                                refresh(item.getActionView());

                                if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    Log.e(TAG, "Permissions Denied");
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, 200);
                                    }
                                } else {
                                    isgivepermission=true;
                                    LocalDate mintime = new LocalDate().minusYears(5);
                                    LocalDate maxtime = new LocalDate().plusYears(5);
                                    alleventlist = Utility.readCalendarEvent(v.getContext(), mintime, maxtime);
                                }
                            }
                        } catch(Exception e) {
                            Toast.makeText(MainActivity.this, "Invalid Values, please use the specified datetime format",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                    else {
                        Toast.makeText(MainActivity.this, "Please fill all the fields",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
               @Override
                public void onClick(View v) {
                   dialog.dismiss();
               }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = ResourcesCompat.getFont(this, R.font.montserrat);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
    public int getnavigationHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private void setMargins(View view, int left, int top, int right, int bottom, int width, int height) {

        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            view.setLayoutParams(new CoordinatorLayout.LayoutParams(width, height));
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public void closebtnClick() {
        closebtn.setVisibility(View.GONE);
        eventnametextview.setVisibility(View.GONE);
        roundrect.setVisibility(View.GONE);
        eventrangetextview.setVisibility(View.GONE);
        //calendaricon.setVisibility(View.GONE);
        //holidaytextview.setVisibility(View.GONE);
        eventfixstextview.setVisibility(View.GONE);
        ValueAnimator animwidth = ValueAnimator.ofInt(getDevicewidth(), eventview.getWidth());
        animwidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                layoutParams.width = val;
                redlay.setLayoutParams(layoutParams);
            }
        });
        animwidth.setDuration(300);

        ValueAnimator animheight = ValueAnimator.ofInt(getDeviceHeight(), 0);
        animheight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                layoutParams.height = val;
                redlay.setLayoutParams(layoutParams);
                if (redlay.getTranslationZ() != 0 && valueAnimator.getAnimatedFraction() > 0.7) {
                    GradientDrawable shape =  new GradientDrawable();
                    shape.setCornerRadius( getResources().getDimensionPixelSize(R.dimen.fourdp) );
                    shape.setColor(mycolor);
                    redlay.setBackground(shape);
                    redlay.setTranslationZ(0);
                    shadow.setVisibility(View.GONE);
                }
            }
        });
        animheight.setDuration(300);

        ValueAnimator animx = ValueAnimator.ofFloat(0, eventview.getLeft());
        animx.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                Float val = (Float) valueAnimator.getAnimatedValue();

                redlay.setTranslationX(val);
            }
        });
        animx.setDuration(300);

        ValueAnimator animy = ValueAnimator.ofFloat(0, fullview.getTop() + toolbar.getHeight());

        animy.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float val = (Float) valueAnimator.getAnimatedValue();
                redlay.setTranslationY(val);
            }
        });
        animy.setDuration(300);
        animwidth.start();
        animheight.start();
        animy.start();
        animx.start();

        //Canvas canvas = new Canvas();
        //mWeekView.redraw(canvas);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String token = "RnwfiB53OHY3zjC275THrCIS8Tl2jg";

        AccountManager am = AccountManager.get(this);
        Bundle options = new Bundle();

        EsperDeviceSDK sdk = EsperDeviceSDK.getInstance(getApplicationContext());
        Context context = getApplicationContext();

        mWeekView = (WeekView) findViewById(R.id.weekView);
        weekviewcontainer=findViewById(R.id.weekViewcontainer);
        drawerLayout=findViewById(R.id.drawer_layout);
        NavigationView navigationView=findViewById(R.id.navigation_view);
        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            applyFontToMenuItem(mi);
        }

        /*Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        timeElapsedBeforeReboot += 1000;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (timeElapsedBeforeReboot == (20 * 60 * 1000)) {
                                    timeElapsedBeforeReboot = 0;
                                    weekviewcontainer.setVisibility(View.VISIBLE);
                                    monthviewpager.setVisibility(View.GONE);
                                    mNestedView.setVisibility(View.GONE);
                                    mWeekView.setNumberOfVisibleDays(1);
                                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                                    mWeekView.setAllDayEventHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 26, getResources().getDisplayMetrics()));
                                    Calendar todaydate=Calendar.getInstance();
                                    todaydate.set(Calendar.DAY_OF_MONTH,MainActivity.lastdate.getDayOfMonth());
                                    todaydate.set(Calendar.MONTH,MainActivity.lastdate.getMonthOfYear()-1);
                                    todaydate.set(Calendar.YEAR,MainActivity.lastdate.getYear());
                                    mWeekView.goToDate(todaydate);
                                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
                                    ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(true);
                                    mAppBar.setElevation(0);
                                    mArrowImageView.setVisibility(View.VISIBLE);
                                    drawerLayout.closeDrawer(Gravity.LEFT);

                                    final LocalDate localDate = LocalDate.now();
                                    if (monthviewpager.getVisibility() == View.VISIBLE && monthviewpager.getAdapter() != null) {
                                        monthviewpager.setCurrentItem(calendarView.calculateCurrentMonth(localDate), false);
                                    }
                                    if (weekviewcontainer.getVisibility()==View.VISIBLE){
                                        Calendar todaysdate=Calendar.getInstance();
                                        todaysdate.set(Calendar.DAY_OF_MONTH,localDate.getDayOfMonth());
                                        todaysdate.set(Calendar.MONTH,localDate.getMonthOfYear()-1);
                                        todaysdate.set(Calendar.YEAR,localDate.getYear());
                                        mWeekView.goToDate(todaysdate);

                                    }
                                    final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mNestedView.getLayoutManager();
                                    mNestedView.stopScroll();
                                    if (indextrack.containsKey(new LocalDate(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth()))) {
                                        final Integer val = indextrack.get(new LocalDate(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth()));
                                        if (isAppBarExpanded()) {
                                            calendarView.setCurrentmonth(new LocalDate());
                                            expandedfirst = val;
                                            topspace = 20;
                                            linearLayoutManager.scrollToPositionWithOffset(val, 20);
                                            EventBus.getDefault().post(new MonthChange(localDate, 0));
                                            month = localDate.getDayOfMonth();
                                            lastdate = localDate;
                                        } else {
                                            expandedfirst = val;
                                            topspace = 20;
                                            linearLayoutManager.scrollToPositionWithOffset(val, 20);
                                            EventBus.getDefault().post(new MonthChange(localDate, 0));
                                            month = localDate.getDayOfMonth();
                                            lastdate = localDate;
                                        }
                                    }
                                }
                                //Log.d(TAG, "Time elapsed: " + timeElapsedBeforeReboot);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "interrupted, something went wrong with the code");
                }
            }
        };

        t.start();
        */

        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(MainActivity.this, "user is inactive from last 5 seconds",Toast.LENGTH_SHORT).show();
                weekviewcontainer.setVisibility(View.VISIBLE);
                monthviewpager.setVisibility(View.GONE);
                mNestedView.setVisibility(View.GONE);
                mWeekView.setNumberOfVisibleDays(1);
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                mWeekView.setAllDayEventHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 26, getResources().getDisplayMetrics()));
                Calendar todaydate = Calendar.getInstance();
                todaydate.set(Calendar.DAY_OF_MONTH, MainActivity.lastdate.getDayOfMonth());
                todaydate.set(Calendar.MONTH, MainActivity.lastdate.getMonthOfYear() - 1);
                todaydate.set(Calendar.YEAR, MainActivity.lastdate.getYear());
                mWeekView.goToDate(todaydate);
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
                ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(true);
                mAppBar.setElevation(0);
                mArrowImageView.setVisibility(View.VISIBLE);
                drawerLayout.closeDrawer(Gravity.LEFT);
                final LocalDate localDate = LocalDate.now();
                if (monthviewpager.getVisibility() == View.VISIBLE && monthviewpager.getAdapter() != null) {
                    monthviewpager.setCurrentItem(calendarView.calculateCurrentMonth(localDate), false);
                }
                if (weekviewcontainer.getVisibility() == View.VISIBLE) {
                    Calendar todaysdate = Calendar.getInstance();
                    todaysdate.set(Calendar.DAY_OF_MONTH, localDate.getDayOfMonth());
                    todaysdate.set(Calendar.MONTH, localDate.getMonthOfYear() - 1);
                    todaysdate.set(Calendar.YEAR, localDate.getYear());
                    mWeekView.goToDate(todaysdate);
                }
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mNestedView.getLayoutManager();
                mNestedView.stopScroll();
                if (indextrack.containsKey(new LocalDate(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth()))) {
                    final Integer val = indextrack.get(new LocalDate(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth()));
                    if (isAppBarExpanded()) {
                        calendarView.setCurrentmonth(new LocalDate());
                        expandedfirst = val;
                        topspace = 20;
                        linearLayoutManager.scrollToPositionWithOffset(val, 20);
                        EventBus.getDefault().post(new MonthChange(localDate, 0));
                        month = localDate.getDayOfMonth();
                        lastdate = localDate;
                    } else {
                        expandedfirst = val;
                        topspace = 20;
                        linearLayoutManager.scrollToPositionWithOffset(val, 20);
                        EventBus.getDefault().post(new MonthChange(localDate, 0));
                        month = localDate.getDayOfMonth();
                        lastdate = localDate;
                    }
                }
            }
        };
        startHandler();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==R.id.Day){
                    weekviewcontainer.setVisibility(View.VISIBLE);
                    monthviewpager.setVisibility(View.GONE);
                    mNestedView.setVisibility(View.GONE);
                    mWeekView.setNumberOfVisibleDays(1);
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                        mWeekView.setAllDayEventHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 26, getResources().getDisplayMetrics()));
                        Calendar todaydate=Calendar.getInstance();
                        todaydate.set(Calendar.DAY_OF_MONTH,MainActivity.lastdate.getDayOfMonth());
                        todaydate.set(Calendar.MONTH,MainActivity.lastdate.getMonthOfYear()-1);
                        todaydate.set(Calendar.YEAR,MainActivity.lastdate.getYear());
                        mWeekView.goToDate(todaydate);
                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
                    ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(true);
                    mAppBar.setElevation(0);
                    mArrowImageView.setVisibility(View.VISIBLE);
                        drawerLayout.closeDrawer(Gravity.LEFT);

                }
               else if (item.getItemId()==R.id.Week){
                    weekviewcontainer.setVisibility(View.VISIBLE);
                    monthviewpager.setVisibility(View.GONE);
                    mNestedView.setVisibility(View.GONE);
                    mWeekView.setNumberOfVisibleDays(7);
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setAllDayEventHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
                    Calendar todaydate=Calendar.getInstance();
                    todaydate.set(Calendar.DAY_OF_MONTH,MainActivity.lastdate.getDayOfMonth());
                    todaydate.set(Calendar.MONTH,MainActivity.lastdate.getMonthOfYear()-1);
                    todaydate.set(Calendar.YEAR,MainActivity.lastdate.getYear());
                    mWeekView.goToDate(todaydate);
                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
                    ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(true);
                    mAppBar.setElevation(0);
                    mArrowImageView.setVisibility(View.VISIBLE);
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
                else {
                    LocalDate localDate = new LocalDate();
                    String yearstr = MainActivity.lastdate.getYear() == localDate.getYear() ? "" : MainActivity.lastdate.getYear() + "";
                    monthname.setText(MainActivity.lastdate.toString("MMMM") + " " + yearstr);
                    calendarView.setCurrentmonth(MainActivity.lastdate);
                    calendarView.adjustheight();
                    mIsExpanded = false;
                    mAppBar.setExpanded(false, false);
                    EventBus.getDefault().post(new MessageEvent(MainActivity.lastdate));
                    monthviewpager.setVisibility(View.GONE);
                    weekviewcontainer.setVisibility(View.GONE);
                    mNestedView.setVisibility(View.VISIBLE);
                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
                    ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(true);
                    mAppBar.setElevation(20);
                    mArrowImageView.setVisibility(View.VISIBLE);
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
                item.setChecked(true);
                return true;
            }

        });

        eventalllist = new ArrayList<>();
        indextrack = new HashMap<>();
        dupindextrack = new HashMap<>();
        mAppBar = findViewById(R.id.app_bar);
        redlay = findViewById(R.id.redlay);
        redlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        shadow = findViewById(R.id.shadow);
        closebtn = findViewById(R.id.closebtn);
        updateEvent = findViewById(R.id.btnUpd);

        delEvent = findViewById(R.id.btnDelete);
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vh) {
                closebtnClick();
            }
        });
        roundrect = findViewById(R.id.roundrect);
        eventnametextview = findViewById(R.id.textView12);
        eventrangetextview = findViewById(R.id.textView13);
        //holidaytextview = findViewById(R.id.textView14);

        eventfixstextview=findViewById(R.id.textView014);
        calendarView = findViewById(R.id.calander);

        calendarView.setPadding(0, getStatusBarHeight(), 0, 0);
        mNestedView = findViewById(R.id.nestedView);
        monthviewpager = findViewById(R.id.monthviewpager);
        monthviewpager.setOffscreenPageLimit(1);

        delEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView title = findViewById(R.id.textView12);
                String t = title.getText().toString();

                removeEvent(v, t);
                if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Permissions Denied");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, 200);
                    }
                } else {
                    isgivepermission=true;
                    Log.e(TAG, "Permissions given, good to go");
                    LocalDate mintime = new LocalDate().minusYears(5);
                    LocalDate maxtime = new LocalDate().plusYears(5);
                    alleventlist = Utility.readCalendarEvent(v.getContext(), mintime, maxtime);
                    Log.e(TAG, alleventlist.toString());
                }

                closebtnClick();
                TextView eventtextview = v.findViewById(R.id.view_item_textview);
                if (eventtextview != null) {
                    eventtextview.setVisibility(View.GONE);
                }

                mWeekView.notifyDatasetChanged();

                //onRestart();

            }
        });


        updateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String total = eventrangetextview.getText().toString();
                if (total.length() < 20) ;
                else {
                    System.out.println("Total:----" + total);
                    int indexOfComma = total.indexOf(',');
                    String date = total.substring(indexOfComma + 2, total.indexOf('·'));
                    String dayOfMonth = date.substring(0, date.indexOf(' '));
                    String monthString = date.substring(date.indexOf(' ') + 1, date.indexOf(' ') + 4);

                    String startingTimeFull = total.substring(total.indexOf('·') + 2, total.indexOf("M -") + 1);
                    char startingAMPM = startingTimeFull.charAt(startingTimeFull.length() - 2);
                    String startingHour = startingTimeFull.substring(0, startingTimeFull.indexOf(':'));
                    int shtime = Integer.parseInt(startingHour);
                    if (startingAMPM == 'P') {
                        if(shtime != 12) shtime += 12;
                    }
                    String startingMinute = startingTimeFull.substring(startingTimeFull.indexOf(':') + 1, startingTimeFull.indexOf(':') + 3);
                    int shmtime = Integer.parseInt(startingMinute);

                    String endingTimeFull = total.substring(total.indexOf('-') + 2);
                    char endingAMPM = endingTimeFull.charAt(endingTimeFull.length() - 2);
                    String endingHour = endingTimeFull.substring(0, endingTimeFull.indexOf(':'));
                    int ehtime = Integer.parseInt(endingHour);
                    if (endingAMPM == 'P') {
                        if (ehtime != 12) ehtime += 12;
                    }
                    String endingMinute = endingTimeFull.substring(endingTimeFull.indexOf(':') + 1, endingTimeFull.indexOf(' '));
                    int ehmtime = Integer.parseInt(endingMinute);


                    dialogBuilder = new AlertDialog.Builder(view.getContext());
                    final View contactPopupView = getLayoutInflater().inflate(R.layout.update_event, null);

                    updatedTitle = (EditText) contactPopupView.findViewById(R.id.updatingTitle);
                    updatedLocation = (EditText) contactPopupView.findViewById(R.id.updatingLocation);
                    updatedDescription = (EditText) contactPopupView.findViewById(R.id.updatingDescription);
                    finishUpdate = (Button) contactPopupView.findViewById(R.id.btnUpdate);
                    cancelUpdate = (Button) contactPopupView.findViewById(R.id.btnExitUpdateScreen);
                    updateTime = (EditText) contactPopupView.findViewById(R.id.etUpdateTime);
                    updateEndTime = (EditText) contactPopupView.findViewById(R.id.etUpdateEndTime);
                    updateDate = (EditText) contactPopupView.findViewById(R.id.updateDate);

                    dialogBuilder.setView(contactPopupView);
                    dialog = dialogBuilder.create();
                    dialog.show();

                    updatedTitle.setText(eventnametextview.getText().toString());
                    if (shtime < 10 && shmtime < 10) {
                        updateTime.setText("0" + shtime + ":0" + shmtime);
                    } else if (shtime < 10) {
                        updateTime.setText("0" + shtime + ":" + shmtime);
                    } else if (shmtime < 10) {
                        updateTime.setText("" + shtime + ":0" + shmtime);
                    } else {
                        updateTime.setText("" + shtime + ":" + shmtime);
                    }
                    if (ehtime < 10 && ehmtime < 10) {
                        updateEndTime.setText("0" + ehtime + ":0" + ehmtime);
                    } else if (ehtime < 10) {
                        updateEndTime.setText("0" + ehtime + ":" + ehmtime);
                    } else if (ehmtime < 10) {
                        updateEndTime.setText("" + ehtime + ":0" + ehmtime);
                    } else {
                        updateEndTime.setText("" + ehtime + ":" + ehmtime);
                    }
                    switch (monthString) {
                        case "Jan":
                            monthString = "January";
                            break;
                        case "Feb":
                            monthString = "February";
                            break;
                        case "Mar":
                            monthString = "March";
                            break;
                        case "Apr":
                            monthString = "April";
                            break;
                        case "May":
                            monthString = "May";
                            break;
                        case "Jun":
                            monthString = "June";
                            break;
                        case "Jul":
                            monthString = "July";
                            break;
                        case "Aug":
                            monthString = "August";
                            break;
                        case "Sep":
                            monthString = "September";
                            break;
                        case "Oct":
                            monthString = "October";
                            break;
                        case "Nov":
                            monthString = "November";
                            break;
                        case "Dec":
                            monthString = "December";
                            break;

                    }


                    updateDate.setText(monthString + " " + dayOfMonth + ", " + Calendar.getInstance().get(Calendar.YEAR));
                    updateDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatePickerDialog datePickerDialog = new DatePickerDialog(
                                    MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    view.setMaxDate(System.currentTimeMillis());
                                    Calendar c = Calendar.getInstance();
                                    c.set(Calendar.YEAR, year);
                                    c.set(Calendar.MONTH, month);
                                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
                                    int commaIndex = currentDateString.indexOf(',');
                                    currentDateString = currentDateString.substring(commaIndex + 2);
                                    dateChooser.setText(currentDateString);
                                }
                            }, year, monthOfYear, day);
                            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                            Calendar maxTime = Calendar.getInstance();
                            maxTime.set(Calendar.getInstance().get(Calendar.YEAR), 11, 31, 23, 59);
                            long totalMaxTime = maxTime.getTimeInMillis();
                            datePickerDialog.getDatePicker().setMaxDate(totalMaxTime);
                            datePickerDialog.show();
                        }
                    });

                    updateTime.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            calendar = Calendar.getInstance();
                            currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                            currentMinute = calendar.get(Calendar.MINUTE);

                            timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    updateTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                                    //chooseTime.setText(hourOfDay + ":" + minute);
                                }
                            }, currentHour, currentMinute, false);

                            timePickerDialog.show();
                        }
                    });
                    updateEndTime.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            calendar = Calendar.getInstance();
                            currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                            currentMinute = calendar.get(Calendar.MINUTE);

                            timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    updateEndTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                                }
                            }, currentHour, currentMinute, false);

                            timePickerDialog.show();
                        }
                    });
                    finishUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!updatedTitle.getText().toString().isEmpty() && !updatedLocation.getText().toString().isEmpty() &&
                                    !updatedDescription.getText().toString().isEmpty() && !updateTime.getText().toString().isEmpty() && !updateEndTime.getText().toString().isEmpty() && !updateDate.getText().toString().isEmpty()) {
                                try {
                                    String eventTitle = updatedTitle.getText().toString();
                                    String originalName = eventnametextview.getText().toString();

                                    removeEvent(v, originalName);

                                    long calID = 4;
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    String date = updateDate.getText().toString();
                                    int spaceIndex = date.indexOf(' ');
                                    String month = date.substring(0, spaceIndex);
                                    int commIndex = date.indexOf(',');
                                    String dayOfMonth = date.substring(spaceIndex + 1, commIndex);
                                    int dom = Integer.parseInt(dayOfMonth);
                                    String year = date.substring(commIndex + 2);
                                    int yearOfEvent = Integer.parseInt(year);

                                    String startingTime = updateTime.getText().toString();
                                    String startingTimeHour = startingTime.substring(0, 2);
                                    int sth = Integer.parseInt(startingTimeHour);
                                    String startingTimeMinute = startingTime.substring(3, 5);
                                    int stm = Integer.parseInt(startingTimeMinute);
                                    String endingTime = updateEndTime.getText().toString();
                                    String endingTimeHour = endingTime.substring(0, 2);
                                    int eth = Integer.parseInt(endingTimeHour);
                                    String endingTimeMinute = startingTime.substring(3, 5);
                                    int etm = Integer.parseInt(endingTimeMinute);

                                    int monthConverted = 0;

                                    switch (month) {
                                        case "January":
                                            monthConverted = 0;
                                            break;
                                        case "February":
                                            monthConverted = 1;
                                            break;
                                        case "March":
                                            monthConverted = 2;
                                            break;
                                        case "April":
                                            monthConverted = 3;
                                            break;
                                        case "May":
                                            monthConverted = 4;
                                            break;
                                        case "June":
                                            monthConverted = 5;
                                            break;
                                        case "July":
                                            monthConverted = 6;
                                            break;
                                        case "August":
                                            monthConverted = 7;
                                            break;
                                        case "September":
                                            monthConverted = 8;
                                            break;
                                        case "October":
                                            monthConverted = 9;
                                            break;
                                        case "November":
                                            monthConverted = 10;
                                            break;
                                        case "December":
                                            monthConverted = 11;
                                            break;
                                    }

                                    if ((eth < sth) || ((eth == sth) && (etm < stm))) {
                                        Snackbar.make(v, "Invalid times, please try again", Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        beginTime.set(yearOfEvent, monthConverted, dom, sth, stm);
                                        startMillis = beginTime.getTimeInMillis();
                                        Calendar endTime = Calendar.getInstance();
                                        endTime.set(yearOfEvent, monthConverted, dom, eth, etm);
                                        endMillis = endTime.getTimeInMillis();

                                        ContentResolver cr = getContentResolver();
                                        ContentValues values = new ContentValues();
                                        values.put(CalendarContract.Events.DTSTART, startMillis);
                                        values.put(CalendarContract.Events.DTEND, endMillis);
                                        values.put(CalendarContract.Events.TITLE, eventTitle);
                                        values.put(CalendarContract.Events.DESCRIPTION, updatedDescription.getText().toString());
                                        values.put(CalendarContract.Events.EVENT_LOCATION, updatedLocation.getText().toString());
                                        values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");
                                        values.put(CalendarContract.Events.ORGANIZER, "google_calendar@gmail.com");

                                        if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                                            Log.e(TAG, "Permissions Denied");
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, 200);
                                            }
                                        } else {
                                            isgivepermission = true;
                                            LocalDate mintime = new LocalDate().minusYears(5);
                                            LocalDate maxtime = new LocalDate().plusYears(5);
                                            alleventlist = Utility.readCalendarEvent(v.getContext(), mintime, maxtime);
                                        }
                                        dialog.dismiss();
                                        closebtnClick();
                                        refresh(v);
                                        Toast.makeText(MainActivity.this, "Event updated",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "Invalid values, please use the specified DateTime format",
                                            Toast.LENGTH_LONG).show();
                                    Log.e("DateTime Exception", e.toString());

                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Please fill all the fields",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    cancelUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                }
            }
        });
        monthviewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {

                if (monthviewpager.getVisibility() == View.GONE) return;
                if (isAppBarClosed()) {
                    Log.e("selected", i + "");
                    LocalDate localDate = new LocalDate();
                    MonthPageAdapter monthPageAdapter = (MonthPageAdapter) monthviewpager.getAdapter();
                    MonthModel monthModel = monthPageAdapter.getMonthModels().get(i);
                    String year = monthModel.getYear() == localDate.getYear() ? "" : monthModel.getYear() + "";
                    monthname.setText(monthModel.getMonthnamestr() + " " + year);
                    MainActivity.lastdate=new LocalDate(monthModel.getYear(),monthModel.getMonth(),1);
                    // EventBus.getDefault().post(new MessageEvent(new LocalDate(monthModel.getYear(),monthModel.getMonth(),1)));
                    // if (monthChangeListner!=null)monthChangeListner.onmonthChange(myPagerAdapter.monthModels.get(position));
                } else {
                    // calendarView.setCurrentmonth(i);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mNestedView.setAppBarTracking(this);


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mNestedView.setLayoutManager(linearLayoutManager);
        DateAdapter dateAdapter = new DateAdapter();
        mNestedView.setAdapter(dateAdapter);

        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(dateAdapter);
        mNestedView.addItemDecoration(headersDecor);
        EventBus.getDefault().register(this);


        monthname = findViewById(R.id.monthname);
        calendarView.setMonthChangeListner(new MonthChangeListner() {
            @Override
            public void onmonthChange(MonthModel monthModel) {
                /**
                 * call when Googlecalendarview is open  scroll viewpager available inside GoogleCalendar
                 */
                LocalDate localDate = new LocalDate();
                String year = monthModel.getYear() == localDate.getYear() ? "" : monthModel.getYear() + "";
                monthname.setText(monthModel.getMonthnamestr() + " " + year);
                if (weekviewcontainer.getVisibility()==View.VISIBLE){
                    Calendar todaydate=Calendar.getInstance();
                    todaydate.set(Calendar.DAY_OF_MONTH,1);
                    todaydate.set(Calendar.MONTH,monthModel.getMonth()-1);
                    todaydate.set(Calendar.YEAR,monthModel.getYear());
                    mWeekView.goToDate(todaydate);

                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            Log.e(TAG, "Permissions Denied");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, 200);
            }
        } else {
            isgivepermission=true;
            LocalDate mintime = new LocalDate().minusYears(5);
            LocalDate maxtime = new LocalDate().plusYears(5);
            alleventlist = Utility.readCalendarEvent(this, mintime, maxtime);
            calendarView.init(alleventlist, mintime, maxtime);
            calendarView.setCurrentmonth(new LocalDate());
            calendarView.adjustheight();
            mIsExpanded = false;
            mAppBar.setExpanded(false, false);

        }
        toolbar = findViewById(R.id.toolbar);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        mArrowImageView = findViewById(R.id.arrowImageView);
        if (monthviewpager.getVisibility() == View.VISIBLE) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
            ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(false);
            mAppBar.setElevation(0);
            mArrowImageView.setVisibility(View.INVISIBLE);
        } else {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
            ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(true);
            mAppBar.setElevation(20);
            mArrowImageView.setVisibility(View.VISIBLE);
        }

        mNestedView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            LinearLayoutManager llm = (LinearLayoutManager) mNestedView.getLayoutManager();
            DateAdapter dateAdapter = (DateAdapter) mNestedView.getAdapter();
            int mydy;
            private int offset = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (mAppBarOffset != 0 && isappbarclosed && newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    calendarView.setCurrentmonth(dateAdapter.geteventallList().get(expandedfirst).getLocalDate());
                    calendarView.adjustheight();
                    mIsExpanded = false;
                    mAppBar.setExpanded(false, false);
                    //Log.e("callme", "statechange");

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isappbarclosed) {

                    int pos = llm.findFirstVisibleItemPosition();
                    View view = llm.findViewByPosition(pos);

                    int currentmonth = dateAdapter.geteventallList().get(pos).getLocalDate().getMonthOfYear();

                    if (dateAdapter.geteventallList().get(pos).getType() == 1) {


                        if (dy > 0 && Math.abs(view.getTop()) > 100) {
                            if (month != currentmonth)
                                EventBus.getDefault().post(new MonthChange(dateAdapter.geteventallList().get(pos).getLocalDate(), dy));
                            month = currentmonth;
                            lastdate = dateAdapter.geteventallList().get(pos).getLocalDate();
                            expandedfirst = pos;
                        } else if (dy < 0 && Math.abs(view.getTop()) < 100 && pos - 1 > 0) {


                            pos--;
                            currentmonth = dateAdapter.geteventallList().get(pos).getLocalDate().getMonthOfYear();


                            if (month != currentmonth)
                                EventBus.getDefault().post(new MonthChange(dateAdapter.geteventallList().get(pos).getLocalDate(), dy));
                            month = currentmonth;
                            lastdate = dateAdapter.geteventallList().get(pos).getLocalDate().dayOfMonth().withMaximumValue();
                            expandedfirst = pos;
                        }
                    } else {
                        lastdate = dateAdapter.geteventallList().get(pos).getLocalDate();
                        expandedfirst = pos;
                    }

                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });


        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {

            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        }


        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {


                if (mAppBarOffset != verticalOffset) {
                    mAppBarOffset = verticalOffset;
                    mAppBarMaxOffset = -mAppBar.getTotalScrollRange();
                    int totalScrollRange = appBarLayout.getTotalScrollRange();
                    float progress = (float) (-verticalOffset) / (float) totalScrollRange;
                    if (monthviewpager.getVisibility()==View.GONE&&mNestedView.getVisibility()==View.VISIBLE)mAppBar.setElevation(20+(20*Math.abs(1-progress)));
                    if (weekviewcontainer.getVisibility()==View.VISIBLE){
                        mAppBar.setElevation(20-(20*Math.abs(progress)));


                    }
                    if (Math.abs(progress)>0.45){
                          ViewGroup.LayoutParams params = myshadow.getLayoutParams();
                    params.height = (int) (getResources().getDimensionPixelSize(R.dimen.fourdp)*Math.abs(progress));
                    myshadow.setLayoutParams(params);
                    }


                    mArrowImageView.setRotation(progress * 180);
                    mIsExpanded = verticalOffset == 0;
                    mAppBarIdle = mAppBarOffset >= 0 || mAppBarOffset <= mAppBarMaxOffset;
                    float alpha = (float) -verticalOffset / totalScrollRange;


                    if (mAppBarOffset == -appBarLayout.getTotalScrollRange()) {
                        isappbarclosed = true;
                        setExpandAndCollapseEnabled(false);
                    } else {
                        setExpandAndCollapseEnabled(true);
                    }

                    if (mAppBarOffset == 0) {
                        expandedfirst = linearLayoutManager.findFirstVisibleItemPosition();
                        if (mNestedView.getVisibility() == View.VISIBLE) {
                            topspace = linearLayoutManager.findViewByPosition(linearLayoutManager.findFirstVisibleItemPosition()).getTop();//uncomment jigs 28 feb
                        }
                        if (isappbarclosed) {
                            isappbarclosed = false;
                            mNestedView.stopScroll();

                            calendarView.setCurrentmonth(lastdate);
                        }
                    }

                }


            }
        });

        findViewById(R.id.backsupport).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//
                        if (monthviewpager.getVisibility() == View.VISIBLE) return;
                        mIsExpanded = !mIsExpanded;
                        mNestedView.stopScroll();

                        mAppBar.setExpanded(mIsExpanded, true);


                    }
                });

        /////////////////weekview implemention/////
         myshadow=findViewById(R.id.myshadow);



        mWeekView.setshadow(myshadow);
        mWeekView.setfont( ResourcesCompat.getFont(this, R.font.montserrat),0);
        mWeekView.setfont( ResourcesCompat.getFont(this, R.font.montserrat),1);

        mWeekView.setOnEventClickListener(this);


        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);
        mWeekView.setScrollListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);

        /*EsperSDK*/
        sdk.activateSDK(token, new EsperDeviceSDK.Callback<Void>() {
            @Override
            public void onResponse(Void response) {
                //Activation was successful
                Log.d("activate", "Activation successful");
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Log.d("activate", "activation unsuccessful");
            }
        });

        sdk.isActivated(new EsperDeviceSDK.Callback<Boolean>() {
            @Override
            public void onResponse(Boolean active) {
                if (active) {
                    Log.d("isactivated", "isactivated is activated");
                    //SDK is activated
                } else {
                    Log.d("isactivated", "isactivated is not activated");
                    //SDK is not activated
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("isactivated", "where is activation status?");
                //There was an issue retrieving activation status
                t.printStackTrace();
            }
        });

        sdk.getEsperDeviceInfo(new EsperDeviceSDK.Callback<EsperDeviceInfo>() {
            @Override
            public void onResponse(@Nullable EsperDeviceInfo esperDeviceInfo) {
                String deviceId = esperDeviceInfo.getDeviceId();
                if (sdk.getAPILevel() >= EsperSDKVersions.TESSARION_MR2) {
                    String serialNo = esperDeviceInfo.getSerialNo();
                    String imei1 = esperDeviceInfo.getImei1();
                    String imei2 = esperDeviceInfo.getImei2();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
        sdk.getDeviceSettings(new EsperDeviceSDK.Callback<JSONObject>() {
            @Override
            public void onResponse(@Nullable JSONObject response) {
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
        /**/

        /*Ambient Light Adjustment pt 1-------------------------*/

        SensorManager sensorManager
                = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor lightSensor
                = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor == null) {
            Toast.makeText(this, "No Light Sensor! quit-", Toast.LENGTH_LONG).show();
        } else {
            float max = lightSensor.getMaximumRange();
            String maxReading = "Max Reading: " + max;

            sensorManager.registerListener(lightSensorEventListener,
                    lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

        }
        /**/

    }

    /*Ambient Light Adjustment pt 2-----------------------*/

    SensorEventListener lightSensorEventListener
            = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            EsperDeviceSDK sdk = EsperDeviceSDK.getInstance(getApplicationContext());

            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                float currentReading = event.values[0];

                if (currentReading < 50){
                    //Log.i("light", String.valueOf(currentReading));
                    sdk.setBrightness(10, new EsperDeviceSDK.Callback<Boolean>() {
                        @Override
                        public void onResponse(@Nullable Boolean response) {
                            if(response != null) {
                                String notNullResponse = response.toString();
                                //Log.d("Light onResponse not Null", notNullResponse);
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e("Brightness Adjustment", "Failed", t);
                        }
                    });

                } else if (currentReading < 200){
                    //Log.i("light", String.valueOf(currentReading));
                    sdk.setBrightness(50, new EsperDeviceSDK.Callback<Boolean>() {
                        @Override
                        public void onResponse(@Nullable Boolean response) {
                            if(response != null) {
                                String notNullResponse = response.toString();
                                //Log.d("Light onResponse not Null", notNullResponse);
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.d("Brightness Adjustment", "Failed", t);
                        }
                    });
                } else if (currentReading < 300){
                    //Log.i("light", String.valueOf(currentReading));
                    sdk.setBrightness(75, new EsperDeviceSDK.Callback<Boolean>() {
                        @Override
                        public void onResponse(@Nullable Boolean response) {
                            if(response != null) {
                                String notNullResponse = response.toString();
                                //Log.d("Light onResponse not Null", notNullResponse);
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e("Brightness Adjustment", "Failed", t);
                        }
                    });
                } else {
                    //Log.i("light", String.valueOf(currentReading));
                    sdk.setBrightness(100, new EsperDeviceSDK.Callback<Boolean>() {
                        @Override
                        public void onResponse(@Nullable Boolean response) {
                            if(response != null) {
                                String notNullResponse = response.toString();
                                //Log.d("Light onResponse not Null", notNullResponse);
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.d("Brightness Adjustment", "Failed", t);
                        }
                    });
                }

            }
        }

    };
/**/

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        stopHandler();
        startHandler();
    }

    public void stopHandler() {
        handler.removeCallbacks(r);
    }
    public void startHandler() {
        handler.postDelayed(r, 30*1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LocalDate mintime = new LocalDate().minusYears(5);
            LocalDate maxtime = new LocalDate().plusYears(5);
            alleventlist = Utility.readCalendarEvent(this, mintime, maxtime);
            calendarView.init(alleventlist,mintime, maxtime);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isgivepermission=true;
                    lastdate = new LocalDate();
                    calendarView.setCurrentmonth(new LocalDate());
                    calendarView.adjustheight();
                    mIsExpanded = false;
                    mAppBar.setExpanded(false, false);
                    mWeekView.notifyDatasetChanged();
                }
            }, 10);
        }
    }

    @Subscribe
    public void onEvent(MonthChange event) {


        if (!isAppBarExpanded()) {

            LocalDate localDate = new LocalDate();
            String year = event.getMessage().getYear() == localDate.getYear() ? "" : event.getMessage().getYear() + "";
            monthname.setText(event.getMessage().toString("MMMM") + " " + year);


            long diff = System.currentTimeMillis() - lasttime;
            boolean check = diff > 600;
            if (check && event.mdy > 0) {
                monthname.setTranslationY(35);
                mArrowImageView.setTranslationY(35);
                lasttime = System.currentTimeMillis();
                monthname.animate().translationY(0).setDuration(200).start();
                mArrowImageView.animate().translationY(0).setDuration(200).start();

            } else if (check && event.mdy < 0) {

                monthname.setTranslationY(-35);
                mArrowImageView.setTranslationY(-35);
                lasttime = System.currentTimeMillis();
                monthname.animate().translationY(0).setDuration(200).start();
                mArrowImageView.animate().translationY(0).setDuration(200).start();
            }


        }

    }

    /**
     * call when Googlecalendarview is open and tap on any date or scroll viewpager available inside GoogleCalendar
     */
    @Subscribe
    public void onEvent(MessageEvent event) {

        int previous = lastchangeindex;
        if (previous != -1) {
            int totalremove = 0;
            for (int k = 1; k <= 3; k++) {

                if (eventalllist.get(previous).getEventname().equals("dupli") || eventalllist.get(previous).getEventname().equals("click")) {
                    //totalremove++;
                    //EventModel eventModel = eventalllist.remove(previous);
                }
            }
            indextrack.clear();
            indextrack.putAll(dupindextrack);
            mNestedView.getAdapter().notifyDataSetChanged();

        }

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mNestedView.getLayoutManager();
        if (indextrack.containsKey(event.getMessage())) {
            int index = indextrack.get(event.getMessage());
            int type = eventalllist.get(index).getType();
            if (type == 0 || type == 2) {

                lastdate = event.getMessage();
                expandedfirst = index;
                topspace = 20;
                linearLayoutManager.scrollToPositionWithOffset(expandedfirst, 20);
                lastchangeindex = -1;

            } else {


                lastdate = event.getMessage();


                Integer ind = indextrack.get(event.getMessage());
                ind++;
                for (int i = ind; i < eventalllist.size(); i++) {

                    if (event.getMessage().isBefore(eventalllist.get(i).getLocalDate())) {
                        ind = i;
                        break;
                    }
                }
                lastchangeindex = ind;
                int typeselect = eventalllist.get(ind + 1).getType() == 200 ? 200 : 100;
                if (!eventalllist.get(ind - 1).getEventname().startsWith("dup")) {

                    eventalllist.add(ind, new EventModel("dupli", event.getMessage(), typeselect));
                    ind++;
                }
                expandedfirst = ind;
                eventalllist.add(ind, new EventModel("click", event.getMessage(), 1000));
                ind++;
                if (!eventalllist.get(ind).getEventname().startsWith("dup")) {

                    eventalllist.add(ind, new EventModel("dupli", event.getMessage(), typeselect));
                }
                mNestedView.getAdapter().notifyDataSetChanged();

                topspace = 20;
                linearLayoutManager.scrollToPositionWithOffset(expandedfirst, 20);

                for (int i = lastchangeindex; i < eventalllist.size(); i++) {
                    if (!eventalllist.get(i).getEventname().startsWith("dup"))
                        indextrack.put(eventalllist.get(i).getLocalDate(), i);
                }


            }

        } else {
            Integer ind = indextrack.get(event.getMessage().dayOfWeek().withMinimumValue().minusDays(1));
            ind++;
            for (int i = ind; i < eventalllist.size(); i++) {

                if (event.getMessage().isBefore(eventalllist.get(i).getLocalDate())) {
                    ind = i;
                    break;
                }
            }
            lastchangeindex = ind;
            int typeselect = eventalllist.get(ind + 1).getType() == 200 ? 200 : 100;
            if (!eventalllist.get(ind - 1).getEventname().startsWith("dup")) {

                eventalllist.add(ind, new EventModel("dupli", event.getMessage(), typeselect));
                ind++;
            }
            expandedfirst = ind;

            eventalllist.add(ind, new EventModel("click", event.getMessage(), 1000));
            ind++;
            if (!eventalllist.get(ind).getEventname().startsWith("dup")) {

                eventalllist.add(ind, new EventModel("dupli", event.getMessage(), typeselect));
            }

            mNestedView.getAdapter().notifyDataSetChanged();
            topspace = 20;
            linearLayoutManager.scrollToPositionWithOffset(expandedfirst, 20);

            for (int i = lastchangeindex; i < eventalllist.size(); i++) {
                if (!eventalllist.get(i).getEventname().startsWith("dup"))
                    indextrack.put(eventalllist.get(i).getLocalDate(), i);
            }

        }

    }

    private int getDeviceHeight() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int height1 = size.y;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return height1;
    }

    private int getDevicewidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return width;
    }

    @Override
    public void onBackPressed() {
        if (closebtn.getVisibility() == View.VISIBLE) {
            closebtnClick();

        } else if (mIsExpanded) {
            mIsExpanded = false;
            mNestedView.stopScroll();
            mAppBar.setExpanded(false, true);
        } else if (mNestedView.getVisibility() == View.VISIBLE) {
            monthviewpager.setCurrentItem(calendarView.calculateCurrentMonth(MainActivity.lastdate), false);

            mNestedView.setVisibility(View.GONE);
            monthviewpager.setVisibility(View.VISIBLE);
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
            ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(false);
            mAppBar.setElevation(0);
            mArrowImageView.setVisibility(View.INVISIBLE);
        } else {
            EventBus.getDefault().unregister(this);
            super.onBackPressed();
            finish();
        }


    }

    /**
     * call only one time after googlecalendarview init() method is done
     */
    @Subscribe
    public void onEvent(final AddEvent event) {
        eventalllist = event.getArrayList();



        final TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {

            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            int monthheight = getDeviceHeight() - actionBarHeight - getnavigationHeight() - getStatusBarHeight();
            int recyheight = monthheight - getResources().getDimensionPixelSize(R.dimen.monthtopspace);
            int singleitem = (recyheight - 18) / 6;

                monthviewpager.setAdapter(new MonthPageAdapter(getSupportFragmentManager(), event.getMonthModels(), singleitem));
                monthviewpager.setCurrentItem(calendarView.calculateCurrentMonth(LocalDate.now()), false);

        }


        indextrack = event.getIndextracker();
        for (Map.Entry<LocalDate, Integer> entry : indextrack.entrySet()) {
            dupindextrack.put(entry.getKey(), entry.getValue());
        }

        if (mNestedView.isAttachedToWindow()) {

            mNestedView.getAdapter().notifyDataSetChanged();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LocalDate localDate = new LocalDate();
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mNestedView.getLayoutManager();
                if (indextrack.containsKey(LocalDate.now())) {

                    Integer val = indextrack.get(LocalDate.now());
                    expandedfirst = val;
                    topspace = 20;
                    linearLayoutManager.scrollToPositionWithOffset(expandedfirst, 20);
                    EventBus.getDefault().post(new MonthChange(localDate, 0));
                    month = localDate.getDayOfMonth();
                    lastdate = localDate;


                }
            }
        }, 100);


    }

    private void setExpandAndCollapseEnabled(boolean enabled) {


        if (mNestedView.isNestedScrollingEnabled() != enabled) {
            ViewCompat.setNestedScrollingEnabled(mNestedView, enabled);
        }

    }

    @Override
    public boolean isAppBarClosed() {
        return isappbarclosed;
    }

    @Override
    public int appbaroffset() {
        return expandedfirst;
    }

    public void selectdateFromMonthPager(int year, int month, int day) {
        MainActivity.lastdate = new LocalDate(year, month, day);
        LocalDate localDate = new LocalDate();
        String yearstr = MainActivity.lastdate.getYear() == localDate.getYear() ? "" : MainActivity.lastdate.getYear() + "";
        monthname.setText(MainActivity.lastdate.toString("MMMM") + " " + yearstr);
        calendarView.setCurrentmonth(MainActivity.lastdate);
        calendarView.adjustheight();
        mIsExpanded = false;
        mAppBar.setExpanded(false, false);
        EventBus.getDefault().post(new MessageEvent(new LocalDate(year, month, day)));
        monthviewpager.setVisibility(View.GONE);
        mNestedView.setVisibility(View.VISIBLE);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
        ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(true);
        mAppBar.setElevation(20);
        mArrowImageView.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean isAppBarExpanded() {

        return mAppBarOffset == 0;
    }

    @Override
    public boolean isAppBarIdle() {
        return mAppBarIdle;
    }

    class MonthPageAdapter extends FragmentStatePagerAdapter {
        private ArrayList<MonthModel> monthModels;
        private int singleitemheight;

        // private ArrayList<MonthFragment> firstFragments=new ArrayList<>();

        public MonthPageAdapter(FragmentManager fragmentManager, ArrayList<MonthModel> monthModels, int singleitemheight) {

            super(fragmentManager);
            this.monthModels = monthModels;
            this.singleitemheight = singleitemheight;

//            for (int position=0;position<monthModels.size();position++){
//                firstFragments.add(MonthFragment.newInstance(monthModels.get(position).getMonth(), monthModels.get(position).getYear(), monthModels.get(position).getFirstday(), monthModels.get(position).getDayModelArrayList(), alleventlist, singleitemheight));
//            }
        }

//        public ArrayList<MonthFragment> getFirstFragments() {
//            return firstFragments;
//        }

        public ArrayList<MonthModel> getMonthModels() {
            return monthModels;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return monthModels.size();
        }


        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            return MonthFragment.newInstance(monthModels.get(position).getMonth(), monthModels.get(position).getYear(), monthModels.get(position).getFirstday(), monthModels.get(position).getDayModelArrayList(), alleventlist, singleitemheight);
        }


        // Returns the page title for the top indicator


    }

    public class DateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

        LocalDate today = LocalDate.now();

        public ArrayList<EventModel> geteventallList() {
            return eventalllist;
        }

        @Override
        public int getItemViewType(int position) {
            if (position > 1 && eventalllist.get(position).getType() == 0 && getHeaderId(position) == getHeaderId(position - 1))
                return 5;
            if (position > 1 && eventalllist.get(position).getType() == 3 && eventalllist.get(position - 1).getType() == 1)
                return 7;
            if (position + 1 < eventalllist.size() && eventalllist.get(position).getType() == 3 && (eventalllist.get(position + 1).getType() == 1 || eventalllist.get(position + 1).getType() == 0))
                return 6;
            return eventalllist.get(position).getType();
        }

        public int getHeaderItemViewType(int position) {
            return eventalllist.get(position).getType();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_item, parent, false);
                return new ItemViewHolder(view);
            } else if (viewType == 5) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewitemlessspace, parent, false);
                return new ItemViewHolder(view);
            } else if (viewType == 100) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.extraspace, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            } else if (viewType == 200) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.liitlespace, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            } else if (viewType == 1) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewlast, parent, false);
                return new EndViewHolder(view);
            } else if (viewType == 2) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.noplanlay, parent, false);
                return new NoplanViewHolder(view);
            } else if (viewType == 1000) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.noplanlittlespace, parent, false);
                return new NoplanViewHolder(view);
            } else if (viewType == 6) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rangelayextrabottomspace, parent, false);
                return new RangeViewHolder(view);
            } else if (viewType == 7) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rangelayextratopspace, parent, false);
                return new RangeViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rangelay, parent, false);
                return new RangeViewHolder(view);
            }

        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            int viewtype = getItemViewType(position);
            if (viewtype == 0 || viewtype == 5) {

                ItemViewHolder holder = (ItemViewHolder) viewHolder;
                GradientDrawable shape =  new GradientDrawable();
                shape.setCornerRadius( getResources().getDimensionPixelSize(R.dimen.fourdp) );
                shape.setColor(eventalllist.get(position).getColor());

                holder.eventtextview.setBackground(shape);
                holder.eventtextview.setText(eventalllist.get(position).getEventname());


                if (position + 1 < eventalllist.size() && eventalllist.get(position).getLocalDate().equals(today) && (!eventalllist.get(position + 1).getLocalDate().equals(today) || eventalllist.get(position + 1).getType() == 100 || eventalllist.get(position + 1).getType() == 200)) {
                    //holder.circle.setVisibility(View.VISIBLE);
                    //holder.line.setVisibility(View.VISIBLE);

                } else {
//                    holder.circle.setVisibility(View.GONE);
                   // holder.line.setVisibility(View.GONE);
                }
            } else if (viewtype == 1) {

                EndViewHolder holder = (EndViewHolder) viewHolder;
                //holder.eventimageview.setImageResource(monthresource[eventalllist.get(position).getLocalDate().getMonthOfYear() - 1]);
                holder.monthname.setText(eventalllist.get(position).getLocalDate().toString("MMMM YYYY"));
            } else if (viewtype == 2 || viewtype == 100 || viewtype == 200 || viewtype == 1000) {

            } else {
                RangeViewHolder holder = (RangeViewHolder) viewHolder;
                holder.rangetextview.setText(eventalllist.get(position).getEventname().replaceAll("tojigs", ""));
            }

        }

        @Override
        public long getHeaderId(int position) {


            if (eventalllist.get(position).getType() == 1) return position;
            else if (eventalllist.get(position).getType() == 3) return position;
            else if (eventalllist.get(position).getType() == 100) return position;
            else if (eventalllist.get(position).getType() == 200) return position;
            LocalDate localDate = eventalllist.get(position).getLocalDate();
            String uniquestr = "" + localDate.getDayOfMonth() + localDate.getMonthOfYear() + localDate.getYear();
            return Long.parseLong(uniquestr);

        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int position) {
            int viewtype = getHeaderItemViewType(position);
            if (viewtype == 2) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.todayheader, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            } else if (viewtype == 0 && eventalllist.get(position).getLocalDate().equals(today)) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.todayheader, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            } else if (viewtype == 1 || viewtype == 3 || viewtype == 100 || viewtype == 200) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            } else {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.headerview, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }

        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
            int viewtype = getHeaderItemViewType(position);
            if (viewtype == 0 || viewtype == 2 || viewtype == 1000) {
                TextView vartextView = holder.itemView.findViewById(R.id.textView9);
                TextView datetextView = holder.itemView.findViewById(R.id.textView10);
                vartextView.setText(var[eventalllist.get(position).getLocalDate().getDayOfWeek() - 1]);
                datetextView.setText(eventalllist.get(position).getLocalDate().getDayOfMonth() + "");
                holder.itemView.setTag(position);
            } else {

            }

        }

        @Override
        public int getItemCount() {
            return eventalllist.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {

            TextView eventtextview;
            View circle, line;

            public ItemViewHolder(View itemView) {
                super(itemView);
                eventtextview = itemView.findViewById(R.id.view_item_textview);
                eventtextview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (isAppBarExpanded()) {
                            mIsExpanded = !mIsExpanded;
                            mNestedView.stopScroll();

                            mAppBar.setExpanded(mIsExpanded, true);
                            return;
                        }
                       EventInfo eventInfo = alleventlist.get(eventalllist.get(getAdapterPosition()).getLocalDate());
                        String sfs=eventalllist.get(getAdapterPosition()).getEventname();
                        while (eventInfo!=null&&!sfs.startsWith(eventInfo.title)){
                            eventInfo=eventInfo.nextnode;
                        }

                        eventnametextview.setText(eventInfo.title);

                        if (eventInfo.isallday==false){
                            LocalDateTime start=new LocalDateTime(eventInfo.starttime, DateTimeZone.forID(eventInfo.timezone));
                            LocalDateTime end=new LocalDateTime(eventInfo.endtime, DateTimeZone.forID(eventInfo.timezone));
                            String sf=start.toString("a").equals(end.toString("a"))?"":"a";
                            String rangetext = daysList[start.getDayOfWeek()] + ", " + start.toString("d MMM") + " · " + start.toString("h:mm a"+sf+"") + " - " + end.toString("h:mm a");
                            eventrangetextview.setText(rangetext);
                        }
                        else if (eventInfo.noofdayevent>1){
                            LocalDate localDate=new LocalDate(eventInfo.starttime, DateTimeZone.forID(eventInfo.timezone));
                            LocalDate todaydate = LocalDate.now();
                            LocalDate nextday = localDate.plusDays(eventInfo.noofdayevent-1);
                            if (localDate.getYear() == todaydate.getYear()) {
                                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM") + " - " + daysList[nextday.getDayOfWeek()] + ", " + nextday.toString("d MMM");
                                eventrangetextview.setText(rangetext);
                            } else {
                                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM, YYYY") + " - " + daysList[nextday.getDayOfWeek()] + ", " + nextday.toString("d MMM, YYYY");
                                eventrangetextview.setText(rangetext);
                            }
                        }
                        else {
                            LocalDate localDate = new LocalDate(eventInfo.starttime);
                            LocalDate todaydate = LocalDate.now();
                            if (localDate.getYear() == todaydate.getYear()) {
                                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM") ;
                                eventrangetextview.setText(rangetext);
                            } else {
                                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM, YYYY") ;
                                eventrangetextview.setText(rangetext);
                            }
                        }


                        //holidaytextview.setText(eventInfo.accountname);
                        closebtn.setVisibility(View.VISIBLE);
                        eventnametextview.setVisibility(View.GONE);
                        roundrect.setVisibility(View.GONE);
                        eventrangetextview.setVisibility(View.GONE);
                        //calendaricon.setVisibility(View.GONE);
                        //holidaytextview.setVisibility(View.GONE);
                        eventfixstextview.setVisibility(View.GONE);

                        final View view = mNestedView.getLayoutManager().findViewByPosition(getAdapterPosition());
                        ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                        layoutParams.height = v.getHeight();
                        layoutParams.width = v.getWidth();
                        redlay.setLayoutParams(layoutParams);
                        redlay.setTranslationX(v.getLeft());
                        redlay.setTranslationY(view.getTop() + toolbar.getHeight());
                        redlay.setTranslationZ(0);

                            GradientDrawable shape =  new GradientDrawable();
                            shape.setCornerRadius( getResources().getDimensionPixelSize(R.dimen.fourdp) );
                            mycolor=eventalllist.get(getAdapterPosition()).getColor();
                            shape.setColor(mycolor);
                            redlay.setBackground(shape);
                            roundrect.setBackground(shape);



                        ValueAnimator animwidth = ValueAnimator.ofInt(redlay.getWidth(), getDevicewidth());
                        animwidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int val = (Integer) valueAnimator.getAnimatedValue();
                                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                                layoutParams.width = val;
                                redlay.setLayoutParams(layoutParams);
                            }
                        });
                        animwidth.setDuration(300);

                        ValueAnimator animheight = ValueAnimator.ofInt(redlay.getHeight(), getDeviceHeight());
                        animheight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int val = (Integer) valueAnimator.getAnimatedValue();
                                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                                layoutParams.height = val;
                                redlay.setLayoutParams(layoutParams);
                                if (redlay.getTranslationZ() == 0 && valueAnimator.getAnimatedFraction() > 0.15) {
                                    redlay.setBackgroundColor(Color.WHITE);
                                    shadow.setVisibility(View.VISIBLE);
                                    redlay.setTranslationZ(getResources().getDimensionPixelSize(R.dimen.tendp));
                                }
                            }
                        });
                        animheight.setDuration(300);

                        ValueAnimator animx = ValueAnimator.ofFloat(redlay.getTranslationX(), 0);
                        animx.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                Float val = (Float) valueAnimator.getAnimatedValue();
                                redlay.setTranslationX(val);
                            }
                        });
                        animx.setDuration(300);

                        ValueAnimator animy = ValueAnimator.ofFloat(redlay.getTranslationY(), 0);
                        animy.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                Float val = (Float) valueAnimator.getAnimatedValue();
                                redlay.setTranslationY(val);
                            }
                        });
                        animy.setDuration(300);

                        animheight.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        closebtn.setVisibility(View.VISIBLE);
                                        eventnametextview.setVisibility(View.VISIBLE);
                                        roundrect.setVisibility(View.VISIBLE);
                                        eventrangetextview.setVisibility(View.VISIBLE);
                                        //calendaricon.setVisibility(View.VISIBLE);
                                        //holidaytextview.setVisibility(View.VISIBLE);
                                        eventfixstextview.setVisibility(View.VISIBLE);
                                    }
                                }, 150);

                            }
                        });

                        /*delEvent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                v.setVisibility(View.GONE);

                            }
                        });*/

                        animwidth.start();
                        animheight.start();
                        animy.start();
                        animx.start();
                        eventview = v;
                        fullview = view;

                        }
                });

                circle = itemView.findViewById(R.id.circle);
                line = itemView.findViewById(R.id.line);
            }
        }

        class EndViewHolder extends RecyclerView.ViewHolder {

            //ScrollParallaxImageView eventimageview;
            View eventimageview;
            TextView monthname;

            public EndViewHolder(View itemView) {
                super(itemView);
                monthname = itemView.findViewById(R.id.textView11);
            }
        }

        class NoplanViewHolder extends RecyclerView.ViewHolder {

            TextView noplantextview;

            public NoplanViewHolder(View itemView) {
                super(itemView);
                noplantextview = itemView.findViewById(R.id.view_noplan_textview);
            }
        }

        class RangeViewHolder extends RecyclerView.ViewHolder {

            TextView rangetextview;

            public RangeViewHolder(View itemView) {
                super(itemView);
                rangetextview = itemView.findViewById(R.id.view_range_textview);
            }
        }
    }
    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        if (!isgivepermission)return new ArrayList<>();
        LocalDate initial = new LocalDate(newYear,newMonth,1);
        int length=initial.dayOfMonth().getMaximumValue();
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

        for (int i=1;i<=length;i++){
            LocalDate localDate=new LocalDate(newYear,newMonth,i);
            if (alleventlist.containsKey(localDate)){
                EventInfo eventInfo=alleventlist.get(localDate);
                while (eventInfo!=null){
                    if(eventInfo.timezone == null){
                        eventInfo.timezone = "America/Los_Angeles";
                    }
                    Calendar startTime = Calendar.getInstance(TimeZone.getTimeZone(eventInfo.timezone));
                    startTime.setTimeInMillis(eventInfo.starttime);
                    Calendar endTime = (Calendar) Calendar.getInstance(TimeZone.getTimeZone(eventInfo.timezone));
                    endTime.setTimeInMillis(eventInfo.endtime);
                   int dau= Days.daysBetween(new LocalDate(eventInfo.endtime), new LocalDate(eventInfo.starttime)).getDays();

                    WeekViewEvent event = new WeekViewEvent(eventInfo.id, eventInfo.title, startTime, endTime,eventInfo.accountname);

                    event.setAllDay(eventInfo.isallday);
                    event.setColor(eventInfo.eventcolor);
                    events.add(event);
                    eventInfo=eventInfo.nextnode;
                }
            }
        }
        return events;
    }

    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretday(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                if (mWeekView.getNumberOfVisibleDays()==7)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() ;
            }
            @Override
            public String interpretDate(Calendar date) {
                int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);


                return dayOfMonth+"" ;
            }

            @Override
            public String interpretTime(int hour) {
                if (hour == 12) return 12  + " PM";
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

        if (isAppBarExpanded()) {
            mIsExpanded = !mIsExpanded;
            mNestedView.stopScroll();

            mAppBar.setExpanded(mIsExpanded, true);
            return;
        }
        eventnametextview.setText(event.getName());
        if (event.isAllDay()==false){
            LocalDateTime start=new LocalDateTime(event.getStartTime().getTimeInMillis(), DateTimeZone.forTimeZone(event.getStartTime().getTimeZone()));
            LocalDateTime end=new LocalDateTime(event.getEndTime().getTimeInMillis(), DateTimeZone.forTimeZone(event.getEndTime().getTimeZone()));
            String sf=start.toString("a").equals(end.toString("a"))?"":"a";
            System.out.println("sf value---*" + sf + "*----");
            String rangetext = daysList[start.getDayOfWeek()] + ", " + start.toString("d MMM") + " · " + start.toString("h:mm a"+sf+"") + " - " + end.toString("h:mm a");
            eventrangetextview.setText(rangetext);
        }
        else if (event.isIsmoreday()){
            LocalDate localDate = new LocalDate(event.getActualstart().getTimeInMillis(), DateTimeZone.forTimeZone(event.getStartTime().getTimeZone()));
            LocalDate todaydate = LocalDate.now();
            LocalDate nextday = localDate.plusDays((int) (event.getNoofday()-1));
            if (localDate.getYear() == todaydate.getYear()) {
                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM") + " - " + daysList[nextday.getDayOfWeek()] + ", " + nextday.toString("d MMM");
                eventrangetextview.setText(rangetext);
            } else {
                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM, YYYY") + " - " + daysList[nextday.getDayOfWeek()] + ", " + nextday.toString("d MMM, YYYY");
                eventrangetextview.setText(rangetext);
            }
        }
        else {
            LocalDate localDate = new LocalDate(event.getStartTime().getTimeInMillis());
            LocalDate todaydate = LocalDate.now();
            if (localDate.getYear() == todaydate.getYear()) {
                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM") ;
                eventrangetextview.setText(rangetext);
            } else {
                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM, YYYY") ;
                eventrangetextview.setText(rangetext);
            }
        }

        //holidaytextview.setText(event.getAccountname());
        closebtn.setVisibility(View.VISIBLE);
        eventnametextview.setVisibility(View.GONE);
        roundrect.setVisibility(View.GONE);
        eventrangetextview.setVisibility(View.GONE);
        //calendaricon.setVisibility(View.GONE);
        //holidaytextview.setVisibility(View.GONE);
        eventfixstextview.setVisibility(View.GONE);

        final View view = new View(this);
        ViewGroup.LayoutParams layoutParams1=new ViewGroup.LayoutParams((int)eventRect.width(),(int)eventRect.height());
        view.setLeft((int) eventRect.left);
        view.setTop((int) eventRect.top);
        view.setRight((int) eventRect.right);
        view.setBottom((int) eventRect.bottom);
        view.setLayoutParams(layoutParams1);

        redlay.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
        layoutParams.height = (int) eventRect.height();
        layoutParams.width = (int) eventRect.width();
        redlay.setLayoutParams(layoutParams);
        redlay.setTranslationX(eventRect.left);
        redlay.setTranslationY(eventRect.top + toolbar.getHeight());

       if (event.getColor()!=0){
           GradientDrawable shape =  new GradientDrawable();
           shape.setCornerRadius( getResources().getDimensionPixelSize(R.dimen.fourdp) );
           mycolor=event.getColor();
           shape.setColor(mycolor);
           redlay.setBackground(shape);
           roundrect.setBackground(shape);

       }
       else {
           GradientDrawable shape =  new GradientDrawable();
           shape.setCornerRadius( getResources().getDimensionPixelSize(R.dimen.fourdp) );
           mycolor=Color.parseColor("#6e52b5");
           shape.setColor(mycolor);
           redlay.setBackground(shape);
           roundrect.setBackground(shape);


       }


        redlay.setTranslationZ(0);

        ValueAnimator animwidth = ValueAnimator.ofInt(redlay.getWidth(), getDevicewidth());
        animwidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                layoutParams.width = val;
                redlay.setLayoutParams(layoutParams);
            }
        });
        animwidth.setDuration(300);

        ValueAnimator animheight = ValueAnimator.ofInt(redlay.getHeight(), getDeviceHeight());
        animheight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                layoutParams.height = val;
                redlay.setLayoutParams(layoutParams);
                if (redlay.getTranslationZ() == 0 && valueAnimator.getAnimatedFraction() > 0.2) {
                    redlay.setBackgroundColor(Color.WHITE);
                    shadow.setVisibility(View.VISIBLE);
                    redlay.setTranslationZ(getResources().getDimensionPixelSize(R.dimen.tendp));
                }
            }
        });
        animheight.setDuration(300);

        ValueAnimator animx = ValueAnimator.ofFloat(redlay.getTranslationX(), 0);
        animx.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float val = (Float) valueAnimator.getAnimatedValue();
                redlay.setTranslationX(val);
            }
        });
        animx.setDuration(300);

        ValueAnimator animy = ValueAnimator.ofFloat(redlay.getTranslationY(), 0);
        animy.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float val = (Float) valueAnimator.getAnimatedValue();
                redlay.setTranslationY(val);
            }
        });
        animy.setDuration(300);

        animheight.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        closebtn.setVisibility(View.VISIBLE);
                        eventnametextview.setVisibility(View.VISIBLE);
                        roundrect.setVisibility(View.VISIBLE);
                        eventrangetextview.setVisibility(View.VISIBLE);
                        //calendaricon.setVisibility(View.VISIBLE);
                        //holidaytextview.setVisibility(View.VISIBLE);
                        eventfixstextview.setVisibility(View.VISIBLE);
                    }
                }, 150);

            }
        });
        animwidth.start();
        animheight.start();
        animy.start();
        animx.start();
        eventview = view;
        fullview = view;
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {


        if (weekviewcontainer.getVisibility() == View.GONE||!isgivepermission) return;
        if (isAppBarClosed()) {

            LocalDate localDate=new LocalDate(newFirstVisibleDay.get(Calendar.YEAR),newFirstVisibleDay.get(Calendar.MONTH)+1,newFirstVisibleDay.get(Calendar.DAY_OF_MONTH));
            MainActivity.lastdate=localDate;

            String year = localDate.getYear() == LocalDate.now().getYear() ? "" : localDate.getYear() + "";
            if (!monthname.getText().equals(localDate.toString("MMM") + " " + year)){
                MainActivity.lastdate=localDate;
                calendarView.setCurrentmonth(localDate);
                calendarView.adjustheight();
                mIsExpanded = false;
                mAppBar.setExpanded(false, false);
                monthname.setText(localDate.toString("MMM") + " " + year);

            }

        } else {
            // calendarView.setCurrentmonth(i);
        }
    }
    public void refresh(View view){
        onRestart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        LocalDate mintime = new LocalDate().minusYears(5);
        LocalDate maxtime = new LocalDate().plusYears(5);
        alleventlist = Utility.readCalendarEvent(this, mintime, maxtime);

        Intent i = new Intent(MainActivity.this, MainActivity.class);  //your class
        finish();
        //ViewGroup mainView = findViewById(R.id.drawer_layout);
        //mWeekView.invalidate();

        startActivity(i);

    }


    public void removeEvent(View view, String eventTitle) {
        System.out.println("EVENTITLE: ---" + eventTitle + "---");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            final String[] INSTANCE_PROJECTION = new String[] {
                    CalendarContract.Instances.EVENT_ID,      // 0
                    CalendarContract.Instances.BEGIN,         // 1
                    CalendarContract.Instances.TITLE          // 2
            };
            // The indices for the projection array above.
            final int PROJECTION_ID_INDEX = 0;
            final int PROJECTION_BEGIN_INDEX = 1;
            final int PROJECTION_TITLE_INDEX = 2;

            // Date Range
            Calendar beginTime = Calendar.getInstance();
            beginTime.set(beginTime.get(Calendar.YEAR), 0, 0, 0, 0);
            long startMillis = beginTime.getTimeInMillis();
            Calendar endTime = Calendar.getInstance();
            endTime.set(endTime.get(Calendar.YEAR) + 1, 11, 31, 11, 59);
            long endMillis = endTime.getTimeInMillis();


            // The ID of the recurring event whose instances you are searching for in the Instances table
            String selection = CalendarContract.Instances.TITLE + " = ?";
            String[] selectionArgs = new String[] {eventTitle};

            // Construct the query with the desired date range.
            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, startMillis);
            ContentUris.appendId(builder, endMillis);

            // Submit the query
            Cursor cur =  getContentResolver().query(builder.build(), INSTANCE_PROJECTION, selection, selectionArgs, null);

            while(cur.moveToNext()) {
                // Get the field values
                long eventID = cur.getLong(PROJECTION_ID_INDEX);
                long beginVal = cur.getLong(PROJECTION_BEGIN_INDEX);
                String title = cur.getString(PROJECTION_TITLE_INDEX);

                Uri deleteUri = null;
                deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
                int rows = getContentResolver().delete(deleteUri, null, null);
                Log.i("Calendar", "Rows deleted: " + rows);
            }
            if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                Log.e(TAG, "Permissions Denied");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, 200);
                }
            } else {
                isgivepermission=true;
                LocalDate mintime = new LocalDate().minusYears(5);
                LocalDate maxtime = new LocalDate().plusYears(5);
                alleventlist = Utility.readCalendarEvent(view.getContext(), mintime, maxtime);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
            Log.i("NEED PERMISSIONS", "Requesting Permissions");
        }
    }

    private boolean isEventAlreadyExist(String eventTitle, View v) {
        while (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) v.getContext(), new String[]{Manifest.permission.READ_CALENDAR}, MY_PERMISSIONS_REQUEST_READ_CALENDAR);
            }
        }

        final String[] INSTANCE_PROJECTION = new String[] {
                CalendarContract.Instances.EVENT_ID,      // 0
                CalendarContract.Instances.BEGIN,         // 1
                CalendarContract.Instances.TITLE          // 2
        };

        long calID = 4;
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(Calendar.getInstance().get(Calendar.YEAR) - 1, 0, 0, 0, 00);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.getInstance().get(Calendar.YEAR), 11, 31, 11, 59);
        endMillis = endTime.getTimeInMillis();

        // The ID of the recurring event whose instances you are searching for in the Instances table
        String selection = CalendarContract.Instances.TITLE + " = ?";
        String[] selectionArgs = new String[] {eventTitle};

        // Construct the query with the desired date range.
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);

        // Submit the query
        Cursor cur =  getContentResolver().query(builder.build(), INSTANCE_PROJECTION, selection, selectionArgs, null);

        return cur.getCount() > 0;
    }


    public void checkPermissionTester(ContentResolver cr, ContentValues values, View v, String eventTitle, AlertDialog d) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            long eventID = Long.parseLong(uri.getLastPathSegment());
            Log.i("Calendar", "Event Created, the event id is: " + eventID);
            d.dismiss();
            //Snackbar.make(v, eventTitle + "Event added!", Snackbar.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "Event Created", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
            Snackbar.make(v, "Permissions denied, please try again", Snackbar.LENGTH_SHORT).show();
        }
    }
}

