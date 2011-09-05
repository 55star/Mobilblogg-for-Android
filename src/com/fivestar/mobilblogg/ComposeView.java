package com.fivestar.mobilblogg;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class ComposeView extends Activity implements AdapterView.OnItemSelectedListener {

	private static final String TAG = "Upload";
	private EditText captionText;
	private EditText bodyText;
	private EditText tagsText;
	private ImageView image;
	private Spinner rights;
	private Activity activity;
	private ProgressDialog dialog;
	private Thread composeThread;
	private MobilbloggApp app;
	private String filePath;
	public String caption;
	public String body;
	public String showfor;
	public String secretWord;
	public String tags;
	private String[] itemLabels = new String[5];
	private String[] itemValues = {"","blog", "members","friends", "private"};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compose);
		this.setTitle(R.string.mainMenuUploadPage);

		itemLabels[0] = getString(R.string.all); //"Visa för alla";
		itemLabels[1] = getString(R.string.blog); //"Alla, inte på förstasidan";
		itemLabels[2] = getString(R.string.members); //"Medlemmar";
		itemLabels[3] = getString(R.string.friends); //"Mina vänner";
		itemLabels[4] = getString(R.string.me); // "Mig";

		captionText = (EditText) findViewById(R.id.captionText);
		bodyText = (EditText) findViewById(R.id.bodyText);
		tagsText = (EditText) findViewById(R.id.tags);
		image = (ImageView) findViewById(R.id.image);
		rights = (Spinner) findViewById(R.id.rights);
		activity = this;
		dialog = new ProgressDialog(ComposeView.this);
		dialog.setMessage(getString(R.string.please_wait));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		app = ((MobilbloggApp)getApplicationContext());
		filePath = getIntent().getStringExtra("filepath");
		rights.setOnItemSelectedListener(this);
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemLabels);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		rights.setAdapter(aa);
		rights.setSelection(0);

		// load image from sdcard
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap bm = BitmapFactory.decodeFile(filePath, options);
		image.setImageBitmap(bm); 
	}

	public void promptSecretWord() {
		LayoutInflater li = LayoutInflater.from(activity);
		View view = li.inflate(R.layout.prompt, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(getString(R.string.secretWord));
		builder.setView(view);

		PromptListener pl = new PromptListener(view);
		builder.setPositiveButton(getString(R.string.ok), pl);
		builder.setNegativeButton(getString(R.string.abort), pl);

		AlertDialog ad = builder.create();

		ad.show();
	}


	public void composeClickHandler(View view) {
		if(view.getId() == R.id.abortbutton) {
			Intent mbIntent = new Intent(view.getContext(), MainMenuView.class);
			startActivityForResult(mbIntent, 0);
		} else {
			caption = captionText.getText().toString();
			body = bodyText.getText().toString();
			tags = tagsText.getText().toString();
			showfor = itemValues[rights.getSelectedItemPosition()];

			promptSecretWord();	
		}
	}

	public void uploadPost(final String caption, final String body, final String showfor, final String secret, final String tags, final Activity activity) {
		dialog.show();
		composeThread = new Thread() {
			public void run() {
				String resp = null;
				final String jsonresponse;
				try {
					resp = app.com.doUpload(app.getUserName(), secret, caption, body, showfor, tags, filePath);
				} catch (Throwable e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				app.uploadJson = resp;
				
//				if(resp != null) {
//					jsonresponse = resp;
//				} else {
//	looper.prepare() exception -->	Toast.makeText(activity, getText(R.string.geterror), Toast.LENGTH_SHORT).show();
//					return;
//				}
				uiCallback.sendEmptyMessage(0);
			}
		};
		composeThread.start();
	}

	private Handler uiCallback = new Handler() {
		public void handleMessage(Message msg) {
			int uploadStatus = 0;

			dialog.dismiss();

			if (app.uploadJson != null && app.uploadJson.length()>0) {
				try {
					JSONArray json = new JSONArray(app.uploadJson);
					uploadStatus = json.getJSONObject(0).optInt("imgid");
				} catch (JSONException j) {
					Log.e(TAG,"JSON error:" + j.toString());
				}
			} else {
				Toast.makeText(activity, getText(R.string.geterror), Toast.LENGTH_SHORT).show();
			}

			if (uploadStatus > 0) {
				Toast.makeText(activity, getString(R.string.postuploaded), Toast.LENGTH_SHORT).show();
				Intent myIntent = new Intent(activity, MainMenuView.class);
				startActivityForResult(myIntent, 0);
				finish();
			} else {
				Toast.makeText(activity, getString(R.string.blogfailure), Toast.LENGTH_LONG).show();
			}
		}
	};


public class PromptListener implements android.content.DialogInterface.OnClickListener {
	View promptDialogView = null;

	public PromptListener(View inDialogView) {
		promptDialogView = inDialogView;
	}

	/* Callback from dialog */
	public void onClick(DialogInterface v, int buttonId) {
		if(buttonId == DialogInterface.BUTTON1) {
			/* ok button */
			secretWord = getPromptText();
			uploadPost(caption, body, showfor, secretWord, tags, activity);
		} else {
			/* cancelbutton */
		}
	}

	private String getPromptText() {
		EditText et = (EditText)promptDialogView.findViewById(R.id.editText_prompt);
		return et.getText().toString();
	}
}	

public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
		long arg3) {
	// TODO Auto-generated method stub

}

public void onNothingSelected(AdapterView<?> arg0) {
	// TODO Auto-generated method stub

}
}