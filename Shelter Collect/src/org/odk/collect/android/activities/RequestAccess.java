package org.odk.collect.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.odk.collect.android.R;
import org.odk.collect.android.provider.Post_New_ag_Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
    private Button btnSubmit;
    private Spinner dropdown;
    private String dropdownValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_access);
    }

    public void processForm(View v) throws IOException {
        setupVariables();
        boolean fnameFlag = false;
        boolean emailFlag = false;
        boolean agencyFlag = false;
        boolean desigFlag = false;
        boolean dropDownFlag = false;
        String pass = "adsadas";
        String user = "asdasd";
        String url = "some_url";

        if (!isValidName(fullname.getText().toString().trim())) {
            fullname.setError(Html.fromHtml("<font color='red'>Invalid Full Name!</font>"));
        } else {
            fnameFlag = true;
        }
        if (!isValidEmail(email.getText().toString().trim())) {
            email.setError(Html.fromHtml("<font color='red'>Invalid Email!</font>"));
        } else {
            emailFlag = true;
        }
        if (agencyName.getText().toString().trim() == null || agencyName.getText().toString().isEmpty()) {
            agencyName.setError(Html.fromHtml("<font color='red'>Invalid Agency Name!</font>"));
        } else {
            agencyFlag = true;
        }
        if (desig.getText().toString().trim() == null || desig.getText().toString().isEmpty()) {
            desig.setError(Html.fromHtml("<font color='red'>Invalid Designation!</font>"));
        } else {
            desigFlag = true;
        }
        if ("select one".equals(dropdownValue.toLowerCase())) {
            Log.d("Gaurab Drop", dropdownValue);
            Toast.makeText(getApplicationContext(),
                    "Please Select form from the dropdown!", Toast.LENGTH_SHORT)
                    .show();
        } else {
            dropDownFlag = true;
        }

        if (fnameFlag && emailFlag && agencyFlag && desigFlag && dropDownFlag) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("name", fullname.getText().toString().trim());
            map.put("email", email.getText().toString().trim());
            map.put("agency_name", agencyName.getText().toString().trim());
            map.put("designation", desig.getText().toString().trim());
            map.put("deployment", dropdownValue.trim());
            Post_New_ag_Request m = new Post_New_ag_Request();
            int resp = m.send_request(url, user, pass, map);
            Log.d("GPTest", String.valueOf(resp));
            Toast.makeText(getApplicationContext(),
                    "Thank you, Form Subbmited", Toast.LENGTH_SHORT)
                    .show();
            Intent intent = new Intent(RequestAccess.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void setupVariables() {
        fullname = (EditText) findViewById(R.id.fullname);
        agencyName = (EditText) findViewById(R.id.agencyname);
        email = (EditText) findViewById(R.id.email);
        desig = (EditText) findViewById(R.id.designation);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        dropdown = (Spinner) findViewById(R.id.spinner1);
        dropdownValue = String.valueOf(dropdown.getSelectedItem());
    }

    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidName(String name) {
        String NAME_PATTERN = "^[a-zA-Z\\s]+";
        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }
}
