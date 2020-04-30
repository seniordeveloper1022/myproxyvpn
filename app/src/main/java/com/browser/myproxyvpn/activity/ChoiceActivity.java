package com.browser.myproxyvpn.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.browser.myproxyvpn.MainActivity;
import com.browser.myproxyvpn.R;
import com.browser.myproxyvpn.SplashScreen;
import com.browser.myproxyvpn.constant.Constants;
import com.browser.myproxyvpn.preference.PreferenceManager;
import com.browser.myproxyvpn.utils.IabHelper;
import com.browser.myproxyvpn.utils.IabResult;
import com.browser.myproxyvpn.utils.Inventory;
import com.browser.myproxyvpn.utils.Purchase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import static com.browser.myproxyvpn.SplashScreen.amount;
import static com.browser.myproxyvpn.SplashScreen.billingCycle;
import static com.browser.myproxyvpn.SplashScreen.expiryDate;

public class ChoiceActivity extends AppCompatActivity implements View.OnClickListener {
    @Inject
    protected PreferenceManager mPreferences;
    int subDuration = 0;
    String subscription_id = "free_trial";
    //BillingClient mBillingClient;
    String payMethod;
    TextView start_donePayment;


    /******************************************/
    public static final String MyPrefsSubscription = "MyPrefsSubscription";
    public static final String orderId = "orderId";
    public static final String productId = "productId";
    public static final String purchaseToken = "purchaseToken";
    public static final String autoRenewing = "autoRenewing";

    SharedPreferences sharedpreferences;
    IabHelper mHelper;
    private static final String TAG = "ChoiceActivity";
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;
    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener;
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener;
    private String userType = "0";
    private IInAppBillingService mService;
    String amt, billing_cycle;
    SimpleDateFormat simpleDateFormat;

    /*******************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        sharedpreferences = getSharedPreferences(MyPrefsSubscription, MODE_PRIVATE);

        LinearLayout oneMonthLayout = (LinearLayout) findViewById(R.id.month_1);
        LinearLayout sixMonthsLayout = (LinearLayout) findViewById(R.id.month_6);
        LinearLayout oneYearLayout = (LinearLayout) findViewById(R.id.year_1);
        LinearLayout trialDayLayout = (LinearLayout) findViewById(R.id.trial_day);
        start_donePayment = findViewById(R.id.start_donePayment);
        oneMonthLayout.setOnClickListener(this);
        sixMonthsLayout.setOnClickListener(this);
        oneYearLayout.setOnClickListener(this);
        trialDayLayout.setOnClickListener(this);
        start_donePayment.setOnClickListener(this);

        if (getIntent() != null) {
            if (!getIntent().getStringExtra("payMethod").equalsIgnoreCase("")
                    && !getIntent().getStringExtra("payMethod").equalsIgnoreCase("null")) {
                payMethod = getIntent().getStringExtra("payMethod");
            }
        }

        Log.d("rajniTAG", "" + payMethod);

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

     //  mService.getSkuDetails(3,getPackageName(),"subs",null)


    }

    @Override
    protected void onStart() {
        super.onStart();
        String base64EncodedPublicKey =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl/KmKF6W32wJp1lHfD8N7pp8AYJ+l2iUEUPgOAbNGkKfBkgA8ImwRcGYnZvzeyL8Iob87jIhMjtbDt13X2kaS1oo0P+RHcPsDXIt/TzqmXnvqKw2xWchzowQm6lIB/f1/A1i/fpPmzNZwO/J0zWv/HU5gUAMyBfoWnBj+0hhqgy6+hm/wlrDV0VU7GgVBXJxzzk35CcEzOVihwy0RxGsUk1bX/mzIP20tRgpqRH3LVFg2Go+KJarsJRISgeKB5WfdXG0s0astbyWG7/r4Pl3cATh04N2nwi18aHva7mHKzf7TSNDq7U++CyV/H7HTGQPjEbIDeJqc3yot6F+9z8RIQIDAQAB";

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new
                                   IabHelper.OnIabSetupFinishedListener() {
                                       public void onIabSetupFinished(IabResult result) {
                                           if (!result.isSuccess()) {
                                               Log.d(TAG, "In-app Billing setup failed: " +
                                                       result);
                                           } else {
                                               Log.d(TAG, "In-app Billing is set up OK");
                                           }
                                       }
                                   });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.month_1:
                subDuration = 1;
                subscription_id = "com.browser.myproxyvpn.onemonth";
                break;
            case R.id.month_6:
                subDuration = 6;
                subscription_id = "com.browser.myproxyvpn.sixmonths";
                break;

            case R.id.year_1:
                subDuration = 12;
                subscription_id = "com.browser.myproxyvpn.oneyear";
                break;

            case R.id.start_donePayment:

                Intent browserIntent = new Intent(this, MainActivity.class);
                browserIntent.putExtra("userType", userType);
                //browserIntent.putExtra("Data", subscription);
                browserIntent.putExtra("orderId", sharedpreferences.getString(orderId, ""));
                startActivity(browserIntent);
                finish();

            case R.id.trial_day:
                subscription_id = "com.browser.myproxyvpn.freetrialwithonemonth";

        }
        // if (!subscription_id.equalsIgnoreCase("free_trial")) {
        if (payMethod.equalsIgnoreCase(Constants.GOOGLEPLAY)) {
           /*Intent paymentDetailIntent = new Intent(this, PaymentGooglePlayActivity.class);
           paymentDetailIntent.putExtra("subDuration",subDuration);
           startActivity(paymentDetailIntent);*/

