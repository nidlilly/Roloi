package io.esper.roloi;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthFragment extends Fragment {
    private String title;
    private ArrayList<DayModel> dayModels;
    private RecyclerView gridView;
    private int firstday;
    private int month, year;
    private int singleitemheight;
    private int index;



    public MonthFragment() {
        // Required empty public constructor
    }

    public static MonthFragment newInstance(int month, int year, int page, ArrayList<DayModel> dayModels, HashMap<LocalDate, EventInfo> alleventlist, int singleitemheight) {
        MonthFragment fragmentFirst = new MonthFragment();
        Bundle args = new Bundle();
        args.putInt("singleitemheight", singleitemheight);
        args.putInt("firstday", page);
        args.putInt("month", month);
        args.putInt("year", year);
        LocalDate prevmonth = new LocalDate(year, month, 1);
        LocalDate todaydate = new LocalDate();
        ArrayList<DayModel> adapterdata = new ArrayList<>(43);
        for (int i = 0; i < 42; i++) {
            if (i < page) {
                LocalDate localDate = prevmonth.minusDays(page - i);

                DayModel dayModel = new DayModel();
                if (localDate.isEqual(todaydate)) {
                    dayModel.setToday(true);
                }
                dayModel.setDay(localDate.getDayOfMonth());
                dayModel.setMonth(localDate.getMonthOfYear());
                dayModel.setYear(localDate.getYear());
                if (alleventlist.containsKey(localDate)) {
                    dayModel.setEventInfo(alleventlist.get(localDate));
//                    if (alleventlist.get(localDate).isallday){
//                        LocalDate localDate1=new LocalDate(alleventlist.get(localDate).starttime, DateTimeZone.forID(alleventlist.get(localDate).timezone));
//                        LocalDate localDate2=new LocalDate(alleventlist.get(localDate).endtime, DateTimeZone.forID(alleventlist.get(localDate).timezone));
//                       int day = Days.daysBetween(localDate1,localDate2).getDays();
//                       dayModel.setNoofdayevent(day);
//                       Log.e("noofday",dayModel.getEvents()[0]+","+day);
//                    }
                }

                dayModel.setIsenable(false);
                adapterdata.add(dayModel);

            } else if (i >= dayModels.size() + page) {

                LocalDate localDate = prevmonth.plusDays(i - (page));
                DayModel dayModel = new DayModel();
                if (localDate.isEqual(todaydate)) {
                    dayModel.setToday(true);
                }
                dayModel.setDay(localDate.getDayOfMonth());
                dayModel.setMonth(localDate.getMonthOfYear());
                dayModel.setYear(localDate.getYear());
                dayModel.setIsenable(false);
                if (alleventlist.containsKey(localDate)) {
                    dayModel.setEventInfo(alleventlist.get(localDate));
                    if (alleventlist.get(localDate).isallday){
                        LocalDate localDate1=new LocalDate(alleventlist.get(localDate).starttime, DateTimeZone.forID(alleventlist.get(localDate).timezone));
                        LocalDate localDate2=new LocalDate(alleventlist.get(localDate).endtime, DateTimeZone.forID(alleventlist.get(localDate).timezone));
                        int day = Days.daysBetween(localDate1,localDate2).getDays();
                        dayModel.setNoofdayevent(day);
                        //Log.e("noofday",dayModel.getEvents()[0]+","+day);
                    }
                }
                adapterdata.add(dayModel);
            } else {
                DayModel dayModel = dayModels.get(i - page);
                dayModel.setIsenable(true);
                if (dayModel.isToday()) {
                    args.putInt("index", i % 7);
                }
                LocalDate mydate = new LocalDate(year, month, dayModel.getDay());
                if (alleventlist.containsKey(mydate)) {
                    dayModel.setEventInfo(alleventlist.get(mydate));
//                    if (alleventlist.get(mydate).isallday){
//                        LocalDate localDate1=new LocalDate(alleventlist.get(mydate).starttime, DateTimeZone.forID(alleventlist.get(mydate).timezone));
//                        LocalDate localDate2=new LocalDate(alleventlist.get(mydate).endtime, DateTimeZone.forID(alleventlist.get(mydate).timezone));
//                        int day = Days.daysBetween(localDate1,localDate2).getDays();
//                        dayModel.setNoofdayevent(day);
//                        Log.e("noofday",dayModel.getEvents()[0]+","+day);
//                    }
                }
                adapterdata.add(dayModels.get(i - page));

            }
        }
        fragmentFirst.dayModels = adapterdata;
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstday = getArguments().getInt("firstday");
        month = getArguments().getInt("month");
        year = getArguments().getInt("year");
        index = getArguments().getInt("index", -1);
        singleitemheight = getArguments().getInt("singleitemheight");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_month, container, false);
        JCalendarMonthView jCalendarMonthView=view.findViewById(R.id.jcalendarmonthview);
        jCalendarMonthView.setDayModels(dayModels,index);


//        RecyclerView gridView = view.findViewById(R.id.recyclerview);
//        ConstraintLayout constraintLayout = view.findViewById(R.id.dd);
//        for (int i = 0; i < constraintLayout.getChildCount(); i++) {
//            TextView textView = (TextView) constraintLayout.getChildAt(i);
//            if (i == index) {
//                textView.setTextColor(getResources().getColor(R.color.selectday));
//            } else {
//                textView.setTextColor(getResources().getColor(R.color.unselectday));
//            }
//        }
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 7) {
//
//
//        };
//        gridView.setLayoutManager(gridLayoutManager);
//        MiddleDividerItemDecoration vertecoration = new MiddleDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
//        //vertecoration.setDrawable(new ColorDrawable(Color.LTGRAY));
//        MiddleDividerItemDecoration hortdecoration = new MiddleDividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL);
//        // hortdecoration.setDrawable(new ColorDrawable(Color.LTGRAY));
//        gridView.addItemDecoration(vertecoration);
//        gridView.addItemDecoration(hortdecoration);
//
//        gridView.setAdapter(new Myadapter());
        return view;

    }


    class Myadapter extends RecyclerView.Adapter<Myadapter.MonthViewHolder> {


        @Override
        public MonthViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

         //   RelativeLayout relativeLayout = new RelativeLayout(getActivity());

            // Defining the RelativeLayout layout parameters.
            // In this case I want to fill its parent
//            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.MATCH_PARENT,
//                    singleitemheight);
//            relativeLayout.setMinimumHeight(singleitemheight);
//
//            TextView tv = new TextView(getActivity());
//            tv.setText("Test");
//
//            // Defining the layout parameters of the TextView
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.WRAP_CONTENT,
//                    RelativeLayout.LayoutParams.WRAP_CONTENT);
//            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
//
//            // Setting the parameters on the TextView
//            tv.setLayoutParams(lp);
//
//            // Adding the TextView to the RelativeLayout as a child
//            relativeLayout.addView(tv);
            View view = null;
            if (viewType == 0) {
                view = getActivity().getLayoutInflater().inflate(R.layout.monthgriditemlspace, parent, false);
            } else {
                view = getActivity().getLayoutInflater().inflate(R.layout.monthgriditem, parent, false);
            }

            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = singleitemheight;
            view.setLayoutParams(layoutParams);
            return new MonthViewHolder(view);

        }


        @Override
        public void onBindViewHolder(MonthViewHolder holder, int position) {


            holder.textView.setText(dayModels.get(position).getDay() + "");
            if (dayModels.get(position).isToday()) {
                holder.textView.setBackgroundResource(R.drawable.smallcircle);
                holder.textView.setTextColor(Color.WHITE);
            } else if (dayModels.get(position).isenable()) {
                holder.textView.setTextColor(Color.BLACK);
                holder.textView.setBackgroundColor(Color.TRANSPARENT);
            } else {
                holder.textView.setBackgroundColor(Color.TRANSPARENT);
                holder.textView.setTextColor(getResources().getColor(R.color.lightblack));

            }
            DayModel dayModeltemp = dayModels.get(position);
            String names[] = dayModels.get(position).getEvents();
            if (names != null) {
                if (names.length == 1) {
                    holder.event1.setVisibility(View.VISIBLE);
                    holder.event2.setVisibility(View.GONE);
                    holder.event3.setVisibility(View.GONE);
                    holder.event2.setText("");
                    holder.event3.setText("");
                } else if (names.length == 2) {
                    holder.event1.setVisibility(View.VISIBLE);
                    holder.event2.setVisibility(View.VISIBLE);
                    holder.event3.setVisibility(View.GONE);
                    holder.event3.setText("");

                } else {
                    holder.event1.setVisibility(View.VISIBLE);
                    holder.event2.setVisibility(View.VISIBLE);
                    holder.event3.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < dayModels.get(position).getEvents().length; i++) {
                    if (i == 0) holder.event1.setText(names[0]);
                    else if (i == 1) holder.event2.setText(names[1]);
                    else holder.event3.setText(names[2]);

                }
            } else {
                holder.event1.setVisibility(View.GONE);
                holder.event2.setVisibility(View.GONE);
                holder.event3.setVisibility(View.GONE);

            }


        }

        @Override
        public int getItemCount() {

            return 42;
        }

        @Override
        public int getItemViewType(int position) {
            if (position % 7 == 0) return 0;
            else return 1;
        }

        class MonthViewHolder extends RecyclerView.ViewHolder {

            private TextView textView;
            private TextView event1;
            private TextView event2;
            private TextView event3;

            public MonthViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textView8);
                event1 = itemView.findViewById(R.id.event1);
                event2 = itemView.findViewById(R.id.event2);
                event3 = itemView.findViewById(R.id.event3);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MainActivity mainActivity = (MainActivity) getActivity();
                        if (mainActivity != null) {
                            DayModel dayModel = dayModels.get(getAdapterPosition());
                            mainActivity.selectdateFromMonthPager(dayModel.getYear(), dayModel.getMonth(), dayModel.getDay());
                        }
                    }
                });

            }
        }
    }
}