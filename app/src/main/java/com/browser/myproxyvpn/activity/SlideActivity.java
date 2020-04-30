package com.browser.myproxyvpn.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.browser.myproxyvpn.R;
import com.browser.myproxyvpn.SplashScreen;
import com.browser.myproxyvpn.utils.IabHelper;
import com.browser.myproxyvpn.utils.IabResult;
import com.browser.myproxyvpn.utils.Inventory;
import com.browser.myproxyvpn.utils.Purchase;
import com.matthewtamlin.sliding_intro_screen_library.background.BackgroundManager;
import com.matthewtamlin.sliding_intro_screen_library.core.LockableViewPager;
import com.matthewtamlin.sliding_intro_screen_library.core.PageAdapter;
import com.matthewtamlin.sliding_intro_screen_library.indicators.DotIndicator;
import com.matthewtamlin.sliding_intro_screen_library.indicators.SelectionIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public class SlideActivity extends AppCompatActivity implements View.OnClickListener {
    private final ArrayList<Fragment> pages = new ArrayList<>();
    private static String[] SLIDE_TITLE = {"Unlock Any Website", "Unlock 20+ Premium Servers", "Unlimited IP Changes", "Turbo Speed Servers"};
    private static String[] SLIDE_DESCRIPTION = {"Unlock any website you want to visit. Bypass censorship and geo restrictions easily.",
            "By going Premium, you unlock access to over 20+ locations from around the world.",
            "Change your IP address automatically. Get unlimited amount of server and IP changes.",
            "Enjoy the lightning fast turbo speeds from our premium server locations today!"};
    private static final int DEFAULT_CURRENT_PAGE_INDEX = 0;
    private static final String STATE_KEY_CURRENT_PAGE_INDEX = "current page index";
    private final PageAdapter adapter = new PageAdapter(getSupportFragmentManager(), pages);
    private LockableViewPager viewPager;
    private FrameLayout progressIndicatorWrapper;
    private SelectionIndicator progressIndicator;
    private boolean progressIndicatorAnimationsEnabled = true;
    private BackgroundManager backgroundManager = null;
    private LinearLayout rootView;
    private Button startTrialBtn, upgradePremiumbtn;
    private Button skip_button;
    /********************/
    public static final String MyPrefsSubscription = "MyPrefsSubscription";
    public static final String orderId = "orderId";
    public static final String productId = "productId";
    public static final String purchaseToken = "purchaseToken";
    public static final String autoRenewing = "autoRenewing";
    public static String orderIduser, productIduser, purchaseTokenuser, autoRenewinguser;
    SharedPreferences sharedpreferences;
    String userType = "0";
    TextView tvPrivacy, tvTerms;

    private IInAppBillingService mService;
    private String appPackageName;
    IabHelper mHelper;
    String base64EncodedPublicKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl/KmKF6W32wJp1lHfD8N7pp8AYJ+l2iUEUPgOAbNGkKfBkgA8ImwRcGYnZvzeyL8Iob87jIhMjtbDt13X2kaS1oo0P+RHcPsDXIt/TzqmXnvqKw2xWchzowQm6lIB/f1/A1i/fpPmzNZwO/J0zWv/HU5gUAMyBfoWnBj+0hhqgy6+hm/wlrDV0VU7GgVBXJxzzk35CcEzOVihwy0RxGsUk1bX/mzIP20tRgpqRH3LVFg2Go+KJarsJRISgeKB5WfdXG0s0astbyWG7/r4Pl3cATh04N2nwi18aHva7mHKzf7TSNDq7U++CyV/H7HTGQPjEbIDeJqc3yot6F+9z8RIQIDAQAB";
    int counryPopupOpen = 0;
    private String subscription_id;

    private static final String TAG = "SlideActivity";
    SimpleDateFormat simpleDateFormat;

    /*******************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " +
                            result);
                } else {
                    Log.d(TAG, "In-app Billing is set up OK");
                }
            }
        });

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        sharedpreferences = getSharedPreferences(MyPrefsSubscription, MODE_PRIVATE);
        appPackageName = this.getPackageName();
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        ServiceConnection mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
                try {

                    Bundle ownedItems = mService.getPurchases(3, getPackageName(), "subs", null);
                    //ArrayList ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                    ArrayList purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
               /* ArrayList signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");*/
                    // Log.d("rajniTAG","ownedSkus : "+ownedSkus);
                    Log.d("rajniTAG", "slideactivity purchaseDataList : " + purchaseDataList);
                    if (purchaseDataList != null) {

                        for (int i = 0; i < purchaseDataList.size(); ++i) {
                            try {
                                JSONObject object = new JSONObject(purchaseDataList.get(i).toString());
                                String productIdResp = object.getString("productId");
                                String orderIdResp = object.getString("orderId");
                                //String purchaseTokenResp = object.getString("mypurchasetoken");
                                String purchaseTokenResp = object.getString("purchaseToken");
                                String autoRenewingResp = object.getString("autoRenewing");

                                final SharedPreferences.Editor editor = sharedpreferences.edit();

                                editor.putString(orderId, orderIdResp);
                                editor.putString(productId, productIdResp);
                                editor.putString(purchaseToken, purchaseTokenResp);
                                editor.putString(autoRenewing, autoRenewingResp);
                                editor.commit();

                                //A Vpn
                                userType = "1";
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                /*AvailablePurchaseAsyncTask mAsyncTask = new AvailablePurchaseAsyncTask(appPackageName);
                mAsyncTask.execute();*/
            }
        };
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        rootView = (LinearLayout) findViewById(R.id.slide_root);
        viewPager = (LockableViewPager) findViewById(com.matthewtamlin.sliding_intro_screen_library.R.id.intro_activity_viewPager);
        progressIndicatorWrapper =
                (FrameLayout) findViewById(R.id.activity_progressIndicatorHolder);
        startTrialBtn = findViewById(R.id.start_trial_button);
        upgradePremiumbtn = findViewById(R.id.upgrade_topremium_button);
        skip_button = findViewById(R.id.skip_button);
        tvPrivacy = findViewById(R.id.tvPrivacy);
        tvTerms = findViewById(R.id.tvTerms);
        tvPrivacy.setOnClickListener(this);
        tvTerms.setOnClickListener(this);
        startTrialBtn.setOnClickListener(this);
        upgradePremiumbtn.setOnClickListener(this);
        skip_button.setOnClickListener(this);
        pages.addAll(generatePages(savedInstanceState));
        viewPager.addOnPageChangeListener(pageChangeListenerDelegate);
        initialiseViewPager(savedInstanceState);

        // Initialise the progress indicator
        progressIndicator = new DotIndicator(this);
        regenerateProgressIndicator();

        tvPrivacy.setPaintFlags(tvPrivacy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvTerms.setPaintFlags(tvTerms.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


    }

    /*private class AvailablePurchaseAsyncTask extends AsyncTask<Void, Void, Bundle> {
        String packageName;
        public AvailablePurchaseAsyncTask(String packageName){
            this.packageName = packageName;
        }
        @Override
        protected Bundle doInBackground(Void... voids) {
            ArrayList<String> skuList = new ArrayList<String>();
            skuList.add("com.browser.myproxyvpn.onemonth");
            skuList.add("com.browser.myproxyvpn.sixmonths");
            skuList.add("com.browser.myproxyvpn.oneyear");
            Bundle query = new Bundle();
            query.putStringArrayList("ITEM_ID_LIST", skuList);
            Bundle skuDetails = null;
            try {

                Bundle ownedItems = mService.getPurchases(3, getPackageName(), "subs", null);
                //ArrayList ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
               *//* ArrayList signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");*//*
                // Log.d("rajniTAG","ownedSkus : "+ownedSkus);
                Log.d("rajniTAG","slideactivity purchaseDataList : "+purchaseDataList);
                if(purchaseDataList!=null){


                    for (int i = 0; i < purchaseDataList.size(); ++i) {
                        try {
                            JSONObject object = new JSONObject(purchaseDataList.get(i).toString());
                            String productIdResp = object.getString("productId");
                            String orderIdResp = object.getString("orderId");
                            String purchaseTokenResp = object.getString("mypurchasetoken");
                            String autoRenewingResp = object.getString("autoRenewing");


                            SharedPreferences.Editor editor = sharedpreferences.edit();

                            editor.putString(orderId, orderIdResp);
                            editor.putString(productId, productIdResp);
                            editor.putString(purchaseToken, purchaseTokenResp);
                            editor.putString(autoRenewing, autoRenewingResp);
                            editor.commit();
                            userType="1";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return skuDetails;
        }
        @Override
        protected void onPostExecute(Bundle skuDetails) {
        }
    }*/
    protected Collection<? extends Fragment> generatePages(Bundle savedInstanceState) {
        final ArrayList<Fragment> pages = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final ParallaxPages newPage = ParallaxPages.newInstance();
            newPage.setSlideIndex(i);
            newPage.setSlideTitle(SLIDE_TITLE[i]);
            newPage.setSlideDescription(SLIDE_DESCRIPTION[i]);
            pages.add(newPage);
        }

        return pages;
    }

    private final ViewPager.OnPageChangeListener pageChangeListenerDelegate = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (backgroundManager != null) {
                backgroundManager.updateBackground(rootView, position, positionOffset);
            }
        }

        @Override
        public void onPageSelected(int position) {

            if (progressIndicator != null) {
                progressIndicator.setSelectedItem(position, progressIndicatorAnimationsEnabled);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // Forced to implement this method, just do nothing
        }
    };

    private void regenerateProgressIndicator() {
        // Refresh the View by entirely removing the progress indicator so that it can be re-added
        progressIndicatorWrapper.removeAllViews();

        // Only re-add the indicator if one currently exists
        if (progressIndicator != null) {
            progressIndicatorWrapper.addView((View) progressIndicator);

            // Make sure the number of pages and the displayed page is correct
            progressIndicator.setNumberOfItems(pages.size());
            progressIndicator.setSelectedItem(getIndexOfCurrentPage(), false);
        }
    }

    public final int getIndexOfCurrentPage() {
        return viewPager.getCurrentItem();
    }

    private void initialiseViewPager(final Bundle savedInstanceState) {
        // Restore the page index from the saved instance state if possible
        final int pageIndex = (savedInstanceState == null) ?
                DEFAULT_CURRENT_PAGE_INDEX :
                savedInstanceState.getInt(STATE_KEY_CURRENT_PAGE_INDEX, DEFAULT_CURRENT_PAGE_INDEX);

        // Initialise the dataset of the view pager and display the desired page
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pageIndex);

        // Make sure the background for the current page is displayed
        if (backgroundManager != null) {
            backgroundManager.updateBackground(rootView, pageIndex, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_trial_button:

                subscription_id = "com.browser.myproxyvpn.freetrialwithonemonth";

                mHelper.launchSubscriptionPurchaseFlow(this, subscription_id, 10001,
                        new IabHelper.OnIabPurchaseFinishedListener() {
                            @Override
                            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                                if (result.isFailure()) {
                                    // Handle error
                                    Log.d("rajniTAG", "failure  onIabPurchaseFinished: " + result);
                                    return;
                                } else if (purchase.getSku().equals(subscription_id)) {
                                    consumeItem();
                                }
                            }
                        }, "mypurchasetoken");
                break;


            case R.id.upgrade_topremium_button:


                if (userType.equals("0")) {

                    subscription_id = "com.browser.myproxyvpn.onemonth";

                    mHelper.launchSubscriptionPurchaseFlow(this, subscription_id, 10001,
                            new IabHelper.OnIabPurchaseFinishedListener() {
                                @Override
                                public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                                    if (result.isFailure()) {
                                        // Handle error
                                        Log.d("rajniTAG", "failure  onIabPurchaseFinished: " + result);
                                        return;
                                    } else if (purchase.getSku().equals(subscription_id)) {
                                        consumeItem();
                                    }
                                }
                            }, "mypurchasetoken");

                } else {
                    Toast.makeText(this, getString(R.string.premiumMsg), Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.skip_button:
                Intent splashIntent = new Intent(getApplicationContext(), SplashScreen.class);
                splashIntent.putExtra("userType", userType);
                startActivity(splashIntent);
                break;
            case R.id.tvPrivacy:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://myproxyvpn.com/privacy.php"));
                startActivity(browserIntent);
                break;
            case R.id.tvTerms:
                Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://myproxyvpn.com/terms.php"));
                startActivity(browseIntent);
                break;

        }
    }


    public void consumeItem() {
        //mHelper.queryInventoryAsync(mReceivedInventoryListener);

        mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                if (result.isFailure()) {
                    // Handle failure
                    Log.d("rajniTAG", "failure onQueryInventoryFinished : " + result);
                    userType = "0";
                } else {
                    Purchase purchase = inventory.getPurchase(subscription_id);
                    /*if (purchase!=null){
                        String orderIdResp =purchase.getOrderId();
                        String productId=purchase.getProductId();
                        String purchaseTime = String.valueOf(purchase.getPurchaseTime());
                        String purchaseTokenResp = purchase.getToken();
                        String autoRenewingResp = purchase.get;
                        String purchaseState = String.valueOf(purchase.getPurchaseState());
                    }*/

                    // getData();
                    userType = "1";

                    Calendar calendar = Calendar.getInstance();
                    String subscribed_date = simpleDateFormat.format(calendar.getTime());
                    Log.e("subscribed", subscribed_date);

                    Log.d("rajniTAG", "responseCode : " + result);
                    Log.e("TAG", "" + purchase);

                    sharedpreferences = getSharedPreferences(MyPrefsSubscription, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString(orderId, purchase.getOrderId());
                    editor.putString(productId, purchase.getSku());
                    editor.putString(purchaseToken, purchase.getToken());
                    editor.putString(autoRenewing, String.valueOf(purchase.getDeveloperPayload()));
                    editor.putString(SplashScreen.subscribed_date, subscribed_date);
                    editor.commit();

                    Toast.makeText(SlideActivity.this, getString(R.string.msgPurchase), Toast.LENGTH_LONG).show();

                    Intent splashIntent = new Intent(getApplicationContext(), SplashScreen.class);
                    splashIntent.putExtra("userType", userType);
                    startActivity(splashIntent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
