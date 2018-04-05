package com.naens.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;

import com.naens.dao.ConfigurationDAO;
import com.naens.dao.WordFolderDAO;
import com.naens.dao.androiddao.ConfigurationAndroidPrefDAO;
import com.naens.dao.androiddao.WordFolderAndroidDAO;
import com.naens.model.Word;
import com.naens.model.WordFile;
import com.naens.model.WordFolder;
import com.naens.model.WordPair;
import com.naens.mowords.R;
import com.naens.mowords.WordsActivity;
import com.naens.service.GameLogDbHelper;
import com.naens.tools.SendPost;
import com.naens.tools.SendPost.OnResponseListener;
import com.naens.tools.ToolUtilities;
import com.naens.tools.ToolUtilities.NoAccountIdEception;

public class MainPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	private Activity activity;

	private String siteUrl;

	private String servletCheckByMailUrl;

	private String servletInitUresUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		siteUrl = getResources().getString(R.string.web_site);
		servletCheckByMailUrl = siteUrl + "/checkbymail";
		servletInitUresUrl = siteUrl + "/inituser";
		
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.main_preferences);


		activity = getActivity();
		if (activity == null) {
			throw new RuntimeException("FolderPreferenceFragment: activity null");
		}

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);

		String rootFolder = sp.getString(SettingsValues.KEY_PREF_ROOT_DIRECTORY, null);

		FindFolderDialogPreference rd = (FindFolderDialogPreference) findPreference (SettingsValues.KEY_PREF_ROOT_DIRECTORY);
		rd.setRootFolder(Environment.getExternalStorageDirectory().getAbsolutePath());
//		String rdDef = getResources().getString(R.string.pref_root_directory_default);
//		String rootFolder = sp.getString(KEY_PREF_ROOT_DIRECTORY, rdDef);
		rd.setSummary(rootFolder);
