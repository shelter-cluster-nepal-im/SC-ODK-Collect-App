package org.odk.collect.android.activities;

import org.odk.collect.android.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	private EditText username;
	private EditText password;
	private Button login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void requestAccess(View v){
		Intent intent = new Intent(MainActivity.this, RequestAccess.class);
		startActivity(intent);

	}

	public void authenticateLogin(View view) {
		setupVariables();
		if (username.getText().toString().equals("admin")
				&& password.getText().toString().equals("admin")) {
			Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
			startActivity(intent);

		} else {
			Toast.makeText(getApplicationContext(),
					"Incorrect username or password!", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void setupVariables() {
		username = (EditText) findViewById(R.id.usernameET);
		password = (EditText) findViewById(R.id.passwordET);
		login = (Button) findViewById(R.id.loginBtn);
	}
}
