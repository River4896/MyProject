package com.example.trion.loginexample;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trion.loginexample.util.ViewUtil;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private RadioGroup rg_login;
    private RadioButton rb_password,rb_verifycode;
    private EditText et_phone,et_password;
    private TextView tv_password;
    private Button btn_forget,btn_login;
    private CheckBox chk_remember;

    private int mRequestCode = 0;
    private int mType = 0;
    private boolean bRemember = false;
    private String mPassword = "111111";
    private String mVerifyCode;

    private String[] typeArray = {"個人用戶","公司用戶"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg_login = (RadioGroup)findViewById(R.id.rg_login);
        rb_password = (RadioButton)findViewById(R.id.rb_password);
        rb_verifycode = (RadioButton)findViewById(R.id.rb_verifycode);
        et_phone = (EditText)findViewById(R.id.et_phone);
        et_password = (EditText)findViewById(R.id.et_password);
        tv_password = (TextView)findViewById(R.id.tv_password);
        btn_forget = (Button)findViewById(R.id.btn_forget);
        btn_login = (Button)findViewById(R.id.btn_login);
        chk_remember = (CheckBox)findViewById(R.id.chk_remember);

        rg_login.setOnCheckedChangeListener(new RadioListener());
        chk_remember.setOnCheckedChangeListener(new CheckListener());
        et_phone.addTextChangedListener(new HideTextWatcher(et_phone));
        et_password.addTextChangedListener(new HideTextWatcher(et_password));
        btn_login.setOnClickListener(this);
        btn_forget.setOnClickListener(this);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this,
                R.layout.item_selected, typeArray);
        typeAdapter.setDropDownViewResource(R.layout.item_dropdown);
        Spinner sp_type = (Spinner)findViewById(R.id.sp_type);
        sp_type.setPrompt("請選擇用戶類型");
        sp_type.setAdapter(typeAdapter);
        sp_type.setSelection(mType);
        sp_type.setOnItemSelectedListener(new TypeSelectedListener());
    }

    private class RadioListener implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if(i == R.id.rb_password){
                tv_password.setText("登錄密碼：");
                et_password.setHint("請輸入密碼");
                btn_forget.setText("忘記密碼");
                chk_remember.setVisibility(View.VISIBLE);
            } else if (i == R.id.rb_verifycode){
                tv_password.setText("    驗證碼：");
                et_password.setHint("請輸入驗證碼");
                btn_forget.setText("獲取驗證碼");
                chk_remember.setVisibility(View.INVISIBLE);
            }
        }
    }

    class TypeSelectedListener implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            mType = i;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    private class CheckListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if(compoundButton.getId() == R.id.chk_remember){
                bRemember = isChecked;
            }
        }
    }

    private class HideTextWatcher implements TextWatcher{
        private EditText mView;
        private int mMaxLength;
        private CharSequence mStr;

        public HideTextWatcher(EditText v){
            super();
            mView = v;
            mMaxLength = ViewUtil.getMaxLength(v);
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mStr = charSequence;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(mStr == null || mStr.length() == 0)
                return;
            if((mStr.length() == 11 && mMaxLength == 11) ||
                    (mStr.length() == 6 && mMaxLength == 6)){
                ViewUtil.hideOneInputMethod(MainActivity.this,mView);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == mRequestCode && data != null){
            //用戶密碼已改為新密碼
            mPassword = data.getStringExtra("new_password");
        }
    }

    //从修改密码页面返回登录页面，要清空密码的输入框
    @Override
    protected void onRestart() {
        et_password.setText("");
        super.onRestart();
    }

    @Override
    public void onClick(View view) {
        String phone = et_phone.getText().toString();
        if(view.getId() == R.id.btn_forget){
            if(phone.length() < 11){
                Toast.makeText(getApplicationContext(),"請輸入正確的手機號",Toast.LENGTH_SHORT).show();
                return;
            }
            if(rb_password.isChecked()){
                Intent intent = new Intent(this,ForgetPasswordActivity.class);
                intent.putExtra("phone",phone);
                startActivityForResult(intent,mRequestCode);
            } else if (rb_verifycode.isChecked()){
                mVerifyCode = String.format("%06d",(int)(Math.random()*1000000%1000000));
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("請記得驗證碼");
                builder.setMessage("手機號"+phone+",本次驗證碼是"+mVerifyCode+",請輸入驗證碼");
                builder.setPositiveButton("好的",null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        } else if(view.getId() == R.id.btn_login){
            if (phone.length() < 11){
                Toast.makeText(this,"請輸入正確的手機號",Toast.LENGTH_SHORT).show();
                return;
            }
            if(rb_password.isChecked()){
                if(!et_password.getText().toString().equals(mPassword)) {
                    Toast.makeText(this, "請輸入正確的密碼", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    loginSuccess();
                }
            }
        }
    }

    private void loginSuccess() {
        String desc = String.format("您的手機號碼是%s，類型是%s。恭喜你通過登陸驗證，點擊“確定”按鈕返回三個頁面",
                et_phone.getText().toString(),typeArray[mType]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("登錄成功！")
                .setMessage(desc)
                .setPositiveButton("確定返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("我再看看",null);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