//TODO
//		ListPreference themePreference = (ListPreference) findPreference ("pref_theme");
//		String theme = sp.getString("pref_theme", "Light");
//		if (!sp.contains("pref_theme")) {
//			SharedPreferences.Editor editor = sp.edit();
//			editor.putString("pref_theme", "Light"); 
//			editor.commit();
//		}
//		themePreference.setSummary(theme);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		@SuppressWarnings("unchecked")
		Map <String, Object> prefs = (Map<String, Object>) sharedPreferences.getAll();
		Preference connectionPref = findPreference(key);
		if (connectionPref != null) {
			Object value = prefs.get(key);
			if (key.equals("pref_enable_synchronize")) {

				//choose account
				try {
					ToolUtilities.makeAccountId (getActivity());
				} catch (NoAccountIdEception e) {
					// TODO what if no account id
					// message
					// revert change
					e.printStackTrace();
				}
				final String googleEmail = sharedPreferences.getString(SettingsValues.KEY_GOOGLE_EMAIL, null);
				Log.i("TAG", "google_email=" + googleEmail);

				if (googleEmail != null) {
					final boolean doSync = (Boolean) value;

//					final String checkbymailUrl = "http://192.168.1.7:8080/MoWeb" + servletCheckByMailUrl;
//					final String makeReadyAdress = "http://192.168.1.6:8080/MoWeb/make-ready";
					final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("google_email", googleEmail));
//					nameValuePairs.add(new BasicNameValuePair("ready", "true"));
					Log.i("TAG", "go to " + servletCheckByMailUrl);
					SendPost checkId = new SendPost(activity, servletCheckByMailUrl, nameValuePairs);
					checkId.setOnResponseListener(new OnResponseListener() {

						@Override
						public void onResponseReceived(HttpResponse response) {
							Log.i("TAG", "onResponseReceived");
							Header header = response.getFirstHeader("is_new");
//							Header header = response.getFirstHeader("answer");
//							String answer  = header.getValue();
//							Log.i("TAG", "answer=" + answer);
							if (header == null) {
								Log.e("TAG", "header is null");
								return;
							}
							boolean isNew = Boolean.parseBoolean(header.getValue());
							Log.i("TAG", "doSync=" + doSync);
							Log.i("TAG", "isNew=" + isNew);
							//!! store only if connection to web
							if (doSync) {	//do sync + notify web
								if (isNew) {	//copy to web	+	files
									sendUserData (googleEmail);
								} else {		//sync
									syncData ();
								}
							}
							if (doSync || !isNew) { //send doSync to web TODO
							}
						}
					});
					checkId.execute();
				} else {

				}
			} else if (value instanceof String) {
				String sValue = (String) value;
				connectionPref.setSummary(sValue);
				Log.i("TAG", String.format("preference changed '%s' -> '%s'", key, sValue));
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onPause() {
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	private void sendUserData(String googleEmail) {
		ConfigurationDAO configurationDAO = new ConfigurationAndroidPrefDAO (activity);
		String rootFolder = configurationDAO.getRootFolder ();
		File sdcard = Environment.getExternalStorageDirectory ();
		WordFolderDAO folderDAO = new WordFolderAndroidDAO (sdcard.getAbsolutePath () + "/" + rootFolder, activity);

		try {

			JSONObject json = new JSONObject();

			JSONObject userJson = new JSONObject();
			userJson.put("email", googleEmail);

			json.put("user", userJson);
			Log.v("TAG", "user:" + userJson);

			JSONObject dataJson = new JSONObject();

			List <WordFolder> folders = folderDAO.getFolders();
			for (WordFolder wordFolder : folders) {
				JSONObject folderJson = new JSONObject();
//				folderJson.put("name", wordFolder.getName());
				Log.v("TAG", "folder: " +  wordFolder.getName());

				List<WordFile> wordFiles = wordFolder.getWordFiles();
				for (WordFile wordFile : wordFiles) {
					JSONArray fileJson = new JSONArray();

					List <WordPair> wordPairs = wordFile.getWordPairs();
					for (WordPair wordPair : wordPairs) {
						JSONObject wordPairJson = new JSONObject();
//						Log.v("TAG",  wordPair.toString());

						SparseArray<Word> words = wordPair.getWords ();
						for (int i = 0; i < words.size(); ++ i) {
							int key = words.keyAt(i);
							Word word = words.get(key);

							JSONObject sideJson = new JSONObject ();
							//TODO: relative filepath!!!
							sideJson.put("type", word.isSound() ? "sound" : word.isImage() ? "image" : word.isMdC() ? "mdc" : "text");
							sideJson.put("text", word.getString());

							wordPairJson.put(Integer.toString(word.getSide()), sideJson);
						}
						fileJson.put(wordPairJson);
					}
					folderJson.put(wordFile.getName(), fileJson);
				}
				dataJson.put(wordFolder.getName(), folderJson);
			}
			json.put("data", dataJson);
//			Log.v("TAG", "data:" + dataJson);

			GameLogDbHelper dbHelper = new GameLogDbHelper(activity);
			SQLiteDatabase db = dbHelper.getReadableDatabase();

			String[] columns = { WordsActivity.GAMELOG_COLUMN_ID, WordsActivity.GAMELOG_COLUMN_DATE, WordsActivity.GAMELOG_COLUMN_FOLDER,
					WordsActivity.GAMELOG_COLUMN_FILES, WordsActivity.GAMELOG_COLUMN_INVERSE, WordsActivity.GAMELOG_COLUMN_DONE,
					WordsActivity.GAMELOG_COLUMN_TOTAL, WordsActivity.GAMELOG_COLUMN_GAME_TIME,
					WordsActivity.GAMELOG_COLUMN_SIDE, WordsActivity.GAMELOG_COLUMN_SIDES, };

			String sortOrder = WordsActivity.GAMELOG_COLUMN_DATE + " ASC";

			Cursor cursor = db.query(WordsActivity.GAMELOG_TABLE_NAME, columns, null, null, null, null, sortOrder);
			cursor.moveToFirst();

			//array:*{firstdirection:rec1,seconddirection:rec2}
			JSONArray resultsJson = new JSONArray();
			JSONObject resultsRecord = null;
			JSONObject savedFirstRecord = null;
			while (!cursor.isAfterLast()) {
				long id = cursor.getLong(cursor.getColumnIndexOrThrow(WordsActivity.GAMELOG_COLUMN_ID));
				String folderName = cursor.getString(cursor.getColumnIndexOrThrow(WordsActivity.GAMELOG_COLUMN_FOLDER));
				long date = cursor.getLong(cursor.getColumnIndexOrThrow(WordsActivity.GAMELOG_COLUMN_DATE));
				String files = cursor.getString(cursor.getColumnIndexOrThrow(WordsActivity.GAMELOG_COLUMN_FILES));
				boolean inverse = cursor.getInt(cursor.getColumnIndexOrThrow(WordsActivity.GAMELOG_COLUMN_INVERSE)) > 0;
				int done = cursor.getInt(cursor.getColumnIndexOrThrow(WordsActivity.GAMELOG_COLUMN_DONE));
				int total = cursor.getInt(cursor.getColumnIndexOrThrow(WordsActivity.GAMELOG_COLUMN_TOTAL));
				int gameTime = cursor.getInt(cursor.getColumnIndexOrThrow(WordsActivity.GAMELOG_COLUMN_GAME_TIME));
				int side = cursor.getInt(cursor.getColumnIndexOrThrow(WordsActivity.GAMELOG_COLUMN_SIDE));
				int sides = cursor.getInt(cursor.getColumnIndexOrThrow(WordsActivity.GAMELOG_COLUMN_SIDES));

				if (side == 2) {
					savedFirstRecord = resultsRecord;
					if (savedFirstRecord == null) {
						Log.w("TAG", "savedFirstRecord == null");
					}
				}
				if (resultsRecord == null) {
					Log.i("TAG", "resultsRecord == null");
				}
				resultsRecord = new JSONObject();

				resultsRecord.put (WordsActivity.GAMELOG_COLUMN_ID, id);
				resultsRecord.put (WordsActivity.GAMELOG_COLUMN_FOLDER, folderName);
				resultsRecord.put (WordsActivity.GAMELOG_COLUMN_DATE, date);
				resultsRecord.put (WordsActivity.GAMELOG_COLUMN_FILES, files);
				resultsRecord.put (WordsActivity.GAMELOG_COLUMN_INVERSE, inverse);
				resultsRecord.put (WordsActivity.GAMELOG_COLUMN_DONE, done);
				resultsRecord.put (WordsActivity.GAMELOG_COLUMN_TOTAL, total); 
				resultsRecord.put (WordsActivity.GAMELOG_COLUMN_GAME_TIME, gameTime);

				JSONObject allrecs = new JSONObject();
				if (side == sides) {
					if (side == 1 && sides == 1) {
						allrecs.put("rec1", resultsRecord);
					}
					if (side == 2 && sides == 2) {
						allrecs.put("rec1", savedFirstRecord);
						allrecs.put("rec2", resultsRecord);
					}
					resultsJson.put (allrecs);
				}

				cursor.moveToNext();
			}
			db.close();
			dbHelper.close();
			json.put("results", resultsJson);
//			Log.v("TAG", "results:" + resultsJson);

			String jsonString = json.toString();
//			StringEntity se = new StringEntity(jsonString);
//            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//            String url = "http://192.168.1.7:8080/MoWeb" + servletInitUresUrl;
            SendPost checkId = new SendPost(activity, servletInitUresUrl, jsonString, true);
			Log.i("TAG", "go to " + servletInitUresUrl);
			checkId.setOnResponseListener(new OnResponseListener() {
				@Override
				public void onResponseReceived(HttpResponse response) {
					Log.i("TAG", "data sent.");
				}
			});
			checkId.execute();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void syncData() {
		// TODO Auto-generated method stub
	}

}
