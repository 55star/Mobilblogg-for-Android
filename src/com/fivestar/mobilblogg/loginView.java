package com.fivestar.mobilblogg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class loginView extends Activity {

	private EditText userNameText;
	private EditText passWordText;
	private ProgressDialog dialog;
	private Thread loginThread;
	private MobilbloggApp app;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		userNameText = (EditText) findViewById(R.id.EditText01);
		passWordText = (EditText) findViewById(R.id.EditText02);
		dialog = new ProgressDialog(loginView.this);
		dialog.setMessage(getString(R.string.please_wait));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		app = ((MobilbloggApp)getApplicationContext());
	}

	public void loginClickHandler(View view) {

		final String userName = userNameText.getText().toString();
		final String passWord = passWordText.getText().toString();
		final Activity activity = this;

		dialog.show();
		loginThread = new Thread() {
			public void run() {
				Communicator com = new Communicator();
				final int loginStatus = com.doLogin(userName, passWord);

				Runnable action = new Runnable() {
					public void run() {

						dialog.dismiss();

						if (loginStatus == 1) {
							System.out.println("Login successful!");
							app.setUserName(userName);
							app.setLoggedInStatus(true);
							Intent myIntent = new Intent(activity,
									mainMenuView.class);
							startActivityForResult(myIntent, 0);
							finish();
						} else {
							System.out.println("Login failure");
							Toast.makeText(activity, "Inloggningen misslyckades", Toast.LENGTH_SHORT).show();
						}
					}
				};
				activity.runOnUiThread(action);
			}
		};
		loginThread.start();
	}
}