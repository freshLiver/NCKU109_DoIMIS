package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import android.os.StrictMode;

public class MainActivity extends Activity {

    /***********************************************************************
     * DATA AND CONSTANTS
     ***********************************************************************/
    protected ImageButton BtnOptSearch, BtnVoiceInput;
    protected Button BtnPlayHistory;
    protected EditText ETKeywords;
    protected TextView TVHistoryDate, TVHistoryContent;

    /// 歷史上的今天相關變數
    public ArrayList<String[]> historyEvents;        // 所有歷史上的今天的事件，每個 element 為 String[Time,Intro]
    public int iHistory;                             // 當前顯示的是哪個事件（index）

    private ImageView mic, mic_red;
    private TaiwaneseRecorder taiwaneseRecorder;
    private MediaRecorder mediaRecorder = null;
    private File recordFile;
    private String taiRecString;

    public tw2ch change = new tw2ch();
    private static final String token = "eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9" +
            ".eyJzY29wZXMiOiIwIiwidmVyIjowLjEsImlhdCI6MTYwMDEzNzMwNSwic2VydmljZV9pZCI6IjEwIiwiZXhwIjoxNjYzMjA5MzA1LCJhdWQiOiJ3bW1rcy5jc2llLmVkdS50dyIsInN1YiI6IiIsImlkIjozNDAsImlzcyI6IkpXVCIsIm5iZiI6MTYwMDEzNzMwNSwidXNlcl9pZCI6IjExOSJ9.WVjd0GjTKpVEflf2rteOfxL495XYpzUelcSI4SIJwBI5sQIvMGh-z7dcclmGRmFrloEe4qzcTs9Q1nFO8fR74ayXkCsreQY9CyuInYvfWAOJCxM0iWwFCGlBYAfFgEIoQDrv-696KVduywafWjQYOaiX4Ggw0AXmoRrU_TcO6ks";
    public String data ;

    /***********************************************************************
     * Constructors/onCreate & get bundle & component settings
     ***********************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // must before setContentView
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_main);

        // get components and set their events
        setComponents();
        checkPermission();
        button();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        // init today's history
        try {
            this.historyEvents = null;
            initHistory();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String get_array(){
        return this.historyEvents.get(this.iHistory)[1];
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
        this.BtnVoiceInput.setOnClickListener(new MainButtonsMap(this));
        // ! history content clicked event
        this.TVHistoryContent.setOnClickListener(v -> changeHistory(false));
        this.TVHistoryContent.setMovementMethod(new ScrollingMovementMethod());
    }

    public void button() {
        BtnVoiceInput.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //mic_red.setVisibility(View.VISIBLE);
                    //mic.setVisibility(View.INVISIBLE);
                    try {
                        recordFile = File.createTempFile("record_temp", "m4a", getCacheDir());
                        mediaRecorder = new MediaRecorder();
                        mediaRecorder.setOutputFile(recordFile.getAbsolutePath());
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                        mediaRecorder.setAudioEncodingBitRate(326000);
                        mediaRecorder.setAudioSamplingRate(44100);
                        mediaRecorder.setAudioChannels(1);
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //mic_red.setVisibility(View.INVISIBLE);
                    //mic.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "處理中，請稍等一下", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            endTaiwaneseRecognition();
                        }
                    }, 500);

                }
                return true;
            }
        });
    }

    public void endTaiwaneseRecognition() {
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
        taiwaneseRecorder = new TaiwaneseRecorder();
        try {
            taiRecString = taiwaneseRecorder.execute(recordFile.getAbsolutePath()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //data = token + "@@@" + taiRecString;
        //String result = change.sendText2("140.116.245.149", 27002, data);

        String data = token + "@@@" + "覓佇遐五四三";
        String result = change.sendText2("140.116.245.149", 27002, data);

        ETKeywords.setText(result);
    }

    public void checkPermission() {
        int recordPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int writePermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (recordPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }

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
                Calendar today = Calendar.getInstance();
                int month_tmp = today.get(Calendar.MONTH) + 1;
                String month = Integer.toString(month_tmp);
                int day_tmp = today.get(Calendar.DAY_OF_MONTH);
                String day = Integer.toString(day_tmp);
                try {
                    historyEvents = new ArrayList<>();

                    String page = "https://zh.wikipedia.org/zh-tw/" + month + "%E6%9C%88" + day + "%E6%97%A5";
                    Document document = Jsoup.connect(page).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                            + "(KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36").get();
                    Elements parentElement = document.getElementsByTag("ul");
                    boolean flag = false;
                    //ArrayList<String> event_list = new ArrayList<>();
                    //ArrayList<Integer> event_year = new ArrayList<>();
                    for (Element ele : parentElement) {
                        Elements childElement = ele.getElementsByTag("li");
                        for (Element ele_child : childElement) {
                            String name = ele_child.text();
                            if (check_event(name)) {
                                if (check_year(name) > 1900) {
                                    flag = true;
                                }
                                if (flag) {
                                    if (check_year(name) > 1900) {
                                        //event_list.add(seperate(name));
                                        //event_year.add(check_year(name));
                                        historyEvents.add(new String[]{Integer.toString(check_year(name)), seperate(name)});

                                    } else {
                                        break;
                                    }
                                } else {
                                    historyEvents.add(new String[]{Integer.toString(check_year(name)), seperate(name)});
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    public static boolean check_event(String text) { //check whether events or not
        String tmp = "";
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '年') {
                try {
                    int result = Integer.parseInt(tmp);
                    if (result > 0 || result < 2100) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
            } else {
                tmp += text.charAt(i);
            }
        }

        return false;
    }

    public static int check_year(String text) {
        String tmp = "";
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '年') {
                try {
                    int result = Integer.parseInt(tmp);
                    return result;
                } catch (NumberFormatException e) {
                    return 0;
                }
            } else {
                tmp += text.charAt(i);
            }
        }
        return 0;
    }

    public static String seperate(String text) {
        String tmp = "";
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '年') {
                try {
                    //int result = Integer.parseInt(tmp);
                    return text.substring(i + 2, text.length());
                } catch (NumberFormatException e) {
                    return "";
                }
            }
        }
        return "";
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
            //this.TVHistoryContent.setText(this.event_list.get(0));
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
    private MediaPlayer mediaPlayer;
    private TaiwaneseSynthesis taiwaneseSynthesis;
    private String wav_path;
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

                        taiwaneseSynthesis = new TaiwaneseSynthesis();
                        try {

                            wav_path = taiwaneseSynthesis.execute(main.get_array()).get();
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(wav_path);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mp.release();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

