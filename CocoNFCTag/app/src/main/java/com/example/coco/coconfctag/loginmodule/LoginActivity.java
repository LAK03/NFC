package com.example.coco.coconfctag.loginmodule;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.listeners.LockNavigationListener;


public class LoginActivity extends AppCompatActivity implements LockNavigationListener {

    private SearchView mSearchView;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        openFrag(0);
        init();
    }

    private void init() {
        mSearchView = (SearchView) findViewById(R.id.search_view);
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setIconified(false);
            }
        });
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Cocosoft");
        initNavigationDrawer();
    }

    private void initNavigationDrawer() {

        //Setting <a href="#">Navigation View</a> Item Selected Listener to handle the item click of the navigation menu
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);
                //Closing drawer on item click
                mDrawerLayout.closeDrawers();
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.menu_home:
                        return true;
                    // For rest of the options we just show a toast on click
                    case R.id.menu_ordered:
                        return true;
                    case R.id.menu_favourite:
                        return true;
                    case R.id.menu_settings:
                        return true;
                    case R.id.menu_edit:
                        return true;
                    default:
                        return true;
                }
            }
        });
        // Initializing Drawer Layout and ActionBarToggle
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                for (int i = 0; i < mNavigationView.getMenu().size(); i++) {
                    mNavigationView.getMenu().getItem(i).setChecked(false);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        mActionBarDrawerToggle.syncState();
        getSupportFragmentManager().addOnBackStackChangedListener(new android.support.v4.app.FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    Log.e("homescreen", "===");

                    mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
            }
        });
        mActionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("backst", "kstack");
                getSupportFragmentManager().popBackStack();
            }
        });

    }

    private void openFrag(int i) {
        Fragment firstFragment = null;
        switch (i) {
            case 0:
                firstFragment = new HomeFragment();
                ((HomeFragment)firstFragment).setListener(this);
                break;

            case 1:
                firstFragment = new LoginFragment();
                break;

        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.frame, firstFragment, "h");
        fragmentTransaction.addToBackStack("h");
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            Log.i("LoginActivity", "popping backstack");
            fm.popBackStack();

        } else {
            Log.i("LoginActivity", "nothing on backstack, calling super");
            this.finish();
        }


    }

    @Override
    public void onFragmentOpen() {
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }
}
