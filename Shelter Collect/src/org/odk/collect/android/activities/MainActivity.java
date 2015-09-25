package org.odk.collect.android.activities;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.utilities.WebUtils;
import org.opendatakit.httpclientandroidlib.Header;
import org.opendatakit.httpclientandroidlib.HttpEntity;
import org.opendatakit.httpclientandroidlib.HttpResponse;
import org.opendatakit.httpclientandroidlib.auth.AuthScope;
import org.opendatakit.httpclientandroidlib.auth.UsernamePasswordCredentials;
import org.opendatakit.httpclientandroidlib.client.HttpClient;
import org.opendatakit.httpclientandroidlib.client.ResponseHandler;
import org.opendatakit.httpclientandroidlib.client.methods.HttpGet;
import org.opendatakit.httpclientandroidlib.impl.client.BasicResponseHandler;
import org.opendatakit.httpclientandroidlib.impl.client.DefaultHttpClient;
import org.opendatakit.httpclientandroidlib.protocol.HttpContext;

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

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;

public class MainActivity extends Activity {
    private EditText username;
    private EditText password;
    private Button login;
    String host = "52.4.253.24";
    String urlServer = "http://52.4.253.24/Aggregate.html";

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
        if (flag) {
//            int code = loginCheck();
            int statusCode = loginCheck();
            if (statusCode == 200) {
                Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Incorrect username or password!", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
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
        /*DefaultHttpClient httpclient = new DefaultHttpClient();
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        httpclient.getCredentialsProvider().setCredentials(
                new AuthScope(host, 80),
                new UsernamePasswordCredentials(username.getText().toString(), password.getText().toString()));

        HttpGet httpget = new HttpGet(urlServer);

        HttpResponse httpResp = httpclient.execute(httpget);
        int code = httpResp.getStatusLine().getStatusCode();*/

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
}
