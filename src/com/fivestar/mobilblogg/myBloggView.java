/**
 * 
 */
package com.fivestar.mobilblogg;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class myBloggView extends Activity {
	ImageAdapter imgAdapter;
	ListView list;
	ProgressDialog dialog;
	Thread myBloggThread;
	String[] imgs;
	String[] headlines;
	String[] texts;
	MobilbloggApp app;

	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myblogg);
		list=(ListView)findViewById(R.id.list);
		
		dialog = new ProgressDialog(myBloggView.this);
		dialog.setMessage(getString(R.string.please_wait));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

		app = ((MobilbloggApp)getApplicationContext());
		
		final Activity activity = this;

		dialog.show();
		myBloggThread = new Thread() {
			public void run() {

				final String jsonresponse = app.com.getBlogg(app.getUserName());				

				Runnable action = new Runnable() {
					public void run() {
						dialog.dismiss();

						if (jsonresponse != null && jsonresponse.length()>0) {

							try {
								JSONArray json = new JSONArray(jsonresponse);
								imgs = new String[json.length()];
								headlines = new String[json.length()];
								texts = new String[json.length()];
								for(int i=0; i<json.length();i++) {
									imgs[i] = (String)json.getJSONObject(i).get("picture_small");
									headlines[i] = (String)json.getJSONObject(i).get("caption");
									texts[i] = (String)json.getJSONObject(i).get("body");
								}
								fillList(imgs,headlines,texts);
							} catch (JSONException j) {
								System.out.println("JSON error:" + j.toString());
							}
						} else {
							System.out.println("myBlogg failure");
							Toast.makeText(activity, "Hämtningen misslyckades", Toast.LENGTH_SHORT).show();
						}
					}
				};
				activity.runOnUiThread(action);
			}
		};
		myBloggThread.start();
	}
	
	public void fillList(String[] imgs, String[] headlines, String[] texts) {
		System.out.println("fillstart");
        imgAdapter = new ImageAdapter(this, imgs, headlines, texts);
		list.setAdapter(imgAdapter);
		System.out.println("fillend nbr:"+imgAdapter.getCount());
	}

    @Override
    public void onDestroy()
    {
        imgAdapter.imageLoader.stopThread();
        list.setAdapter(null);
        super.onDestroy();
    }
	
	public void onListItemClick(ListView parent, View v, int position, long id) {
	}
	
}