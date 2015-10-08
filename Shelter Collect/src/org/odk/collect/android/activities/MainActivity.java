package org.odk.collect.android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.utilities.WebUtils;
import org.opendatakit.httpclientandroidlib.HttpEntity;
import org.opendatakit.httpclientandroidlib.HttpResponse;
import org.opendatakit.httpclientandroidlib.client.HttpClient;
import org.opendatakit.httpclientandroidlib.client.methods.HttpGet;
import org.opendatakit.httpclientandroidlib.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class MainActivity extends Activity {
    String host = "52.20.96.40";
    String urlServer = "http://52.20.96.40/formList";
    private EditText username;
    private EditText password;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d("GTEST", "resume called");
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int is_logged_in = sharedPref.getInt("is_logged_in", 0);
        if (is_logged_in == 1) {
//            Log.d("GTEST", "is logined");
            Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
            startActivity(intent);
        } else {
//            Log.d("GTEST", "not logge din");
        }
    }


    public void requestAccess(View v) {
        Intent intent = new Intent(MainActivity.this, RequestAccess.class);
        startActivity(intent);

    }

    public void authenticateLogin(View view) throws IOException {
        setupVariables();
        boolean blankCheck = isValidUserInfo();
        if (blankCheck) {
            boolean flag = isNetworkAvailable();
            if (flag) {
//            int code = loginCheck();
                int statusCode = loginCheck();
                if (statusCode == 200) {
                    storeSharedPref();
                    Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Incorrect username or password!", Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "No network connection!", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean isValidUserInfo() {
        boolean flag = false;
        String uname = username.getText().toString();
        String pass = password.getText().toString();
        boolean uCheck = false;
        boolean pCheck = false;
        if (uname.isEmpty()) {
            username.setError(Html.fromHtml("<font color='red'>Please enter username!</font>"));
        } else {
            uCheck = true;
        }
        if (pass.isEmpty()) {
            password.setError(Html.fromHtml("<font color='red'>Please enter password!</font>"));
        } else {
            pCheck = true;
        }

        if (uCheck && pCheck) {
            flag = true;
        }
        return flag;
    }

    public int loginCheck() throws IOException {
        URL url = new URL(urlServer);
        URI uri = null;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        WebUtils.addCredentials(username.getText().toString(), password.getText().toString(), host);
        HttpContext localContext = Collect.getInstance().getHttpContext();
        HttpClient httpclient = WebUtils.createHttpClient(WebUtils.CONNECTION_TIMEOUT);
        HttpGet req = WebUtils.createOpenRosaHttpGet(uri);
        req.addHeader(WebUtils.ACCEPT_ENCODING_HEADER, WebUtils.GZIP_CONTENT_ENCODING);
        HttpResponse response;
        response = httpclient.execute(req, localContext);
        int statusCode = response.getStatusLine().getStatusCode();

        InputStream is = null;
        try {
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } finally {
            if (is != null) {
                try {
                    // ensure stream is consumed...
                    final long count = 1024L;
                    while (is.skip(count) == count)
                        ;
                } catch (Exception e) {
                    // no-op
                }
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }
        return statusCode;
    }

    private void setupVariables() {
        username = (EditText) findViewById(R.id.usernameET);
        password = (EditText) findViewById(R.id.passwordET);
        login = (Button) findViewById(R.id.loginBtn);
    }

    private void storeSharedPref() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username_", username.getText().toString());
        editor.putString("password_", password.getText().toString());
        editor.putInt("is_logged_in", 1);
        editor.commit();
    }

    private void logouttest() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("is_logged_in", 0);
        editor.commit();
    }
}