                /*mHelper.launchSubscriptionPurchaseFlow(this, subscription_id, 10001,
                        mPurchaseFinishedListener, "mypurchasetoken");
*/

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


                /*mBillingClient = BillingClient.newBuilder(this).setListener(this).build();

                mBillingClient.startConnection(new BillingClientStateListener() {
                    @Override
                    public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                        if (billingResponseCode == BillingClient.BillingResponse.OK) {
                            // The billing client is ready. You can query purchases here.


                            BillingFlowParams.Builder builder = BillingFlowParams.newBuilder()
                                    .setAccountId("123456")
                                    .setSku(subscription_id).setType(BillingClient.SkuType.SUBS);


                            int responseCode = mBillingClient.launchBillingFlow(ChoiceActivity.this, builder.build());

                            List<String> skuList = new ArrayList<>();
                            skuList.add(subscription_id);
                            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                            params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
                            mBillingClient.querySkuDetailsAsync(params.build(),
                                    new SkuDetailsResponseListener() {
                                        @Override
                                        public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                                            if (responseCode == BillingClient.BillingResponse.OK
                                                    && skuDetailsList != null) {
                                                for (SkuDetails skuDetails : skuDetailsList) {
                                                    String sku = skuDetails.getSku();
                                                    String price = skuDetails.getPrice();
                                                    if (subscription_id.equals(sku)) {
                                                        Log.d("subscription_id",subscription_id);
                                                        Toast.makeText(ChoiceActivity.this, "Premium server activated for "+subDuration+" month", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }

                                        }
                                    });
                            startSubscribe();
                        }
                    }
                    @Override
                    public void onBillingServiceDisconnected() {
                        // Try to restart the connection on the next request to
                        // Google Play by calling the startConnection() method.
                    }
                });*/


        } else {
            Intent paymentDetailIntent = new Intent(this, PaymentDetailActivity.class);

            if (subscription_id == "com.browser.myproxyvpn.onemonth") {

                paymentDetailIntent.putExtra("paymentText", "1 month" + "~" + "9.99" + "~" + "/month");

            } else if (subscription_id == "com.browser.myproxyvpn.sixmonths") {

                paymentDetailIntent.putExtra("paymentText", "6 months" + "~" + "6.65" + "~" + "/month");

            } else {

                paymentDetailIntent.putExtra("paymentText", "1 Year" + "~" + "4.99" + "~" + "/month");
            }

            startActivity(paymentDetailIntent);
        }

        mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, com.browser.myproxyvpn.utils.Purchase purchase) {

                if (result.isFailure()) {
                    // Handle error
                    Log.d("rajniTAG", "failure  onIabPurchaseFinished: " + result);
                    return;
                } else if (purchase.getSku().equals(subscription_id)) {
                    consumeItem();

                }
            }

        };

        mReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result,
                                                 Inventory inventory) {

                if (result.isFailure()) {
                    // Handle failure
                    Log.d("rajniTAG", "failure onQueryInventoryFinished : " + result);
                } else {
                    Purchase purchase = inventory.getPurchase(subscription_id);
                    Log.d("rajniTAG", "responseCode : " + result);
                    Log.e("TAG", "" + purchase);


                    sharedpreferences = getSharedPreferences(MyPrefsSubscription, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString(orderId, purchase.getOrderId());
                    editor.putString(productId, purchase.getSku());
                    editor.putString(purchaseToken, purchase.getToken());
                    editor.putString(autoRenewing, String.valueOf(purchase.getDeveloperPayload()));
                    editor.commit();
                    startActivity(new Intent(ChoiceActivity.this, MainActivity.class));
                    
                    /* mHelper.consumeAsync(inventory.getPurchase(subscription_id),
                            mConsumeFinishedListener);*/
                }


            }
        };


        mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
            @Override
            public void onConsumeFinished(com.browser.myproxyvpn.utils.Purchase purchase, IabResult result) {
                if (result.isSuccess()) {
                    //clickButton.setEnabled(true);
                    Log.d("rajniTAG", "responseCode : " + result);
                    sharedpreferences = getSharedPreferences(MyPrefsSubscription, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString(orderId, purchase.getOrderId());
                    editor.putString(productId, purchase.getSku());
                    editor.putString(purchaseToken, purchase.getToken());
                    editor.putString(autoRenewing, String.valueOf(purchase.getDeveloperPayload()));
                    editor.commit();
                    startActivity(new Intent(ChoiceActivity.this, MainActivity.class));

                } else {
                    // handle error
                    Log.d("rajniTAG", "failure  onConsumeFinished: " + result);
                }
            }
        };


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
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

                    getData();
                    Log.d("rajniTAG", "responseCode : " + result);
                    Log.e("TAG", "" + purchase);

                    Calendar calendar = Calendar.getInstance();
                    String subscribed_date = simpleDateFormat.format(calendar.getTime());
                    Log.e("subscribed", subscribed_date);

                    sharedpreferences = getSharedPreferences(MyPrefsSubscription, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString(orderId, purchase.getOrderId());
                    editor.putString(productId, purchase.getSku());
                    editor.putString(purchaseToken, purchase.getToken());
                    editor.putString(SplashScreen.subscribed_date, subscribed_date);
                    editor.putString(autoRenewing, String.valueOf(purchase.getDeveloperPayload()));
                    editor.commit();

                    Toast.makeText(ChoiceActivity.this, "Thank you for purchasing MyProxyVPN Premium. Your premium membership access is now enabled. You now have access to all premium servers, faster speeds, and no advertisements.", Toast.LENGTH_LONG).show();

                    //userType="1";
                    start_donePayment.setVisibility(View.VISIBLE);
                    // startActivity(new Intent(ChoiceActivity.this, MainActivity.class));

                }
            }
        });

    }

    //Call method to get subscription details
    private void getData() {
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
                                String date = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
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
                                    }
                                    Log.e("cycle", "" + cycle);
                                } else if (productIdResp.equalsIgnoreCase("free_trial")) {
                                    billing_cycle = "Free trial";
                                    amt = getString(R.string.trial_price);
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

                                //startActivity(new Intent(ChoiceActivity.this, MainActivity.class));
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
            }
        };

        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*private void startSubscribe() {
        mBillingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP,
                new PurchaseHistoryResponseListener() {
                    @Override
                    public void onPurchaseHistoryResponse(@BillingClient.BillingResponse int responseCode,
                                                          List<Purchase> purchasesList) {
                        if (responseCode == BillingClient.BillingResponse.OK
                                && purchasesList != null) {
                            for (Purchase purchase : purchasesList) {
                                // Process the result.
                                Log.d("rajniTAG","purchase :"+purchase);
                            }
                        }
                    }
                });
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        Log.d("rajniTAG","responseCode : "+responseCode+" onPurchasesUpdated : "+purchases);

        if (responseCode == BillingClient.BillingResponse.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                Log.d("rajniTAG","onPurchasesUpdated OK: "+purchase);
                Log.d("rajniTAG","onPurchasesUpdated OK: "+purchase.getOrderId());


                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString(orderId, purchase.getOrderId());
                editor.putString(productId, purchase.getSku());
                editor.putString(purchaseToken, purchase.getPurchaseToken());
                editor.putString(autoRenewing, String.valueOf(purchase.isAutoRenewing()));
                editor.commit();
                startActivity(new Intent(ChoiceActivity.this, MainActivity.class));
                //handlePurchase(purchase);
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            Log.d("rajniTAG","onPurchasesUpdated :USER_CANCELED");
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            Log.d("rajniTAG","onPurchasesUpdated :other"+responseCode);
            // Handle any other error codes.
        }

    }*/
}
