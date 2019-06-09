package com.android_aes.administrator.androidaes.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.android_aes.administrator.androidaes.Cipher.AESCipher;
import com.android_aes.administrator.androidaes.Cipher.AESConstants;
import com.android_aes.administrator.androidaes.Cipher.ArrayUtil;
import com.android_aes.administrator.androidaes.ParseSystemUtil;
import com.android_aes.administrator.androidaes.R;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {
    private RadioGroup radioGroup1, radioGroup2;
    private EditText input, print,rawkey_edit;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater ().inflate ( R.menu.menu1, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId ()) {
            case R.id.Button_setting:
                Intent intent = new Intent ( CalculatorActivity.this, SettingActivity.class );
                startActivity ( intent );
                break;
            case R.id.Button_Calculator:
                Intent intent1 = new Intent ( CalculatorActivity.this, CalculatorActivity.class );
                startActivity ( intent1 );
                break;
            case R.id.main:
                Intent intent2 = new Intent ( CalculatorActivity.this, MainActivity.class );
                startActivity ( intent2 );
                break;
            default:
                break;
        }
        return true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.calculator_main );

        input = findViewById ( R.id.input );
        print = findViewById ( R.id.print );
        radioGroup1 = findViewById ( R.id.rgSex );
        radioGroup2 = findViewById ( R.id.Mode );
        rawkey_edit = findViewById ( R.id.rawkey_Edit );
        Button execute = findViewById ( R.id.execute );

        execute.setOnClickListener ( this );

    }

    @Override
    public void onClick(View v) {
        short[][] Sbox = null;
        short[][] Shift_table = null;
        short[][] Mixcolumns_table = null;
        String INPUT = input.getText ().toString ().trim ();
        int checkedId = radioGroup1.getCheckedRadioButtonId ();
        int Mode = radioGroup2.getCheckedRadioButtonId ();
        System.out.println ( checkedId );
        System.out.println ( Mode );

        switch (Mode) {
            case R.id.Positive:
                Sbox = AESConstants.SUBSTITUTE_BOX;
                Shift_table = AESConstants.SHIFTING_TABLE;
                Mixcolumns_table = AESConstants.CX;
                break;
            case R.id.inverted:
                Sbox = AESConstants.INVERSE_SUBSTITUTE_BOX;
                Shift_table = AESConstants.INVERSE_SHIFTING_TABLE;
                Mixcolumns_table = AESConstants.INVERSE_CX;
                break;
            default:
                break;
        }

        try {
            switch (checkedId) {
                case R.id.subBytes:
//                    String subbyte = "d9c7d56954307ea95d3cc54f7b93bb1e";//d9c7d56954307ea95d3cc54f7b93bb1e//e531b5e4fd088ab78d6d07920322fee9
                    short[][] initialTextState = AESCipher.transfer ( ArrayUtil.byteToShorts ( ParseSystemUtil.parseHexStr2Byte ( INPUT ) ) );
                    short[][] result = AESCipher.substituteState ( initialTextState, Sbox );
                    print.setText ( AESCipher.getStateHex ( result ) );
                    rawkey_edit.setText ( "" );
                    break;

                case R.id.shiftrow:
//                    String shiftrow = "d930c51e543cbb695d93d5a97bc77e4f";//d930c51e543cbb695d93d5a97bc77e4f//d9c7d56954307ea95d3cc54f7b93bb1e
                    initialTextState = AESCipher.transfer ( ArrayUtil.byteToShorts ( ParseSystemUtil.parseHexStr2Byte ( INPUT ) ) );
                    result = AESCipher.shiftRows ( initialTextState, Shift_table );
                    print.setText ( AESCipher.getStateHex ( result ) );
                    rawkey_edit.setText ( "" );
                    break;

                case R.id.mixcolumns:
//                    String mixcolumns = "22f35ab93e93bea968ad9fe8952391aa";//22f35ab93e93bea968ad9fe8952391aa//d930c51e543cbb695d93d5a97bc77e4f
                    initialTextState = AESCipher.transfer ( ArrayUtil.byteToShorts ( ParseSystemUtil.parseHexStr2Byte ( INPUT ) ) );
                    result = AESCipher.mixColumns ( initialTextState, Mixcolumns_table );
                    rawkey_edit.setText ( "" );
                    print.setText ( AESCipher.getStateHex ( result ) );
                    break;

                case R.id.addrawkey:
//                    String addrawkey = "6598c89cdea504440b643d3062832e8c";//22f35ab93e93bea968ad9fe8952391aa
//                    String roundkey = "476b9225e036baed63c9a2d8f7a0bf26";//6598c89cdea504440b643d3062832e8c
                    String RAWKEY = rawkey_edit.getText ().toString ().trim ();
                    initialTextState = AESCipher.transfer ( ArrayUtil.byteToShorts ( ParseSystemUtil.parseHexStr2Byte ( INPUT ) ) );
                    short[][] initialroundkey = AESCipher.transfer ( ArrayUtil.byteToShorts ( ParseSystemUtil.parseHexStr2Byte ( RAWKEY ) ) );
                    result = AESCipher.xor ( initialTextState, initialroundkey );
                    print.setText ( AESCipher.getStateHex ( result ) );
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace ();
            Toast.makeText ( CalculatorActivity.this, "请输入正确内容", Toast.LENGTH_SHORT ).show ();
        }
    }
}
