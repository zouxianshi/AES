package com.android_aes.administrator.androidaes.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.android_aes.administrator.androidaes.R;

public class SettingActivity extends AppCompatActivity {
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
            default:
                break;
        }
        return true;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.setting_main );

    }
}