package com.fivestar.mobilblogg;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
	private String[] itemLabels = {"Alla","Alla, inte på förstasidan","Medlemmar","Mina vänner", "Mig"};
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
		
		captionText = (EditText) findViewById(R.id.captionText);
		bodyText = (EditText) findViewById(R.id.bodyText);
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
		builder.setTitle("Hemligt ord");
		builder.setView(view);
		
		PromptListener pl = new PromptListener(view);
		builder.setPositiveButton("OK", pl);
		builder.setNegativeButton("Avbryt", pl);
		
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
			showfor = itemValues[rights.getSelectedItemPosition()];

			promptSecretWord();	
		}
	}

	public void uploadPost(final String caption, final String body, final String showfor, final String secret, final Activity activity) {
		dialog.show();
		composeThread = new Thread() {
			public void run() {
				String resp = null;
				final String jsonresponse;
				try {
					resp = app.com.doUpload(app.getUserName(), secret, caption, body, showfor, filePath);
				} catch (Throwable e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(resp != null) {
					jsonresponse = resp;
				} else {
					Toast.makeText(activity, "Bloggningen misslyckades", Toast.LENGTH_SHORT).show();
					return;
				}

				Runnable action = new Runnable() {
					public void run() {
						int uploadStatus = 0;

						dialog.dismiss();

						if (jsonresponse != null && jsonresponse.length()>0) {
							try {
								JSONArray json = new JSONArray(jsonresponse);
								uploadStatus = json.getJSONObject(0).optInt("imgid");

							} catch (JSONException j) {
								Log.e(TAG,"JSON error:" + j.toString());
							}
						} else {
							Toast.makeText(activity, "Bloggningen misslyckades", Toast.LENGTH_SHORT).show();
						}

						if (uploadStatus > 0) {
							Toast.makeText(activity, "Inlägget skickat", Toast.LENGTH_SHORT).show();
							Intent myIntent = new Intent(activity, MainMenuView.class);
							startActivityForResult(myIntent, 0);
							finish();
						} else {
							Toast.makeText(activity, "Bloggningen misslyckades, fel hemligt ord?", Toast.LENGTH_LONG).show();
						}
					}
				};
				activity.runOnUiThread(action);
			}
		};
		composeThread.start();
	}

	public class PromptListener implements android.content.DialogInterface.OnClickListener {
		private static final String TAG = "PromptListener";
		private String promptReply = null;
		View promptDialogView = null;
		
		public PromptListener(View inDialogView) {
			promptDialogView = inDialogView;
		}
		
		/* Callback from dialog */
		public void onClick(DialogInterface v, int buttonId) {
			if(buttonId == DialogInterface.BUTTON1) {
				/* ok button */
				secretWord = getPromptText();
				uploadPost(caption, body, showfor, secretWord, activity);
			} else {
				/* cancelbutton */
				promptReply = null;
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