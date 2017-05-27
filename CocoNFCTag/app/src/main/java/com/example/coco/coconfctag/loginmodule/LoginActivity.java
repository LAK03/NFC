package com.example.coco.coconfctag.loginmodule;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coco.coconfctag.NfcWriter;
import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.cartmodule.CartFragment;
import com.example.coco.coconfctag.cartmodule.CartItem;
import com.example.coco.coconfctag.database.DatabaseHandler;
import com.example.coco.coconfctag.orderHistory.OrderHistory;
import com.example.coco.coconfctag.scanlistmodule.ProductItem;
import com.example.coco.coconfctag.wishlistmodule.WishListFragment;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    private SearchView mSearchView;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private ImageView mCartImg;
    private DatabaseHandler mDB;
    private NfcAdapter mNfcAdapter;
    private Context context;
    public static final String TAG = "NFCReaderDemo";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private Fragment firstFragment = null;
    private SharedPreferences appSharedPrefs;
    private Gson gson;
    private ArrayList<CartItem> mCartArray=new ArrayList<>();
    private TextView mCountTxtView;
    private TextView _usrName;
    private RelativeLayout mSearchLayout;

    String userName = "";
    int Flag=0;

    private SharedPreferences.Editor editor;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private static final int RC_SIGN_IN = 9001;
    GoogleSignInAccount acct;



    private CallbackManager mCallbackManager;




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
/*Login with Facebook*/

        init();
        openFrag(0);


        Profile fbProfile = Profile.getCurrentProfile();
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    Log.d(TAG, "onLogout catched");

                }
            }
        };


            mCallbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(mCallbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResults) {

                            GraphRequest request = GraphRequest.newMeRequest(
                                    AccessToken.getCurrentAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            FacebookSdk.setIsDebugEnabled(true);
                                            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
                                            Profile profile = Profile.getCurrentProfile();
                                            Log.d("Anusha","FACEBOOK success");
                                            if (profile != null) {

                                                Log.d("Anusha","profile");
                                                userName = profile.getName();

                                                updateUI(true,userName);

                                            }

                                        }
                                    });

                            request.executeAsync();
                        }

                        @Override
                        public void onCancel() {

                            Log.e("dd", "facebook login canceled");

                        }

                        @Override
                        public void onError(FacebookException e) {

                            Log.e("dd", "facebook login failed error");

                        }

                    });

        accessTokenTracker.startTracking();

/*Login with Google*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    private void init() {
        context = this;

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
        mCartImg = (ImageView) findViewById(R.id.cart_img);
        mCountTxtView = (TextView)findViewById(R.id.total_count);
        mCartImg.setOnClickListener(this);
        mSearchLayout = (RelativeLayout)findViewById(R.id.search_layout);

        setSupportActionBar(mToolbar);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initNavigationDrawer();
        mDB = new DatabaseHandler(this);
        mDB.addProduct(new ProductItem("501", "Dove Soap", 20, 1, 0, false));
        mDB.addProduct(new ProductItem("502", "Dove Shampoo", 30, 1, 0, false));
        mDB.addProduct(new ProductItem("503", "Fair & Lovely", 25, 1, 0, false));
        mDB.addProduct(new ProductItem("504", "Fog Perfume", 50, 1, 0, false));
        mDB.addProduct(new ProductItem("505", "Hair Oil", 40, 1, 0, false));
        appSharedPrefs = getSharedPreferences("cocosoft", MODE_PRIVATE);

        _usrName = (TextView)findViewById(R.id.userName);


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
                        openFrag(0);
                        return true;
                    // For rest of the options we just show a toast on click
                    case R.id.menu_ordered:
                        openFrag(2);
                        return true;
                    case R.id.menu_favourite:
                        openFrag(5);
                        return true;
                    case R.id.menu_settings:
                        openFrag(4);
                        return true;
                    case R.id.menu_edit:
                        openFrag(3);
                        return true;
                    case R.id.menu_nfcwriter:
                        Intent intent = new Intent(context, NfcWriter.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu_login:
                        openFrag(1);
                        return true;
                    case R.id.menu_history:
                        openFrag(6);
                        return true;
                    case R.id.menu_logout:
                       // handleLogout();
                        _usrName.setText("");
                        signOut();
                        fb_logOut();
                        _usrName.setText("");
                        appSharedPrefs.edit().putBoolean("isloggedin",false).commit();
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
                boolean isloggedin = appSharedPrefs.getBoolean("isloggedin", false);
                Menu menu = mNavigationView.getMenu();
                MenuItem loginitem = menu.findItem(R.id.menu_login);
                MenuItem logoutitem = menu.findItem(R.id.menu_logout);
                if (isloggedin) {
                    loginitem.setVisible(false);
                    logoutitem.setVisible(true);
                }
                else
                {
                    loginitem.setVisible(true);
                    logoutitem.setVisible(false);
                }
            }
        };
        //Setting the actionbarToggle to drawer layout
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        //calling sync state is necessay or else your hamburger icon wont show up
        mActionBarDrawerToggle.syncState();
        getSupportFragmentManager().addOnBackStackChangedListener(new android.support.v4.app.FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
               /* if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    Log.e("homescreen", "===");
                    mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }*/

               changeCount();
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



    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {

            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            Flag =1;
            handleSignInResult(result);

        } else {

            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);

                }
            });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        else
        {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);

        }


    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            acct = result.getSignInAccount();

            if(Flag ==0)
                updateUI(true,acct.getDisplayName());

        } else {

            updateUI(false,null);
        }
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void updateUI(boolean signedIn,String userName) {
        if (signedIn) {

            editor = getSharedPreferences("cocosoft", MODE_PRIVATE).edit();
            editor.putBoolean("isloggedin", true);
            editor.putString("username",userName );
            editor.commit();
            _usrName.setText("Hi "+userName);
            mSearchLayout.setVisibility(View.VISIBLE);
            getSupportFragmentManager().popBackStack();

        } else {


           // Toast.makeText(this,"User Not Signed IN",Toast.LENGTH_SHORT).show();

        }
    }


    public void signOut() {

            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // [START_EXCLUDE]
                            updateUI(false,null);
                            Flag =0;
                            // [END_EXCLUDE]
                        }
                    });
    }



    private void fb_logOut()
    {

        LoginManager.getInstance().logOut();

    }

