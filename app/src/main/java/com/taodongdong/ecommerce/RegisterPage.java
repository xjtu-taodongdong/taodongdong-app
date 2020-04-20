package com.taodongdong.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.taodongdong.ecommerce.api.ApiCallback;

import org.json.JSONException;

public class RegisterPage extends AbstractActivity implements View.OnClickListener {
    private Button register = null;
    private Button cancel = null;
    EditText username = null;
    EditText password = null;
    EditText password_cfm = null;
    RadioButton asSaler = null;
    boolean check = false;
    @Override
    public void onClick(View view){
        if(view == cancel){
            this.finish();
        }else if(view == register){
            String usr = username.getText().toString();
            String pwd = password.getText().toString();
            String pwd_cfm = password_cfm.getText().toString();
            if(usr.length() < 6){
                Toast.makeText(RegisterPage.this,"用户名太短",Toast.LENGTH_SHORT).show();
                return;
            }
            if(pwd.length() < 6){
                Toast.makeText(RegisterPage.this,"密码太短",Toast.LENGTH_SHORT).show();
                return;
            }
            if(pwd.equals(pwd_cfm)){
                int authority = asSaler.isChecked()?1:0;
                api().register(usr, pwd, authority, new ApiCallback<String>() {
                    @Override
                    public void onSuccess(String data) throws JSONException {
                        Toast.makeText(RegisterPage.this,"注册成功",Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(int code, String message, Object data) throws JSONException {
                        Toast.makeText(RegisterPage.this,"注册失败："+message,Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(this,"两次输入的密码不一致",Toast.LENGTH_SHORT).show();
            }

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        register = (Button)findViewById(R.id.confirm_register);
        cancel = (Button)findViewById(R.id.cancel_register);
        username = (EditText)findViewById(R.id.reg_in_name);
        password = (EditText)findViewById(R.id.reg_in_password);
        password_cfm = (EditText)findViewById(R.id.reg_confirm_password);
        asSaler = (RadioButton)findViewById(R.id.register_as_saler);
        asSaler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton rb = (RadioButton)v;
                if(check){
                    rb.setChecked(false);
                    check = false;
                }else{
                    rb.setChecked(true);
                    check = true;
                }
            }
        });
        cancel.setOnClickListener(this);

    }

}
