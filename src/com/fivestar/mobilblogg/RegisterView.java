package com.fivestar.mobilblogg;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
	private EditText userName;
	private EditText passWord;
	private EditText secretWord;
	private EditText email;

	private ProgressDialog dialog;
	private MobilbloggApp app;
	private Context cntx;

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

		userName = (EditText) findViewById(R.id.username);
		passWord = (EditText) findViewById(R.id.password);
		secretWord = (EditText) findViewById(R.id.secret);
		email = (EditText) findViewById(R.id.email);

		app = ((MobilbloggApp)getApplicationContext());
		cntx = this;

		userName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					checkUserName(userName.getText().toString());
				}
			}
		});
	}

	public void registerClickHandler(View view) {
		switch(view.getId()) {
		case R.id.register:
			Utils.log(TAG, "Do the actual registration");
			Toast.makeText(this, getString(R.string.notdone), Toast.LENGTH_SHORT).show();
		}
	}

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
				AlertDialog.Builder alertbox = new AlertDialog.Builder(cntx);
				alertbox.setMessage(getText(R.string.usernameinuse));

				alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						// the button was clicked
						// maybe set focus to username edittext?
					}
				});
				alertbox.show();
			}
		}
	};
}