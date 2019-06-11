package com.android_aes.administrator.androidaes.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.android_aes.administrator.androidaes.R;

import java.util.Objects;

public class SettingActivity extends AppCompatActivity {
    SharedPreferences preference;
    EditText rounds;

    RadioButton radioButton1_1;
    RadioButton radioButton1_2;
    RadioButton radioButton2_1;
    RadioButton radioButton2_2;
    TextView path;
    Button save;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Button_setting:
                Intent intent = new Intent( SettingActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.Button_Calculator:
                Intent intent1 = new Intent( SettingActivity.this,CalculatorActivity.class);
                startActivity(intent1);
                break;
            case R.id.main:
                Intent intent2 = new Intent( SettingActivity.this,MainActivity.class);
                startActivity(intent2);
                break;
            case R.id.Button_Exit:
                finishAffinity ();
                break;
            default:
                break;
        }
        return true;
    }
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.setting_main );

        rounds = findViewById ( R.id.rounds );
        radioButton1_1= findViewById ( R.id.radio1);
        radioButton1_2= findViewById ( R.id.radio2);
        radioButton2_1= findViewById ( R.id.radio3);
        radioButton2_2= findViewById ( R.id.radio4);
        path = findViewById ( R.id.path );
        save = findViewById ( R.id.save );
        save.setOnClickListener ( new MyOnClickListener ()  );

        //获取SharedPreferences对象
        preference = getSharedPreferences("setting",MODE_PRIVATE);
        //读取sharedPreferences数据
        int round = preference.getInt ( "rounds",10);
//        System.out.println ( "round:"+round );
        rounds.setText ( String.valueOf ( round ) );
        boolean A = preference.getBoolean ( "radioButton1_1" ,false);
        boolean B = preference.getBoolean ( "radioButton1_2" ,false);
        boolean C = preference.getBoolean ( "radioButton2_1" ,false);
        boolean D = preference.getBoolean ( "radioButton2_2" ,false);

//        System.out.println ( "1-1"+A );
//        System.out.println ( "1-2"+B );
//        System.out.println ( "2-2"+C );
//        System.out.println ( "2-2"+D );

        radioButton1_1.setChecked ( A );
        radioButton1_2.setChecked ( B );
        radioButton2_1.setChecked ( C );
        radioButton2_2.setChecked ( D );

        String privatePath = Objects.requireNonNull ( getExternalFilesDir ( Environment.DIRECTORY_DOWNLOADS ) ).toString ();
        path.setText ( privatePath );
    }

     class MyOnClickListener implements View.OnClickListener {
        public void onClick(View v) {
            if (v.getId () == R.id.save) {
                int r = Integer.parseInt(rounds.getText ().toString ());
                //使sharedPreferences处于可编辑状态
                SharedPreferences.Editor editor = preference.edit();
                //写入
                editor.putInt ("rounds", r);
                if(radioButton1_1.isChecked ()){
                    editor.putBoolean ( "radioButton1_1" ,true);
                    editor.putBoolean ( "radioButton1_2" ,false);
                }else{
                    editor.putBoolean ( "radioButton1_1" ,false);
                    editor.putBoolean ( "radioButton1_2" ,true);
                }

                if (radioButton2_1.isChecked ()){
                    editor.putBoolean ( "radioButton2_1" ,true);
                    editor.putBoolean ( "radioButton2_2" ,false);
                }else {
                    editor.putBoolean ( "radioButton2_1" ,false);
                    editor.putBoolean ( "radioButton2_2" ,true);
                }
                //存储
                editor.apply();
                Toast.makeText ( SettingActivity.this, "保存成功", Toast.LENGTH_SHORT ).show ();
            }
        }
    }
}