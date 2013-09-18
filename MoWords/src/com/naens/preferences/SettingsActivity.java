package com.naens.preferences;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.naens.dao.WordFolderDAO;
import com.naens.dao.androiddao.WordFolderAndroidDAO;
import com.naens.model.WordFolder;
import com.naens.mowords.R;
import com.naens.tools.FontProvider;

@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    public static final String SIDE_INTERFIX = "_side";
	public static final String SIDES_SUFFIX = "_sides";
	public static final String KEY_PREF_FONTS = "pref_fonts";
    public static final String KEY_PREF_ADD_SIDE = "pref_add_side";
    public static final String KEY_PREF_DEL_SIDE = "pref_del_side";

	public static final String FOLDER_PREFIX = "pref_folder_";
	public static final String TYPEFACE_SUFFIX = "_typeface";
	public static final String FONTSIZE_SUFFIX = "_fontSize";
	public static final String ENCODING_SUFFIX = "_encoding";
	public static final String VISIBLE_SUFFIX = "_visible";
	public static final String ONE_DIR_SUFFIX = "_one_dir";
	public static final String LIMITS_SUFFIX = "_limits";
	public static final String KEY_PREF_ROOT_DIRECTORY = "pref_root_dir";
	public static final String DEF_LIMIT_SUFFIX = "_def_limit";
	private static SharedPreferences sp;
	private static String [] typeFaceStrings;

	//static key getters
	public static String getVisibleKey (String folderName) {
		return FOLDER_PREFIX + folderName + VISIBLE_SUFFIX;
	}

	public static String getOneDirKey(String folderName) {
		return FOLDER_PREFIX + folderName + ONE_DIR_SUFFIX;
	}

	public static String getLimitsKey(String folderName) {
		return FOLDER_PREFIX + folderName + LIMITS_SUFFIX;
	}

	public static String getDefLimitKey(String folderName) {
		return FOLDER_PREFIX + folderName + DEF_LIMIT_SUFFIX;
	}

	public static String getSidesKey(String folderName) {
		return FOLDER_PREFIX + folderName + SIDES_SUFFIX;
	}

	public static String getFontSizeKey(String folderName, int side) {
		return FOLDER_PREFIX + folderName + SIDE_INTERFIX + side + FONTSIZE_SUFFIX;
	}

	public static String getFontNameKey(String folderName, int side) {
		return FOLDER_PREFIX + folderName + SIDE_INTERFIX + side + TYPEFACE_SUFFIX;
	}

	public static String getEncodingKey(String folderName, int side) {
		return FOLDER_PREFIX + folderName + SIDE_INTERFIX + side + ENCODING_SUFFIX;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
        sp = getPreferenceScreen().getSharedPreferences();

    	String rootFolder = sp.getString(KEY_PREF_ROOT_DIRECTORY, null);
//    	rootFolder = null;
//    	if (rootFolder == null) {
//            FindFolderDialogPreference rdi = (FindFolderDialogPreference) findPreference (KEY_PREF_ROOT_DIRECTORY);
//            rdi.setRootFolder(Environment.getExternalStorageDirectory().getAbsolutePath());
//            rdi.showDialog(null);
////			finish ();
//            return;
//    	}

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

        FindFolderDialogPreference rd = (FindFolderDialogPreference) findPreference (KEY_PREF_ROOT_DIRECTORY);
        rd.setRootFolder(Environment.getExternalStorageDirectory().getAbsolutePath());
//    	String rdDef = getResources().getString(R.string.pref_root_directory_default);
//    	String rootFolder = sp.getString(KEY_PREF_ROOT_DIRECTORY, rdDef);
    	rd.setSummary(rootFolder);

    	ListPreference themePreference = (ListPreference) findPreference ("pref_theme");
    	String theme = sp.getString("pref_theme", "Dark");
    	if (!sp.contains("pref_theme")) {
			SharedPreferences.Editor editor = sp.edit();
			editor.putString("pref_theme", "Dark"); 
			editor.commit();
    	}
    	themePreference.setSummary(theme);

        PreferenceScreen folderListScreen = (PreferenceScreen) findPreference (KEY_PREF_FONTS);

    	if (theme.equals("Light")) {
    		setTheme(android.R.style.Theme_Light);
    		getListView().setBackgroundResource(android.R.color.background_light);
    		
    	}
    	if (theme.equals("Dark")) {
    		setTheme(android.R.style.Theme);
    		getListView().setBackgroundResource(android.R.color.background_dark);
    	}


		File sdcard = Environment.getExternalStorageDirectory ();
		WordFolderDAO wordFolderDAO = new WordFolderAndroidDAO (sdcard.getAbsolutePath () + "/" + rootFolder);
        List <WordFolder> wordFolders = wordFolderDAO.getFolders();

        for (WordFolder folder : wordFolders) {
        	String folderName = folder.getName();
        	//create and add screen to fonts screen
        	PreferenceScreen folderScreen = getPreferenceManager().createPreferenceScreen(this);
        	folderScreen.setKey(FOLDER_PREFIX + folderName);
        	folderScreen.setPersistent(false);
        	folderScreen.setTitle(folderName);
        	folderListScreen.addPreference(folderScreen);

        	//fscreen: add	Category Font (sides 1, 2, 3...)		KEYS: pref_folder_[wf]_side[1,2,3]
        	//				visible									KEY : pref_folder_[wf]_visible
        	//				nSides									KEY : pref_folder_[wf]_sides
        	int sides = Integer.parseInt(sp.getString(getSidesKey (folderName), "2"));

        	PreferenceScreen sideScreen = getPreferenceManager().createPreferenceScreen(this);
        	sideScreen.setTitle(R.string.pref_folder_sides_title);
        	sideScreen.setSummary(R.string.pref_folder_sides_summary);
        	sideScreen.setPersistent(false);
        	folderScreen.addPreference(sideScreen);
        	for (int i = 0; i < sides; ++i) {
            	fillSidePreference (this, sideScreen, i, folderName);
        	}
        	AddDelPreference addDelPreference = new AddDelPreference(this);
        	addDelPreference.setAddDelListner(new SideAddDelClickListener (this, sp, folder.getName(), sideScreen));
        	addDelPreference.setOrder(99);
        	sideScreen.addPreference(addDelPreference);

        	CheckBoxPreference oneDirPref = new CheckBoxPreference(this);
        	oneDirPref.setKey(getOneDirKey(folderName));
        	oneDirPref.setPersistent(true);
        	oneDirPref.setDefaultValue(Boolean.parseBoolean(getResources().getString(R.string.pref_folder_one_direction_default)));
        	oneDirPref.setTitle(R.string.pref_folder_one_direction);
        	folderScreen.addPreference(oneDirPref);

        	MultiChoiceDialogPreference limitsPref = new MultiChoiceDialogPreference(this);
        	limitsPref.setKey(getLimitsKey (folderName));
        	limitsPref.setPersistent(true);
        	String limitsDef = getResources().getString(R.string.pref_folder_limits_default);
           	String limDefVal = "";	//guarantee at least one value!
           	for (String lim : limitsDef.split ("[, ]+")) {
           		if (lim.length() > 0) {
           			limDefVal += lim + ", ";
           		}
			}
           	String[] allLimits = getResources().getString(R.string.pref_folder_limits_values).split("[, ]+");
			limitsPref.setEntryValues(allLimits);
           	limitsPref.setDefaultValue(limDefVal);
           	String limitsValue = sp.getString(getLimitsKey (folderName), limDefVal.subSequence(0, limDefVal.length() - 2).toString());
           	limitsPref.setSummary(limitsValue);
        	limitsPref.setTitle(R.string.pref_folder_limits_title);
        	folderScreen.addPreference(limitsPref);

        	ListPreference defLimitPref = new ListPreference(this);
        	defLimitPref.setKey(getDefLimitKey (folderName));
        	defLimitPref.setPersistent(true);
        	String [] lims = (String[]) limitsValue.split ("[, ]+");
        	defLimitPref.setEntries(lims);
        	defLimitPref.setEntryValues(lims);
        	String defLimitDef = lims.length == 0 ? "---" : lims [0];
        	defLimitPref.setDefaultValue(defLimitDef);
        	//value in lims!
        	String defLimitValue = sp.getString(getDefLimitKey (folderName), defLimitDef);
        	if (!Arrays.asList(lims).contains(defLimitValue)) {
        		defLimitValue = defLimitDef;
        		defLimitPref.setValue(defLimitDef);
        	}
        	defLimitPref.setSummary(defLimitValue);

        	defLimitPref.setTitle(R.string.pref_folder_default_limit_title);
        	folderScreen.addPreference(defLimitPref);

        	CheckBoxPreference visiblePref = new CheckBoxPreference(this);
        	visiblePref.setKey(getVisibleKey (folderName));
        	visiblePref.setPersistent(true);
        	visiblePref.setDefaultValue(Boolean.parseBoolean(getResources().getString(R.string.pref_folder_visible_default)));
        	visiblePref.setTitle(R.string.pref_folder_visible_title);
        	folderScreen.addPreference(visiblePref);
        }
	}

	static void fillSidePreference (Context context, PreferenceGroup parent, int sideOrder, String folderName) {
		int side = sideOrder + 1;
    	PreferenceCategory sideCategory = new PreferenceCategory(context);
    	sideCategory.setTitle("Side " + side);
    	sideCategory.setOrder(sideOrder);
    	sideCategory.setPersistent(false);
    	parent.addPreference(sideCategory);

    	ListPreference typefacePref = new ListPreference(context);
    	typefacePref.setKey(getFontNameKey(folderName, side));
    	typefacePref.setPersistent(true);
    	typefacePref.setEntries(getTypeFaceStrings(context));
    	typefacePref.setEntryValues(getTypeFaceStrings(context));
    	String typefaceDef = context.getResources().getString(R.string.pref_side_typeface_default);
    	String typefaceValue = sp.getString(getFontNameKey(folderName, side), typefaceDef);
    	typefacePref.setDefaultValue(typefaceDef);
    	typefacePref.setSummary(typefaceValue);
    	typefacePref.setTitle(R.string.pref_side_typeface);
    	sideCategory.addPreference(typefacePref);

    	EditTextPreference fontSizePref = new EditTextPreference(context);
    	fontSizePref.setKey(getFontSizeKey(folderName, side));
    	fontSizePref.setPersistent(true);
    	fontSizePref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
    	String fontSizeDef = context.getResources().getString(R.string.pref_side_font_size_default);
    	String fontSizeValue = sp.getString(getFontSizeKey(folderName, side), fontSizeDef);
    	fontSizePref.setDefaultValue(fontSizeDef);
    	fontSizePref.setSummary(fontSizeValue);
    	fontSizePref.setTitle(R.string.pref_side_font_size);
    	sideCategory.addPreference(fontSizePref);

    	ListPreference encodingPref = new ListPreference(context);
    	encodingPref.setKey(getEncodingKey(folderName, side));
    	encodingPref.setPersistent(true);
    	encodingPref.setEntries(R.array.pref_side_encoding_values);
    	encodingPref.setEntryValues(R.array.pref_side_encoding_values);
    	String encodingDef = context.getResources().getString(R.string.pref_side_encoding_default);
    	String encodingValue = sp.getString(getEncodingKey(folderName, side), encodingDef);
    	encodingPref.setDefaultValue(encodingDef);
    	encodingPref.setSummary(encodingValue);
    	encodingPref.setTitle(R.string.pref_side_encoding);
    	sideCategory.addPreference(encodingPref);
	}
		
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	@SuppressWarnings("unchecked")
		Map <String, Object> prefs = (Map<String, Object>) sharedPreferences.getAll();
    	Preference connectionPref = findPreference(key);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
    	if (connectionPref != null) {
	    	Object value = prefs.get(key);
	    	if (value instanceof Boolean) {
	    	}
	    	if (value instanceof String) {
	    		String sValue = (String) value;
	            connectionPref.setSummary(sValue);
	            Log.i("TAG", String.format("preference changed '%s' -> '%s'", key, sValue));
	            if (key.matches("^pref_folder_.+_limits$")) {
	            	String folderKey = key.replaceFirst("_limits$", "");
	            	String defKey = folderKey + "_def_limit";
	            	ListPreference defPref = (ListPreference) findPreference(defKey);
	            	if (sp.contains(defKey)) {
	            		String defValue = sp.getString(defKey, "-=-=-");
	                	String [] lims = (String[]) sValue.split ("[, ]+");
	            		defPref.setEntries(lims);
	            		defPref.setEntryValues(lims);
	    	            if (!Arrays.asList(lims).contains(defValue)) {
	    	            	String newDefLimit = lims.length == 0 ? "---" : lims [0];
	    	            	defPref.setValue(newDefLimit);
	    	            }
	            	}
	            }
	        	if (value.equals("Light")) {
	        		finish();
	        		startActivity(getIntent());
	        	}
	        	if (value.equals("Dark")) {
	        		finish();
	        		startActivity(getIntent());
	        	}
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

	public static CharSequence[] getTypeFaceStrings(Context context) {
		if (typeFaceStrings == null) {
	        Set <String> fonts = FontProvider.getFontSet ();
	        fonts.add(FontProvider.MDC_HIEROGLIPH_FONT);
	        fonts.add(FontProvider.MDC_TRANSLITERATION_FONT);
	        typeFaceStrings = new String [fonts.size() + 1];
	    	int z = 1;
	    	typeFaceStrings [0] = context.getResources().getString(R.string.pref_side_typeface_default);
	    	for (String s : fonts) {
				typeFaceStrings [z] = s;
	        	++ z;
			}
		}
    	return typeFaceStrings;
	}

}
