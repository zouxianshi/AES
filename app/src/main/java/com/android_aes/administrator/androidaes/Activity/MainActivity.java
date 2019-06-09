package com.android_aes.administrator.androidaes.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android_aes.administrator.androidaes.*;
import com.android_aes.administrator.androidaes.Cipher.AESCipher;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Button Button_Encrypt;
    Button Button_Decrypt;
    Button Button_Browse;
    EditText Text_Content;
    EditText Text_Key;
    EditText Text_File;
    TextView Text_Result;
    private TextView Text_Path;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Button_setting:
                Intent intent = new Intent( MainActivity.this,SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.Button_Calculator:
                Intent intent1 = new Intent( MainActivity.this,CalculatorActivity.class);
                startActivity(intent1);
                break;
            case R.id.main:
                Intent intent2 = new Intent( MainActivity.this, MainActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
        return true;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_main );
        //获取xml文件的button控件
        Button_Encrypt = findViewById ( R.id.Button_Encrypt );
        Button_Decrypt = findViewById ( R.id.Button_Decrypt );
        Button_Browse = findViewById ( R.id.Button_Browse );
        Text_Content = findViewById ( R.id.Text_Content );
        Text_Key = findViewById ( R.id.Text_Key );
        Text_Result = findViewById ( R.id.Text_Result );
        Text_File = findViewById ( R.id.Text_File );
        Text_Path = findViewById ( R.id.Text_Privatepath );

        String privatePath = Objects.requireNonNull ( getExternalFilesDir ( Environment.DIRECTORY_DOWNLOADS ) ).toString ();
        Text_Path.setText ( "文件路径:"+privatePath );


        //设置监听器
        Button_Encrypt.setOnClickListener ( new MyOnClickListener () );
        Button_Decrypt.setOnClickListener ( new MyOnClickListener () );
        Button_Browse.setOnClickListener ( new MyOnClickListener () );

    }

    class MyOnClickListener implements View.OnClickListener {

        private static final String TAG = "PravitePath";

        @RequiresApi(api = Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {

            //点击加密处理方法
            if (v.getId () == R.id.Button_Encrypt) {
                //获取用户输入内容和密码
                String filepath = Text_File.getText ().toString ().trim ();
                String password = Text_Key.getText ().toString ().trim ();
                String content = Text_Content.getText ().toString ().trim ();
                String filename = Text_Result.getText ().toString ().trim ();
                //判断文件路径和密码是否为空
                if (TextUtils.isEmpty ( filepath ) || TextUtils.isEmpty ( password )) {
                    if (TextUtils.isEmpty ( content ) || TextUtils.isEmpty ( password )) {
                        Toast.makeText ( MainActivity.this, "内容或密码不能为空", Toast.LENGTH_SHORT ).show ();
                    } else {
                        try{
                            AESCipher AESCipher = new AESCipher ();
                            String result = AESCipher.encrypt ( content, password );
                            Text_Result.setText ( result );
                            Toast.makeText ( getApplicationContext (), "加密成功", Toast.LENGTH_SHORT ).show ();
                        }catch (Exception e){
                            e.printStackTrace ();
                            Toast.makeText ( getApplicationContext (), "加密失败", Toast.LENGTH_SHORT ).show ();
                        }

                    }
                } else {
                    String privatePath = Objects.requireNonNull ( getExternalFilesDir ( Environment.DIRECTORY_DOWNLOADS ) ).toString ();
                    Log.d ( TAG, "公共存储路径" + privatePath );
                    String savePath = privatePath +"/"+ filename + "_加密.txt";
                    try {
                        Encrypt.fileEncrpty ( password, filepath, savePath );
                        Text_Path.setText ( "存储路径:"+savePath );
                        Toast.makeText ( getApplicationContext (), "加密成功", Toast.LENGTH_SHORT ).show ();
                    } catch (Exception e) {
                        e.printStackTrace ();
                        Toast.makeText ( getApplicationContext (), "加密失败", Toast.LENGTH_SHORT ).show ();
                    }
                }
            }

             //点击解密处理方法
            if (v.getId () == R.id.Button_Decrypt) {
                //获取用户输入内容和密码
                String filepath = Text_File.getText ().toString ().trim ();
                String password = Text_Key.getText ().toString ().trim ();
                String content = Text_Content.getText ().toString ().trim ();
                String filename = Text_Result.getText ().toString ().trim ();

                //判断文件路径和密码是否为空
                if (TextUtils.isEmpty ( filepath ) || TextUtils.isEmpty ( password )) {
                    //判断内容和密码是否为空
                    if (TextUtils.isEmpty ( content ) || TextUtils.isEmpty ( password )) {
                        Toast.makeText ( MainActivity.this, "内容或密码不能为空", Toast.LENGTH_SHORT ).show ();
                    } else {
                        try{
                            AESCipher AESCipher = new AESCipher ();
                            String result = AESCipher.decrypt ( content, password );
                            Text_Result.setText ( result );
                            Toast.makeText ( getApplicationContext (), "解密成功", Toast.LENGTH_SHORT ).show ();
                        }catch (Exception e){
                            e.printStackTrace ();
                            Toast.makeText ( getApplicationContext (), "解密失败", Toast.LENGTH_SHORT ).show ();
                        }

                    }
                } else {
                    String privatePath = Objects.requireNonNull ( getExternalFilesDir ( Environment.DIRECTORY_DOWNLOADS ) ).toString ();
                    Log.d ( TAG, "公共存储路径" + privatePath );
                    String savePath = privatePath + "/" + filename + "_解密.txt";
                    try {
                        Decrypt.fileDecrpty ( password, filepath, savePath );
                        System.out.println ( savePath );
                        Text_Path.setText ( "存储路径:"+savePath );
                        Toast.makeText ( getApplicationContext (), "解密成功", Toast.LENGTH_SHORT ).show ();
                    } catch (Exception e) {
                        e.printStackTrace ();
                        Toast.makeText ( getApplicationContext (), "解密失败", Toast.LENGTH_SHORT ).show ();
                    }
                }
            }

            //点击浏览处理方法
            if (v.getId () == R.id.Button_Browse) {
                showFileChooser ();
            }

            if (v.getId () == R.id.Button_setting){
                    Intent intent = new Intent( MainActivity.this,SettingActivity.class);
                    startActivity(intent);
                }
            }
        }

    //调用文件选择器
    private static final int FILE_SELECT_CODE = 0;
    private void showFileChooser() {
        Intent intent = new Intent ( Intent.ACTION_GET_CONTENT );
        intent.setType ( "*/*" );
        intent.addCategory ( Intent.CATEGORY_OPENABLE );
        try {
            startActivityForResult ( Intent.createChooser ( intent, "选择文件" ), FILE_SELECT_CODE );
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText ( this, "请安装文件管理器", Toast.LENGTH_SHORT ).show ();
        }
    }

    //回传获取到的路径和文件名
    private static final String TAG = "ChooseFile";
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_SELECT_CODE) {
            if (resultCode == RESULT_OK) {
                // Get the Uri of the selected file
               String url = Objects.requireNonNull ( data.getData () ).toString ();
               String URL = null;
                try {
                    URL = URLDecoder.decode ( url, "UTF-8" );
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace ();
                }
                Uri uri = Uri.parse ( URL );
                Log.d ( TAG, "Uri: " + (uri != null ? uri.toString () : null) );
                String[] fileInfo = UriToPathUtil.getFileFromUri ( uri, this );
                String path = null;
                String fileName = null;
                if (fileInfo != null) {
                    path = fileInfo[0];
                    fileName = fileInfo[1];
                }
                Log.d ( TAG, "文件路径: " + path );
                Log.d ( TAG, "文件名称: " + fileName );
                Text_File.setText ( path );
                Text_Result.setText ( fileName );
            }
        }
        super.onActivityResult ( requestCode, resultCode, data );
    }
}


