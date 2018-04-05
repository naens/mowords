package com.naens.preferences;

import android.app.Activity;
import android.os.Bundle;

public class WordPreferenceActivity extends Activity {

	private WordPreferenceFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragment = new WordPreferenceFragment();
		getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
