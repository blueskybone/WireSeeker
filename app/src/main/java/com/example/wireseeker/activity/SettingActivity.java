package com.example.wireseeker.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.wireseeker.R;
import com.example.wireseeker.adapter.SettingAdapter;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {

    private static final String DATA_PATH=  "userInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_dialog);

        ArrayList<String> listString = new ArrayList<String>();
        listString.add("0");
        listString.add("1");

        ListView lvSetting = findViewById(R.id.list_setting);
        SettingAdapter settingAdapter = new SettingAdapter(SettingActivity.this, listString);
        // invoke error
        lvSetting.setAdapter(settingAdapter);
        lvSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.e("1","position = "+position);
                switch (position) {
                    case 0:
                        switchTheme();
                        break;
                    case 1:
                        changeWord();
                        break;
                    default:
                        break;
                }
            }
        });

//        @SuppressLint("UseSwitchCompatOrMaterialCode")
//        Switch mySwitch = (Switch)findViewById(R.id.switch_theme);
//        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                // do something, the isChecked will be
//                // true if the switch is in the On position
//                if (isChecked)
//                {
//                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                    recreate();
//                }else {
//                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                    recreate();
//                }
//            }
//        });
    }

    private void switchTheme() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        Log.e("night","currentmode" + currentNightMode);
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO: {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                recreate();
                break;//黑夜模式
            }
            case Configuration.UI_MODE_NIGHT_YES: {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                recreate();
                break;//白天模式
            }
        }
    }

    private void changeWord() {
        int wordNum = readUserInfoWord();
        AlertDialog.Builder builder5 = new AlertDialog.Builder(SettingActivity.this);
        View view = LayoutInflater.from(SettingActivity.this).inflate(R.layout.dialog_change_word, null);
        SeekBar seekBar = (SeekBar)view.findViewById(R.id.seekBar_word);
        seekBar.setProgress(wordNum);
        TextView textView = (TextView)view.findViewById(R.id.textView_progress);
         textView.setText(String.valueOf(wordNum));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                textView.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
              // textView.setText(String.valueOf(seekBar.getProgress()));
            }
        });
        builder5.setTitle("设置字体大小")
                .setView(view)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int wordNum = seekBar.getProgress();
                        saveUserInfo(wordNum);
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void saveUserInfo(Integer num) {
        SharedPreferences userInfo = getSharedPreferences(DATA_PATH, MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();//获取Editor
        editor.putInt("word_num",num);
        editor.commit();
    }
    private int readUserInfoWord()
    {
        SharedPreferences userInfo = getSharedPreferences(DATA_PATH, MODE_PRIVATE);
        return userInfo.getInt("word_num",0);
    }

}

