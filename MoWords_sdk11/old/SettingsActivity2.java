package com.naens.preferences;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.naens.dao.WordFolderDAO;
import com.naens.dao.androiddao.WordFolderAndroidDAO;
import com.naens.mowords.R;
import com.naens.model.WordFolder;
import com.naens.tools.FontProvider;

@SuppressWarnings("deprecation")
public class SettingsActivity2 extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_ROOT_DIRECTORY = "pref_root_directory";
    public static final String KEY_PREF_FONTS = "pref_fonts";
    public static final String KEY_PREF_VISIBILITY = "pref_visibility";
    public static final String KEY_PREF_ADD_SIDE = "pref_add_side";
    public static final String KEY_PREF_DEL_SIDE = "pref_del_side";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

/*
        for (String key : new TreeSet<String>(sp.getAll().keySet())) {
        	sp.edit().remove(key);
		}
        sp.edit().clear();
        sp.edit().commit();
*/
        ListView listView = getListView();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        	@Override
        	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        		ListView listView = (ListView) parent;
        		ListAdapter listAdapter = listView.getAdapter();
        		Object obj = listAdapter.getItem(position);
        		if (obj != null && obj instanceof View.OnLongClickListener) {
        			View.OnLongClickListener longListener = (View.OnLongClickListener) obj;
        			return longListener.onLongClick(view);
        		}
        		return false;
        	}
        });

        EditTextPreference rootDirectoryPref = (EditTextPreference) findPreference (KEY_PREF_ROOT_DIRECTORY);
		String rootFolder = sp.getString(KEY_PREF_ROOT_DIRECTORY, "");
        rootDirectoryPref.setSummary(rootFolder);

        PreferenceScreen fontScreen = (PreferenceScreen) findPreference (KEY_PREF_FONTS);
        PreferenceScreen visibilityScreen = (PreferenceScreen) findPreference (KEY_PREF_VISIBILITY);

		File sdcard = Environment.getExternalStorageDirectory ();
		WordFolderDAO wordFolderDAO = new WordFolderAndroidDAO (sdcard.getAbsolutePath () + "/" + rootFolder);
        List <WordFolder> wordFolders = wordFolderDAO.getFolders();

        Set <String> fonts = FontProvider.getFontSet ();
    	String [] typeFaceStrings = new String [fonts.size() + 1];
    	int z = 1;
    	typeFaceStrings [0] = getResources().getString(R.string.pref_fonts_typeface_default);
    	for (String s : fonts) {
			typeFaceStrings [z] = s;
        	++ z;
		}
    	
        for (WordFolder folder : wordFolders) {
        	//create and add screen to fonts screen
        	PreferenceScreen fScreen = getPreferenceManager().createPreferenceScreen(this);
        	fScreen.setKey("pref_folder_" + folder.getName());
        	fScreen.setPersistent(false);
        	fScreen.setTitle(folder.getName());
        	fontScreen.addPreference(fScreen);

        	//fscreen: add	Category Font (sides 1, 2, 3...)		KEYS: pref_folder_[wf]_side[1,2,3]
        	//				visible									KEY : pref_folder_[wf]_visible
        	//				nSides									KEY : pref_folder_[wf]_sides
        	int sides = Integer.parseInt(sp.getString("pref_folder_" + folder.getName() + "_sides", "2"));

        	CheckBoxPreference visibilityPref = new CheckBoxPreference(this);
        	visibilityPref.setKey("pref_folder_" + folder.getName() + "_visible");
        	visibilityPref.setPersistent(true);
        	visibilityPref.setDefaultValue(true);
        	visibilityPref.setTitle(folder.getName());
        	visibilityScreen.addPreference(visibilityPref);

        	PreferenceCategory folderCategory = new PreferenceCategory (this);
        	fScreen.addPreference(folderCategory);
        	folderCategory.setTitle(R.string.pref_folders_category_title);
        	folderCategory.setPersistent(false);
        	folderCategory.setKey("pref_folder_" + folder.getName() + "_fonts");
        	for (int i = 0; i < sides; ++i) {
            	String sideKey = "pref_folder_" + folder.getName() + "_side" + (i + 1);

            	Log.i("TAG", "get side " + sideKey);
            	TypefaceAndSizeDialogPreference tasPreference = new TypefaceAndSizeDialogPreference(this);
            	tasPreference.setTitle("Side " + (i + 1));
            	tasPreference.setOrder(i);
            	tasPreference.setKey(sideKey);
            	tasPreference.setPersistent(true);
            	tasPreference.setEntryValues(typeFaceStrings);
            	String defFont = getResources().getString(R.string.pref_fonts_typeface_default);
            	String defSize = getResources().getString(R.string.pref_fonts_size_default);
            	tasPreference.setDefaultFont(defFont);
            	tasPreference.setDefaultSize(defSize);
    			String fontValue = sp.getString(sideKey + TypefaceAndSizeDialogPreference.TYPEFACE_SUFFIX, defFont);
    			String sizeValue = sp.getString(sideKey + TypefaceAndSizeDialogPreference.FONTSIZE_SUFFIX, defSize);
            	tasPreference.setSummary(fontValue + ", " + sizeValue);
            	folderCategory.addPreference(tasPreference);
        	}

        	AddDelPreference addDelPreference = new AddDelPreference(this);
        	addDelPreference.setAddDelListner(new SideAddDelClickListener (this, sp, folder.getName(), folderCategory, typeFaceStrings));
        	addDelPreference.setOrder(99);
        	fScreen.addPreference(addDelPreference);
        }
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	@SuppressWarnings("unchecked")
		Map <String, Object> prefs = (Map<String, Object>) sharedPreferences.getAll();
    	Preference connectionPref = findPreference(key);
    	if (connectionPref != null) {
	    	Object value = prefs.get(key);
	    	if (value instanceof Boolean) {
	    	}
	    	if (value instanceof String) {
	            connectionPref.setSummary(value.toString());
	            Log.i("TAG", String.format("preference changed '%s' -> '%s'", key, value.toString()));
	    	}
	    	if (value instanceof Integer) {
	            connectionPref.setSummary(value.toString());
	    	}
	    }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
    	super.onPreferenceTreeClick(preferenceScreen, preference);
    	if (preference!=null) {
	    	if (preference instanceof PreferenceScreen) {
	    		PreferenceScreen pScreen = (PreferenceScreen) preference;
	        	if (pScreen.getDialog()!=null) {
	        		Drawable bkg = getWindow().getDecorView().getBackground().getConstantState().newDrawable();
	        		pScreen.getDialog().getWindow().setBackgroundDrawable(bkg);
	        	}
	    	}
    	}
    	return false;
    }

    @SuppressLint("NewApi")
	@Override
    public boolean onNavigateUp() {
        Log.i("TAG", "onNavigateUp");
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN){
        	return super.onNavigateUp();
        } else{
            return false;
        }
    }

    @SuppressLint("NewApi")
	@Override
    public boolean onNavigateUpFromChild(Activity child) {
        Log.i("TAG", "onNavigateUpFromChild");
    	return super.onNavigateUpFromChild(child);
    }
}
