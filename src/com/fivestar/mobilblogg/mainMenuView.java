/**
 * 
 */
package com.fivestar.mobilblogg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class mainMenuView extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainmenu);
	}

	public void mainMenuClickHandler(View view) {
		switch(view.getId()) {
		case R.id.Button01:
			System.out.println("Goto my blogg");
			Intent mbIntent = new Intent(view.getContext(), myBloggView.class);
			startActivityForResult(mbIntent, 0);
			break;
		case R.id.Button02:
			System.out.println("Goto my start page");
			Intent spIntent = new Intent(view.getContext(), StartPageView.class);
			startActivityForResult(spIntent, 0);
			break;
		case R.id.Button03:
			System.out.println("Goto cemera");
			Intent cIntent = new Intent(view.getContext(), CameraView.class);
			startActivityForResult(cIntent, 0);
			break;
		}
	}
}