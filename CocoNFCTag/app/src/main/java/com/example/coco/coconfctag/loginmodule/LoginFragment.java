package com.example.coco.coconfctag.loginmodule;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.database.DatabaseHandler;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by cocoadmin on 3/16/2017.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {
    private TextView mSignupTxt;
    private TextView mLoginTxt;
    private EditText mUserNameEdtTxt;
    private EditText mPwdEdtTxt;
    private DatabaseHandler mDB;
    private TextView mWarnTxt;
    private ImageView mSettingsImg;
    private SharedPreferences.Editor editor;



    private TextView mCountTxtView;
    private TextView mTitleTxtView;
    private ImageView mCartImg;
    private RelativeLayout mSearchLayout;


    @Override
    public void onResume() {
        super.onResume();
        mTitleTxtView.setText("Login");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        init(v);
        setListeners();
        return v;
    }

    private void setListeners() {
        mSignupTxt.setOnClickListener(this);
        mLoginTxt.setOnClickListener(this);
        mSettingsImg.setOnClickListener(this);
    }

    private void init(View v) {
        mSignupTxt = (TextView) v.findViewById(R.id.signup_txt);
        mLoginTxt = (TextView) v.findViewById(R.id.login_txt);
        mUserNameEdtTxt = (EditText) v.findViewById(R.id.username_etxt);
        mPwdEdtTxt = (EditText) v.findViewById(R.id.pwd_etxt);
        mDB = new DatabaseHandler(getContext());
        mWarnTxt = (TextView) v.findViewById(R.id.warning_txt);
        mSettingsImg = (ImageView) v.findViewById(R.id.settings_img);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mCountTxtView = (TextView) toolbar.findViewById(R.id.total_count);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mCartImg = (ImageView) toolbar.findViewById(R.id.cart_img);
        mCountTxtView.setVisibility(View.GONE);
        mCartImg.setVisibility(View.GONE);
        mSearchLayout = (RelativeLayout) getActivity().findViewById(R.id.search_layout);
        mSearchLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_txt:
                openFrag(1);
                break;
            case R.id.settings_img:
                openFrag(0);
                break;
            case R.id.login_txt:
                doLogin();
                break;
        }
    }

    private void doLogin() {
        UserItem item = mDB.getUserItem(mUserNameEdtTxt.getText().toString().trim());
        if (mUserNameEdtTxt.getText().toString().trim().length() == 0) {
            mWarnTxt.setVisibility(View.VISIBLE);
            mWarnTxt.setText("Please enter a valid Username");
        } else if (item == null) {
            mWarnTxt.setVisibility(View.VISIBLE);
            mWarnTxt.setText("Incorrect username");
        } else if (!(item.getPassword().equals(mPwdEdtTxt.getText().toString().trim()))) {
            mWarnTxt.setVisibility(View.VISIBLE);
            mWarnTxt.setText("Incorrect password");
        } else {
            editor = getContext().getSharedPreferences("cocosoft", MODE_PRIVATE).edit();
            editor.putBoolean("isloggedin", true);
            editor.putString("username", mUserNameEdtTxt.getText().toString().trim());
            editor.commit();
            Toast.makeText(getContext(), "Successfully Logged In", Toast.LENGTH_SHORT).show();
         /*   Intent i = new Intent(getContext(), MainActivity.class);
            startActivity(i);*/
        getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void openFrag(int i) {
        Fragment firstFragment = null;
        switch (i)
        {
            case 0:
                firstFragment = new SettingsFragment();
                break;
            case 1:
                firstFragment = new SignupFragment();
                break;

        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.frame, firstFragment, "h");
        fragmentTransaction.addToBackStack("h");
        fragmentTransaction.commit();
    }

}
