package com.fivestar.mobilblogg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginView extends Activity {

	final String TAG = "LoginView";
	final Handler mHandler = new Handler();
	final Activity activity = this;
	String userName;
	String passWord;
	int loginStatus;
	private EditText userNameText;
	private EditText passWordText;
	private ProgressDialog dialog;
	private Thread loginThread;
	private MobilbloggApp app;
	private CheckBox rememberMe;
	private Context cntx;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		this.setTitle(R.string.login);
		userNameText = (EditText) findViewById(R.id.EditText01);
		passWordText = (EditText) findViewById(R.id.EditText02);
		rememberMe = (CheckBox) findViewById(R.id.check01);
		dialog = new ProgressDialog(LoginView.this);
		dialog.setMessage(getString(R.string.logging_in));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		app = ((MobilbloggApp)getApplicationContext());
		cntx = this;
	}

	final Runnable mUpdateResults = new Runnable() {
		// Update UI in UI-thread
		public void run() {
			if(dialog.isShowing()) {
				dialog.dismiss();
			}

			if (loginStatus == 1) {
				app.setUserName(userName);
				app.setLoggedInStatus(true);
				if(rememberMe.isChecked()) { 
					Utils.saveCredentials(cntx, userName, passWord);
				}

				Intent myIntent = new Intent(activity, MainMenuView.class);
				startActivityForResult(myIntent, 0);
				finish();
			} else {
				Toast.makeText(activity, getString(R.string.loginerror), Toast.LENGTH_LONG).show();
			}			
		}
	};

	protected void doRemoteLogin(final String userName,final String passWord) {
		loginThread = new Thread() {
			public void run() {
				try {
					loginStatus = app.com.doLogin(userName, passWord);
				} catch (CommunicatorException e) {
					// TODO Auto-generated catch block
					e.getMessage();
				}
				mHandler.post(mUpdateResults);
			}
		};
		loginThread.start();
	}

	public void loginClickHandler(View view) {

		userName = userNameText.getText().toString();
		passWord = passWordText.getText().toString();
		doRemoteLogin(userName, passWord);
		dialog.show();
	}
}