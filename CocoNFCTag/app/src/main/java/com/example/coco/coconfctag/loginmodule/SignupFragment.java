package com.example.coco.coconfctag.loginmodule;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.common.MonthYearPickerDialog;
import com.example.coco.coconfctag.database.DatabaseHandler;

/**
 * Created by cocoadmin on 3/16/2017.
 */

public class SignupFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private EditText mUserNameEdtTxt;
    private EditText mPwdEdtTxt;
    private EditText mConfirmPwdEdtTxt;
    private TextView mSignupTxt, mWarnTxt, mDOBTxt;
    private DatabaseHandler mDB;

    private TextView mCountTxtView;
    private TextView mTitleTxtView;
    private ImageView mCartImg;
    private RelativeLayout mSearchLayout;

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
        mDOBTxt.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
     mTitleTxtView.setText("SignUp");
    }
    private void init(View v) {
        mUserNameEdtTxt = (EditText) v.findViewById(R.id.username_etxt);
        mPwdEdtTxt = (EditText) v.findViewById(R.id.pwd_etxt);
        mConfirmPwdEdtTxt = (EditText) v.findViewById(R.id.confirm_pwd_etxt);
        mSignupTxt = (TextView) v.findViewById(R.id.finish_signup_txt);
        mWarnTxt = (TextView) v.findViewById(R.id.warning_txt);
        mDOBTxt = (TextView) v.findViewById(R.id.dob_txt);
        mDB = new DatabaseHandler(getContext());

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
            case R.id.finish_signup_txt:
                doSignup();
                break;
            case R.id.dob_txt:
                MonthYearPickerDialog pd = new MonthYearPickerDialog();
                pd.setListener(this);
                pd.show(getFragmentManager(), "MonthYearPickerDialog");
                break;
        }
    }

    private void doSignup() {
        if (mUserNameEdtTxt.getText().toString().trim().length() == 0) {
            mWarnTxt.setVisibility(View.VISIBLE);
            mWarnTxt.setText("Please enter a valid Username");
        } else if (mDB.getUserItem(mUserNameEdtTxt.getText().toString().trim()) != null) {
            mWarnTxt.setVisibility(View.VISIBLE);
            mWarnTxt.setText("This username not available");
        } else if (mPwdEdtTxt.getText().toString().trim().length() == 0 || mConfirmPwdEdtTxt.getText().toString().trim().length() == 0) {
            mWarnTxt.setVisibility(View.VISIBLE);
            mWarnTxt.setText("Please enter a valid Password");
        } else if (!(mPwdEdtTxt.getText().toString().trim().equals(mConfirmPwdEdtTxt.getText().toString().trim()))) {
            mWarnTxt.setVisibility(View.VISIBLE);
            mWarnTxt.setText("Passwords do not match ");
        } else {
            mDB.addUser(new UserItem("", mUserNameEdtTxt.getText().toString().trim(), mConfirmPwdEdtTxt.getText().toString().trim()));
            Toast.makeText(getContext(), "Account Created", Toast.LENGTH_SHORT).show();
            //int count = getActivity().getSupportFragmentManager().getBackStackEntryCount();
            //if(count >1)


            LoginFragment.setValue(3);

            Fragment firstFragment = null;
            firstFragment = new LoginFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            fragmentTransaction.replace(R.id.frame, firstFragment, "h");
            fragmentTransaction.addToBackStack("h");
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mDOBTxt.setText("" + month + "-" + year);
    }
}
