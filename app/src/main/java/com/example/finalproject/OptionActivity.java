package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OptionActivity extends AppCompatActivity {

    private String keywords;
    private ArrayList<String> checkedMedia, checkedTypes;
    public TextView TVStart, TVEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        // init arraylist to log checked boxes
        this.checkedMedia = new ArrayList<>();
        this.checkedTypes = new ArrayList<>();

        // get keywords from bundle
        Bundle bundle = this.getIntent().getExtras();
        this.keywords = bundle.getString(String.valueOf(R.string.bundle_kws_main2opt));

        // ! find all components
        Button btnBackToMain = findViewById(R.id.BtnBackToMain);
        Button btnSearch = findViewById(R.id.BtnSearch);
        Button btnSetRangeStart = findViewById(R.id.BtnSetDateStart);
        Button btnSetRangeEnd = findViewById(R.id.BtnSetDateEnd);
        TVStart = findViewById(R.id.TVOptDateStart);
        TVEnd = findViewById(R.id.TVOptDateEnd);

        // set default date
        MyDatePickerListener.MainActivity = this;

        // ! button clicked event
        btnSearch.setOnClickListener(new OptSearchButtonsMap(this));
        btnBackToMain.setOnClickListener(new OptSearchButtonsMap(this));
        btnSetRangeStart.setOnClickListener(new OptSearchButtonsMap(this));
        btnSetRangeEnd.setOnClickListener(new OptSearchButtonsMap(this));
    }

    public Pair<Boolean, String> checkLegalSearch() {
        // result is a pair <illegal, msg> or <legal, "keywords & before & after">
        Pair<Boolean, String> result;

        // get start and end date
        if (OptSearchButtonsMap.start == null || OptSearchButtonsMap.end == null)
            return new Pair<>(false, "請設定開始以及結束日期");
        String after = this.TVStart.getText().toString();
        String before = this.TVEnd.getText().toString();


        // get wish types
        this.checkedTypes.clear();
        final LinearLayout LLNewsTypes = findViewById(R.id.LLNewsTypes);
        final ArrayList<View> types = getChildrenWithTag(LLNewsTypes, "TYPES");

        // check all children whose tag is TYPES
        for (View v : types) {
            CheckBox box = (CheckBox) v;
            if (box.isChecked() == true)
                this.checkedTypes.add(box.getText().toString());
        }
        // if no type checked, return false
        if (checkedTypes.size() == 0)
            return new Pair<>(false, "請至少句選一個類別");

        // get wish media
        this.checkedMedia.clear();
        final LinearLayout LLNewsMedia = findViewById(R.id.LLNewsMedia);
        final ArrayList<View> media = getChildrenWithTag(LLNewsMedia, "MEDIA");

        // check all children whose tag is MEDIA_PARENT
        for (View v : media) {
            CheckBox box = (CheckBox) v;
            if (box.isChecked() == true)
                this.checkedMedia.add(box.getText().toString());
        }
        // return false if all not checked
        if (checkedMedia.size() == 0)
            return new Pair<>(false, "請至少句選一項媒體");

        // everything fine, build query
        String query = String.format("%s before:%s after:%s", this.keywords, before, after);
        return new Pair<>(true, query);
    }

    public Pair<ArrayList<String>, ArrayList<String>> getMediaAndTypes() {
        return new Pair<>(this.checkedMedia, this.checkedTypes);
    }

    public ArrayList<View> getChildrenWithTag(View v, String tag) {

        // if not viewgroup, only check tag
        if ((v instanceof ViewGroup) == false) {
            // if this view with same tag, return it as list
            if (v.getTag() != null && (v.getTag().toString().equals(tag))) {
                ArrayList<View> views = new ArrayList<>();
                views.add(v);
                return views;
            }
            // if diff tag, just return empty list
            return new ArrayList<>();
        }

        // if it is viewgroup, might have many children
        ArrayList<View> result = new ArrayList<>();

        // convert to viewgroup and check its children views
        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            // get its children with same tag
            View child = viewGroup.getChildAt(i);
            result.addAll(getChildrenWithTag(child, tag));
        }

        // return all children
        return result;
    }
}

class OptSearchButtonsMap implements View.OnClickListener {

    public static Calendar start = null, end = null;
    @SuppressLint("StaticFieldLeak")
    private final OptionActivity optActivity;

    public OptSearchButtonsMap(OptionActivity opt) {
        this.optActivity = opt;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.BtnSearch:
                // check setting legal or not
                Pair<Boolean, String> res = optActivity.checkLegalSearch();

                // illegal, show toast for hint
                if (res.first == false) {
                    Toast.makeText(optActivity, res.second, Toast.LENGTH_SHORT).show();
                }
                // legal, do search
                else {
                    Intent optSearch = new Intent(this.optActivity, ResultActivity.class);

                    // show query
                    Toast.makeText(optActivity, res.second, Toast.LENGTH_SHORT).show();
                    // use bundle to send settings
                    Bundle bundle = new Bundle();

                    ArrayList<String> media = this.optActivity.getMediaAndTypes().first;
                    ArrayList<String> types = this.optActivity.getMediaAndTypes().second;
                    bundle.putStringArrayList("MEDIA", media);
                    bundle.putStringArrayList("TYPES", types);

                    // wrap bundle with intent and start result activity
                    optSearch.putExtras(bundle);
                    this.optActivity.startActivity(optSearch);
                }
                break;

            case R.id.BtnBackToMain:
                start = null;
                end = null;
                optActivity.finish();
                break;

            case R.id.BtnSetDateStart:
            case R.id.BtnSetDateEnd:
                DatePickerDialog picker = new DatePickerDialog(v.getContext());

                // get today date info
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int month = Calendar.getInstance().get(Calendar.MONTH);
                int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

                // set max date (today)
                picker.getDatePicker().setMaxDate(new Date().getTime());

                // if now setting start time and end is set -> set max date
                if (v.getId() == R.id.BtnSetDateStart && (end != null))
                    picker.getDatePicker().setMaxDate(end.getTimeInMillis());

                // if now setting end time and start is set -> set min date
                if (v.getId() == R.id.BtnSetDateEnd && (start != null))
                    picker.getDatePicker().setMinDate(start.getTimeInMillis());

                // set listener and default date
                picker.setCancelable(true);
                picker.updateDate(year, month, day);
                picker.setOnDateSetListener(new MyDatePickerListener(v.getId()));

                // show picker dialog
                picker.show();
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }
}

class MyDatePickerListener implements DatePickerDialog.OnDateSetListener {

    @SuppressLint("StaticFieldLeak")
    public static Activity MainActivity;
    private final int ClickedBtnID;

    public MyDatePickerListener(int btnID) {
        this.ClickedBtnID = btnID;
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // get target tv
        TextView TVStart = MainActivity.findViewById(R.id.TVOptDateStart);
        TextView TVEnd = MainActivity.findViewById(R.id.TVOptDateEnd);

        // set target TV Text and start or end date
        if (this.ClickedBtnID == R.id.BtnSetDateStart) {
            OptSearchButtonsMap.start = Calendar.getInstance();
            OptSearchButtonsMap.start.set(year, month, day);
            TVStart.setText(String.format("%4d-%02d-%02d", year, month, day));

        } else {
            OptSearchButtonsMap.end = Calendar.getInstance();
            OptSearchButtonsMap.end.set(year, month, day);
            TVEnd.setText(String.format("%4d-%02d-%02d", year, month, day));
        }
    }

}
