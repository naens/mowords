package com.naens.mowords;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.naens.dialogs.FindFolderDialog;
import com.naens.preferences.SettingsActivity;

public class WelcomeActivity extends Activity {

	private String filesFolder = "";
	private String root;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		filesFolder = getResources().getString(R.string.pref_root_directory_default);
		root = Environment.getExternalStorageDirectory().getAbsolutePath();

		setContentView(R.layout.activity_welcome);
	}

	public void changeFolder(View v) {

		final FindFolderDialog findFolderDialog = new FindFolderDialog(this);
		findFolderDialog.setPath(filesFolder);
		findFolderDialog.setRootFolder(root);
		findFolderDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface arg0, int arg1) {
				filesFolder = findFolderDialog.getFolderPath().toString();
				((TextView) findViewById(R.id.welcome_folder_value)).setText(filesFolder);
			}
		});
		findFolderDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
			}
		});
		findFolderDialog.show();
	}

	public void okDone(View v) {
		// save preferences & exit activity
		String path = root + "/" + filesFolder;
		File dir = new File(path);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new IllegalStateException("could not create directory: " + path);
			}
		}
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPref.edit();
		if (dir.isDirectory()) {
			if (dir.listFiles().length == 0) {
				// directory created: copy files
				AssetManager assetManager = this.getAssets();
				String assets[] = null;
				try {
					assets = assetManager.list("wordfiles");
					if (assets.length > 0) {
						for (String string : assets) {
							copyFromAssets(assetManager, "wordfiles", string, path);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				// initial preferences: fonts, font sizes, mdc...
				editor.putString(SettingsActivity.getEncodingKey("eg", 1), "MdC Hieroglyphs");
				editor.putString(SettingsActivity.getFontSizeKey("eg", 1), "48");
				editor.putString(SettingsActivity.getFontNameKey("eg", 1), "Gardiner");
				editor.putString(SettingsActivity.getEncodingKey("eg", 2), "MdC Transliteration");
				editor.putString(SettingsActivity.getFontSizeKey("eg", 2), "36");
				editor.putString(SettingsActivity.getFontNameKey("eg", 2), "MDCTranslitLC");

				editor.putString(SettingsActivity.getFontNameKey("gr", 1), "TheanoDidot-Regular");
				editor.putString(SettingsActivity.getFontSizeKey("gr", 1), "36");
				editor.putString(SettingsActivity.getFontNameKey("gr", 2), "TheanoDidot-Regular");
				editor.putString(SettingsActivity.getFontSizeKey("gr", 2), "36");

				editor.putString(SettingsActivity.getFontSizeKey("ja", 1), "36");
				editor.putString(SettingsActivity.getFontSizeKey("ja", 2), "36");
			}
		} else {
			throw new IllegalStateException("not a directory: " + path);
		}
		editor.putString(SettingsActivity.KEY_PREF_ROOT_DIRECTORY, filesFolder);
		editor.commit();
		finish();
	}

	private void copyFromAssets(AssetManager assetManager, String fromBase, String path, final String targetPath) {
		String toFileName = targetPath + "/" + path;
		String fromFileName = fromBase + "/" + path;
		try {
			String assets[] = assetManager.list(fromFileName);
			if (assets.length == 0) {
				copyFile(assetManager, fromBase, path, targetPath);
			} else {
				File dir = new File(toFileName);
				if (!dir.mkdirs()) {
					Log.i("tag", "could not create dir " + toFileName);
				}
				for (int i = 0; i < assets.length; ++i) {
					Log.i("TAG", "copy " + fromFileName + "," + assets[i] + "," + toFileName);
					copyFromAssets(assetManager, fromFileName, assets[i], toFileName);
				}
			}
		} catch (IOException ex) {
			Log.e("tag", "I/O Exception", ex);
		}
	}

	private void copyFile(AssetManager assetManager, String fromBase, String filename, final String targetPath) {
		String toFileName = targetPath + "/" + filename;
		String fromFileName = fromBase + "/" + filename;
		try {
			Log.i("TAG", "copy " + fromFileName + "->" + toFileName);
			InputStream in = assetManager.open(fromFileName);
			OutputStream out = new FileOutputStream(toFileName);

			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			out.flush();
			out.close();
		} catch (Exception e) {
			Log.e("tag", "Exception in copyFile() of [" + toFileName + "]" + e.toString());
		}
	}

	@Override
	public void onBackPressed() {
        finish();          
        moveTaskToBack(true);
	}
}
