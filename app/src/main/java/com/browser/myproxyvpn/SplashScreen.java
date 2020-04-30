package com.browser.myproxyvpn;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.browser.myproxyvpn.activity.LoginActivity;
import com.browser.myproxyvpn.utils.IabHelper;
import com.browser.myproxyvpn.utils.SubscriptionClass;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.browser.myproxyvpn.activity.UpgradeActivity;
import com.browser.myproxyvpn.browser.activity.ThemableBrowserActivity;
import com.browser.myproxyvpn.constant.Constants;
import com.browser.myproxyvpn.dialog.ProxyData;
import com.browser.myproxyvpn.dialog.ProxyModel;
import com.browser.myproxyvpn.utils.AESUtil;

import static com.browser.myproxyvpn.utils.UserPrefUtils.IS_REMEMBER;
import static com.browser.myproxyvpn.utils.UserPrefUtils.IS_SUBSCRIBED;
import static com.browser.myproxyvpn.utils.UserPrefUtils.IS_WHMS;
import static com.browser.myproxyvpn.utils.UserPrefUtils.KEY_EMAIL;
import static com.browser.myproxyvpn.utils.UserPrefUtils.PREF_NAME;
import static com.browser.myproxyvpn.utils.UserPrefUtils.SUBSCRIPTION;


public class SplashScreen extends ThemableBrowserActivity implements View.OnClickListener {

    private Timer timer;
    private final int timeToShow = 3000;
    private static final String URL = "https://sl.myproxyvpn.com/get/";
    private static final String KEY = "0123456789abcdef";


    /********************/
    public static final String MyPrefsSubscription = "MyPrefsSubscription";
    public static final String orderId = "orderId";
    public static final String productId = "productId";
    public static final String purchaseToken = "purchaseToken";
    public static final String autoRenewing = "autoRenewing";
    public static final String expiryDate = "expiryDate";
    public static final String billingCycle = "billingCycle";
    public static final String amount = "amount";
    public static final String subscribed_date = "subscribed_date";
    public static String orderIduser, productIduser, purchaseTokenuser, autoRenewinguser;
    SharedPreferences sharedpreferences;
    SharedPreferences userPref;
    String userType = "0";
    boolean isLogin = false;
    SubscriptionClass subscription;

    private IInAppBillingService mService;
    private String appPackageName;
    IabHelper mHelper;
    String base64EncodedPublicKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl/KmKF6W32wJp1lHfD8N7pp8AYJ+l2iUEUPgOAbNGkKfBkgA8ImwRcGYnZvzeyL8Iob87jIhMjtbDt13X2kaS1oo0P+RHcPsDXIt/TzqmXnvqKw2xWchzowQm6lIB/f1/A1i/fpPmzNZwO/J0zWv/HU5gUAMyBfoWnBj+0hhqgy6+hm/wlrDV0VU7GgVBXJxzzk35CcEzOVihwy0RxGsUk1bX/mzIP20tRgpqRH3LVFg2Go+KJarsJRISgeKB5WfdXG0s0astbyWG7/r4Pl3cATh04N2nwi18aHva7mHKzf7TSNDq7U++CyV/H7HTGQPjEbIDeJqc3yot6F+9z8RIQIDAQAB";
    int counryPopupOpen = 0;

    LinearLayout lnLogin;
    String amt, billing_cycle;

