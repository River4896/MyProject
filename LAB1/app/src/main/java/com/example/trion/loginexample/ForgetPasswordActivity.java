package com.example.trion.loginexample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText et_new_password,et_confirm_password,et_verify_code;
    private String mVerifyCode,mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        et_new_password = (EditText)findViewById(R.id.et_new_password);
        et_confirm_password = (EditText)findViewById(R.id.et_confrim_password);
        et_verify_code = (EditText)findViewById(R.id.et_verify_code);
        findViewById(R.id.btn_verify_code).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        mPhone = getIntent().getStringExtra("phone");
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_verify_code){
            if(mPhone.length() < 11){
                Toast.makeText(getApplicationContext(),"请输入正确的手机号",Toast.LENGTH_SHORT).show();
                return;
            }
            mVerifyCode = String.format("%06d",(int)(Math.random() * 1000000 % 1000000));
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请记住验证码")
                    .setMessage("手机号"+mPhone+", 本次验证码是"+mVerifyCode+", 请输入验证码")
                    .setPositiveButton("好的",null)
                    .show();
        } else if (view.getId() == R.id.btn_confirm){
            String password_first = et_new_password.getText().toString();
            String password_second = et_confirm_password.getText().toString();
            if(password_first.length() < 6 || password_second.length() < 6){
                Toast.makeText(getApplicationContext(),"请输入正确的新密码",Toast.LENGTH_SHORT).show();
                return;
            }
            if(!password_first.equals(password_second)){
                Toast.makeText(getApplicationContext(),"再次输入的新密码不一致",Toast.LENGTH_SHORT).show();
                return;
            }
            if(!et_verify_code.getText().toString().equals(mVerifyCode)){
                Toast.makeText(getApplicationContext(),"请输入正确的验证码",Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(getApplicationContext(),"密码修改成功",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("new_password",password_first);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }
}
