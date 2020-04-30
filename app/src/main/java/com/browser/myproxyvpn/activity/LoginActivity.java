package com.browser.myproxyvpn.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.browser.myproxyvpn.MainActivity;
import com.browser.myproxyvpn.R;
import com.browser.myproxyvpn.utils.ApiClient;
import com.browser.myproxyvpn.utils.ApiInterface;
import com.browser.myproxyvpn.utils.LoginData;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.browser.myproxyvpn.utils.UserPrefUtils.IS_REMEMBER;
import static com.browser.myproxyvpn.utils.UserPrefUtils.IS_SUBSCRIBED;
import static com.browser.myproxyvpn.utils.UserPrefUtils.IS_WHMS;
import static com.browser.myproxyvpn.utils.UserPrefUtils.KEY_EMAIL;
import static com.browser.myproxyvpn.utils.UserPrefUtils.KEY_PASSWORD;
import static com.browser.myproxyvpn.utils.UserPrefUtils.PREF_NAME;
import static com.browser.myproxyvpn.utils.UserPrefUtils.SUBSCRIPTION;

public class LoginActivity extends AppCompatActivity {

    private EditText user_email, user_password;
    private CheckBox remember;
    private Button btn_signin;
    private SharedPreferences preferences;
    ApiInterface apiService;
    String email, pass;
    boolean isRemember;
    ProgressDialog dialog;
    String userType = "0";
    private LoginData loginData;

    /*private static final String PREF_NAME = "MyProxyVPN";
    private static final String KEY_EMAIL = "Email";
    private static final String IS_LOGIN = "LoggedIn";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user_email = findViewById(R.id.user_email);
        user_password = findViewById(R.id.user_password);
        remember = findViewById(R.id.remember);
        btn_signin = findViewById(R.id.btn_signin);

        apiService = ApiClient.getLoginClient().create(ApiInterface.class);

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        if (preferences.contains(IS_REMEMBER)) {
            isRemember = preferences.getBoolean(IS_REMEMBER, false);
        }

        if (isRemember) {
            user_email.setText(preferences.getString(KEY_EMAIL, ""));
            user_password.setText(preferences.getString(KEY_PASSWORD, ""));
            remember.setChecked(true);
        }

        dialog = new ProgressDialog(this);
    }

    private void Login() {

        dialog.setCancelable(false);
        dialog.setMessage("Loading...");
        dialog.show();

        email = user_email.getText().toString().trim();
        pass = user_password.getText().toString().trim();

        if (email.isEmpty()) {
            user_email.setError("Email Require");
            user_email.requestFocus();
            dialog.dismiss();
            //return;
        } else if (pass.isEmpty()) {
            user_password.setError("Password Require");
            user_password.requestFocus();
            dialog.dismiss();
            // return;
        } else {
            apiCalling();
            //new ApiCall().execute(email, pass);
        }

        /*if (email.equals("admin@gmail.com") && pass.equals("password")) {

            preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_EMAIL, email);
            editor.putBoolean(IS_LOGIN, true);
            editor.apply();
            editor.commit();
            Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
        } else {
            // onStop();
            Toast.makeText(this, "Enter Valid Username and Password", Toast.LENGTH_SHORT).show();
        }*/

    }

    /*private class ApiCall extends AsyncTask<String, Void, Void> {

        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected Void doInBackground(String... strings) {
            *//*URL myUrl = null;
            try {
                myUrl = new URL(BASE_URL + "billing/includes/login_api.php");
                HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.connect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*//*

            HttpPost httppost = new HttpPost(BASE_URL + "billing/includes/login_api.php");
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = null;
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("email", strings[0]));
                nameValuePairs.add(new BasicNameValuePair("password", strings[1]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpclient.execute(httppost);
                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                Log.e("status", "" + status);
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    try {
                        JSONObject jsono = new JSONObject(data);
                        Log.e("json===", jsono.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            dialog.dismiss();
            Log.e("post", "post");
        }
    }*/

    private void apiCalling() {
        Call<LoginData> loginDataCall = apiService.getLoginDetails(email, pass);
        loginDataCall.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(Call<LoginData> call, Response<LoginData> response) {

                /*try {
                    Log.e("response1", "" + response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                loginData = response.body();
                /*InActiveUser loginData = response.body();
                dialog.dismiss();*/

                if (response.errorBody() == null) {

                    if (loginData != null) {

                        Log.e("response", "" + loginData.getResponse_code());

                        if (loginData.getResponse_code()==200) {

                            SharedPreferences.Editor editor = preferences.edit();
                            if (loginData.getData().getSubscribed_package_detail() != null) {
                                editor.putBoolean(IS_SUBSCRIBED, true);
                                userType = "1";
                            } else {
                                userType = "0";
                                editor.putBoolean(IS_SUBSCRIBED, false);
                            }

                            if (remember.isChecked()) {
                                editor.putBoolean(IS_REMEMBER, true);
                                editor.putString(KEY_PASSWORD, pass);
                                editor.putString(KEY_EMAIL, email);
                                editor.putBoolean(IS_WHMS, true);

                                Gson gson = new Gson();
                                String json = gson.toJson(loginData.getData().getSubscribed_package_detail());
                                editor.putString(SUBSCRIPTION, json);
                                editor.commit();
                            } else {
                                editor.clear();
                                editor.putString(KEY_EMAIL, email);
                                editor.putBoolean(IS_WHMS, true);

                                Gson gson = new Gson();
                                String json = gson.toJson(loginData.getData().getSubscribed_package_detail());
                                editor.putString(SUBSCRIPTION, json);
                                editor.commit();
                            }

                            Log.e("status", "success");
                            dialog.dismiss();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("Data", loginData.getData().getSubscribed_package_detail());
                            intent.putExtra("userType", userType);
                            Log.e("userType", userType);
                            startActivity(intent);
                            finish();
                        } else if (loginData.getResponse_code()==400) {
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, loginData.getError(), Toast.LENGTH_LONG).show();
                        } else {
                            dialog.dismiss();
                            Log.e("status", "unsuccess");
                            Toast.makeText(LoginActivity.this, "Either email id or password is wrong!!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                }
            }

           /* @Override
            public void onFailure(Call<InActiveUser> call, Throwable t) {
                dialog.dismiss();
                Log.e("error", "" + t.getMessage());
                Toast.makeText(LoginActivity.this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
            }*/

            @Override
            public void onFailure(Call<LoginData> call, Throwable t) {
                dialog.dismiss();
                Log.e("error", "" + t.getMessage());
                Toast.makeText(LoginActivity.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
