package com.browser.myproxyvpn.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.browser.myproxyvpn.R;

import org.w3c.dom.Text;

public class PaymentDetailActivity extends AppCompatActivity implements View.OnClickListener{

    TextView payment_value;
    TextView total_plan;
    float totalPlanValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);
        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(this);
        payment_value=findViewById(R.id.payment_value);
        total_plan=findViewById(R.id.total_plan);

        String sessionId= getIntent().getStringExtra("paymentText");

        String[] separated = sessionId.split("~");


        if(separated[1].equals("9.99")){

            totalPlanValue = Float.parseFloat(separated[1])*1;

        }else if(separated[1].equals("6.65"))
            {

            totalPlanValue = Float.parseFloat(separated[1])*6;
        }else {

            totalPlanValue = Float.parseFloat(separated[1])*12;
        }

        payment_value.setText("Your plan: $ "+ separated[1]+separated[2]+"($ "+String.format("%.2f", totalPlanValue)+ " total)");
        total_plan.setText(separated[0]);
        Log.e("chck",sessionId);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.submit_button:

        }
    }
}
