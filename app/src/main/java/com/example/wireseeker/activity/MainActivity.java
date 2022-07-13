package com.example.wireseeker.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.wireseeker.R;
import com.example.wireseeker.adapter.WireAdapter;
import com.example.wireseeker.database.Wire;
import com.example.wireseeker.database.WireDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class MainActivity extends AppCompatActivity implements RecognitionListener {
    // public static final String EXTRA_MESSAGE = "com.example.myriads.MESSAGE";
    private EditText main_et;
    private String temp;
    private List<Wire> wireList;        //固定显示在Activity全部的数据
    private List<Wire> wireListQuery;   //查询的数据
    private WireAdapter wireAdapterQuery;   //适配器2
    private WireDatabase wireDatabase;      //数据库实例
    // private Handler handler;
    private ListView lvWireQuery;
    private static final String FORECAST_SEARCH = "forecast";
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private ExecutorService executorService;    //线程池
    private SpeechRecognizer recognizer;        //话筒

    public static final String EXTRA_MESSAGE = "com.example.wireseeker.MESSAGE";
    private int isLongClick = 0;
   // private HashMap<String, Integer> captions;
    private static final String DATA_PATH=  "userInfo";

    private int wordNum = 16;
    private int THEME_MODE = Configuration.UI_MODE_NIGHT_NO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wordNum = readUserInfoWord();
        THEME_MODE = readUserInfoTheme();
       // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setting(THEME_MODE);
        setContentView(R.layout.activity_main);
        WatchInput();
        ImageButton recordButton = findViewById(R.id.recordButton);
        recordButton.setOnLongClickListener(new recordButtonListener());
        recordButton.setOnClickListener(new stopRecordListener());
        // 初始化wireList
        wireList = new ArrayList<>();
        ListView lvWire = findViewById(R.id.lvWire);

        //适配器1
        WireAdapter wireAdapter = new WireAdapter(MainActivity.this, wireList, wordNum);
        lvWire.setAdapter(wireAdapter);

        //同上
        wireListQuery = new ArrayList<>();
        lvWireQuery = findViewById(R.id.lvWireQuery);

        executorService = Executors.newFixedThreadPool(10);

        // 创建wireDatabase实例
        wireDatabase = WireDatabase.getDatabase(this);
        InitWireDB();

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
        runRecognizerSetup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.setting:
                sendMessage(item.getActionView());
                return true;
            case R.id.about:
                showAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void sendMessage(View view)
    {
        Intent intent = new Intent(this, SettingActivity.class);
        String message = "test";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
    private void setting(int currentNightMode)
    {
        //int currentNightMode = getResources().getConfiguration().uiMode  &Configuration.UI_MODE_NIGHT_MASK;
        switch(currentNightMode)
        {
            case Configuration.UI_MODE_NIGHT_NO:
            {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                recreate();
                break;//黑夜模式
            }
            case Configuration.UI_MODE_NIGHT_YES:
            {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                recreate();
                break;//白天模式
            }
        }
    }

    private void showAbout()
    {

        AlertDialog.Builder builder5=new AlertDialog.Builder(MainActivity.this);
        View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog,null);
        builder5.setTitle("WireSeeker 1.0").setView(view).show();
    }
    /**
     * Called when the user taps the Send button
     */
    // watch edit-text-view input, once text change, query in database
    public void WatchInput(){
       main_et = this.findViewById(R.id.editTextTextPersonName);
       main_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = main_et.getText().toString();
                String str = filterChinese(text);
                if(!str.equals(""))
                {
                    WireQuery(str);
                }
                else
                {
                    wireListQuery.clear();
                }
                wireAdapterQuery = null;
                wireAdapterQuery = new WireAdapter(MainActivity.this, wireListQuery, wordNum);
                lvWireQuery.setAdapter(wireAdapterQuery);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
    // 过滤中文标点
    public String filterChinese(String str){
         String regEx="[\u3002|\uff1f|\uff01|\uff0c]?";
        Pattern p= Pattern.compile(regEx);
        Matcher matcher = p.matcher(str);
        return matcher.replaceAll("").trim();
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {

    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        ((TextView) findViewById(R.id.editTextTextPersonName)).setText("");
        if (hypothesis != null) {
            String sourceText = hypothesis.getHypstr();
            String[] strings = sourceText.split(" ");
            StringBuilder text = new StringBuilder();
            for (String str : strings) {
                System.out.println(str);
                switch (str)
                {
                    case "YI": text.append("1");break;
                    case "YAO": text.append("1");break;
                    case "ER": text.append("2");break;
                    case "SAN": text.append("3");break;
                    case "SI": text.append("4");break;
                    case "WU": text.append("5");break;
                    case "LIU": text.append("6");break;
                    case "QI": text.append("7");break;
                    case "BA": text.append("8");break;
                    case "JIU": text.append("9");break;
                    case "LING": text.append("0");break;
                    case "DIT": text.append(".");break;
                    default:break;
                }
            }
            ((TextView) findViewById(R.id.editTextTextPersonName)).setText(text.toString());
            //makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {

    }
    class recordButtonListener implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View v) {
            isLongClick = 1;
           /* mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(FileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("Audio Tag", "prepare() failed");
            }
            mRecorder.start();
            */
            recognizer.startListening(FORECAST_SEARCH,10000);
            return false; //KeyPoint：setOnLongClickListener中return的值决定是否在长按后再加一个短按动作，true为不加短按,false为加入短按
        }
    }
    class stopRecordListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(isLongClick==1){
//                mRecorder.stop();
//                mRecorder.release();
//                mRecorder = null;
                recognizer.stop();
                Log.d("2","测试");
                isLongClick=0;
            }
        }
    }
    // init database
    private void InitWireDB(){
        executorService.submit(new Runnable() {
            @Override
            public void run(){
                wireList.clear();
                wireList.addAll(wireDatabase.WireDao().getAllList());
            }
        });
    }
    // with string, search in database, return result list
    private void WireQuery(String str){
        executorService.submit(new Runnable() {
            @Override
            public void run(){
                temp = str;
                Log.d("2","看看输出"+str);
                wireListQuery.clear();
                wireListQuery.addAll(wireDatabase.WireDao().matchQuery(temp));
                Log.d("1",wireListQuery.toString());
            }
        });
    }

    private void runRecognizerSetup(){
        executorService.submit(new Runnable() {
            @Override
            public void run(){
                try {
                    Assets assets = new Assets(MainActivity.this);
                    File assetDir  = assets.syncAssets();
                    setupRecognizer(assetDir);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "cn-digits-semi"))
                .setDictionary(new File(assetsDir, "wireseeker_db.dic"))
                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .getRecognizer();
        Log.d("3","setup终了");
        recognizer.addListener(this);

        // Create language model search
        File languageModel = new File(assetsDir, "wireseeker_db.lm.bin");
        recognizer.addNgramSearch(FORECAST_SEARCH, languageModel);
        Log.d("3","setup终了");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    private void saveUserInfo(Integer num, Integer THEME_MODE) {
        SharedPreferences userInfo = getSharedPreferences(DATA_PATH, MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();//获取Editor
        editor.putInt("word_num",num);
        editor.putInt("theme",THEME_MODE);
        editor.commit();
    }
    private int readUserInfoWord()
    {
        SharedPreferences userInfo = getSharedPreferences(DATA_PATH, MODE_PRIVATE);
        return userInfo.getInt("word_num",0);
    }
    private int readUserInfoTheme()
    {
        SharedPreferences userInfo = getSharedPreferences(DATA_PATH, MODE_PRIVATE);
        return userInfo.getInt("theme",0);
    }
}
