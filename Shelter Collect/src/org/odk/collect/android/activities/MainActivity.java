package org.odk.collect.android.activities;

import org.odk.collect.android.R;
import org.odk.collect.android.utilities.WebUtils;
import org.opendatakit.httpclientandroidlib.HttpResponse;
import org.opendatakit.httpclientandroidlib.auth.AuthScope;
import org.opendatakit.httpclientandroidlib.auth.UsernamePasswordCredentials;
import org.opendatakit.httpclientandroidlib.client.ResponseHandler;
import org.opendatakit.httpclientandroidlib.client.methods.HttpGet;
import org.opendatakit.httpclientandroidlib.impl.client.BasicResponseHandler;
import org.opendatakit.httpclientandroidlib.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends Activity {
    private EditText username;
    private EditText password;
    private Button login;
    String host = "52.4.253.24";
    String url = "http://52.4.253.24/Aggregate.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public void requestAccess(View v) {
        Intent intent = new Intent(MainActivity.this, RequestAccess.class);
        startActivity(intent);

    }

    public void authenticateLogin(View view) throws IOException {
        setupVariables();
        boolean flag = isNetworkAvailable();
        if(flag) {
            int code = loginCheck();
            if (code == 200) {
                WebUtils.addCredentials(username.getText().toString(), password.getText()
                        .toString(), host);
                String KEY_USERNAME = username.getText().toString();
                String KEY_PASSWORD = password.getText().toString();

                Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Incorrect username or password!", Toast.LENGTH_SHORT)
                        .show();
            }
        }else {
            Toast.makeText(getApplicationContext(),
                    "No Internet Conncetion!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public int loginCheck() throws IOException {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        httpclient.getCredentialsProvider().setCredentials(
                new AuthScope(host, 80),
                new UsernamePasswordCredentials(username.getText().toString(), password.getText().toString()));

        HttpGet httpget = new HttpGet(url);

        HttpResponse httpResp = httpclient.execute(httpget);
        int code = httpResp.getStatusLine().getStatusCode();
        return code;
    }
    private void setupVariables() {
        username = (EditText) findViewById(R.id.usernameET);
        password = (EditText) findViewById(R.id.passwordET);
        login = (Button) findViewById(R.id.loginBtn);
    }
}
