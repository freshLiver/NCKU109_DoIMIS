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

public class MainActivity extends AppCompatActivity {

    /***********************************************************************
     * DATA AND CONSTANTS
     ***********************************************************************/
    protected Button BtnOptSearch, BtnVoiceInput, BtnPlayHistory;
    protected EditText ETKeywords;
    protected TextView TVHistoryDate, TVHistoryContent;

    /// 歷史上的今天相關變數
    protected ArrayList<String[]> historyEvents;        // 所有歷史上的今天的事件，每個 element 為 String[Time,Intro]
    protected int iHistory;                             // 當前顯示的是哪個事件（index）

    /***********************************************************************
     * Constructors/onCreate & get bundle & component settings
     ***********************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get components and set their events
        setComponents();

        // init today's history
        try {
            this.historyEvents = null;
            initHistory();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void setComponents() {
        // ! Find Components
        this.BtnOptSearch = findViewById(R.id.BtnOptSearch);
        this.BtnVoiceInput = findViewById(R.id.BtnVoiceInput);
        this.BtnPlayHistory = findViewById(R.id.BtnPlayHistory);
        this.ETKeywords = findViewById(R.id.ETKeywords);

        this.TVHistoryDate = findViewById(R.id.TVHistoryDate);
        this.TVHistoryContent = findViewById(R.id.TVHistoryContent);

        // ! button clicked event
        this.BtnOptSearch.setOnClickListener(new MainButtonsMap(this));
        this.BtnPlayHistory.setOnClickListener(new MainButtonsMap(this));

        // ! history content clicked event
        this.TVHistoryContent.setOnClickListener(v -> changeHistory(false));
    }

    /***********************************************************************
     * CLASS METHODS
     ***********************************************************************/

    /**
     * 獲取歷史上的今天的事件，放入 historyEvents 後會進行洗牌並選擇 index 0 的事件作為預設事件
     */
    private void initHistory() throws InterruptedException {

        Thread thWiki = new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO get history event list

//                ↓ SAMPLE CODE
//                historyEvents = returnYourHistoryEventsHere();

//                ↓ 預設要顯示的測試事件(需要先 new ArrayList<>() )
//                historyEvents.add(new String[]{"8787", "8787878787"});
//                historyEvents.add(new String[]{"666", "77777"});
            }
        });
        thWiki.start();

        // 等待爬蟲結束後才會進行洗牌以及更新 UI
        thWiki.join();

        // 將所有事件洗牌並設定當前要顯示的 index 為 0
        this.iHistory = 0;

        if (this.historyEvents != null)
            Collections.shuffle(this.historyEvents);

        // 更新 UI 以及 index
        changeHistory(true);
    }

    /**
     * 切換到下一個歷史事件
     *
     * @param isInit 是否從 initHistory() 呼叫
     */
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    protected void changeHistory(boolean isInit) {
        // get today date
        Calendar today = Calendar.getInstance();

        // 如果不是正在初始化的話就需要 ++index (超出範圍後會回到 index 0)
        if (isInit == false && this.historyEvents != null)
            this.iHistory = (this.iHistory + 1) % this.historyEvents.size();

        // history events 裡面有事件（not null）的話，就根據當前 index
        if (this.historyEvents != null) {

            // 根據 event 年份以及今天日期設定要顯示的日期
            String dateInfo = String.format("西元 %4s / %02d / %02d "
                    , this.historyEvents.get(this.iHistory)[0]                  // 獲得 event 的年份（數字用 %04d, 字串用 %4s）
                    , today.get(Calendar.MONTH) + 1                             // 獲得今天月份（Calendar.MONTH 從 0 開始計算）
                    , today.get(Calendar.DAY_OF_MONTH));                        // 獲得今天日期
            this.TVHistoryDate.setText(dateInfo);

            // 將 TextView 的 text 設定為 event 的簡介
            this.TVHistoryContent.setText(this.historyEvents.get(this.iHistory)[1]);
        } else {

            // 如果沒有事件的話則顯示無事件
            String dateInfo = String.format("西元 YYYY / %02d / %02d "
                    , today.get(Calendar.MONTH) + 1
                    , today.get(Calendar.DAY_OF_MONTH));

            this.TVHistoryDate.setText(dateInfo);
            this.TVHistoryContent.setText("找不到歷史上的事件");
        }
        Toast.makeText(this, "下一則", Toast.LENGTH_SHORT).show();
    }


    /***********************************************************************
     * GETTERS/SETTERS
     ***********************************************************************/

    /**
     * 取得 EditText 中的文字作為關鍵字
     *
     * @return EditText.text
     */
    public String getKeywords() {
        return this.ETKeywords.getText().toString();
    }
}


class MainButtonsMap implements OnClickListener {

    public MainActivity main;

    public MainButtonsMap(MainActivity main) {
        this.main = main;
    }

    @Override
    @SuppressLint("NonConstantResourceId")
    public void onClick(View v) {
        switch (v.getId()) {

            /// 搜尋
            case R.id.BtnOptSearch:
                // bring keywords to next activity
                Intent optSearch = new Intent(v.getContext(), OptionActivity.class);

                // put keywords into bundle
                Bundle bundle = new Bundle();
                bundle.putString(String.valueOf(R.string.bundle_kws_main2opt), this.main.getKeywords());

                // put bundle into intent and start activity
                optSearch.putExtras(bundle);
                this.main.startActivity(optSearch);
                break;

            /// 台語語音輸入
            case R.id.BtnVoiceInput:
                break;


            /// 語音播放歷史
            case R.id.BtnPlayHistory:
                Thread thPlay = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO convert and play taiwanese here
                    }
                });

                // start this thread
                thPlay.start();

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }
}

