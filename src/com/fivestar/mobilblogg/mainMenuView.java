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
			Intent myIntent = new Intent(view.getContext(), myBloggView.class);
			startActivityForResult(myIntent, 0);
			break;
		case R.id.Button02:
			System.out.println("Goto my start page");
			break;
		case R.id.Button03:
			System.out.println("Goto start page");
			break;
		}
	}
}