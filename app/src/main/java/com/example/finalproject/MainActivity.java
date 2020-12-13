package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    protected Button BntOptSearch, BtnPlayHistory;
    protected EditText ETKeywords;
    protected TextView TVHistoryDate, TVHistoryContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ! Find Components
        this.BntOptSearch = findViewById(R.id.BtnOptSearch);
        this.BtnPlayHistory = findViewById(R.id.BtnPlayHistory);
        this.ETKeywords = findViewById(R.id.ETKeywords);
        this.TVHistoryDate = findViewById(R.id.TVHistoryDate);
        this.TVHistoryContent = findViewById(R.id.TVHistoryContent);

        // ! button clicked event
        this.BntOptSearch.setOnClickListener(new MainButtonsMap());
        this.BtnPlayHistory.setOnClickListener(new MainButtonsMap());

        // ! history content clicked event
        this.TVHistoryContent.setOnClickListener(v -> NewHistory(false));

        // init today's history
        InitHistory();
    }

    /**
     * 歷史上的今天相關 method 以及 variables
     */
    protected ArrayList<String[]> historyEvents;
    protected int iHistory;

    private void InitHistory() {
        iHistory = 0;
        historyEvents = null;

        // TODO get history event list
        historyEvents = new ArrayList<>();
        historyEvents.add(new String[]{"8787", "8787878787"});
        historyEvents.add(new String[]{"666", "77777"});

        // shuffle history events
        Collections.shuffle(historyEvents);

        // new a history
        NewHistory(true);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    protected void NewHistory(boolean isInit) {
        // get today date
        Calendar today = Calendar.getInstance();

        // if is not init increase history index
        if (isInit == false)
            iHistory = (iHistory + 1) % historyEvents.size();

        // get new history
        if (historyEvents != null) {
            String dateInfo = String.format("西元 %4s 年 %2d 月 %2d 日"
                    , historyEvents.get(iHistory)[0]
                    , today.get(Calendar.MONTH)
                    , today.get(Calendar.DAY_OF_MONTH));

            TVHistoryDate.setText(dateInfo);
            TVHistoryContent.setText(historyEvents.get(iHistory)[1]);
        } else {
            String dateInfo = String.format("西元 YYYY 年 %2d 月 %2d 日"
                    , today.get(Calendar.MONTH)
                    , today.get(Calendar.DAY_OF_MONTH));

            TVHistoryDate.setText(dateInfo);
            TVHistoryContent.setText("No Event (NULL)");
        }
        Toast.makeText(this, "下一則", Toast.LENGTH_SHORT).show();
    }
}


class MainButtonsMap implements OnClickListener {

    @Override
    @SuppressLint("NonConstantResourceId")
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.BtnOptSearch:
                Intent optSearch = new Intent(v.getContext(), OptionActivity.class);
                v.getContext().startActivity(optSearch);
                break;
            case R.id.BtnPlayHistory:
                // TODO
                String msg = "台語播放";
                Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }
}

