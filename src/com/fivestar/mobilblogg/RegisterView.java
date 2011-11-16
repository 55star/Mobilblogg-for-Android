package com.fivestar.mobilblogg;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterView extends Activity {

	final String TAG = "LoginView";
	final Handler mHandler = new Handler();
	final Activity activity = this;
	private EditText userNameInput;
	private EditText passWordInput;
	private EditText secretWordInput;
	private EditText emailInput;

	private ProgressDialog dialog;
	private MobilbloggApp app;
	private Context mContext;

	String userName = "";
	String passWord = "";
	String secretWord = "";
	String email = "";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.register);

		userNameInput = (EditText) findViewById(R.id.username);
		passWordInput = (EditText) findViewById(R.id.password);
		secretWordInput = (EditText) findViewById(R.id.secret);
		emailInput = (EditText) findViewById(R.id.email);

		app = ((MobilbloggApp)getApplicationContext());
		mContext = this;

		dialog = new ProgressDialog(RegisterView.this);
		dialog.setMessage(getString(R.string.loading));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

		//		userNameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		//			public void onFocusChange(View v, boolean hasFocus) {
		//				if(!hasFocus) {
		//					checkUserName(userNameInput.getText().toString());
		//				}
		//			}
		//		});
	}

	public void registerClickHandler(View view) {
		switch(view.getId()) {
		case R.id.register:
			userName = userNameInput.getText().toString();
			passWord = passWordInput.getText().toString();
			secretWord = secretWordInput.getText().toString();
			email = emailInput.getText().toString();

			Utils.log(TAG, "Check input params");

			/* Check for empty strings */
			if (userName.equals("") || passWord.equals("") || secretWord.equals("") || email.equals("")) {
				Toast.makeText(this, getString(R.string.fillallfields), Toast.LENGTH_LONG).show();
				return;
			}

			// Check password
			if(passWord.length() <= 3) {
				Toast.makeText(this, getString(R.string.passwordlength), Toast.LENGTH_LONG).show();
				passWordInput.requestFocus();
				return;
			}
			if(userName.equals(passWord)) {
				Toast.makeText(this, getString(R.string.badpassword), Toast.LENGTH_LONG).show();
				passWordInput.requestFocus();
				return;
			}

			// Check email
			int at = email.indexOf('@');
			int lastdot = email.lastIndexOf('.');
			if (at <= 0 || lastdot <= 0 || lastdot < at) {
				Toast.makeText(this, getString(R.string.novalidemail), Toast.LENGTH_LONG).show();
				emailInput.requestFocus();
				return;
			}

			// Check if user already exists
			dialog.show();

			checkUserName(userName);
		}
	}

	private void registerUser(String userName, String passWord, String secretWord, String email) {
		final String mUser = userName;
		final String mPass = passWord;
		final String mSecret = secretWord;
		final String mEmail = email;
		Thread mThread = new Thread() {
			public void run() {
				int userId = 0;
				userId = app.com.register(mUser, mPass, mEmail, mSecret);
				registerCallback.sendEmptyMessage(userId);
			}
		};
		mThread.start();
	}

	private Handler registerCallback = new Handler() {
		public void handleMessage(Message msg) {
			if(dialog.isShowing()) {
				dialog.dismiss();
			}
			if (msg.what <= 0) {
				Toast.makeText(app, "Register error, response: " + msg.what, Toast.LENGTH_LONG).show();
			} else {
				Utils.log(TAG, "Register success, new userid: " + msg.what);
				Intent mIntent = new Intent(app, SplashView.class);
				startActivity(mIntent);
			}
		}
	};


	private void checkUserName(String userName) {
		Utils.log(TAG, "Check username");
		final String user = userName;
		Thread mThread = new Thread() {
			public void run() {
				try {
					if(app.com.foundUser(user)) {
						checkUserCallback.sendEmptyMessage(0);					
					}
				} catch (CommunicatorException c) {
					checkUserCallback.sendEmptyMessage(0);
				}
				checkUserCallback.sendEmptyMessage(1);					
			}
		};
		mThread.start();
	}

	private Handler checkUserCallback = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what <= 0) {
				AlertDialog.Builder alertbox = new AlertDialog.Builder(mContext);
				alertbox.setMessage(getText(R.string.usernameinuse));

				alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						userNameInput.requestFocus();
					}
				});
				if(dialog.isShowing()) {
					dialog.dismiss();
				}
				alertbox.show();
			} else {
				registerUser(userName, passWord, secretWord, email);
			}
		}
	};
}