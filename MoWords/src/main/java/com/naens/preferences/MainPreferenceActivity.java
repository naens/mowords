package com.naens.preferences;

import android.app.Activity;
import android.os.Bundle;

public class MainPreferenceActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
	}

}
