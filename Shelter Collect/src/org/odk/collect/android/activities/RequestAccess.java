package org.odk.collect.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.odk.collect.android.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 18/09/2015.
 */
public class RequestAccess extends Activity {
    private EditText fullname;
    private EditText agencyName;
    private EditText email;
    private EditText desig;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_access);
        setupVariables();
    }
    public void processForm(View v){
        if (!isValidEmail(email.toString().trim())) {
            email.setError("Invalid Email");
        }

    }
    private void setupVariables() {
        fullname = (EditText) findViewById(R.id.fullname);
        agencyName = (EditText) findViewById(R.id.agencyname);
        email = (EditText) findViewById(R.id.email);
        desig = (EditText)findViewById(R.id.designation);
        button = (Button) findViewById(R.id.subBtn);
    }

    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
