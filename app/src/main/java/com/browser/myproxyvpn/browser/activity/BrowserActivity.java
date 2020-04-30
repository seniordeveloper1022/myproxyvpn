/*
 * Copyright 2015 Anthony Restaino
 */

package com.browser.myproxyvpn.browser.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebIconDatabase;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.vending.billing.IInAppBillingService;
import com.anthonycr.bonsai.Completable;
import com.anthonycr.bonsai.CompletableOnSubscribe;
import com.anthonycr.bonsai.Schedulers;
import com.anthonycr.bonsai.SingleOnSubscribe;
import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.progress.AnimatedProgressBar;
import com.browser.myproxyvpn.BrowserApp;
import com.browser.myproxyvpn.IncognitoActivity;
import com.browser.myproxyvpn.R;
import com.browser.myproxyvpn.SplashScreen;
import com.browser.myproxyvpn.activity.UpgradeActivity;
import com.browser.myproxyvpn.adblock.AdModel;
import com.browser.myproxyvpn.browser.BookmarksView;
import com.browser.myproxyvpn.browser.BrowserPresenter;
import com.browser.myproxyvpn.browser.BrowserView;
import com.browser.myproxyvpn.browser.SearchBoxModel;
import com.browser.myproxyvpn.browser.TabsManager;
import com.browser.myproxyvpn.browser.TabsView;
import com.browser.myproxyvpn.browser.fragment.BookmarksFragment;
import com.browser.myproxyvpn.browser.fragment.TabsFragment;
import com.browser.myproxyvpn.constant.Constants;
import com.browser.myproxyvpn.constant.DownloadsPage;
import com.browser.myproxyvpn.constant.HistoryPage;
import com.browser.myproxyvpn.controller.UIController;
import com.browser.myproxyvpn.database.HistoryItem;
import com.browser.myproxyvpn.database.bookmark.BookmarkModel;
import com.browser.myproxyvpn.database.history.HistoryModel;
import com.browser.myproxyvpn.dialog.BrowserDialog;
import com.browser.myproxyvpn.dialog.LightningDialogBuilder;
import com.browser.myproxyvpn.dialog.ProxyAdapter;
import com.browser.myproxyvpn.dialog.ProxyData;
import com.browser.myproxyvpn.dialog.ProxyModel;
import com.browser.myproxyvpn.interpolator.BezierDecelerateInterpolator;
import com.browser.myproxyvpn.reading.activity.ReadingActivity;
import com.browser.myproxyvpn.receiver.NetworkReceiver;
import com.browser.myproxyvpn.search.SearchEngineProvider;
import com.browser.myproxyvpn.search.SuggestionsAdapter;
import com.browser.myproxyvpn.search.engine.BaseSearchEngine;
import com.browser.myproxyvpn.settings.activity.SettingsActivity;
import com.browser.myproxyvpn.utils.ApiClient;
import com.browser.myproxyvpn.utils.ApiInterface;
import com.browser.myproxyvpn.utils.DrawableUtils;
import com.browser.myproxyvpn.utils.IabHelper;
import com.browser.myproxyvpn.utils.IabResult;
import com.browser.myproxyvpn.utils.IntentUtils;
import com.browser.myproxyvpn.utils.Inventory;
import com.browser.myproxyvpn.utils.LoginData;
import com.browser.myproxyvpn.utils.Preconditions;
import com.browser.myproxyvpn.utils.ProxyUtils;
import com.browser.myproxyvpn.utils.Purchase;
import com.browser.myproxyvpn.utils.SubscriptionClass;
import com.browser.myproxyvpn.utils.ThemeUtils;
import com.browser.myproxyvpn.utils.UrlUtils;
import com.browser.myproxyvpn.utils.Utils;
import com.browser.myproxyvpn.utils.WebUtils;
import com.browser.myproxyvpn.view.Handlers;
import com.browser.myproxyvpn.view.LightningView;
import com.browser.myproxyvpn.view.SearchView;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.Gravity.BOTTOM;
import static com.browser.myproxyvpn.preference.PreferenceManager.CHECK_DIALOG;
import static com.browser.myproxyvpn.utils.UserPrefUtils.IS_SUBSCRIBED;
import static com.browser.myproxyvpn.utils.UserPrefUtils.IS_WHMS;
import static com.browser.myproxyvpn.utils.UserPrefUtils.KEY_EMAIL;
import static com.browser.myproxyvpn.utils.UserPrefUtils.PREF_NAME;


public abstract class BrowserActivity extends ThemableBrowserActivity implements BrowserView,
        UIController, OnClickListener {

    private static final String TAG = "BrowserActivity";
    private InterstitialAd mInterstitialAd;
    public RewardedVideoAd mAd;
    private static final String INTENT_PANIC_TRIGGER = "info.guardianproject.panic.action.TRIGGER";

    private static final String TAG_BOOKMARK_FRAGMENT = "TAG_BOOKMARK_FRAGMENT";
    private static final String TAG_TABS_FRAGMENT = "TAG_TABS_FRAGMENT";
    //private static  String DEVICE_ID = "6C6B260D335FA87C8348BFE05CEF2D66";
    private static String DEVICE_ID = "BD711FFA45608A9C4FB420EF171F5F93";//82DD50D8A634CCF305EBC5E463BD2CD9";//BD711FFA45608A9C4FB420EF171F5F93
    //private static final String INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712";
    private static final String INTERSTITIAL_ID = "ca-app-pub-5125896501347711/2967082182";
    //private static final String REWARDED_ID = "ca-app-pub-3940256099942544/5224354917";
    private static final String REWARDED_ID = "ca-app-pub-5125896501347711/7172537615";
    private static final String APP_ID = "ca-app-pub-5125896501347711~4957437812";
    //private static final String APP_ID = "ca-app-pub-3940256099942544~3347511713";
    private static final String AD_FAIL_GUIDE = "Sorry, there is no available ads to show to you. Please try again in a few hours. If problem persists, please wait 24 hours";
    private int rewardedAdFailCount = 0;
    // Static Layout
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.content_frame)
    FrameLayout mBrowserFrame;
    @BindView(R.id.left_drawer)
    ViewGroup mDrawerLeft;
    @BindView(R.id.right_drawer)
    ViewGroup mDrawerRight;
    @BindView(R.id.ui_layout)
    ViewGroup mUiLayout;
    @BindView(R.id.toolbar_layout)
    ViewGroup mToolbarLayout;
    @BindView(R.id.progress_view)
    AnimatedProgressBar mProgressBar;
    @BindView(R.id.search_bar)
    RelativeLayout mSearchBar;
    @BindView(R.id.btn_nonPremium)
    RelativeLayout btn_nonPremium;
    @BindView(R.id.txtPremium)
    TextView txtPremium;
    @BindView(R.id.txtSubscribe)
    TextView txtSubscribe;
    @BindView(R.id.imageShield)
    ImageView imageShield;

    // Toolbar Views
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private View mSearchBackground;
    private SearchView mSearch;
    private ImageView mArrowImage;
    private ImageView mCountryImage;
    private View mCountryBackground;
    private View selectedView;
    private Dialog serverListDialog;
    // Current tab view being displayed
    @Nullable
    private View mCurrentView;
    private ProxyAdapter premiumAdapter;
    // Full Screen Video Views
    private FrameLayout mFullscreenContainer;
    private VideoView mVideoView;
    private View mCustomView;
    private ListView premiumListView;
    private ListView freeListView;
    private TextView minuteView;
    private TextView secondView;
    private LinearLayout timerLayout;
    private LinearLayout premiumLayout;
    private AlertDialog.Builder serverBuilder;
    // Adapter
    private SuggestionsAdapter mSuggestionsAdapter;
    // Callback
    private CustomViewCallback mCustomViewCallback;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;
    private ProxyAdapter freeAdapter;

    // Primitives
    private boolean mFullScreen;
    private boolean mDarkTheme;
    private boolean mIsFullScreen = false;
    private boolean mIsImmersive = false;
    private boolean mShowTabsInDrawer;
    private boolean mSwapBookmarksAndTabs;
    private int mOriginalOrientation;
    private int mBackgroundColor;
    private int mIconColor;
    private int mDisabledIconColor;
    private int mCurrentUiColor = Color.BLACK;
    private long mKeyDownStartTime;
    private String mSearchText;
    private String mUntitledTitle;
    private String mCameraPhotoPath;
    SimpleDateFormat dateFormat;
    int dialogCnt;
    String amt, billing_cycle;

    // The singleton BookmarkManager
    @Inject
    BookmarkModel mBookmarkManager;

    @Inject
    HistoryModel mHistoryModel;

    @Inject
    LightningDialogBuilder mBookmarksDialogBuilder;

    @Inject
    SearchBoxModel mSearchBoxModel;

    @Inject
    SearchEngineProvider mSearchEngineProvider;

    private TabsManager mTabsManager;

    // Image
    private Bitmap mWebpageBitmap;
    private final ColorDrawable mBackground = new ColorDrawable();
    private Drawable mDeleteIcon, mRefreshIcon, mClearIcon, mIcon;

    private BrowserPresenter mPresenter;
    private TabsView mTabsView;
    private BookmarksView mBookmarksView;
    private CountDownTimer downTimer;
    ApiInterface apiClient;

    // Proxy
    @Inject
    ProxyUtils mProxyUtils;

    private boolean checkAds = false;


    //vpn
    int premiumServerEnable = -1;

    // Constant
    private static final int LOCKTIME = 300000;
    private static final int API = Build.VERSION.SDK_INT;
    private static final String NETWORK_BROADCAST_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private final int timeToShow = 120;
    private static final LayoutParams MATCH_PARENT = new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT);
    private static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    private int subDuration;
    private String subscription_id;
    private int postionLast;
    private int positionNew;
    private int checkPosition;
    private AlertDialog dialog;
    private boolean userDialog;

    protected abstract boolean isIncognito();

    public abstract void closeActivity();

    public abstract void updateHistory(@Nullable final String title, @NonNull final String url);

    /***vpn****/
    //BillingClient mBillingClient;
    public static final String MyPrefsSubscription = "MyPrefsSubscription";
    public static final String orderId = "orderId";
    public static final String productId = "productId";
    public static final String purchaseToken = "purchaseToken";
    public static final String autoRenewing = "autoRenewing";
    public static String orderIduser, productIduser, purchaseTokenuser, autoRenewinguser, amount, expiryDate, billingCycle;

    SharedPreferences sharedpreferences;
    SharedPreferences pref;
    String userEmailID;
    String userType = "0";
    //  public String checkAd = "0";
    CountDownTimer timer;

    private IInAppBillingService mService;
    private String appPackageName;
    IabHelper mHelper;
    String base64EncodedPublicKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl/KmKF6W32wJp1lHfD8N7pp8AYJ+l2iUEUPgOAbNGkKfBkgA8ImwRcGYnZvzeyL8Iob87jIhMjtbDt13X2kaS1oo0P+RHcPsDXIt/TzqmXnvqKw2xWchzowQm6lIB/f1/A1i/fpPmzNZwO/J0zWv/HU5gUAMyBfoWnBj+0hhqgy6+hm/wlrDV0VU7GgVBXJxzzk35CcEzOVihwy0RxGsUk1bX/mzIP20tRgpqRH3LVFg2Go+KJarsJRISgeKB5WfdXG0s0astbyWG7/r4Pl3cATh04N2nwi18aHva7mHKzf7TSNDq7U++CyV/H7HTGQPjEbIDeJqc3yot6F+9z8RIQIDAQAB";
    int counryPopupOpen = 0;

    SubscriptionClass subscription;
    final ArrayList<ProxyModel> premiumList = new ArrayList<>();
    Intent serviceIntent;
    ServiceConnection mServiceConn;
    int premDialog = 0;
    boolean isWHMS = false;
    boolean isSubscribed = false;
    ArrayList<ProxyModel> freeList;
    String subscribeDate;
    SimpleDateFormat simpleDateFormat;
    public int selectedCountry;
    ArrayList<AdModel> adArrayList;

    AdModel randomStr;
    public static String XML_URL = "https://www.myproxyvpn.com/list/adreplist.xml";
    private String ad_Image, ad_Url, ad_BottomColor, ad_Title, ad_Description, ad_BottomText, ad_TextBg, ad_ButtonColor, ad_ButtonText;

    /************/
    @NonNull
    protected abstract Completable updateCookiePreference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BrowserApp.getAppComponent().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adArrayList = new ArrayList<>();

        selectedCountry = mPreferences.getMpvgProxyPort();

        btn_nonPremium.setOnClickListener(this);
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        /************************************************/

        if (getIntent() != null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                userType = extras.getString("userType");
                Log.d("rajniTAG", "browseractivity usertype " + userType);
            }
        }




        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        if (pref.contains(CHECK_DIALOG)) {
            userDialog = pref.getBoolean(CHECK_DIALOG, false);
            Log.e("userDialog", userDialog + "");
        }

        if (pref.contains(KEY_EMAIL)) {
            userEmailID = pref.getString(KEY_EMAIL, "");
        }

        if (pref.contains(IS_WHMS)) {
            isWHMS = pref.getBoolean(IS_WHMS, false);
        }

        if (pref.contains(IS_SUBSCRIBED)) {
            isSubscribed = pref.getBoolean(IS_SUBSCRIBED, false);
        }

        Log.e("isWhms", "" + isWHMS);
        Log.e("isSubs", "" + isSubscribed);

        sharedpreferences = getSharedPreferences(MyPrefsSubscription, MODE_PRIVATE);
        if (sharedpreferences.contains(orderId)) {
            orderIduser = sharedpreferences.getString(orderId, "");
            productIduser = sharedpreferences.getString(productId, "");
            purchaseTokenuser = sharedpreferences.getString(purchaseToken, "");
            autoRenewinguser = sharedpreferences.getString(autoRenewing, "");
            Log.d("rajniTAG", "sahredpref:" + orderIduser);
            userType = "1";
        }

        if (sharedpreferences.contains(SplashScreen.expiryDate)) {
            expiryDate = sharedpreferences.getString(SplashScreen.expiryDate, "");
            /*billingCycle = sharedpreferences.getString(SplashScreen.billingCycle, "");
            amount = sharedpreferences.getString(SplashScreen.amount, "");*/
        }

        subscription = (SubscriptionClass) getIntent().getSerializableExtra("Data");

        apiClient = ApiClient.getClient().create(ApiInterface.class);

        freeList = new ArrayList<>();

        /*else{
            userType=0;
        }*/
        //Purchase purchase = mInventory.getPurchase('abc');

        appPackageName = this.getPackageName();

        //API Calling to check subscription
        Timer timer = new Timer();
        //Looper.prepare();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("userEmailID", "" + userEmailID);

                        if (expiryDate != null && !expiryDate.isEmpty()) {
                            getGooglePlayDetails();
                        } else {
                            checkSubscriptionDetail();
                        }
                    }
                });
            }
        };

        //timer.schedule(task, 1, LOCKTIME);
        //LOCKTIME 60000 300000

