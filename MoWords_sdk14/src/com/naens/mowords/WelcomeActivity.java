package com.naens.mowords;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.naens.dialogs.FindFolderDialog;
import com.naens.preferences.SettingsValues;
import com.naens.tools.ToolUtilities;
import com.naens.tools.ToolUtilities.NoAccountIdEception;

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
		String yesString = getResources().getString(R.string.welcome_dialog_yes);
		findFolderDialog.setPositiveButton(yesString, new DialogInterface.OnClickListener() {

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
				editor.putBoolean(SettingsValues.getMdCKey("eg", 1), true);

				//egyptian
				editor.putString(SettingsValues.getFontSizeKey("eg", 1), "48");
				editor.putString(SettingsValues.getFontNameKey("eg", 1), "Gardiner");
				editor.putBoolean(SettingsValues.getMdCKey("eg", 1), true);
				editor.putString(SettingsValues.getFontSizeKey("eg", 2), "36");
				editor.putString(SettingsValues.getFontNameKey("eg", 2), "MDCTranslitLC");

				//greek
				editor.putString(SettingsValues.getFontSizeKey("gr", 1), "24");
				editor.putString(SettingsValues.getFontNameKey("gr", 1), "VL-PGothic");
				editor.putString(SettingsValues.getFontSizeKey("gr", 2), "24");
				editor.putString(SettingsValues.getFontNameKey("gr", 2), "VL-PGothic");

				//arabic
				editor.putString(SettingsValues.getFontSizeKey("ar", 1), "48");
				editor.putString(SettingsValues.getFontNameKey("ar", 1), "KacstBook");
				editor.putString(SettingsValues.getFontSizeKey("ar", 2), "28");
				editor.putString(SettingsValues.getFontNameKey("ar", 2), "LinuxLibertine");
				editor.putString(SettingsValues.getFontSizeKey("ar", 3), "28");
				editor.putString(SettingsValues.getFontNameKey("ar", 3), "LinuxLibertine");

				//japanese
				editor.putString(SettingsValues.getFontSizeKey("ja", 1), "36");
				editor.putString(SettingsValues.getFontNameKey("ja", 1), "VL-PGothic");
				editor.putString(SettingsValues.getFontSizeKey("ja", 2), "36");
				editor.putString(SettingsValues.getFontNameKey("ja", 2), "VL-PGothic");
				editor.putString(SettingsValues.getFontSizeKey("ja", 3), "24");
				editor.putString(SettingsValues.getFontNameKey("ja", 3), "VL-PGothic");

				//art
				editor.putBoolean(SettingsValues.getOneDirKey("art"), true);

				//accountId -> store TODO:test
				try {
					ToolUtilities.makeAccountId (this);
				} catch (NoAccountIdEception e) {
					Toast.makeText(this, "no account id found...", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		} else {
			throw new IllegalStateException("not a directory: " + path);
		}
		editor.putString(SettingsValues.KEY_PREF_ROOT_DIRECTORY, filesFolder);
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
