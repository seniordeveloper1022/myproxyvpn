package com.browser.myproxyvpn.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.browser.myproxyvpn.MainActivity;
import com.browser.myproxyvpn.R;

public class LogoutActivity extends AppCompatActivity {

    private Button logout;
    private SharedPreferences preferences;


    private static final String PREF_NAME = "MyProxyVPN";
    private static final String KEY_EMAIL = "Email";
    private static final String IS_LOGIN = "LoggedIn";
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        logout = findViewById(R.id.logout);

        /*if (email != null) {
                preferences = getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                preferences.getString(KEY_EMAIL, email);
                Log.d("EMAIL", "" + preferences.getString(KEY_EMAIL, email));
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }*/

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences = getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                preferences.getString(KEY_EMAIL, "");

                Log.d("getEmail", preferences.getString(KEY_EMAIL, ""));

                if (editor != null) {
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

    }
}
