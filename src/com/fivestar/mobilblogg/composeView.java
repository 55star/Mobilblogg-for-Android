package com.fivestar.mobilblogg;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class composeView extends Activity implements AdapterView.OnItemSelectedListener {

	private static final String TAG = "Upload";
	private EditText captionText;
	private EditText bodyText;
	private EditText secretText;
	private ImageView image;
	private Spinner rights;
	private ProgressDialog dialog;
	private Thread composeThread;
	private MobilbloggApp app;
	private String[] itemLabels = {"Alla","Alla, inte på förstasidan","Medlemmar","Mina vänner", "Mig"};
	private String[] itemValues = {"","blog", "members","friends", "private"};

	private int selectedRightsItem=0;
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compose);
		captionText = (EditText) findViewById(R.id.captionText);
		bodyText = (EditText) findViewById(R.id.bodyText);
		secretText = (EditText) findViewById(R.id.secretText);
		image = (ImageView) findViewById(R.id.image);
		rights = (Spinner) findViewById(R.id.rights);
		dialog = new ProgressDialog(composeView.this);
		dialog.setMessage(getString(R.string.please_wait));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		app = ((MobilbloggApp)getApplicationContext());

		rights.setOnItemSelectedListener(this);
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemLabels);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		rights.setAdapter(aa);
		
		// load image from sdcard
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap bm = BitmapFactory.decodeFile("/sdcard/Mobilblogg/latest/0.jpg", options);
		image.setImageBitmap(bm); 
	}

	public void onItemSelected(AdapterView<?> parent, View v, int position, long id){
		selectedRightsItem = position;
	}
	
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void composeClickHandler(View view) {

		final String secret = secretText.getText().toString();
		final String caption = captionText.getText().toString();
		final String body = bodyText.getText().toString();
		final String rights = itemValues[selectedRightsItem];
		final Activity activity = this;

		System.out.println("RIGHTS: "+rights);
		
		dialog.show();
		composeThread = new Thread() {
			public void run() {
				String resp = null;
				final String jsonresponse;
				try {
					resp = app.com.doUpload(app.getUserName(), secret, caption, body);
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
								System.out.println("JSON error:" + j.toString());
							}
						} else {
							System.out.println("Upload failure");
							Toast.makeText(activity, "Bloggningen misslyckades", Toast.LENGTH_SHORT).show();
						}
						
						if (uploadStatus > 0) {
							Log.e(TAG, "Upload successful!");
							Toast.makeText(activity, "Inlägget skickat", Toast.LENGTH_SHORT).show();
							Intent myIntent = new Intent(activity, mainMenuView.class);
							startActivityForResult(myIntent, 0);
							finish();
						} else {
							Log.e(TAG, "Upload failure");
							Toast.makeText(activity, "Bloggningen misslyckades, fel hemligt ord?", Toast.LENGTH_LONG).show();
						}
					}
				};
				activity.runOnUiThread(action);
			}
		};
		composeThread.start();
	}
}