//        timer.scheduleAtFixedRate(task, 0, 180000);
        timer.scheduleAtFixedRate(task, 0, 180000);

        //set the properties for button

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


        /***************************************/
        mTabsManager = new TabsManager();
        mPresenter = new BrowserPresenter(this, isIncognito());
        MobileAds.initialize(this, APP_ID);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(INTERSTITIAL_ID);

        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        DEVICE_ID = md5(android_id).toUpperCase();

        Log.d("DEVICE_ID", DEVICE_ID);

        // Use an activity context to get the rewarded video instance.
        mAd = MobileAds.getRewardedVideoAdInstance(this);
        initialize(savedInstanceState);
        LoadAdDetail();
        displayInterestitialAd();

        mAd.loadAd(REWARDED_ID, new AdRequest.Builder().build());


    }

    public void LoadAdDetail() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {

            URL url = new URL(XML_URL);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize(); // getting DOM element

            Log.e("checkAd", "checkingAdd");

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("ad");

            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;

                    ad_Image = getValue("topimage", element2);
                    ad_Url = getValue("adurl", element2);
                    ad_BottomColor = getValue("bottomcolor", element2);
                    ad_Title = getValue("title", element2);
                    ad_Description = getValue("description", element2);
                    ad_BottomText = getValue("bottomtext", element2);
                    ad_TextBg = getValue("textbg", element2);
                    ad_ButtonColor = getValue("buttoncolor", element2);
                    ad_ButtonText = getValue("buttontext", element2);

                    AdModel adModel = new AdModel();
                    adModel.setAdvImage(ad_Image);
                    adModel.setAdvUrl(ad_Url);
                    adModel.setAdvBottomColor(ad_BottomColor);
                    adModel.setAdvTitle(ad_Title);
                    adModel.setAdvDescription(ad_Description);
                    adModel.setAdvBottomText(ad_BottomText);
                    adModel.setAdvTestBg(ad_TextBg);
                    adModel.setAdvButtonColor(ad_ButtonColor);
                    adModel.setAdvButtonText(ad_ButtonText);
                    adArrayList.add(adModel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    private Drawable getDrawableWithRadius() {

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadii(new float[]{10, 10, 10, 10, 10, 10, 10, 10});
        gradientDrawable.setColor(Color.parseColor("#" + randomStr.getAdvButtonColor()));
        return gradientDrawable;
    }

    public void NoadDialog() {


        randomStr = adArrayList.get(new Random().nextInt(adArrayList.size()));

        final Dialog dialog = new Dialog(BrowserActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.ad_layout);

        ImageView closeDialog = dialog.findViewById(R.id.closeDialog);
        RelativeLayout clickLayout = dialog.findViewById(R.id.clickLayout);
        ImageView adImage = dialog.findViewById(R.id.adImage);
        TextView titleAd = dialog.findViewById(R.id.titleAd);
        TextView descAd = dialog.findViewById(R.id.descAd);
        Button buttonTextAd = dialog.findViewById(R.id.buttonTextAd);
        TextView bottomTextAd = dialog.findViewById(R.id.bottomTextAd);
        LinearLayout bottomColorAd = dialog.findViewById(R.id.bottomColorAd);
        LinearLayout bgAd = dialog.findViewById(R.id.bgAd);

        bottomColorAd.setBackgroundColor(Color.parseColor("#" + randomStr.getAdvBottomColor()));
        titleAd.setText(randomStr.getAdvTitle());
        descAd.setText(randomStr.getAdvDescription());
        bgAd.setBackgroundColor(Color.parseColor("#" + randomStr.getAdvTestBg()));
        bottomTextAd.setText(randomStr.getAdvBottomText());
        buttonTextAd.setText(randomStr.getAdvButtonText());

        bgAd.setBackground(getDrawableWithRadius());
        buttonTextAd.setBackground(getDrawableWithRadius());

        Log.e("cmliUrl", randomStr.getAdvImage());

        // Picasso.get().load(ad_Image).into(adImage);
        Glide.with(BrowserActivity.this).load(randomStr.getAdvImage()).into(adImage);
        checkAds = true;

        closeDialog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                checkAds = false;
            }
        });

        clickLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(randomStr.getAdvUrl()));
                startActivity(browserIntent);
                checkAds = false;


            }
        });


        if (checkAds) {

            dialog.show();
        }


    }

    private void displayInterestitialAd() {
        Log.e("userType", userType);
        if (userType.equalsIgnoreCase("0")) {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());

            /*************************/
            /*if(mInterstitialAd.isLoaded())
            {
                mInterstitialAd.show();
            }*/
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    //Toast.makeText(getApplicationContext(), "Closed", Toast.LENGTH_SHORT).show();
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    if (userType.equalsIgnoreCase("0")) {
                        //Toast.makeText(getApplicationContext(), "No Ads", Toast.LENGTH_SHORT).show();
                        //checkAd="0";
                        if (!checkAds) {

                            NoadDialog();
                        }

                    }
                    super.onAdFailedToLoad(i);
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                }

                @Override
                public void onAdOpened() {
                    //Toast.makeText(getApplicationContext(), "onAdOpened", Toast.LENGTH_SHORT).show();
                    super.onAdOpened();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    if (userType.equalsIgnoreCase("0")) {
                        mInterstitialAd.show();
                        //checkAd = "1";
                    }
                }
            });
        }
    }

    private void getGooglePlayDetails() {
        Calendar calendar = Calendar.getInstance();
        final Date date = calendar.getTime();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm a");

        serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);

                try {
                    Bundle ownedItems = mService.getPurchases(3, getPackageName(), "subs", null);

                    ArrayList ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                    ArrayList purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                    ArrayList signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                    String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");
                    // Log.d("rajniTAG","ownedSkus : "+ownedSkus);
                    Log.d("rajniTAG", "purchaseDataList : " + purchaseDataList);
                    Toast.makeText(BrowserActivity.this, "Payment Api Call ", Toast.LENGTH_SHORT).show();
                    if (purchaseDataList != null) {

                        if (purchaseDataList.size() > 0) {

                            for (int i = 0; i < purchaseDataList.size(); ++i) {
                                try {
                                    JSONObject object = new JSONObject(purchaseDataList.get(i).toString());
                                    String productIdResp = object.getString("productId");
                                    String orderIdResp = object.getString("orderId");
                                    //String purchaseTokenResp = object.getString("mypurchasetoken");
                                    String purchaseTokenResp = object.getString("purchaseToken");
                                    String autoRenewingResp = object.getString("autoRenewing");
                                    String purchaseTime = object.getString("purchaseTime");
                                    String purchaseState = object.getString("purchaseState");

                                    Log.d("rajniTAG", "browseractivity productIdResp : " + productIdResp);
                                    Log.d("rajniTAG", "browseractivity orderIdResp : " + orderIdResp);
                                    Log.d("rajniTAG", "browseractivity purchaseTokenResp : " + purchaseTokenResp);

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

                                    orderIduser = orderIdResp;
                                    productIduser = productIdResp;
                                    purchaseTokenuser = purchaseTokenResp;
                                    autoRenewinguser = autoRenewingResp;
                                    expiryDate = date;

                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString(orderId, orderIdResp);
                                    editor.putString(productId, productIdResp);
                                    editor.putString(purchaseToken, purchaseTokenResp);
                                    editor.putString(autoRenewing, autoRenewingResp);
                                    editor.putString(SplashScreen.expiryDate, date);
                                    editor.putString(SplashScreen.billingCycle, billing_cycle);
                                    editor.putString(SplashScreen.amount, amt);
                                    editor.commit();

                                    userType = "1";

                                    SharedPreferences.Editor editor_pre = pref.edit();
                                    editor_pre.putBoolean(CHECK_DIALOG, true);
                                    userDialog = true;
                                    editor_pre.commit();

                                /*orderIduser = sharedpreferences.getString(orderId, "");
                                productIduser = sharedpreferences.getString(productId, "");
                                purchaseTokenuser = sharedpreferences.getString(purchaseToken, "");
                                autoRenewinguser = sharedpreferences.getString(autoRenewing, "");*/

                                    Log.d("rajniTAG", "browseractivity userType : " + userType);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            userType = "0";
                            displayInterestitialAd();



                            if (freeList != null && freeList.size() > 0) {

                               /* setFreeProxy(freeList.get(1), selectedView);
                                freeListView.setItemChecked(1, true);*/

                                if (selectedView != null)
                                    selectedView.setBackgroundResource(R.color.transparent);
                                selectedView = getViewByPosition(1, freeListView);
                                selectedView.setBackgroundResource(R.color.secondary_color_settings);
                                setFreeProxy(freeList.get(1), selectedView);

                                premiumAdapter.setSelectedItem(-1);

                                Log.e("conutryName", freeList.get(1).getCountry().toString());

                                /*if (freeListView != null) {
                                    Log.e("selected", freeListView.getSelectedItem().toString());
                                    Log.e("selected", freeListView.getSelectedView().toString());
                                }
                                selectedView = freeListView.getSelectedView();*/
                                //selectedView.setBackgroundResource(R.color.transparent);
                            }

                            btn_nonPremium.setBackgroundColor(getResources().getColor(R.color.red_color));
                            txtPremium.setText(getResources().getString(R.string.upgrade_premium));
                            txtSubscribe.setText(getResources().getString(R.string.try_7days));
                            imageShield.setImageResource(R.drawable.ic_redshield);

                            Log.e("checkFree", "8");
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.remove(SplashScreen.billingCycle);
                            editor.remove(SplashScreen.expiryDate);
                            editor.remove(SplashScreen.amount);
                            editor.remove(orderId);
                            editor.commit();

                            if (premDialog == 0|| userDialog) {
                                premDialog++;
                                AlertDialog alertDialog = new AlertDialog.Builder(BrowserActivity.this)
                                        .setMessage("Your Premium membership has expired. Please purchase again to continue using the premium benefits.")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();

                                                Log.e("checkFree", "1");
                                            }
                                        })
                                        .show();

                                showDialog();
                            }

                            //   setFreeProxy(freeList.get(1), selectedView);
                        }
                    } else {
                        userType = "0";
                        displayInterestitialAd();

                        if (freeList != null && freeList.size() > 0) {
                            // setFlag(freeList.get(1).getCountry());
                            // setFreeProxy(freeList.get(1), selectedView);
                            //freeListView.setItemChecked(1, true);

                            if (selectedView != null)
                                selectedView.setBackgroundResource(R.color.transparent);
                            selectedView = getViewByPosition(1, freeListView);
                            selectedView.setBackgroundResource(R.color.secondary_color_settings);
                            setFreeProxy(freeList.get(1), selectedView);

                            premiumAdapter.setSelectedItem(-1);

                            //freeListView.setSelection(1);
                            //selectedView.setBackgroundResource(R.color.transparent);
                        }

                        btn_nonPremium.setBackgroundColor(getResources().getColor(R.color.red_color));
                        txtPremium.setText(getResources().getString(R.string.upgrade_premium));
                        txtSubscribe.setText(getResources().getString(R.string.try_7days));
                        imageShield.setImageResource(R.drawable.ic_redshield);
                        Log.e("checkFree", "5");

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.remove(SplashScreen.billingCycle);
                        editor.remove(SplashScreen.expiryDate);
                        editor.remove(SplashScreen.amount);
                        editor.remove(orderId);
                        editor.commit();

                        if (premDialog == 0|| userDialog) {
                            premDialog++;
                            AlertDialog alertDialog = new AlertDialog.Builder(BrowserActivity.this)
                                    .setMessage(getString(R.string.msgExpirePremium))
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            Log.e("checkFree", "2");

                                            if (selectedView != null)
                                                selectedView.setBackgroundResource(R.color.transparent);
                                            selectedView = getViewByPosition(1, freeListView);
                                            selectedView.setBackgroundResource(R.color.secondary_color_settings);
                                            setFreeProxy(freeList.get(1), selectedView);
                                        }
                                    })
                                    .show();

                            showDialog();
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };

        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
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

                ArrayList ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");
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

                            Log.d("rajniTAG","browseractivity productIdResp : "+productIdResp);
                            Log.d("rajniTAG","browseractivity orderIdResp : "+orderIdResp);
                            Log.d("rajniTAG","browseractivity purchaseTokenResp : "+purchaseTokenResp);
                            SharedPreferences.Editor editor = sharedpreferences.edit();

                            editor.putString(orderId, orderIdResp);
                            editor.putString(productId, productIdResp);
                            editor.putString(purchaseToken, purchaseTokenResp);
                            editor.putString(autoRenewing, autoRenewingResp);
                            editor.commit();

                            orderIduser = sharedpreferences.getString(orderId, "");
                            productIduser = sharedpreferences.getString(productId, "");
                            purchaseTokenuser = sharedpreferences.getString(purchaseToken, "");
                            autoRenewinguser = sharedpreferences.getString(autoRenewing, "");
                            userType="1";
                            Log.d("rajniTAG","browseractivity userType : "+userType);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        *//*String purchaseData = (String) purchaseDataList.get(i);
                        String signature = (String) signatureList.get(i);
                        String sku = (String) ownedSkus.get(i);
                        Log.d("rajniTAG","PURACHSED ITEM " + i + " === " + sku);*//*

                        // do something with this purchase information
                        // e.g. display the updated list of products owned by user
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

    /*@Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        Log.d("rajniTAG","onPurchasesUpdated : "+responseCode+" onPurchasesUpdated : "+purchases);

        if (responseCode == BillingClient.BillingResponse.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {


                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString(orderId, purchase.getOrderId());
                editor.putString(productId, purchase.getSku());
                editor.putString(purchaseToken, purchase.getPurchaseToken());
                editor.putString(autoRenewing, String.valueOf(purchase.isAutoRenewing()));
                editor.commit();
                startActivity(new Intent(BrowserActivity.this, MainActivity.class));
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


    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, "" + e);
        }
        return "";
    }

    private synchronized void initialize(Bundle savedInstanceState) {
        initializeToolbarHeight(getResources().getConfiguration());
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        //TODO make sure dark theme flag gets set correctly
        mDarkTheme = mPreferences.getUseTheme() != 0 || isIncognito();
        mIconColor = mDarkTheme ? ThemeUtils.getIconDarkThemeColor(this) : ThemeUtils.getIconLightThemeColor(this);
        mDisabledIconColor = mDarkTheme ? ContextCompat.getColor(this, R.color.icon_dark_theme_disabled) :
                ContextCompat.getColor(this, R.color.icon_light_theme_disabled);
        mShowTabsInDrawer = mPreferences.getShowTabsInDrawer(!isTablet());
        mSwapBookmarksAndTabs = mPreferences.getBookmarksAndTabsSwapped();

        // initialize background ColorDrawable
        int primaryColor = ThemeUtils.getPrimaryColor(this);
        mBackground.setColor(primaryColor);

        // Drawer stutters otherwise
        mDrawerLeft.setLayerType(View.LAYER_TYPE_NONE, null);
        mDrawerRight.setLayerType(View.LAYER_TYPE_NONE, null);

        mDrawerLayout.addDrawerListener(new DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_DRAGGING) {
                    mDrawerLeft.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    mDrawerRight.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                } else if (newState == DrawerLayout.STATE_IDLE) {
                    mDrawerLeft.setLayerType(View.LAYER_TYPE_NONE, null);
                    mDrawerRight.setLayerType(View.LAYER_TYPE_NONE, null);
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !mShowTabsInDrawer) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        setNavigationDrawerWidth();
        mDrawerLayout.addDrawerListener(new DrawerLocker());

        mWebpageBitmap = ThemeUtils.getThemedBitmap(this, R.drawable.ic_webpage, mDarkTheme);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        TabsFragment tabsFragment = (TabsFragment) fragmentManager.findFragmentByTag(TAG_TABS_FRAGMENT);
        BookmarksFragment bookmarksFragment = (BookmarksFragment) fragmentManager.findFragmentByTag(TAG_BOOKMARK_FRAGMENT);

        if (tabsFragment != null) {
            fragmentManager.beginTransaction().remove(tabsFragment).commit();
        }
        tabsFragment = TabsFragment.createTabsFragment(isIncognito(), mShowTabsInDrawer);

        mTabsView = tabsFragment;

        if (bookmarksFragment != null) {
            fragmentManager.beginTransaction().remove(bookmarksFragment).commit();
        }
        bookmarksFragment = BookmarksFragment.createFragment(isIncognito());

        mBookmarksView = bookmarksFragment;

        fragmentManager.executePendingTransactions();

        fragmentManager
                .beginTransaction()
                .replace(getTabsFragmentViewId(), tabsFragment, TAG_TABS_FRAGMENT)
                .replace(getBookmarksFragmentViewId(), bookmarksFragment, TAG_BOOKMARK_FRAGMENT)
                .commit();
        if (mShowTabsInDrawer) {
            mToolbarLayout.removeView(findViewById(R.id.tabs_toolbar_container));
        }

        Preconditions.checkNonNull(actionBar);

        // set display options of the ActionBar
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.toolbar_content);

        View customView = actionBar.getCustomView();
        LayoutParams lp = customView.getLayoutParams();
        lp.width = LayoutParams.MATCH_PARENT;
        lp.height = LayoutParams.MATCH_PARENT;
        customView.setLayoutParams(lp);

        mArrowImage = customView.findViewById(R.id.arrow);
        FrameLayout arrowButton = customView.findViewById(R.id.arrow_button);
        if (mShowTabsInDrawer) {
            if (mArrowImage.getWidth() <= 0) {
                mArrowImage.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            }
            updateTabNumber(0);

            // Post drawer locking in case the activity is being recreated
            Handlers.MAIN.post(new Runnable() {
                @Override
                public void run() {
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, getTabDrawer());
                }
            });
        } else {

            // Post drawer locking in case the activity is being recreated
            Handlers.MAIN.post(new Runnable() {
                @Override
                public void run() {
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, getTabDrawer());
                }
            });
            mArrowImage.setImageResource(R.drawable.ic_action_home);
            mArrowImage.setColorFilter(mIconColor, PorterDuff.Mode.SRC_IN);
        }


        mCountryImage = customView.findViewById(R.id.country);
        setFlag(mPreferences.getCountry());
        mProxyUtils.initializeProxy(this);
        mCountryBackground = customView.findViewById(R.id.country_button);
        mCountryBackground.getBackground().setColorFilter(getSearchBarColor(primaryColor, primaryColor), PorterDuff.Mode.SRC_IN);
        mCountryBackground.setOnClickListener(this);
        // Post drawer locking in case the activity is being recreated
        Handlers.MAIN.post(new Runnable() {
            @Override
            public void run() {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, getBookmarkDrawer());
            }
        });

        arrowButton.setOnClickListener(this);

        // create the search EditText in the ToolBar
        mSearch = customView.findViewById(R.id.search);
        mSearchBackground = customView.findViewById(R.id.search_container);

        // initialize search background color
        mSearchBackground.getBackground().setColorFilter(getSearchBarColor(primaryColor, primaryColor), PorterDuff.Mode.SRC_IN);

        mSearch.setHintTextColor(ThemeUtils.getThemedTextHintColor(mDarkTheme));
        mSearch.setTextColor(mDarkTheme ? Color.WHITE : Color.BLACK);

        mUntitledTitle = getString(R.string.untitled);
        mBackgroundColor = ThemeUtils.getPrimaryColor(this);
        mDeleteIcon = ThemeUtils.getThemedDrawable(this, R.drawable.ic_action_delete, mDarkTheme);
        mRefreshIcon = ThemeUtils.getThemedDrawable(this, R.drawable.ic_action_refresh, mDarkTheme);
        mClearIcon = ThemeUtils.getThemedDrawable(this, R.drawable.ic_action_delete, mDarkTheme);

        int iconBounds = Utils.dpToPx(24);
        mDeleteIcon.setBounds(0, 0, iconBounds, iconBounds);
        mRefreshIcon.setBounds(0, 0, iconBounds, iconBounds);
        mClearIcon.setBounds(0, 0, iconBounds, iconBounds);
        mIcon = mRefreshIcon;
        SearchListenerClass search = new SearchListenerClass();
//        mSearch.setCompoundDrawablePadding(Utils.dpToPx(3));
        mSearch.setCompoundDrawables(null, null, mRefreshIcon, null);
        mSearch.setOnKeyListener(search);
        mSearch.setOnFocusChangeListener(search);
        mSearch.setOnEditorActionListener(search);
        mSearch.setOnTouchListener(search);
        mSearch.setOnPreFocusListener(search);

        initializeSearchSuggestions(mSearch);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_right_shadow, GravityCompat.END);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_left_shadow, GravityCompat.START);

        if (API <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //noinspection deprecation
            WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());
        }

        @SuppressWarnings("VariableNotUsedInsideIf")
        Intent intent = savedInstanceState == null ? getIntent() : null;

        boolean launchedFromHistory = intent != null && (intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0;

        if (isPanicTrigger(intent)) {
            setIntent(null);
            panicClean();
        } else {
            if (launchedFromHistory) {
                intent = null;
            }
            mPresenter.setupTabs(intent);
            setIntent(null);
            mProxyUtils.checkForProxy(this);
        }
    }

    public void setFlag(String countryName) {
        try {
            // get input stream
            InputStream ims = getAssets().open("flags/" + countryName + ".png");

            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            mCountryImage.setImageDrawable(d);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @IdRes
    private int getBookmarksFragmentViewId() {
        return mSwapBookmarksAndTabs ? R.id.left_drawer : R.id.right_drawer;
    }

    private int getTabsFragmentViewId() {
        if (mShowTabsInDrawer) {
            return mSwapBookmarksAndTabs ? R.id.right_drawer : R.id.left_drawer;
        } else {
            return R.id.tabs_toolbar_container;
        }
    }

    /**
     * Determines if an intent is originating
     * from a panic trigger.
     *
     * @param intent the intent to check.
     * @return true if the panic trigger sent
     * the intent, false otherwise.
     */
    protected static boolean isPanicTrigger(@Nullable Intent intent) {
        return intent != null && INTENT_PANIC_TRIGGER.equals(intent.getAction());
    }

    protected void panicClean() {
        Log.d(TAG, "Closing browser");
        mTabsManager.newTab(this, "", false);
        mTabsManager.switchToTab(0);
        mTabsManager.clearSavedState();
        HistoryPage.deleteHistoryPage(getApplication()).subscribe();
        closeBrowser();
        // System exit needed in the case of receiving
        // the panic intent since finish() isn't completely
        // closing the browser
        System.exit(1);
    }

    private class SearchListenerClass implements OnKeyListener, OnEditorActionListener,
            OnFocusChangeListener, OnTouchListener, SearchView.PreFocusListener {

        @Override
        public boolean onKey(View searchView, int keyCode, KeyEvent keyEvent) {

            switch (keyCode) {
                case KeyEvent.KEYCODE_ENTER:
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
                    searchTheWeb(mSearch.getText().toString());
                    final LightningView currentView = mTabsManager.getCurrentTab();
                    if (currentView != null) {
                        currentView.requestFocus();
                    }
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    return true;
                default:
                    break;
            }
            return false;
        }

        @Override
        public boolean onEditorAction(TextView arg0, int actionId, KeyEvent arg2) {
            // hide the keyboard and search the web when the enter key
            // button is pressed
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_NEXT
                    || actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_SEARCH
                    || (arg2.getAction() == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
                searchTheWeb(mSearch.getText().toString());
                final LightningView currentView = mTabsManager.getCurrentTab();
                if (currentView != null) {
                    currentView.requestFocus();
                }
                return true;
            }
            return false;
        }

        @Override
        public void onFocusChange(final View v, final boolean hasFocus) {
            final LightningView currentView = mTabsManager.getCurrentTab();
            if (!hasFocus && currentView != null) {
                setIsLoading(currentView.getProgress() < 100);
                updateUrl(currentView.getUrl(), false);
            } else if (hasFocus && currentView != null) {

                // Hack to make sure the text gets selected
                ((SearchView) v).selectAll();
                mIcon = mClearIcon;
                mSearch.setCompoundDrawables(null, null, mClearIcon, null);
            }

            if (!hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d("rajniTAg", "ontouch");
            if (mSearch.getCompoundDrawables()[2] != null) {
                boolean tappedX = event.getX() > (mSearch.getWidth()
                        - mSearch.getPaddingRight() - mIcon.getIntrinsicWidth());
                if (tappedX) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (mSearch.hasFocus()) {
                            mSearch.setText("");
                        } else {
                            refreshOrStop();
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onPreFocus() {
            final LightningView currentView = mTabsManager.getCurrentTab();
            if (currentView == null) {
                return;
            }
            String url = currentView.getUrl();
            if (!UrlUtils.isSpecialUrl(url)) {
                if (!mSearch.hasFocus()) {
                    mSearch.setText(url);
                }
            }
        }
    }

    private class DrawerLocker implements DrawerListener {

        @Override
        public void onDrawerClosed(View v) {
            View tabsDrawer = getTabDrawer();
            View bookmarksDrawer = getBookmarkDrawer();

            if (v == tabsDrawer) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, bookmarksDrawer);
            } else if (mShowTabsInDrawer) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, tabsDrawer);
            }
        }

        @Override
        public void onDrawerOpened(View v) {
            View tabsDrawer = getTabDrawer();
            View bookmarksDrawer = getBookmarkDrawer();

            if (v == tabsDrawer) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, bookmarksDrawer);
            } else {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, tabsDrawer);
            }
        }

        @Override
        public void onDrawerSlide(View v, float arg) {
        }

        @Override
        public void onDrawerStateChanged(int arg) {
        }

    }

    private void setNavigationDrawerWidth() {
        int width = getResources().getDisplayMetrics().widthPixels - Utils.dpToPx(56);
        int maxWidth;
        if (isTablet()) {
            maxWidth = Utils.dpToPx(320);
        } else {
            maxWidth = Utils.dpToPx(300);
        }
        if (width > maxWidth) {
            DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mDrawerLeft
                    .getLayoutParams();
            params.width = maxWidth;
            mDrawerLeft.setLayoutParams(params);
            mDrawerLeft.requestLayout();
            DrawerLayout.LayoutParams paramsRight = (DrawerLayout.LayoutParams) mDrawerRight
                    .getLayoutParams();
            paramsRight.width = maxWidth;
            mDrawerRight.setLayoutParams(paramsRight);
            mDrawerRight.requestLayout();
        } else {
            DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mDrawerLeft
                    .getLayoutParams();
            params.width = width;
            mDrawerLeft.setLayoutParams(params);
            mDrawerLeft.requestLayout();
            DrawerLayout.LayoutParams paramsRight = (DrawerLayout.LayoutParams) mDrawerRight
                    .getLayoutParams();
            paramsRight.width = width;
            mDrawerRight.setLayoutParams(paramsRight);
            mDrawerRight.requestLayout();
        }
    }

    private void initializePreferences() {
        final LightningView currentView = mTabsManager.getCurrentTab();
        mFullScreen = mPreferences.getFullScreenEnabled();
        boolean colorMode = mPreferences.getColorModeEnabled();
        colorMode &= !mDarkTheme;
        if (!isIncognito() && !colorMode && !mDarkTheme && mWebpageBitmap != null) {
            changeToolbarBackground(mWebpageBitmap, null);
        } else if (!isIncognito() && currentView != null && !mDarkTheme) {
            changeToolbarBackground(currentView.getFavicon(), null);
        } else if (!isIncognito() && !mDarkTheme && mWebpageBitmap != null) {
            changeToolbarBackground(mWebpageBitmap, null);
        }

        FragmentManager manager = getSupportFragmentManager();
        Fragment tabsFragment = manager.findFragmentByTag(TAG_TABS_FRAGMENT);
        if (tabsFragment instanceof TabsFragment) {
            ((TabsFragment) tabsFragment).reinitializePreferences();
        }
        Fragment bookmarksFragment = manager.findFragmentByTag(TAG_BOOKMARK_FRAGMENT);
        if (bookmarksFragment instanceof BookmarksFragment) {
            ((BookmarksFragment) bookmarksFragment).reinitializePreferences();
        }

        // TODO layout transition causing memory leak
//        mBrowserFrame.setLayoutTransition(new LayoutTransition());

        setFullscreen(mPreferences.getHideStatusBarEnabled(), false);

        BaseSearchEngine currentSearchEngine = mSearchEngineProvider.getCurrentSearchEngine();
        mSearchText = currentSearchEngine.getQueryUrl();

        updateCookiePreference().subscribeOn(Schedulers.worker()).subscribe();
        mProxyUtils.updateProxySettings(this);
    }

    @Override
    public void onWindowVisibleToUserAfterResume() {
        super.onWindowVisibleToUserAfterResume();
        mToolbarLayout.setTranslationY(0);
        setWebViewTranslation(mToolbarLayout.getHeight());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (mSearch.hasFocus()) {
                searchTheWeb(mSearch.getText().toString());
            }
        } else if ((keyCode == KeyEvent.KEYCODE_MENU)
                && (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN)
                && (Build.MANUFACTURER.compareTo("LGE") == 0)) {
            // Workaround for stupid LG devices that crash
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            mKeyDownStartTime = System.currentTimeMillis();
            Handlers.MAIN.postDelayed(mLongPressBackRunnable, ViewConfiguration.getLongPressTimeout());
        }
        return super.onKeyDown(keyCode, event);
    }

    private final Runnable mLongPressBackRunnable = new Runnable() {
        @Override
        public void run() {
            final LightningView currentTab = mTabsManager.getCurrentTab();
            showCloseDialog(mTabsManager.positionOf(currentTab));
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_MENU)
                && (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN)
                && (Build.MANUFACTURER.compareTo("LGE") == 0)) {
            // Workaround for stupid LG devices that crash
            openOptionsMenu();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            Handlers.MAIN.removeCallbacks(mLongPressBackRunnable);
            if ((System.currentTimeMillis() - mKeyDownStartTime) > ViewConfiguration.getLongPressTimeout()) {
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Keyboard shortcuts
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.isCtrlPressed()) {
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_F:
                        // Search in page
                        findInPage();
                        return true;
                    case KeyEvent.KEYCODE_T:
                        // Open new tab
                        newTab(null, true);
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                        return true;
                    case KeyEvent.KEYCODE_W:
                        // Close current tab
                        mPresenter.deleteTab(mTabsManager.indexOfCurrentTab());
                        return true;
                    case KeyEvent.KEYCODE_Q:
                        // Close browser
                        closeBrowser();
                        return true;
                    case KeyEvent.KEYCODE_R:
                        // Refresh current tab
                        LightningView currentTab = mTabsManager.getCurrentTab();
                        if (currentTab != null) {
                            currentTab.reload();
                        }
                        Log.d("rajniTAG", "refresh");
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                        return true;
                    case KeyEvent.KEYCODE_TAB:
                        int nextIndex;
                        if (event.isShiftPressed()) {
                            // Go back one tab
                            if (mTabsManager.indexOfCurrentTab() > 0) {
                                nextIndex = mTabsManager.indexOfCurrentTab() - 1;
                            } else {
                                nextIndex = mTabsManager.last();
                            }
                        } else {
                            // Go forward one tab
                            if (mTabsManager.indexOfCurrentTab() < mTabsManager.last()) {
                                nextIndex = mTabsManager.indexOfCurrentTab() + 1;
                            } else {
                                nextIndex = 0;
                            }
                        }
                        mPresenter.tabChanged(nextIndex);
                        return true;
                }
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_SEARCH) {
                // Highlight search field
                mSearch.requestFocus();
                mSearch.selectAll();
                return true;
            } else if (event.isAltPressed()) {
                // Alt + tab number
                if (KeyEvent.KEYCODE_0 <= event.getKeyCode() && event.getKeyCode() <= KeyEvent.KEYCODE_9) {
                    int nextIndex;
                    if (event.getKeyCode() > mTabsManager.last() + KeyEvent.KEYCODE_1 || event.getKeyCode() == KeyEvent.KEYCODE_0) {
                        nextIndex = mTabsManager.last();
                    } else {
                        nextIndex = event.getKeyCode() - KeyEvent.KEYCODE_1;
                    }
                    mPresenter.tabChanged(nextIndex);
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final LightningView currentView = mTabsManager.getCurrentTab();
        final String currentUrl = currentView != null ? currentView.getUrl() : null;
        // Handle action buttons
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(getBookmarkDrawer())) {
                    mDrawerLayout.closeDrawer(getBookmarkDrawer());
                }
                return true;
            case R.id.action_back:
                if (currentView != null && currentView.canGoBack()) {
                    currentView.goBack();
                }
                return true;
            case R.id.action_forward:
                if (currentView != null && currentView.canGoForward()) {
                    currentView.goForward();
                }
                return true;
            case R.id.action_add_to_homescreen:
                if (currentView != null) {
                    HistoryItem shortcut = new HistoryItem(currentView.getUrl(), currentView.getTitle());
                    shortcut.setBitmap(currentView.getFavicon());
                    Utils.createShortcut(this, shortcut);
                }
                return true;
            case R.id.action_new_tab:
                newTab(null, true);
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                return true;
            case R.id.action_incognito:
                startActivity(new Intent(this, IncognitoActivity.class));
                overridePendingTransition(R.anim.slide_up_in, R.anim.fade_out_scale);
                return true;
            case R.id.action_share:
                new IntentUtils(this).shareUrl(currentUrl, currentView != null ? currentView.getTitle() : null);
                return true;
            case R.id.action_bookmarks:
                openBookmarks();
                return true;
            case R.id.action_copy:
                if (currentUrl != null && !UrlUtils.isSpecialUrl(currentUrl)) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", currentUrl);
                    clipboard.setPrimaryClip(clip);
                    Utils.showSnackbar(this, R.string.message_link_copied);
                }
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_history:
                openHistory();
                return true;
            case R.id.action_downloads:
                openDownloads();
                return true;
            case R.id.action_add_bookmark:
                if (currentUrl != null && !UrlUtils.isSpecialUrl(currentUrl)) {
                    addBookmark(currentView.getTitle(), currentUrl);
                }
                return true;
            case R.id.action_find:
                findInPage();
                return true;
            case R.id.action_reading_mode:
                if (currentUrl != null) {
                    Intent read = new Intent(this, ReadingActivity.class);
                    read.putExtra(Constants.LOAD_READING_URL, currentUrl);
                    startActivity(read);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // By using a manager, adds a bookmark and notifies third parties about that
    private void addBookmark(final String title, final String url) {

        final HistoryItem item = new HistoryItem(url, title);
        mBookmarkManager.addBookmarkIfNotExists(item)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.main())
                .subscribe(new SingleOnSubscribe<Boolean>() {
                    @Override
                    public void onItem(@Nullable Boolean item) {
                        if (Boolean.TRUE.equals(item)) {
                            mSuggestionsAdapter.refreshBookmarks();
                            mBookmarksView.handleUpdatedUrl(url);
                        }
                    }
                });
    }

    private void deleteBookmark(final String title, final String url) {
        final HistoryItem item = new HistoryItem(url, title);

        mBookmarkManager.deleteBookmark(item)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.main())
                .subscribe(new SingleOnSubscribe<Boolean>() {
                    @Override
                    public void onItem(@Nullable Boolean item) {
                        if (Boolean.TRUE.equals(item)) {
                            mSuggestionsAdapter.refreshBookmarks();
                            mBookmarksView.handleUpdatedUrl(url);
                        }
                    }
                });
    }

    private void putToolbarInRoot() {
        if (mToolbarLayout.getParent() != mUiLayout) {
            if (mToolbarLayout.getParent() != null) {
                ((ViewGroup) mToolbarLayout.getParent()).removeView(mToolbarLayout);
            }

            mUiLayout.addView(mToolbarLayout, 0);
            mUiLayout.requestLayout();
        }
        setWebViewTranslation(0);
    }

    private void overlayToolbarOnWebView() {
        if (mToolbarLayout.getParent() != mBrowserFrame) {
            if (mToolbarLayout.getParent() != null) {
                ((ViewGroup) mToolbarLayout.getParent()).removeView(mToolbarLayout);
            }

            mBrowserFrame.addView(mToolbarLayout);
            mBrowserFrame.requestLayout();
        }
        setWebViewTranslation(mToolbarLayout.getHeight());
    }

    private void setWebViewTranslation(float translation) {
        if (mFullScreen && mCurrentView != null) {
            mCurrentView.setTranslationY(translation);
        } else if (mCurrentView != null) {
            mCurrentView.setTranslationY(0);
        }
    }

    /**
     * method that shows a dialog asking what string the user wishes to search
     * for. It highlights the text entered.
     */
    private void findInPage() {
        BrowserDialog.showEditText(this,
                R.string.action_find,
                R.string.search_hint,
                R.string.search_hint, new BrowserDialog.EditorListener() {
                    @Override
                    public void onClick(String text) {
                        if (!TextUtils.isEmpty(text)) {
                            mPresenter.findInPage(text);
                            showFindInPageControls(text);
                        }
                    }
                });
    }

    private void showFindInPageControls(@NonNull String text) {
        mSearchBar.setVisibility(View.VISIBLE);

        TextView tw = (TextView) findViewById(R.id.search_query);
        tw.setText('\'' + text + '\'');

        ImageButton up = (ImageButton) findViewById(R.id.button_next);
        up.setOnClickListener(this);

        ImageButton down = (ImageButton) findViewById(R.id.button_back);
        down.setOnClickListener(this);

        ImageButton quit = (ImageButton) findViewById(R.id.button_quit);
        quit.setOnClickListener(this);
    }

    @Override
    public TabsManager getTabModel() {
        return mTabsManager;
    }

    @Override
    public void showCloseDialog(final int position) {
        if (position < 0) {
            return;
        }
        BrowserDialog.show(this, R.string.dialog_title_close_browser,
                new BrowserDialog.Item(R.string.close_tab) {
                    @Override
                    public void onClick() {
                        mPresenter.deleteTab(position);
                    }
                },
                new BrowserDialog.Item(R.string.close_other_tabs) {
                    @Override
                    public void onClick() {
                        mPresenter.closeAllOtherTabs();
                    }
                },
                new BrowserDialog.Item(R.string.close_all_tabs) {
                    @Override
                    public void onClick() {
                        closeBrowser();
                    }
                });
    }

    @Override
    public void notifyTabViewRemoved(int position) {
        Log.d(TAG, "Notify Tab Removed: " + position);
        mTabsView.tabRemoved(position);
    }

    @Override
    public void notifyTabViewAdded() {
        Log.d(TAG, "Notify Tab Added");
        mTabsView.tabAdded();
    }

    @Override
    public void notifyTabViewChanged(int position) {
        Log.d(TAG, "Notify Tab Changed: " + position);

        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mTabsView.tabChanged(position);


    }

    @Override
    public void notifyTabViewInitialized() {
        Log.d(TAG, "Notify Tabs Initialized");
        mTabsView.tabsInitialized();
    }

    @Override
    public void tabChanged(LightningView tab) {
        mPresenter.tabChangeOccurred(tab);
    }

    @Override
    public void removeTabView() {

        Log.d(TAG, "Remove the tab view");

        // Set the background color so the color mode color doesn't show through
        mBrowserFrame.setBackgroundColor(mBackgroundColor);

        removeViewFromParent(mCurrentView);

        mCurrentView = null;

        // Use a delayed handler to make the transition smooth
        // otherwise it will get caught up with the showTab code
        // and cause a janky motion
        Handlers.MAIN.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerLayout.closeDrawers();
            }
        }, 200);

    }

    @Override
    public void setTabView(@NonNull final View view) {
        if (mCurrentView == view) {
            return;
        }

        Log.d(TAG, "Setting the tab view");


        // Set the background color so the color mode color doesn't show through
        mBrowserFrame.setBackgroundColor(mBackgroundColor);

        removeViewFromParent(view);
        removeViewFromParent(mCurrentView);


        if (userType.equals("1")) {

            btn_nonPremium.setBackgroundColor(getResources().getColor(R.color.green_color));
            txtPremium.setText(getResources().getString(R.string.vpn_enabled));
            txtSubscribe.setText(getResources().getString(R.string.view_subscription));
            imageShield.setImageResource(R.drawable.ic_greenshield);
            btn_nonPremium.setVisibility(View.VISIBLE);
        } else {
            btn_nonPremium.setVisibility(View.VISIBLE);
        }


        mBrowserFrame.addView(view, 0, MATCH_PARENT);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = BOTTOM;
        if (expiryDate != null || subscription != null) {
            // mBrowserFrame.addView(subscriptionDetailButton, params);
        }
        if (mFullScreen) {
            view.setTranslationY(mToolbarLayout.getHeight() + mToolbarLayout.getTranslationY());
        } else {
            view.setTranslationY(0);
        }

        view.requestFocus();

        mCurrentView = view;

        showActionBar();

        // Use a delayed handler to make the transition smooth
        // otherwise it will get caught up with the showTab code
        // and cause a janky motion
        Handlers.MAIN.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerLayout.closeDrawers();
            }
        }, 200);

        // Handlers.MAIN.postDelayed(new Runnable() {
        //     @Override
        //     public void run() {
        // Remove browser frame background to reduce overdraw
        //TODO evaluate performance
        //         mBrowserFrame.setBackgroundColor(Color.TRANSPARENT);
        //     }
        // }, 300);
    }

    @Override
    public void showBlockedLocalFileDialog(@NonNull DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Dialog dialog = builder.setCancelable(true)
                .setTitle(R.string.title_warning)
                .setMessage(R.string.message_blocked_local)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.action_open, listener)
                .show();

        BrowserDialog.setDialogSize(this, dialog);
    }

    @Override
    public void showSnackbar(@StringRes int resource) {
        Utils.showSnackbar(this, resource);
    }

    @Override
    public void tabCloseClicked(int position) {
        mPresenter.deleteTab(position);
    }

    @Override
    public void tabClicked(int position) {
        showTab(position);
    }

    @Override
    public void newTabButtonClicked() {
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mPresenter.newTab(null, true);
    }

    @Override
    public void newTabButtonLongClicked() {
        String url = mPreferences.getSavedUrl();
        if (url != null) {
            newTab(url, true);

            Utils.showSnackbar(this, R.string.deleted_tab);
        }
        mPreferences.setSavedUrl(null);
    }

    @Override
    public void bookmarkButtonClicked() {
        final LightningView currentTab = mTabsManager.getCurrentTab();
        final String url = currentTab != null ? currentTab.getUrl() : null;
        final String title = currentTab != null ? currentTab.getTitle() : null;
        if (url == null) {
            return;
        }

        if (!UrlUtils.isSpecialUrl(url)) {
            mBookmarkManager.isBookmark(url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.main())
                    .subscribe(new SingleOnSubscribe<Boolean>() {
                        @Override
                        public void onItem(@Nullable Boolean item) {
                            if (Boolean.TRUE.equals(item)) {
                                deleteBookmark(title, url);
                            } else {
                                addBookmark(title, url);
                            }
                        }
                    });
        }
    }

    @Override
    public void bookmarkItemClicked(@NonNull HistoryItem item) {
        mPresenter.loadUrlInCurrentView(item.getUrl());
        // keep any jank from happening when the drawer is closed after the
        // URL starts to load
        Handlers.MAIN.postDelayed(new Runnable() {
            @Override
            public void run() {
                closeDrawers(null);
            }
        }, 150);
    }

    @Override
    public void handleHistoryChange() {
        openHistory();
    }

    /**
     * displays the WebView contained in the LightningView Also handles the
     * removal of previous views
     *
     * @param position the poition of the tab to display
     */
    // TODO move to presenter
    private synchronized void showTab(final int position) {
        mPresenter.tabChanged(position);
    }

    private static void removeViewFromParent(@Nullable View view) {
        if (view == null) {
            return;
        }
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
    }

    protected void handleNewIntent(Intent intent) {
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                userType = extras.getString("userType");
                orderIduser = extras.getString("orderId");

                Log.d("rajniTAG", "handleNewIntent intent " + userType + " = " + orderIduser);
            }
        }
        mPresenter.onNewIntent(intent);
    }

    @Override
    public void closeEmptyTab() {
        // Currently do nothing
        // Possibly closing the current tab might close the browser
        // and mess stuff up

    }

    @Override
    public void onTrimMemory(int level) {
        if (level > TRIM_MEMORY_MODERATE && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Log.d(TAG, "Low Memory, Free Memory");
            mPresenter.onAppLowMemory();
        }
    }

    // TODO move to presenter
    private synchronized boolean newTab(String url, boolean show) {
        return mPresenter.newTab(url, show);
    }

    protected void performExitCleanUp() {
        final LightningView currentTab = mTabsManager.getCurrentTab();
        if (mPreferences.getClearCacheExit() && currentTab != null && !isIncognito()) {
            WebUtils.clearCache(currentTab.getWebView());
            Log.d(TAG, "Cache Cleared");
        }
        if (mPreferences.getClearHistoryExitEnabled() && !isIncognito()) {
            WebUtils.clearHistory(this, mHistoryModel);
            Log.d(TAG, "History Cleared");
        }
        if (mPreferences.getClearCookiesExitEnabled() && !isIncognito()) {
            WebUtils.clearCookies(this);
            Log.d(TAG, "Cookies Cleared");
        }
        if (mPreferences.getClearWebStorageExitEnabled() && !isIncognito()) {
            WebUtils.clearWebStorage();
            Log.d(TAG, "WebStorage Cleared");
        } else if (isIncognito()) {
            WebUtils.clearWebStorage();     // We want to make sure incognito mode is secure
        }
        mSuggestionsAdapter.clearCache();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d(TAG, "onConfigurationChanged");

        if (mFullScreen) {
            showActionBar();
            mToolbarLayout.setTranslationY(0);
            setWebViewTranslation(mToolbarLayout.getHeight());
        }

        supportInvalidateOptionsMenu();
        initializeToolbarHeight(newConfig);

    }

    private void initializeToolbarHeight(@NonNull final Configuration configuration) {
        // TODO externalize the dimensions
        doOnLayout(mUiLayout, new Runnable() {
            @Override
            public void run() {
                int toolbarSize;
                if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    // In portrait toolbar should be 56 dp tall
                    toolbarSize = Utils.dpToPx(56);
                } else {
                    // In landscape toolbar should be 48 dp tall
                    toolbarSize = Utils.dpToPx(52);
                }
                mToolbar.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, toolbarSize));
                mToolbar.setMinimumHeight(toolbarSize);
                doOnLayout(mToolbar, new Runnable() {
                    @Override
                    public void run() {
                        setWebViewTranslation(mToolbarLayout.getHeight());
                    }
                });
                mToolbar.requestLayout();

            }
        });
    }

    public void closeBrowser() {
        mBrowserFrame.setBackgroundColor(mBackgroundColor);
        removeViewFromParent(mCurrentView);
        performExitCleanUp();
        int size = mTabsManager.size();
        mTabsManager.shutdown();
        mCurrentView = null;
        for (int n = 0; n < size; n++) {
            mTabsView.tabRemoved(0);
        }
        finish();
    }

    @Override
    public synchronized void onBackPressed() {
        checkAds = false;
        final LightningView currentTab = mTabsManager.getCurrentTab();
        if (mDrawerLayout.isDrawerOpen(getTabDrawer())) {
            mDrawerLayout.closeDrawer(getTabDrawer());
        } else if (mDrawerLayout.isDrawerOpen(getBookmarkDrawer())) {
            mBookmarksView.navigateBack();
        } else {
            if (currentTab != null) {
                Log.d(TAG, "onBackPressed");
                if (mSearch.hasFocus()) {
                    currentTab.requestFocus();
                } else if (currentTab.canGoBack()) {
                    if (!currentTab.isShown()) {
                        onHideCustomView();
                    } else {
                        currentTab.goBack();
                    }
                } else {
                    if (mCustomView != null || mCustomViewCallback != null) {
                        onHideCustomView();
                    } else {
                        mPresenter.deleteTab(mTabsManager.positionOf(currentTab));
                    }
                }
            } else {
                Log.e(TAG, "This shouldn't happen ever");
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onPause() {
        mAd.pause();
        super.onPause();
        Log.d(TAG, "onPause");
        /*vpn
         * ISSUE in ads close and click*/
        //mTabsManager.pauseAll();
        /*try {

            getApplication().unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Receiver was not registered", e);
        }*/
        /***************/
        /*if (isIncognito() && isFinishing()) {
            overridePendingTransition(R.anim.fade_in_scale, R.anim.slide_down_out);
        }*/
    }

    protected void saveOpenTabs() {
        if (mPreferences.getRestoreLostTabsEnabled()) {
            mTabsManager.saveState();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        mProxyUtils.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        mAd.destroy();
        Handlers.MAIN.removeCallbacksAndMessages(null);

        mPresenter.shutdown();

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mProxyUtils.onStart(this);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTabsManager.shutdown();
    }

    private void checkSubscriptionDetail() {
        Calendar calendar = Calendar.getInstance();
        final Date date = calendar.getTime();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm a");

        /*AlertDialog dialog = new AlertDialog.Builder(BrowserActivity.this)
                .setMessage("Hi " + dateFormat.format(date))
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();*/

        Call<LoginData> data = apiClient.getSubscriptionDetail(userEmailID);
        data.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                LoginData loginData = response.body();
                Toast.makeText(BrowserActivity.this, "Payment api call", Toast.LENGTH_SHORT).show();
                if (loginData.getResponse_code() == 200) {
                    subscription = loginData.getData().getSubscribed_package_detail();
                    if (subscription != null) {
                        Log.e("subscription", "" + subscription.getAmount());
                        userType = "1";
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean(IS_SUBSCRIBED, true);
                        editor.commit();
                        isSubscribed = true;

                        btn_nonPremium.setBackgroundColor(getResources().getColor(R.color.green_color));
                        txtPremium.setText(getResources().getString(R.string.vpn_enabled));
                        txtSubscribe.setText(getResources().getString(R.string.view_subscription));
                        imageShield.setImageResource(R.drawable.ic_greenshield);

                        SharedPreferences.Editor editor_pre = pref.edit();
                        editor_pre.putBoolean(CHECK_DIALOG, true);
                        userDialog = true;
                        editor_pre.commit();

                    } else {
                        Log.e("subscription", "no data");

                        userType = "0";
                        displayInterestitialAd();


                        if (freeList != null && freeList.size() > 0) {
                            setFlag(freeList.get(1).getCountry());
                            setFreeProxy(freeList.get(1), selectedView);
                            freeListView.setItemChecked(1, true);
                            //freeListView.setSelection(1);
                            //selectedView = freeListView.getSelectedView();
                            //selectedView.setBackgroundResource(R.color.transparent);
                        }

                        if (userDialog) {
                            showDialog();
                        }

                        if (premDialog == 0 && isSubscribed) {
                            //subscription = null;
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean(IS_SUBSCRIBED, false);
                            editor.commit();
                            isSubscribed = false;

                            premDialog++;

                            AlertDialog alertDialog = new AlertDialog.Builder(BrowserActivity.this)
                                    .setMessage("Your Premium membership has expired. Please purchase again to continue using the " +
                                            "premium benefits.")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            Log.e("checkFree", "3");

                                            if (selectedView != null)
                                                selectedView.setBackgroundResource(R.color.transparent);
                                            selectedView = getViewByPosition(1, freeListView);
                                            selectedView.setBackgroundResource(R.color.secondary_color_settings);
                                            setFreeProxy(freeList.get(1), selectedView);
                                        }
                                    })
                                    .show();
                        }

                        btn_nonPremium.setBackgroundColor(getResources().getColor(R.color.red_color));
                        txtPremium.setText(getResources().getString(R.string.upgrade_premium));
                        txtSubscribe.setText(getResources().getString(R.string.try_7days));
                        imageShield.setImageResource(R.drawable.ic_redshield);
                        Log.e("checkFree", "6");

                       /* if (selectedView != null)
                            selectedView.setBackgroundResource(R.color.transparent);
                        selectedView = getViewByPosition(1, freeListView);
                        selectedView.setBackgroundResource(R.color.secondary_color_settings);
                        setFreeProxy(freeList.get(1), selectedView);*/
                    }
                } else {
                    Log.e("subscription", "no data");

                    displayInterestitialAd();

                    if (freeList != null && freeList.size() > 0) {
                        setFlag(freeList.get(1).getCountry());
                        setFreeProxy(freeList.get(1), selectedView);
                        freeListView.setItemChecked(1, true);
                        //freeListView.setSelection(1);
                        //selectedView = freeListView.getSelectedView();
                        //selectedView.setBackgroundResource(R.color.transparent);
                    }
                    //subscription = null;
                    userType = "0";

                    if (userDialog) {
                        showDialog();
                    }


                    if (premDialog == 0 && isSubscribed) {

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean(IS_SUBSCRIBED, false);
                        editor.commit();
                        isSubscribed = false;

                        premDialog++;

                        AlertDialog alertDialog = new AlertDialog.Builder(BrowserActivity.this)
                                .setMessage("Your Premium membership has expired. Please purchase again to continue using the " +
                                        "premium benefits.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        Log.e("checkFree", "4");

                                        if (selectedView != null)
                                            selectedView.setBackgroundResource(R.color.transparent);
                                        selectedView = getViewByPosition(1, freeListView);
                                        selectedView.setBackgroundResource(R.color.secondary_color_settings);
                                        setFreeProxy(freeList.get(1), selectedView);
                                    }
                                })
                                .show();
                    }

                    btn_nonPremium.setBackgroundColor(getResources().getColor(R.color.red_color));
                    txtPremium.setText(getResources().getString(R.string.upgrade_premium));
                    txtSubscribe.setText(getResources().getString(R.string.try_7days));
                    imageShield.setImageResource(R.drawable.ic_redshield);
                    Log.e("checkFree", "7");

                }
            }

            @Override
            public void onFailure(Call<LoginData> call, Throwable t) {
                if (userDialog) {
                    showDialog();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        mAd.resume();
        super.onResume();

        Log.d(TAG, "onResume");
        if (mSwapBookmarksAndTabs != mPreferences.getBookmarksAndTabsSwapped()) {
            restart();
        }

        if (mSuggestionsAdapter != null) {
            mSuggestionsAdapter.refreshPreferences();
            mSuggestionsAdapter.refreshBookmarks();
        }
        mTabsManager.resumeAll(this);
        initializePreferences();

        supportInvalidateOptionsMenu();

        IntentFilter filter = new IntentFilter();
        filter.addAction(NETWORK_BROADCAST_ACTION);
        getApplication().registerReceiver(mNetworkReceiver, filter);

        if (mFullScreen) {
            overlayToolbarOnWebView();
        } else {
            putToolbarInRoot();
        }
    }

    /**
     * searches the web for the query fixing any and all problems with the input
     * checks if it is a search, url, etc.
     */
    private void searchTheWeb(@NonNull String query) {
        final LightningView currentTab = mTabsManager.getCurrentTab();
        if (query.isEmpty()) {
            return;
        }
        String searchUrl = mSearchText + UrlUtils.QUERY_PLACE_HOLDER;
        query = query.trim();
        if (currentTab != null) {
            currentTab.stopLoading();
            mPresenter.loadUrlInCurrentView(UrlUtils.smartUrlFilter(query, true, searchUrl));
        }
    }

    /**
     * Animates the color of the toolbar from one color to another. Optionally animates
     * the color of the tab background, for use when the tabs are displayed on the top
     * of the screen.
     *
     * @param favicon       the Bitmap to extract the color from
     * @param tabBackground the optional LinearLayout to color
     */
    @Override
    public void changeToolbarBackground(@NonNull Bitmap favicon, @Nullable final Drawable tabBackground) {
        final int defaultColor = ContextCompat.getColor(this, R.color.primary_color);
        if (mCurrentUiColor == Color.BLACK) {
            mCurrentUiColor = defaultColor;
        }
        Palette.from(favicon).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {

                // OR with opaque black to remove transparency glitches
                int color = 0xff000000 | palette.getVibrantColor(defaultColor);

                final int finalColor; // Lighten up the dark color if it is
                // too dark
                if (!mShowTabsInDrawer || Utils.isColorTooDark(color)) {
                    finalColor = Utils.mixTwoColors(defaultColor, color, 0.25f);
                } else {
                    finalColor = color;
                }

                final Window window = getWindow();
                if (!mShowTabsInDrawer) {
                    window.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                }

                final int startSearchColor = getSearchBarColor(mCurrentUiColor, defaultColor);
                final int finalSearchColor = getSearchBarColor(finalColor, defaultColor);

                Animation animation = new Animation() {
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        final int color = DrawableUtils.mixColor(interpolatedTime, mCurrentUiColor, finalColor);
                        if (mShowTabsInDrawer) {
                            mBackground.setColor(color);
                            Handlers.MAIN.post(new Runnable() {
                                @Override
                                public void run() {
                                    window.setBackgroundDrawable(mBackground);
                                }
                            });
                        } else if (tabBackground != null) {
                            tabBackground.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                        }
                        mCurrentUiColor = color;
                        mToolbarLayout.setBackgroundColor(color);
                        mSearchBackground.getBackground().setColorFilter(DrawableUtils.mixColor(interpolatedTime,
                                startSearchColor, finalSearchColor), PorterDuff.Mode.SRC_IN);
                    }
                };
                animation.setDuration(300);
                mToolbarLayout.startAnimation(animation);
            }
        });
    }

    private int getSearchBarColor(int requestedColor, int defaultColor) {
        if (requestedColor == defaultColor) {
            return mDarkTheme ? DrawableUtils.mixColor(0.25f, defaultColor, Color.WHITE) : Color.WHITE;
        } else {
            return DrawableUtils.mixColor(0.25f, requestedColor, Color.WHITE);
        }
    }

    @Override
    public boolean getUseDarkTheme() {
        return mDarkTheme;
    }

    @ColorInt
    @Override
    public int getUiColor() {
        return mCurrentUiColor;
    }

    @Override
    public void updateUrl(@Nullable String url, boolean isLoading) {
        if (url == null || mSearch == null || mSearch.hasFocus()) {
            return;
        }
        final LightningView currentTab = mTabsManager.getCurrentTab();
        mBookmarksView.handleUpdatedUrl(url);

        String currentTitle = currentTab != null ? currentTab.getTitle() : null;

        mSearch.setText(mSearchBoxModel.getDisplayContent(url, currentTitle, isLoading));
    }

    @Override
    public void updateTabNumber(int number) {
        if (mArrowImage != null && mShowTabsInDrawer) {
            mArrowImage.setImageBitmap(DrawableUtils.getRoundedNumberImage(number, Utils.dpToPx(24),
                    Utils.dpToPx(24), ThemeUtils.getIconThemeColor(this, mDarkTheme), Utils.dpToPx(2.5f)));
        }
    }

    @Override
    public void updateProgress(int n) {
        setIsLoading(n < 100);
        mProgressBar.setProgress(n);
    }

    protected void addItemToHistory(@Nullable final String title, @NonNull final String url) {
        if (UrlUtils.isSpecialUrl(url)) {
            return;
        }

        mHistoryModel.visitHistoryItem(url, title)
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableOnSubscribe() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.e(TAG, "Exception while updating history", throwable);
                    }
                });
    }

    /**
     * method to generate search suggestions for the AutoCompleteTextView from
     * previously searched URLs
     */
    private void initializeSearchSuggestions(final AutoCompleteTextView getUrl) {

        mSuggestionsAdapter = new SuggestionsAdapter(this, mDarkTheme, isIncognito());

        getUrl.setThreshold(1);
        getUrl.setDropDownWidth(-1);
        getUrl.setDropDownAnchor(R.id.toolbar_layout);
        getUrl.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                String url = null;
                CharSequence urlString = ((TextView) view.findViewById(R.id.url)).getText();
                if (urlString != null) {
                    url = urlString.toString();
                }
                if (url == null || url.startsWith(getString(R.string.suggestion))) {
                    CharSequence searchString = ((TextView) view.findViewById(R.id.title)).getText();
                    if (searchString != null) {
                        url = searchString.toString();
                    }
                }
                if (url == null) {
                    return;
                }
                getUrl.setText(url);
                searchTheWeb(url);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getUrl.getWindowToken(), 0);
                mPresenter.onAutoCompleteItemPressed();
            }

        });

        getUrl.setSelectAllOnFocus(true);
        getUrl.setAdapter(mSuggestionsAdapter);
    }

    /**
     * function that opens the HTML history page in the browser
     */
    private void openHistory() {
        new HistoryPage().getHistoryPage()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.main())
                .subscribe(new SingleOnSubscribe<String>() {
                    @Override
                    public void onItem(@Nullable String item) {
                        Preconditions.checkNonNull(item);
                        LightningView view = mTabsManager.getCurrentTab();
                        if (view != null) {
                            view.loadUrl(item);
                        }
                    }
                });
    }

    private void openDownloads() {
        new DownloadsPage().getDownloadsPage()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.main())
                .subscribe(new SingleOnSubscribe<String>() {
                    @Override
                    public void onItem(@Nullable String item) {
                        Preconditions.checkNonNull(item);
                        LightningView view = mTabsManager.getCurrentTab();
                        if (view != null) {
                            view.loadUrl(item);
                        }
                    }
                });
    }

    private View getBookmarkDrawer() {
        return mSwapBookmarksAndTabs ? mDrawerLeft : mDrawerRight;
    }

    private View getTabDrawer() {
        return mSwapBookmarksAndTabs ? mDrawerRight : mDrawerLeft;
    }

    /**
     * helper function that opens the bookmark drawer
     */
    private void openBookmarks() {
        if (mDrawerLayout.isDrawerOpen(getTabDrawer())) {
            mDrawerLayout.closeDrawers();
        }
        mDrawerLayout.openDrawer(getBookmarkDrawer());
    }

    /**
     * This method closes any open drawer and executes
     * the runnable after the drawers are completely closed.
     *
     * @param runnable an optional runnable to run after
     *                 the drawers are closed.
     */
    protected final void closeDrawers(@Nullable final Runnable runnable) {
        if (!mDrawerLayout.isDrawerOpen(mDrawerLeft) && !mDrawerLayout.isDrawerOpen(mDrawerRight)) {
            if (runnable != null) {
                runnable.run();
                return;
            }
        }
        mDrawerLayout.closeDrawers();

        mDrawerLayout.addDrawerListener(new DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (runnable != null) {
                    runnable.run();
                }
                mDrawerLayout.removeDrawerListener(this);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    @Override
    public void setForwardButtonEnabled(boolean enabled) {
        if (mForwardMenuItem != null && mForwardMenuItem.getIcon() != null) {
            int colorFilter;
            if (enabled) {
                colorFilter = mIconColor;
            } else {
                colorFilter = mDisabledIconColor;
            }
            mForwardMenuItem.getIcon().setColorFilter(colorFilter, PorterDuff.Mode.SRC_IN);
            mForwardMenuItem.setIcon(mForwardMenuItem.getIcon());
        }
    }

    @Override
    public void setBackButtonEnabled(boolean enabled) {
        if (mBackMenuItem != null && mBackMenuItem.getIcon() != null) {
            int colorFilter;
            if (enabled) {
                colorFilter = mIconColor;
            } else {
                colorFilter = mDisabledIconColor;
            }
            mBackMenuItem.getIcon().setColorFilter(colorFilter, PorterDuff.Mode.SRC_IN);
            mBackMenuItem.setIcon(mBackMenuItem.getIcon());
        }
    }

    private MenuItem mBackMenuItem;
    private MenuItem mForwardMenuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mBackMenuItem = menu.findItem(R.id.action_back);
        mForwardMenuItem = menu.findItem(R.id.action_forward);
        if (mBackMenuItem != null && mBackMenuItem.getIcon() != null)
            mBackMenuItem.getIcon().setColorFilter(mIconColor, PorterDuff.Mode.SRC_IN);
        if (mForwardMenuItem != null && mForwardMenuItem.getIcon() != null)
            mForwardMenuItem.getIcon().setColorFilter(mIconColor, PorterDuff.Mode.SRC_IN);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * opens a file chooser
     * param ValueCallback is the message from the WebView indicating a file chooser
     * should be opened
     */
    @Override
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        startActivityForResult(Intent.createChooser(i, getString(R.string.title_file_chooser)), 1);
    }

    /**
     * used to allow uploading into the browser
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (API < Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == 1) {
                if (null == mUploadMessage) {
                    return;
                }
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;

            }
        }

        if (!mHelper.handleActivityResult(requestCode,
                resultCode, intent)) {
            super.onActivityResult(requestCode, resultCode, intent);
        }

        if (requestCode != 1 || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, intent);
            return;
        }

        Uri[] results = null;

        // Check that the response is a good one
        if (resultCode == Activity.RESULT_OK) {
            if (intent == null) {
                // If there is not data, then we may have taken a photo
                if (mCameraPhotoPath != null) {
                    results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                }
            } else {
                String dataString = intent.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }

        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
    }

    @Override
    public void showFileChooser(ValueCallback<Uri[]> filePathCallback) {
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
        }
        mFilePathCallback = filePathCallback;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = Utils.createImageFile();
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "Unable to create Image File", ex);
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }

        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("*/*");

        Intent[] intentArray;
        if (takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent};
        } else {
            intentArray = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        startActivityForResult(chooserIntent, 1);
    }

    @Override
    public synchronized void onShowCustomView(View view, CustomViewCallback callback) {
        int requestedOrientation = mOriginalOrientation = getRequestedOrientation();
        onShowCustomView(view, callback, requestedOrientation);
    }

    @Override
    public synchronized void onShowCustomView(final View view, CustomViewCallback callback, int requestedOrientation) {
        final LightningView currentTab = mTabsManager.getCurrentTab();
        if (view == null || mCustomView != null) {
            if (callback != null) {
                try {
                    callback.onCustomViewHidden();
                } catch (Exception e) {
                    Log.e(TAG, "Error hiding custom view", e);
                }
            }
            return;
        }
        try {
            view.setKeepScreenOn(true);
        } catch (SecurityException e) {
            Log.e(TAG, "WebView is not allowed to keep the screen on");
        }
        mOriginalOrientation = getRequestedOrientation();
        mCustomViewCallback = callback;
        mCustomView = view;

        setRequestedOrientation(requestedOrientation);
        final FrameLayout decorView = (FrameLayout) getWindow().getDecorView();

        mFullscreenContainer = new FrameLayout(this);
        mFullscreenContainer.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black));
        if (view instanceof FrameLayout) {
            if (((FrameLayout) view).getFocusedChild() instanceof VideoView) {
                mVideoView = (VideoView) ((FrameLayout) view).getFocusedChild();
                mVideoView.setOnErrorListener(new VideoCompletionListener());
                mVideoView.setOnCompletionListener(new VideoCompletionListener());
            }
        } else if (view instanceof VideoView) {
            mVideoView = (VideoView) view;
            mVideoView.setOnErrorListener(new VideoCompletionListener());
            mVideoView.setOnCompletionListener(new VideoCompletionListener());
        }
        decorView.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
        mFullscreenContainer.addView(mCustomView, COVER_SCREEN_PARAMS);
        decorView.requestLayout();
        setFullscreen(true, true);
        if (currentTab != null) {
            currentTab.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void closeBookmarksDrawer() {
        mDrawerLayout.closeDrawer(getBookmarkDrawer());
    }

    @Override
    public void onHideCustomView() {
        final LightningView currentTab = mTabsManager.getCurrentTab();
        if (mCustomView == null || mCustomViewCallback == null || currentTab == null) {
            if (mCustomViewCallback != null) {
                try {
                    mCustomViewCallback.onCustomViewHidden();
                } catch (Exception e) {
                    Log.e(TAG, "Error hiding custom view", e);
                }
                mCustomViewCallback = null;
            }
            return;
        }
        Log.d(TAG, "onHideCustomView");
        currentTab.setVisibility(View.VISIBLE);
        try {
            mCustomView.setKeepScreenOn(false);
        } catch (SecurityException e) {
            Log.e(TAG, "WebView is not allowed to keep the screen on");
        }
        setFullscreen(mPreferences.getHideStatusBarEnabled(), false);
        if (mFullscreenContainer != null) {
            ViewGroup parent = (ViewGroup) mFullscreenContainer.getParent();
            if (parent != null) {
                parent.removeView(mFullscreenContainer);
            }
            mFullscreenContainer.removeAllViews();
        }

        mFullscreenContainer = null;
        mCustomView = null;
        if (mVideoView != null) {
            Log.d(TAG, "VideoView is being stopped");
            mVideoView.stopPlayback();
            mVideoView.setOnErrorListener(null);
            mVideoView.setOnCompletionListener(null);
            mVideoView = null;
        }
        if (mCustomViewCallback != null) {
            try {
                mCustomViewCallback.onCustomViewHidden();
            } catch (Exception e) {
                Log.e(TAG, "Error hiding custom view", e);
            }
        }

        mCustomViewCallback = null;
        setRequestedOrientation(mOriginalOrientation);
    }

    private class VideoCompletionListener implements MediaPlayer.OnCompletionListener,
            MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            onHideCustomView();
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "onWindowFocusChanged");
        if (hasFocus) {
            setFullscreen(mIsFullScreen, mIsImmersive);
        }
    }

    @Override
    public void onBackButtonPressed() {
        final LightningView currentTab = mTabsManager.getCurrentTab();
        if (currentTab != null) {
            if (currentTab.canGoBack()) {
                currentTab.goBack();
                closeDrawers(null);
            } else {
                mPresenter.deleteTab(mTabsManager.positionOf(currentTab));
            }
        }
    }

    @Override
    public void onForwardButtonPressed() {
        final LightningView currentTab = mTabsManager.getCurrentTab();
        if (currentTab != null) {
            if (currentTab.canGoForward()) {
                currentTab.goForward();
                closeDrawers(null);
            }
        }
    }

    @Override
    public void onHomeButtonPressed() {
        final LightningView currentTab = mTabsManager.getCurrentTab();
        if (currentTab != null) {
            currentTab.loadHomepage();
            closeDrawers(null);
        }
    }

    /**
     * This method sets whether or not the activity will display
     * in full-screen mode (i.e. the ActionBar will be hidden) and
     * whether or not immersive mode should be set. This is used to
     * set both parameters correctly as during a full-screen video,
     * both need to be set, but other-wise we leave it up to user
     * preference.
     *
     * @param enabled   true to enable full-screen, false otherwise
     * @param immersive true to enable immersive mode, false otherwise
     */
    private void setFullscreen(boolean enabled, boolean immersive) {
        mIsFullScreen = enabled;
        mIsImmersive = immersive;
        Window window = getWindow();
        View decor = window.getDecorView();
        if (enabled) {
            if (immersive) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            } else {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    /**
     * This method handles the JavaScript callback to create a new tab.
     * Basically this handles the event that JavaScript needs to create
     * a popup.
     *
     * @param resultMsg the transport message used to send the URL to
     *                  the newly created WebView.
     */

    @Override
    public synchronized void onCreateWindow(Message resultMsg) {
        if (resultMsg == null) {
            return;
        }
        if (newTab("", true)) {

            LightningView newTab = mTabsManager.getTabAtPosition(mTabsManager.size() - 1);
            if (newTab != null) {
                final WebView webView = newTab.getWebView();
                if (webView != null) {
                    WebSettings ws = webView.getSettings();
                    ws.setJavaScriptEnabled(true);
                    ws.setDomStorageEnabled(true);
                    webView.addJavascriptInterface(new WebAppInterface(this), "Android");


                    /**************************************/
                    /*webView.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            Log.d("rajniTAG","-1");
                            return true;
                        }

                        @Override
                        public void onLoadResource(WebView view, String url) {
                            Log.d("rajniTAG","0");
                            if (url.contains("googleads.g.doubleclick.net")) {
                                Log.d("rajniTAG","1");
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                                view.stopLoading();
                            }
                            else {
                                    Log.d("rajniTAG","2");
                            }
                        }
                    });*/

                    /*****************************************/


                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(webView);
                    resultMsg.sendToTarget();
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }
            }
        }
    }

    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showAdFromJs() {

            Toast.makeText(mContext, "Loading Ad", Toast.LENGTH_SHORT).show();
            runOnUiThread(new Runnable() {
                public void run() {
                    if (mInterstitialAd.isLoaded()) {
                        if (userType.equalsIgnoreCase("0")) {
                            mInterstitialAd.show();
                            mInterstitialAd.loadAd(new AdRequest.Builder().build());
                        }

                    } else
                        Toast.makeText(getApplicationContext(), "Ad not loaded", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }

    /**
     * Closes the specified {@link LightningView}. This implements
     * the JavaScript callback that asks the tab to close itself and
     * is especially helpful when a page creates a redirect and does
     * not need the tab to stay open any longer.
     *
     * @param view the LightningView to close, delete it.
     */
    @Override
    public void onCloseWindow(LightningView view) {
        mPresenter.deleteTab(mTabsManager.positionOf(view));
    }

    /**
     * Hide the ActionBar using an animation if we are in full-screen
     * mode. This method also re-parents the ActionBar if its parent is
     * incorrect so that the animation can happen correctly.
     */
    @Override
    public void hideActionBar() {
        if (mFullScreen) {
            if (mToolbarLayout == null || mBrowserFrame == null)
                return;

            final int height = mToolbarLayout.getHeight();
            if (mToolbarLayout.getTranslationY() > -0.01f) {
                Animation show = new Animation() {
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        float trans = interpolatedTime * height;
                        mToolbarLayout.setTranslationY(-trans);
                        setWebViewTranslation(height - trans);
                    }
                };
                show.setDuration(250);
                show.setInterpolator(new BezierDecelerateInterpolator());
                mBrowserFrame.startAnimation(show);
            }
        }
    }

    /**
     * Display the ActionBar using an animation if we are in full-screen
     * mode. This method also re-parents the ActionBar if its parent is
     * incorrect so that the animation can happen correctly.
     */
    @Override
    public void showActionBar() {
        if (mFullScreen) {
            Log.d(TAG, "showActionBar");
            if (mToolbarLayout == null)
                return;

            int height = mToolbarLayout.getHeight();
            if (height == 0) {
                mToolbarLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                height = mToolbarLayout.getMeasuredHeight();
            }

            final int totalHeight = height;
            if (mToolbarLayout.getTranslationY() < -(height - 0.01f)) {
                Animation show = new Animation() {
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        float trans = interpolatedTime * totalHeight;
                        mToolbarLayout.setTranslationY(trans - totalHeight);
                        setWebViewTranslation(trans);
                    }
                };
                show.setDuration(250);
                show.setInterpolator(new BezierDecelerateInterpolator());
                mBrowserFrame.startAnimation(show);
            }
        }
    }

    @Override
    public void handleBookmarksChange() {
        final LightningView currentTab = mTabsManager.getCurrentTab();
        if (currentTab != null && UrlUtils.isBookmarkUrl(currentTab.getUrl())) {
            currentTab.loadBookmarkpage();
        }
        if (currentTab != null) {
            mBookmarksView.handleUpdatedUrl(currentTab.getUrl());
        }
    }

    @Override
    public void handleDownloadDeleted() {
        final LightningView currentTab = mTabsManager.getCurrentTab();
        if (currentTab != null && UrlUtils.isDownloadsUrl(currentTab.getUrl())) {
            currentTab.loadDownloadspage();
        }
        if (currentTab != null) {
            mBookmarksView.handleUpdatedUrl(currentTab.getUrl());
        }
    }

    @Override
    public void handleBookmarkDeleted(@NonNull HistoryItem item) {
        mBookmarksView.handleBookmarkDeleted(item);
        handleBookmarksChange();
    }

    @Override
    public void handleNewTab(@NonNull LightningDialogBuilder.NewTab newTabType, @NonNull String url) {
        mDrawerLayout.closeDrawers();
        switch (newTabType) {
            case FOREGROUND:
                newTab(url, true);
                break;
            case BACKGROUND:
                newTab(url, false);
                break;
            case INCOGNITO:
                Intent intent = new Intent(BrowserActivity.this, IncognitoActivity.class);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up_in, R.anim.fade_out_scale);
                break;
        }
    }

    /**
     * Performs an action when the provided view is laid out.
     *
     * @param view     the view to listen to for layouts.
     * @param runnable the runnable to run when the view is
     *                 laid out.
     */
    private static void doOnLayout(@NonNull final View view, @NonNull final Runnable runnable) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    //noinspection deprecation
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                runnable.run();
            }
        });
    }

    /**
     * This method lets the search bar know that the page is currently loading
     * and that it should display the stop icon to indicate to the user that
     * pressing it stops the page from loading
     */

    private void setIsLoading(boolean isLoading) {
        if (!mSearch.hasFocus()) {
            mIcon = isLoading ? mDeleteIcon : mRefreshIcon;
            mSearch.setCompoundDrawables(null, null, mIcon, null);
        }
    }

    /**
     * handle presses on the refresh icon in the search bar, if the page is
     * loading, stop the page, if it is done loading refresh the page.
     * See setIsFinishedLoading and setIsLoading for displaying the correct icon
     */
    private void refreshOrStop() {
        final LightningView currentTab = mTabsManager.getCurrentTab();
        if (currentTab != null) {
            if (currentTab.getProgress() < 100) {
                currentTab.stopLoading();
            } else {
                currentTab.reload();
            }
        }
    }

    /**
     * Handle the click event for the views that are using
     * this class as a click listener. This method should
     * distinguish between the various views using their IDs.
     *
     * @param v the view that the user has clicked
     */
    @Override
    public void onClick(View v) {
        final LightningView currentTab = mTabsManager.getCurrentTab();
        if (currentTab == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.arrow_button:
                if (mSearch != null && mSearch.hasFocus()) {
                    currentTab.requestFocus();
                } else if (mShowTabsInDrawer) {
                    mDrawerLayout.openDrawer(getTabDrawer());
                } else {
                    currentTab.loadHomepage();
                }
                break;
            case R.id.country_button:
                showProxies();
                break;
            case R.id.button_next:
                currentTab.findNext();
                break;
            case R.id.button_back:
                currentTab.findPrevious();
                break;
            case R.id.button_quit:
                currentTab.clearFindMatches();
                mSearchBar.setVisibility(View.GONE);
                break;
            case R.id.action_reading:
                Intent read = new Intent(this, ReadingActivity.class);
                read.putExtra(Constants.LOAD_READING_URL, currentTab.getUrl());
                startActivity(read);
                break;
            case R.id.action_toggle_desktop:
                currentTab.toggleDesktopUA(this);
                currentTab.reload();
                closeDrawers(null);
                break;
            case R.id.close_btn:
                serverListDialog.hide();
                serverListDialog.dismiss();


                break;

            case R.id.btn_nonPremium:

                if (userType.equals("1")) {

                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);

                    final int width = metrics.widthPixels / 2;

                   /* btn_nonPremium.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {*/

                    //Subscription dialog
                    final Dialog dialog = new Dialog(BrowserActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_subscription);
                    TextView txtHeadingName = dialog.findViewById(R.id.txtHeadingName);
                    TextView txtHeadingBilling = dialog.findViewById(R.id.txtHeadingBilling);
                    TextView txtHeadingExpiry = dialog.findViewById(R.id.txtHeadingExpiry);
                    TextView txtHeadingAmt = dialog.findViewById(R.id.txtHeadingAmt);
                    TextView txtName = dialog.findViewById(R.id.txtName);
                    TextView txtBillingCycle = dialog.findViewById(R.id.txtBillingCycle);
                    TextView txtInitDate = dialog.findViewById(R.id.txtInitDate);
                    TextView txtExpiryDate = dialog.findViewById(R.id.txtExpiryDate);
                    TextView txtAmt = dialog.findViewById(R.id.txtAmt);
                    Button btnOk = dialog.findViewById(R.id.btnOk);

                    txtHeadingName.setWidth(width);
                    txtHeadingBilling.setWidth(width);
                    txtHeadingExpiry.setWidth(width);
                    txtHeadingAmt.setWidth(width);
                    txtName.setWidth(width);
                    txtBillingCycle.setWidth(width);
                    txtExpiryDate.setWidth(width);
                    txtAmt.setWidth(width);

                    if (sharedpreferences.contains(SplashScreen.expiryDate)) {
                        expiryDate = sharedpreferences.getString(SplashScreen.expiryDate, "");
                        billingCycle = sharedpreferences.getString(SplashScreen.billingCycle, "");
                        amount = sharedpreferences.getString(SplashScreen.amount, "");
                        subscribeDate = sharedpreferences.getString(SplashScreen.subscribed_date, "");
                    } else {
                        expiryDate = null;
                        billingCycle = null;
                        amount = null;
                    }

                    if (subscription != null) {
                        if (subscription.getUsername() != null) {
                            txtName.setText(subscription.getUsername());
                        }

                        txtInitDate.setText(subscription.getCreated_at());

                        Date olddate = new Date();

                        dateFormat = new SimpleDateFormat("MMMM d, yyyy");

                        SimpleDateFormat olddateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        //expiryDate = dateFormat.format(olddate);
                        String expiryDate1 = null;
                        try {
                            Log.e("nextDue", subscription.getNextduedate());
                            olddate = olddateFormat.parse(subscription.getNextduedate());
                            expiryDate1 = dateFormat.format(olddate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        txtBillingCycle.setText(subscription.getBillingcycle());
                        txtExpiryDate.setText(expiryDate1);
                        txtAmt.setText(subscription.getAmount());

                    } else if (expiryDate != null && billingCycle != null && !billingCycle.isEmpty()) {

                        /*if (billingCycle.equalsIgnoreCase("Free trial")) {
                            Calendar calendar = Calendar.getInstance();
                            String date = simpleDateFormat.format(calendar.getTime());
                            try {
                                Date today = simpleDateFormat.parse(date);
                                Date subscribed = simpleDateFormat.parse(subscribeDate);
                                calendar.setTime(subscribed);
                                calendar.add(Calendar.DAY_OF_MONTH, 7);
                                String strSeventhDate = simpleDateFormat.format(calendar.getTime());
                                Date seventhDate = simpleDateFormat.parse(strSeventhDate);
                                Log.e("dateToday", date);
                                Log.e("dateSeven", strSeventhDate);

                                if (today.before(seventhDate) || today.equals(seventhDate)) {

                                } else {
                                    billingCycle = "1 month";
                                    amount = getString(R.string.month_1_price);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }*/

                        txtBillingCycle.setText(billingCycle);
                        txtExpiryDate.setText(expiryDate);
                        txtAmt.setText(amount);
                    }

               /* try {
                    expireDate = dateFormat.parse(expiryDate);
                    *//*Date date = new Date(System.currentTimeMillis());
                    Log.e("expireDate", "" + expireDate.getTime());
                    Log.e("todayDate", "" + date.getTime());*//*
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/

                    btnOk.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    if (subscription != null || expiryDate != null) {

                        if (billingCycle != null) {
                            dialog.show();
                        } /*else {
                            Toast.makeText(BrowserActivity.this, "You are free subscriber", Toast.LENGTH_LONG).show();
                        }*/
                    } else {
                        subscription_id = "com.browser.myproxyvpn.freetrialwithonemonth";

                        mHelper.launchSubscriptionPurchaseFlow(BrowserActivity.this, subscription_id, 10001,
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
                        //Toast.makeText(BrowserActivity.this, "Your Premium membership has expired. Please purchase again to continue using the premium benefits!!!", Toast.LENGTH_SHORT).show();
                    }
                   /*      }
                   });*/
                    dialog.show();
                } else {

                    //subDuration = 1;
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
                }
        }
    }

    private void showProxies() {
        ProxyData proxyData = new ProxyData();
        proxyData = new Gson().fromJson(mPreferences.getProxyServerData(), ProxyData.class);
        if (proxyData != null) {
            showProxyDialog(this, proxyData.serverlist.proxy);
        }
    }

    private void showProxyDialog(Context context, final ArrayList<ProxyModel> proxies) {


        if (serverListDialog == null) {

            serverBuilder = new AlertDialog.Builder(context);
            View layout = LayoutInflater.from(context).inflate(R.layout.server_list_dialog, null);
            freeListView = layout.findViewById(R.id.free_list);
            premiumListView = layout.findViewById(R.id.premium_list);
            TextView closeView = layout.findViewById(R.id.close_btn);
            closeView.setOnClickListener(this);
            for (ProxyModel proxy : proxies) {

                /***********vpn********************/
                if (userType.equalsIgnoreCase("1") && billingCycle != null && !billingCycle.equalsIgnoreCase("")) {
                    freeList.add(proxy);
                } else if (proxy.getType().equals("Premium")) {
                    premiumList.add(proxy);
                } else {
                    freeList.add(proxy);
                }
                /**********************/

               /* if (proxy.getType().equals("Premium")) {
                    premiumList.add(proxy);
                }
                else freeList.add(proxy);*/
            }
            if (freeList.size() > 0) {
                freeAdapter = new ProxyAdapter(context, freeList);
                freeListView.setAdapter(freeAdapter);
            }
            if (premiumList.size() > 0) {
                premiumAdapter = new ProxyAdapter(context, premiumList);
                premiumListView.setAdapter(premiumAdapter);
            }

            serverBuilder.setView(layout);
            serverListDialog = serverBuilder.create();

            if (userType.equals("1")) {
                premiumListView.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < premiumList.size(); i++) {
                            if (premiumList.get(i).getPort() == selectedCountry) {

                                // Log.e("getCountry===", mPreferences.getCountry() + " " + i);
                                Log.e("getselectedCountry===", selectedCountry + " " + i);

                                premiumListView.setItemChecked(i, true);
                                premiumListView.setSelection(i);
                                if (selectedView != null)
                                    selectedView.setBackgroundResource(R.color.transparent);
                                selectedView = getViewByPosition(i, premiumListView);
                                selectedView.setBackgroundResource(R.color.secondary_color_settings);
                                premiumAdapter.setSelectedItem(i);

                                break;
                            }
                        }
                    }
                });
            }


            if (selectedView == null && mPreferences.getProxyChoice() == Constants.PROXY_MPVG) {
                freeListView.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < freeAdapter.getCount(); i++) {
                            if (freeAdapter.getItem(i).getPort() == mPreferences.getMpvgProxyPort()) {
                                if (selectedView != null)
                                    selectedView.setBackgroundResource(R.color.transparent);
                                selectedView = getViewByPosition(i, freeListView);
                                selectedView.setBackgroundResource(R.color.secondary_color_settings);

                                //    setFreeProxy(freeList.get(i), selectedView);


                                Log.e("getCountry===", selectedCountry + " " + i);

                                break;
                            }
                        }
                    }
                });
            }

            freeListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (selectedView != null)
                        selectedView.setBackgroundResource(R.color.transparent);
                    selectedView = view;
                    selectedView.setBackgroundResource(R.color.secondary_color_settings);

                    try {

                        premiumAdapter.setSelectedItem(-1);
                    } catch (Exception e) {


                    }


                    // selectedCountry = -1;
                    Log.e("getCountryNumber===", selectedCountry + "");
                    if (timerLayout != null) {
                        timerLayout.setVisibility(View.GONE);
                        premiumLayout.setVisibility(View.VISIBLE);
                    }

                    setFreeProxy(freeList.get(position), view);


                }
            });

            premiumListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    minuteView = (TextView) view.findViewById(R.id.minute);
                    secondView = (TextView) view.findViewById(R.id.second);
                    //if(premiumServerEnable != position){
                   /* if (selectedView != null)
                        selectedView.setBackgroundResource(R.color.transparent);
                    selectedView = view;*/
                    setPremiumProxy(premiumList.get(position), view, position);

                    checkPosition = position;

                    //}

                    //vpn
                    //setPremiumProxy(premiumList.get(position), view);
                }
            });


        } else {

            serverListDialog.show();

        }


    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public void setFreeProxy(ProxyModel proxy, View view) {
        mPreferences.setProxyChoice(Constants.PROXY_MPVG);
        mPreferences.setMpvgProxyPort(proxy.getPort());
        mPreferences.setCountry(proxy.getCountry());
        mPreferences.setUserSetting(Constants.FREE);
        mProxyUtils.initializeProxy(this);
        setFlag(proxy.getCountry());

        //  selectedCountry = mPreferences.getMpvgProxyPort();
        if (userType.equalsIgnoreCase("1")) {
            premiumLayout = (LinearLayout) view.findViewById(R.id.premium_extra);
            premiumLayout.setVisibility(View.GONE);
        }
        serverListDialog.hide();
    }





    public void setPremiumProxy(final ProxyModel proxy, final View view, final int position) {


        Log.e("userType", "" + userType);

        if (userType.equalsIgnoreCase("0")) {

            /* if (dialogCnt == 0) {*/

            final Dialog dialog = new Dialog(BrowserActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_non_premium);

            Button watchVideo = (Button) dialog.findViewById(R.id.watch_video);
            Button upgradePremium = (Button) dialog.findViewById(R.id.upgradePremium);
            Button noThanks = (Button) dialog.findViewById(R.id.noThanks);


            upgradePremium.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent premiumIntent = new Intent(BrowserActivity.this, UpgradeActivity.class);
                    startActivity(premiumIntent);
                    dialogCnt++;

                    dialog.dismiss();
                }
            });


            noThanks.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();
                    dialogCnt++;
                    counryPopupOpen = 0;
                }
            });


            watchVideo.setOnClickListener(new OnClickListener() {
                                              @Override
                                              public void onClick(View view2) {

                                                  dialogCnt++;
                                                  mAd.loadAd(REWARDED_ID, new AdRequest.Builder().build());

                                                  mAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                                                      @Override
                                                      public void onRewardedVideoAdLoaded() {
                                                          Toast.makeText(BrowserActivity.this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();

                                                          mAd.show();
                                                          rewardedAdFailCount = 0;
                                                      }

                                                      @Override
                                                      public void onRewardedVideoAdOpened() {
                                                      }

                                                      @Override
                                                      public void onRewardedVideoStarted() {
                                                      }

                                                      @Override
                                                      public void onRewardedVideoAdClosed() {
                                                          Toast.makeText(getApplicationContext(), "Video closed", Toast.LENGTH_SHORT).show();
                                                          rewardedAdFailCount++;

                                                      }


                                                      @Override
                                                      public void onRewarded(RewardItem rewardItem) {
                                                          Toast.makeText(BrowserActivity.this, "onRewardedVideoAdRewarded", Toast.LENGTH_SHORT).show();
                                                      }

                                                      @Override
                                                      public void onRewardedVideoAdLeftApplication() {
                                                      }

                                                      @Override
                                                      public void onRewardedVideoAdFailedToLoad(int i) {
                                                          Toast.makeText(getApplicationContext(), "No Ads", Toast.LENGTH_SHORT).show();
                                                          rewardedAdFailCount++;
                                                          if (rewardedAdFailCount < 20)
                                                              mAd.loadAd(REWARDED_ID, new AdRequest.Builder().build());
                                                          else
                                                              Toast.makeText(getApplicationContext(), AD_FAIL_GUIDE, Toast.LENGTH_LONG).show();
                                                      }

                                                      @Override
                                                      public void onRewardedVideoCompleted() {

                                                          Log.e(TAG,"Dialog");
                                                          if (selectedView != null) {
                                                              selectedView.setBackgroundResource(R.color.transparent);
                                                          }
                                                          selectedView = view;
                                                          selectedView.setBackgroundResource(R.color.secondary_color_settings);
                                                          if (timerLayout != null) {
                                                              timerLayout.setVisibility(View.GONE);
                                                              premiumLayout.setVisibility(View.VISIBLE);
                                                          }
                                                          timerLayout = (LinearLayout) view.findViewById(R.id.timer);
                                                          premiumLayout = (LinearLayout) view.findViewById(R.id.premium_extra);
                                                          //vpn
                                                          premiumLayout.setVisibility(View.GONE);
                                                          timerLayout.setVisibility(View.VISIBLE);
                                                          if (userType.equalsIgnoreCase("1") && !orderIduser.equalsIgnoreCase("") && !billingCycle.isEmpty()) {
                                                              premiumLayout.setVisibility(View.GONE);
                                                              timerLayout.setVisibility(View.GONE);
                                                          } else {
                                                              premiumLayout.setVisibility(View.GONE);
                                                              timerLayout.setVisibility(View.VISIBLE);
                                                          }

                                                          if (downTimer != null) downTimer.cancel();

                                                          downTimer = new CountDownTimer(LOCKTIME, 1000) {
                                                              public void onTick(long millisUntilFinished) {

                                                                  int timeLeft = Integer.parseInt(String.valueOf(millisUntilFinished / 1000));
                                                                  int minute = timeLeft / 60;
                                                                  int second = timeLeft % 60;
                                                                  DecimalFormat formatter = new DecimalFormat("00");
                                                                  String minuteString = formatter.format(minute) + ":";
                                                                  String secondString = formatter.format(second);
                                                                  minuteView.setText(minuteString);
                                                                  secondView.setText(secondString);
                                                              }

                                                              public void onFinish() {
                                                                  timerLayout.setVisibility(View.GONE);
                                                                  premiumLayout.setVisibility(View.VISIBLE);
                                                                  mPreferences.setProxyChoice(Constants.PROXY_MPVG);
                                                                  mPreferences.setMpvgProxyPort(mPreferences.getInitialProxyPort());
                                                                  mPreferences.setCountry(mPreferences.getDefaultCountry());
                                                                  mPreferences.setUserSetting(Constants.FREE);
                                                                  mProxyUtils.initializeProxy(BrowserActivity.this);
                                                                  setFlag(mPreferences.getCountry());
                                                                  selectedView.setBackgroundResource(R.color.transparent);
                                                                  for (int i = 0; i < freeAdapter.getCount(); i++) {
                                                                      if (freeAdapter.getItem(i).getPort() == mPreferences.getMpvgProxyPort()) {
                                                                          selectedView = getViewByPosition(i, freeListView);
                                                                          selectedView.setBackgroundResource(R.color.secondary_color_settings);
                                                                          break;
                                                                      }
                                                                  }
                                                              }
                                                          };
                                                          downTimer.start();
                                                          mPreferences.setProxyChoice(Constants.PROXY_MPVG);
                                                          mPreferences.setMpvgProxyPort(proxy.getPort());
                                                          mPreferences.setCountry(proxy.getCountry());
                                                          mPreferences.setUserSetting(Constants.PREMIUM);
                                                          mProxyUtils.initializeProxy(BrowserActivity.this);
                                                          setFlag(proxy.getCountry());
                                                          // premiumServerEnable=position;
                                                          dialog.dismiss();
                                                          counryPopupOpen = 0;
                                                      }


                                                  });

                                                  dialog.dismiss();
                                              }
            });

            dialog.show();
            counryPopupOpen = 1;
            BrowserDialog.setDialogSize(this, dialog);

        } else if (userType.equalsIgnoreCase("1")) {

            if ((subscription != null) || (expiryDate != null && !expiryDate.isEmpty())) {

                premiumAdapter.setSelectedItem(position);


                premiumLayout = (LinearLayout) view.findViewById(R.id.premium_extra);
                premiumLayout.setVisibility(View.GONE);

                if (selectedView != null)
                    selectedView.setBackgroundResource(R.color.transparent);
                selectedView = view;
                selectedView.setBackgroundResource(R.color.secondary_color_settings);

                mPreferences.setProxyChoice(Constants.PROXY_MPVG);
                mPreferences.setMpvgProxyPort(proxy.getPort());
                mPreferences.setCountry(proxy.getCountry());
                mPreferences.setUserSetting(Constants.PREMIUM);
                mProxyUtils.initializeProxy(BrowserActivity.this);
                setFlag(mPreferences.getCountry());
                serverListDialog.hide();
            }
        }
    }

    /**
     * This NetworkReceiver notifies each of the WebViews in the browser whether
     * the network is currently connected or not. This is important because some
     * JavaScript properties rely on the WebView knowing the current network state.
     * It is used to help the browser be compliant with the HTML5 spec, sec. 5.7.7
     */
    private final NetworkReceiver mNetworkReceiver = new NetworkReceiver() {
        @Override
        public void onConnectivityChange(boolean isConnected) {
            Log.d(TAG, "Network Connected: " + isConnected);

            if (!isConnected) {
                Toast.makeText(BrowserActivity.this, "No internet!!!", Toast.LENGTH_SHORT).show();
            }
            mTabsManager.notifyConnectionStatus(isConnected);
        }
    };

    /**
     * Handle the callback that permissions requested have been granted or not.
     * This method should act upon the results of the permissions request.
     *
     * @param requestCode  the request code sent when initially making the request
     * @param permissions  the array of the permissions that was requested
     * @param grantResults the results of the permissions requests that provides
     *                     information on whether the request was granted or not
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public void consumeItem() {
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

                    userType = "1";

                    Calendar calendar = Calendar.getInstance();
                    /*SimpleDateFormat todaydateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String todayDate = todaydateFormat.format(calendar.getTime());*/

                    String subscribed_date = simpleDateFormat.format(calendar.getTime());
                    Log.e("subscribed", subscribed_date);

                    getGooglePlayDetails();
                    //getData();

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

                    btn_nonPremium.setBackgroundColor(getResources().getColor(R.color.green_color));
                    txtPremium.setText(getResources().getString(R.string.vpn_enabled));
                    txtSubscribe.setText(getResources().getString(R.string.view_subscription));
                    imageShield.setImageResource(R.drawable.ic_greenshield);


                    Toast.makeText(BrowserActivity.this, getString(R.string.msgPurchase), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

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
                    ArrayList purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                    Log.d("rajniTAG", "purchaseDataList : " + purchaseDataList);
                    if (purchaseDataList != null) {

                        if (purchaseDataList.size() > 0) {

                            for (int i = 0; i < purchaseDataList.size(); ++i) {
                                try {
                                    JSONObject object = new JSONObject(purchaseDataList.get(i).toString());
                                    String productIdResp = object.getString("productId");
                                    String orderIdResp = object.getString("orderId");
                                    String purchaseTime = object.getString("purchaseTime");
                                    String purchaseTokenResp = object.getString("purchaseToken");
                                    String autoRenewingResp = object.getString("autoRenewing");

                                    Log.d("rajniTAG", "productIdResp : " + productIdResp);
                                    Log.d("rajniTAG", "orderIdResp : " + orderIdResp);
                                    Log.d("rajniTAG", "purchaseTokenResp : " + purchaseTokenResp);

                                    //Expiry Date
                                    long millisec = Long.parseLong(purchaseTime);
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(new Date(millisec));
                                    calendar.add(Calendar.MONTH, 1);
                                    String date = new SimpleDateFormat("MM/dd/yyyy").format(calendar.getTime());
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

                                    orderIduser = orderIdResp;
                                    productIduser = productIdResp;
                                    purchaseTokenuser = purchaseTokenResp;
                                    autoRenewinguser = autoRenewingResp;
                                    expiryDate = date;

                                    SharedPreferences.Editor editor = sharedpreferences.edit();

                                    editor.putString(SplashScreen.orderId, orderIdResp);
                                    editor.putString(productId, productIdResp);
                                    editor.putString(purchaseToken, purchaseTokenResp);
                                    editor.putString(autoRenewing, autoRenewingResp);
                                    editor.putString(SplashScreen.expiryDate, date);
                                    editor.putString(SplashScreen.billingCycle, billing_cycle);
                                    editor.putString(SplashScreen.amount, amt);
                                    editor.commit();

                                    userType = "1";
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.remove(SplashScreen.billingCycle);
                            editor.remove(SplashScreen.expiryDate);
                            editor.remove(SplashScreen.amount);
                            editor.remove(SplashScreen.orderId);
                            editor.commit();

                            userType = "0";
                        }
                    } else {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.remove(SplashScreen.billingCycle);
                        editor.remove(SplashScreen.expiryDate);
                        editor.remove(SplashScreen.amount);
                        editor.remove(SplashScreen.orderId);
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

    private void showDialog() {

        SharedPreferences.Editor editor_pre = pref.edit();
        editor_pre.putBoolean(CHECK_DIALOG, false);
        userDialog = false;
        editor_pre.commit();

        Log.e("checkuserDialog", userDialog + "");


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

              /*  Log.e("checkuser", userDialog + "");
                AlertDialog alertDialog = new AlertDialog.Builder(BrowserActivity.this)
                        .setMessage("Your Premium membership has expired. Please purchase again to continue using the " +
                                "premium benefits.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Log.e("checkFree", "4");
                            }
                        })
                        .show();*/
            }
        });
    }


}