    /*******************/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (getIntent() != null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                userType = extras.getString("userType");
                Log.d("rajniTAG", "splashscreen intent " + userType);
            }
        }

        userPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if (userPref.contains(IS_REMEMBER)) {
            isLogin = userPref.getBoolean(IS_REMEMBER, false);
            if (isLogin) {
                if (!userPref.getString(SUBSCRIPTION, "").isEmpty()) {
                    Gson gson = new Gson();
                    String data = userPref.getString(SUBSCRIPTION, "");
                    subscription = gson.fromJson(data, SubscriptionClass.class);
                    if (subscription != null) {
                        userType = "1";
                    } else {
                        userType = "0";
                    }
                }
            }
        } else {
            if (userPref.contains(IS_WHMS)) {
                SharedPreferences.Editor editor = userPref.edit();
                editor.remove(IS_WHMS);
                editor.remove(KEY_EMAIL);
                editor.remove(IS_SUBSCRIBED);
                editor.commit();
            }
        }

        sharedpreferences = getSharedPreferences(MyPrefsSubscription, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(billingCycle);
        editor.remove(expiryDate);
        editor.remove(amount);
        editor.remove(orderId);
        editor.commit();

        appPackageName = this.getPackageName();

        TextView startView = (TextView) findViewById(R.id.start_title);
        TextView reloadView = (TextView) findViewById(R.id.reload_title);
        LinearLayout premiumView = (LinearLayout) findViewById(R.id.premium);
        lnLogin = findViewById(R.id.lnLogin);
        reloadView.setOnClickListener(this);

        startView.setOnClickListener(this);

        premiumView.setOnClickListener(this);
        lnLogin.setOnClickListener(this);
        TimerTask timerTask = new TimerTask() {

            private int doneTime;

            @Override
            public void run() {
                final int timeLeft = timeToShow - doneTime;

                if (timeLeft == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent iMain = new Intent(SplashScreen.this, MainActivity.class);
                            iMain.putExtra("userType", userType);
                            startActivity(iMain);
                            //startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        }
                    });
                }

                doneTime += 1000;
            }
        };

        timer = new Timer();
        //timer.schedule(timerTask, 0, 1000);
        new getProxyData().execute(URL);
    }

    /* private class AvailablePurchaseAsyncTask extends AsyncTask<Void, Void, Bundle> {
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
                Log.d("rajniTAG","purchaseDataList : "+purchaseDataList);
                if(purchaseDataList!=null){


                    for (int i = 0; i < purchaseDataList.size(); ++i) {
                        try {
                            JSONObject object = new JSONObject(purchaseDataList.get(i).toString());
                            String productIdResp = object.getString("productId");
                            String orderIdResp = object.getString("orderId");
                            String purchaseTokenResp = object.getString("mypurchasetoken");
                            String autoRenewingResp = object.getString("autoRenewing");
                            Log.d("rajniTAG","productIdResp : "+productIdResp);
                            Log.d("rajniTAG","orderIdResp : "+orderIdResp);
                            Log.d("rajniTAG","purchaseTokenResp : "+purchaseTokenResp);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_title:
                timer.cancel();
                Intent browserIntent = new Intent(this, MainActivity.class);
                browserIntent.putExtra("userType", userType);
                browserIntent.putExtra("Data", subscription);
                browserIntent.putExtra("orderId", sharedpreferences.getString(orderId, ""));
                startActivity(browserIntent);
                break;
            case R.id.reload_title:
                mPreferences.setProxyChoice(Constants.PROXY_MPVG);
                mPreferences.setMpvgProxyHost();
                mPreferences.setMpvgProxyPort(mPreferences.getInitialProxyPort());
                mPreferences.setCountry(mPreferences.getDefaultCountry());
                timer.cancel();
                Intent reloadIntent = new Intent(this, MainActivity.class);
                reloadIntent.putExtra("userType", userType);
                reloadIntent.putExtra("Data", subscription);
                reloadIntent.putExtra("orderId", sharedpreferences.getString(orderId, ""));
                startActivity(reloadIntent);
                break;
            case R.id.premium:
                timer.cancel();
                Intent premiumIntent = new Intent(this, UpgradeActivity.class);
                startActivity(premiumIntent);
                break;
            case R.id.lnLogin:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

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
                    Log.d("rajniTAG", "purchaseDataList : " + purchaseDataList);
                    if (purchaseDataList != null) {

                        for (int i = 0; i < purchaseDataList.size(); ++i) {
                            try {
                                JSONObject object = new JSONObject(purchaseDataList.get(i).toString());
                                String productIdResp = object.getString("productId");
                                String orderIdResp = object.getString("orderId");
                                String purchaseTime = object.getString("purchaseTime");
                                //String purchaseTokenResp = object.getString("mypurchasetoken");
                                String purchaseTokenResp = object.getString("purchaseToken");
                                String autoRenewingResp = object.getString("autoRenewing");
                                String purchaseState = object.getString("purchaseState");
                                Log.d("rajniTAG", "productIdResp : " + productIdResp);
                                Log.d("rajniTAG", "orderIdResp : " + orderIdResp);
                                Log.d("rajniTAG", "purchaseTokenResp : " + purchaseTokenResp);

                                //Expiry Date
                                long millisec = Long.parseLong(purchaseTime);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(new Date(millisec));
                                calendar.add(Calendar.MONTH, 1);
                                String date = new SimpleDateFormat("MMMM d, yyyy").format(calendar.getTime());
                                Log.e("date", "" + date);

                                //Billing cycle and amount
                                if (productIdResp.contains(".")) {
                                    String[] str = productIdResp.split("\\.");
                                    String cycle = str[str.length - 1];
                                    if (cycle.equalsIgnoreCase("onemonth")) {
                                        billing_cycle = "1 month";
                                        amt = getString(R.string.month_1_price);
                                    } else if (cycle.equalsIgnoreCase("sixmonths")) {
                                        billing_cycle = "6 month";
                                        amt = getString(R.string.month_6_price);
                                    } else if (cycle.equalsIgnoreCase("oneyear")) {
                                        billing_cycle = "1 year";
                                        amt = getString(R.string.year_1_price);
                                    } else if (cycle.equalsIgnoreCase("freetrialwithonemonth")) {
                                        billing_cycle = "1 month";
                                        amt = getString(R.string.month_1_price);
                                    }
                                    Log.e("cycle", "" + cycle);
                                }
                                Log.e("cycle", "" + productIdResp);

                                SharedPreferences.Editor editor = sharedpreferences.edit();

                                editor.putString(orderId, orderIdResp);
                                editor.putString(productId, productIdResp);
                                editor.putString(purchaseToken, purchaseTokenResp);
                                editor.putString(autoRenewing, autoRenewingResp);
                                editor.putString(expiryDate, date);
                                editor.putString(billingCycle, billing_cycle);
                                editor.putString(amount, amt);
                                editor.commit();

                                userType = "1";

                                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.remove(billingCycle);
                        editor.remove(expiryDate);
                        editor.remove(amount);
                        editor.remove(orderId);
                        editor.commit();

                        userType = "0";
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
               /* AvailablePurchaseAsyncTask mAsyncTask = new AvailablePurchaseAsyncTask(appPackageName);
                mAsyncTask.execute();*/
            }
        };

        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    private class getProxyData extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                String line = "";
                String result = "";
                while ((line = reader.readLine()) != null) {
                    result += line;

                }
                stream.close();
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Log.d("ResultSL",""+result);
                String decryptedData = AESUtil.decrypt(KEY, result.substring(2));
                JSONObject jsonObj = null;
                String jsonValue = "";
                try {
                    jsonObj = XML.toJSONObject(decryptedData);
                    jsonValue = jsonObj.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ProxyData proxyData = new ProxyData();
                proxyData = new Gson().fromJson(jsonValue, ProxyData.class);

                mPreferences.setProxyServerData(jsonValue);
                for (ProxyModel proxy : proxyData.serverlist.proxy) {
                    if (proxy.getInitial().equals("yes")) {
                        mPreferences.setInitialProxyPort(proxy.getPort());
                        mPreferences.setDefaultCountry(proxy.getCountry());
                        mPreferences.setMpvgProxyHost();
                    }
                }
                if (mPreferences.getUserSetting().equals(Constants.PREMIUM)) {
                    mPreferences.setMpvgProxyPort(mPreferences.getInitialProxyPort());
                    mPreferences.setCountry(mPreferences.getDefaultCountry());
                    Log.e("mPref", "Premium");
                }
                if (mPreferences.getProxyChoice() == Constants.PROXY_MPVG) {
                    mPreferences.setProxyChoice(Constants.PROXY_MPVG);
                    mPreferences.setMpvgProxyPort(mPreferences.getMpvgProxyPort());
                    Log.e("mPref", "mPref");
                }
            }
        }
    }

}

