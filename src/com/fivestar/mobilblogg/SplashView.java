package com.fivestar.mobilblogg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
			doRemoteLogin(userName, passWord);
			dialog.show();
		}
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
				loginStatus = app.com.doLogin(userName, passWord);
				mHandler.post(mUpdateResults);
			}
		};
		loginThread.start();
	}


	public void splashClickHandler(View view) {
		Intent loginIntent = new Intent(view.getContext(), LoginView.class);
		startActivity(loginIntent);
	}
}