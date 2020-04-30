package com.browser.myproxyvpn.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.browser.myproxyvpn.R;
import com.browser.myproxyvpn.browser.activity.ThemableBrowserActivity;
import com.browser.myproxyvpn.constant.Constants;

public class UpgradeActivity extends ThemableBrowserActivity implements View.OnClickListener{

    String payMethod="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);
        LinearLayout creditCard = (LinearLayout) findViewById(R.id.credit_card);
        LinearLayout googlePlay = (LinearLayout) findViewById(R.id.google_play);
        creditCard.setOnClickListener(this);
        googlePlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.credit_card:
                mPreferences.setPayMethod(Constants.CREDITCARD);
                payMethod=Constants.CREDITCARD;
                break;
            case R.id.google_play:
                mPreferences.setPayMethod(Constants.GOOGLEPLAY);
                payMethod=Constants.GOOGLEPLAY;
                break;

        }
        Intent paymentDetail = new Intent(this, ChoiceActivity.class);
        paymentDetail.putExtra("payMethod",payMethod);
        startActivity(paymentDetail);
    }
}
