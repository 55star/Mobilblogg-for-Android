package com.fivestar.mobilblogg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SplashView extends Activity {

	final Handler mHandler = new Handler();
	final Activity activity = this;
	String TAG = "SplashView";
	int loginStatus;

	private ProgressDialog dialog;
	private Thread loginThread;
	private MobilbloggApp app;
	private Context cntx;
	private TextView version;
	private EditText userNameInput;
	private EditText passWordInput;
	private CheckBox rememberMe;

	private String userName;
	private String passWord;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		
		/* quit app & logout, called from mainmenu */
		String func = getIntent().getStringExtra("func");
		if(func != null && func.equals("quit")) {
			Utils.removeSavedCredentials((Context)this);
			finish();
		}
		app = ((MobilbloggApp)getApplicationContext());
		app.startServices();		

		version = (TextView)findViewById(R.id.TextView02);
		userNameInput = (EditText) findViewById(R.id.EditText01);
		passWordInput = (EditText) findViewById(R.id.EditText02);
		rememberMe = (CheckBox) findViewById(R.id.check01);

		dialog = new ProgressDialog(SplashView.this);
		dialog.setMessage(getString(R.string.logging_in));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		app = ((MobilbloggApp)getApplicationContext());
		cntx = this;

		PackageManager manager = this.getPackageManager();
		PackageInfo info;
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
			version.setText("Version: " + info.versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String creds = Utils.getSavedCredentials(cntx);
		if(creds != null) {
			userName = creds.substring(0, creds.indexOf('|'));
			passWord = creds.substring(creds.indexOf('|')+1,creds.length());
			if(userName.length() > 0 && passWord.length() > 0) {
				doRemoteLogin(userName, passWord);
				dialog.show();
			}
		}
	}

	protected void doRemoteLogin(final String userName, final String passWord) {
		loginThread = new Thread() {
			public void run() {
				try {
					loginStatus = app.com.doLogin(userName, passWord);
				} catch (CommunicatorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					uiCallback.sendEmptyMessage(0);
				}
				uiCallback.sendEmptyMessage(0);
			}
		};
		loginThread.start();
	}


	private Handler uiCallback = new Handler() {
		public void handleMessage(Message msg) {
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


	public void splashClickHandler(View view) {
		switch(view.getId()) {
		case R.id.register:
			// Go to register view
			//			Intent registerIntent = new Intent(view.getContext(), LoginView.class);
			//			startActivity(registerIntent);
			Utils.log(TAG,"Let«s make a nice looking moblog!");

			break;
		case R.id.login:
			userName = userNameInput.getText().toString();
			passWord = passWordInput.getText().toString();

			if(userName.length() > 0 && passWord.length() > 0) {
				doRemoteLogin(userName, passWord);
				dialog.show();
			}
			break;
		}
	}
}