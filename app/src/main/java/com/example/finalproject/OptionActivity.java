package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        // ! find all components
        Button btnBackToMain = findViewById(R.id.BtnBackToMain);
        Button btnSearch = findViewById(R.id.BtnSearch);
        Button btnSetRangeStart = findViewById(R.id.BtnSetDateStart);
        Button btnSetRangeEnd = findViewById(R.id.BtnSetDateEnd);

        // ! button clicked event
        MyDatePickerListener.MainActivity = this;
        btnSearch.setOnClickListener(new OptSearchButtonsMap());
        btnBackToMain.setOnClickListener(new OptSearchButtonsMap());
        btnSetRangeStart.setOnClickListener(new OptSearchButtonsMap());
        btnSetRangeEnd.setOnClickListener(new OptSearchButtonsMap());
    }
}

class OptSearchButtonsMap implements View.OnClickListener {

    public static Calendar start = null, end = null;

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.BtnSearch:
                Intent optSearch = new Intent(v.getContext(), ResultActivity.class);

                v.getContext().startActivity(optSearch);
                break;

            case R.id.BtnBackToMain:
                start = null;
                end = null;
                ((Activity) v.getContext()).finish();
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

    // TODO convert settings to google search keyword
    public List<String> SettingsToGoogleKeyword(String keyword,
                                                String dateRange,
                                                List<String> media,
                                                List<String> newsTypes) {

        return null;
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
        try {
            if (this.ClickedBtnID == R.id.BtnSetDateStart) {
                OptSearchButtonsMap.start = Calendar.getInstance();
                OptSearchButtonsMap.start.set(year, month, day);
                TVStart.setText(String.format("%04d-%2d-%2d", year, month + 1, day));
            } else {
                OptSearchButtonsMap.end = Calendar.getInstance();
                OptSearchButtonsMap.end.set(year, month, day);
                TVEnd.setText(String.format("%04d-%2d-%2d", year, month + 1, day));
            }

        } catch (Exception e) {
            e.getStackTrace();
            Toast.makeText(view.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
