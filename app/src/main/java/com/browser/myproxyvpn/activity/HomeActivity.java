package com.browser.myproxyvpn.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.android.vending.billing.IInAppBillingService;
import com.browser.myproxyvpn.R;
import com.browser.myproxyvpn.SplashScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 3000;
    private static final String TAG = "HomeActivity" ;
    private IInAppBillingService mService;
    private SharedPreferences sharedpreferences;
    public static final String MyPrefsSubscription = "MyPrefsSubscription";
    public static final String orderId = "orderId";
    public static final String productId = "productId";
    public static final String purchaseToken = "purchaseToken";
    public static final String autoRenewing = "autoRenewing";
    String userType = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        changeStatusBarColor();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                callService();

            }
        }, SPLASH_DISPLAY_LENGTH);

    }



    public void callService() {
        Log.e(TAG,"callService:");
        sharedpreferences = getSharedPreferences(MyPrefsSubscription, MODE_PRIVATE);
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

                             /*   String freeTrialPeriod = object.getString("freeTrialPeriod");
                                String subscriptionPeriod = object.getString("subscriptionPeriod");

                                Log.e(TAG,"freeTrialPeriod:"+freeTrialPeriod);
                                Log.e(TAG,"subscriptionPeriod:"+subscriptionPeriod);
*/

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


                    if (userType.equals("1")) {
                        Intent mainIntent = new Intent(HomeActivity.this, SplashScreen.class);
                        mainIntent.putExtra("userType", userType);
                        startActivity(mainIntent);
                        finish();



                    } else {
                        Intent mainIntent = new Intent(HomeActivity.this, SlideActivity.class);
                        mainIntent.putExtra("userType", userType);
                        startActivity(mainIntent);
                        finish();

                    }



                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                /*AvailablePurchaseAsyncTask mAsyncTask = new AvailablePurchaseAsyncTask(appPackageName);
                mAsyncTask.execute();*/
            }
        };
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

    }


    private void changeStatusBarColor(){
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }
    }



}
