package com.example.coco.coconfctag;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;


public class Login extends AppCompatActivity {

    SharedPreferences spf;
    String uname="",pwd="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);




        spf=getSharedPreferences("MegaFile", MODE_PRIVATE);
        final EditText edt_uname=(EditText) findViewById(R.id.editText1);
        final EditText edt_pwd=(EditText) findViewById(R.id.editText2);

        final Button btn_login=(Button) findViewById(R.id.button1);
        final Button btn_signup=(Button) findViewById(R.id.button2);




        btn_login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if(spf!=null)
                {
                    uname=spf.getString("uname", "");
                    edt_uname.setText(uname);
                    pwd=spf.getString("pwd", "");
                }

                if(!uname.equals(edt_uname.getText().toString().trim()))
                {
                    edt_uname.setError("Invalid Username");
                }

                if(!pwd.equals(edt_pwd.getText().toString().trim()))
                {
                    edt_pwd.setError("Invalid Password");
                }

                if(uname.equals(edt_uname.getText().toString().trim())&& pwd.equals(edt_pwd.getText().toString().trim()))
                {
                    Intent it=new Intent(Login.this,MainActivity.class);
                    startActivity(it);
                }
            }
        });

        btn_signup.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final Dialog dlg=new Dialog(Login.this);
                dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dlg.setContentView(R.layout.signupform);
                final EditText dlg_edt_uname=(EditText) dlg.findViewById(R.id.editText1);
                final EditText dlg_edt_pwd=(EditText) dlg.findViewById(R.id.editText2);
                final EditText dlg_edt_cpwd=(EditText) dlg.findViewById(R.id.editText3);
                final Button dlg_btn_submit=(Button) dlg.findViewById(R.id.button1);


                dlg_btn_submit.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {


                        if(dlg_edt_uname.getText().toString().trim().length()==0)
                        {
                            dlg_edt_uname.setError("Please fill the username");
                        }

                        if(dlg_edt_pwd.getText().toString().trim().length()==0)
                        {
                            dlg_edt_pwd.setError("Please fill the password");
                        }
                        if(dlg_edt_cpwd.getText().toString().trim().length()==0)
                        {
                            dlg_edt_cpwd.setError("Please fill the confirm password");
                        }

                        if(dlg_edt_cpwd.getText().toString().trim().equalsIgnoreCase(dlg_edt_pwd.getText().toString().trim()))
                        {
                            Editor store_edt=spf.edit();
                            store_edt.putString("uname", dlg_edt_uname.getText().toString().trim());
                            store_edt.putString("pwd", dlg_edt_pwd.getText().toString().trim());
                            store_edt.commit();

                        }else
                        {
                            dlg_edt_cpwd.setError("password mismatch");
                        }

                        dlg.dismiss();




                    }
                });

                dlg.show();
            }
        });

    }

}
