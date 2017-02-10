package com.example.coco.coconfctag;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by user on 8/8/16.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

        Tab1                    csTab1               = null;
    Tab2                    csTab2               = null;
    Tab3                    csTab3               = null;
    Tab4                    csTab4               = null;
    Tab5                    csTab5               = null;
    Tab6                    csTab6               = null;
    Tab7                    csTab7               = null;

    ArrayList<String>       csTabsList           = null;
    int                     mNumOfTabs;
    Context                 csContext;

    public PagerAdapter(FragmentManager fm, Context cxt, ArrayList<String> tbs)
    {
        super(fm);
            csTabsList                  = tbs;
        this.mNumOfTabs             = csTabsList.size();
        csContext                   = cxt;
    }
    

    @Override
    public Fragment getItem(int position)
    {
        Fragment fragment = null;
            
        if(csTabsList != null)
        {
            String tab = csTabsList.get(position);
            if (tab.equalsIgnoreCase("IOT NFC Tags                 "))
            {
                if(csTab1  == null)
                    csTab1  =  new Tab1();
                fragment = csTab1 ;
            }

            if (tab.equalsIgnoreCase("Action Settings"))
            {
                if(csTab2 == null)
                    csTab2 = new Tab2();
                fragment = csTab2;
            }
           /* if(tab.equalsIgnoreCase("Tuesday"))
            {
                if(csTab3 == null)
                    csTab3 = new Tab3();
                fragment = csTab3;

            }*/
            /*if(tab.equalsIgnoreCase("Wednesday"))
            {
                if(csTab4 == null)
                    csTab4 = new Tab4();
                fragment = csTab4;

            }
            if(tab.equalsIgnoreCase("Thursday"))
            {
                if(csTab5 == null)
                    csTab5 = new Tab5();
                fragment = csTab5;

            }
            if(tab.equalsIgnoreCase("Friday"))
            {
                if(csTab6 == null)
                    csTab6 = new Tab6();
                fragment = csTab6;

            }
            if(tab.equalsIgnoreCase("Saturday"))
            {
                if(csTab7 == null)
                    csTab7 = new Tab7();
                fragment = csTab7;

            }*/
           
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