public void fb_login()
{
    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
}





    private void openFrag(int i) {
        switch (i) {
            case 0:
                firstFragment = new HomeFragment();
                break;
            case 1:
                firstFragment = new SigninFragment();
                break;
            case 2:
                firstFragment = new CartFragment();
                break;
            case 3:
                firstFragment = new EditProfileFragment();
                break;
            case 4:
                firstFragment = new SettingsFragment();
                break;
            case 5:
                boolean isloggedin = appSharedPrefs.getBoolean("isloggedin", false);
                if (isloggedin)
                    firstFragment = new WishListFragment();
                else
                    Toast.makeText(getApplicationContext(), "Please login to continue", Toast.LENGTH_SHORT).show();
                break;
            case 6 :
                firstFragment = new OrderHistory();
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
/*
    @Override
    public void onFragmentOpen() {
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cart_img:

                break;

        }
    }


  /*  @Override
    public void onQuantityChange(String productid, int quantity) {


        saveTempData(mProductArray);
    }*/
/*
    private void saveTempData(ArrayList<ProductItem> mProductArray) {

        String json = gson.toJson(mProductArray);
        prefsEditor.putString("tempdata", json);
        prefsEditor.commit();
    }*/

/*

    @Override
    public void onScanResult(JSONObject obj, int scantype) {

        saveTempData(mProductArray);
    }
*/

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);
            } else {
                Log.e(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();
            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        if (mNfcAdapter != null)
            setupForegroundDispatch(this, mNfcAdapter);

        changeCount();
    }

    private void changeCount() {
        int mCount=0;
        gson=new Gson();
        String tempdata = appSharedPrefs.getString("tempcartlist", null);
        Type type = new TypeToken<List<CartItem>>() {}.getType();
        ArrayList<CartItem> arr=gson.fromJson(tempdata, type);
        if(arr!=null) {
            mCartArray = gson.fromJson(tempdata, type);
        }
        for (int i = 0; i < mCartArray.size(); i++) {
            if (mCartArray.get(i).getCount()>0) {
                {
                    mCount = mCount + mCartArray.get(i).getCount();
                }
            }
        }
        mCountTxtView.setText("" + mCount);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        if (mNfcAdapter != null)
            stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);

    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {

        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};
        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param activity The corresponding {@link Activity} requesting to stop the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }


    /**
     * Background task for reading the data. Do not block the UI thread while reading.
     *
     * @author munik
     */
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {

                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {

                Log.e(TAG, "==" + result);
                try {
                    if (firstFragment != null)
                        ((HomeFragment) firstFragment).openScanListFrag(new JSONObject(result), 1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }


    }

}
