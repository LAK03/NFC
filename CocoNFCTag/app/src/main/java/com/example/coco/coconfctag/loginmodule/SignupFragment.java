package com.example.coco.coconfctag.loginmodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.database.DatabaseHandler;

/**
 * Created by cocoadmin on 3/16/2017.
 */

public class SignupFragment extends Fragment implements View.OnClickListener {

    private EditText mUserNameEdtTxt;
    private EditText mPwdEdtTxt;
    private EditText mConfirmPwdEdtTxt;
    private TextView mSignupTxt, mWarnTxt;
    private DatabaseHandler mDB;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup, container, false);
        init(v);
        setListeners();
        return v;
    }

    private void setListeners() {
        mSignupTxt.setOnClickListener(this);
    }

    private void init(View v) {
        mUserNameEdtTxt = (EditText) v.findViewById(R.id.username_etxt);
        mPwdEdtTxt = (EditText) v.findViewById(R.id.pwd_etxt);
        mConfirmPwdEdtTxt = (EditText) v.findViewById(R.id.confirm_pwd_etxt);
        mSignupTxt = (TextView) v.findViewById(R.id.finish_signup_txt);

        mWarnTxt = (TextView) v.findViewById(R.id.warning_txt);
        mDB = new DatabaseHandler(getContext());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finish_signup_txt:
                doSignup();
                break;
        }
    }

    private void doSignup() {
        if (mUserNameEdtTxt.getText().toString().trim().length() == 0) {
            mWarnTxt.setVisibility(View.VISIBLE);
            mWarnTxt.setText("Please enter a valid Username");
        } else if (mDB.getUserItem(mUserNameEdtTxt.getText().toString().trim())!=null) {
            mWarnTxt.setVisibility(View.VISIBLE);
            mWarnTxt.setText("This username not available");
        }
        else if(mPwdEdtTxt.getText().toString().trim().length()==0||mConfirmPwdEdtTxt.getText().toString().trim().length()==0)
        {
            mWarnTxt.setVisibility(View.VISIBLE);
            mWarnTxt.setText("Please enter a valid Password");
        }
        else if(!(mPwdEdtTxt.getText().toString().trim().equals(mConfirmPwdEdtTxt.getText().toString().trim())))
        {
            mWarnTxt.setVisibility(View.VISIBLE);
            mWarnTxt.setText("Passwords do not match ");
        }
        else
        {
            mDB.addUser(new UserItem("",mUserNameEdtTxt.getText().toString().trim(),mConfirmPwdEdtTxt.getText().toString().trim()));
            Toast.makeText(getContext(),"Account Created",Toast.LENGTH_SHORT).show();
        }

    }
}
