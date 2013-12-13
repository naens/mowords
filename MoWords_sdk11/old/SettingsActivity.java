package com.naens.preferences;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.naens.dao.WordFolderDAO;
import com.naens.dao.androiddao.WordFolderAndroidDAO;
import com.naens.mowords.MainActivity;
import com.naens.mowords.R;
import com.naens.model.WordFolder;
import com.naens.tools.FontProvider;

@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

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

//buid preferences
//		Button btn = new Button(this);
//		Drawable d = getResources().getDrawable(android.R.drawable.ic_delete);
//		btn.setBackgroundDrawable(d);
//		btn.setWidth(d.getMinimumWidth());
//		btn.setHeight(d.getMinimumHeight());
//		btn.setOnLongClickListener(new View.OnLongClickListener() {
//			@Override
//			public boolean onLongClick(View v) {
//				Toast.makeText(SettingsActivity.this, "long click", Toast.LENGTH_SHORT).show();
//				return false;
//			}
//		});
//        ButtonPreference buttonPreference = new ButtonPreference(this, btn);
//        buttonPreference.setTitle("Bn pref");
//        buttonPreference.setPersistent(false);
//        this.getPreferenceScreen().addPreference(buttonPreference);

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

//            EditTextPreference sidesNumberPref =  new EditTextPreference (this);
//            sidesNumberPref.setKey("pref_folder_" + folder.getName() + "_sides");
//            sidesNumberPref.setSummary(Integer.toString(sides));
//            sidesNumberPref.setPersistent(true);
//            sidesNumberPref.setTitle(R.string.pref_folder_size);
//            sidesNumberPref.setDefaultValue("2");
//            sidesNumberPref.getEditText().setKeyListener(DigitsKeyListener.getInstance("123456789"));	// 0 not allowed
//            sidesNumberPref.getEditText().setRawInputType(InputType.TYPE_CLASS_PHONE);
            //limit length
//            InputFilter[] fArray = new InputFilter[1];
//            fArray[0] = new InputFilter.LengthFilter(1);	//length 1
//            sidesNumberPref.getEditText().setFilters(fArray);
//            fScreen.addPreference(sidesNumberPref);

        	for (int i = 0; i < sides; ++i) {
            	PreferenceScreen sideScreen = getPreferenceManager().createPreferenceScreen(this);
            	fScreen.addPreference(sideScreen);

            	addFontParameters (this, folder.getName(), i, sideScreen, typeFaceStrings, sp);
        	}

        	AddDelPreference addDelPreference = new AddDelPreference(this);
        	addDelPreference.setAddDelListner(new SideAddDelClickListener (this, sp, folder.getName(), fScreen, typeFaceStrings));
        	addDelPreference.setOrder(99);
        	fScreen.addPreference(addDelPreference);

//        	Preference addPreference = new Preference(this);
//        	addPreference.setTitle("Add a side");
//        	addPreference.setKey("pref_folder_" + folder.getName() + "_add");
////        	SideAddDelClickListener addCliskListener = new SideAddDelClickListener (this, sp, folder.getName(), 
////        			fontCategory, SideAddDelClickListener.Operation.ADD);
////        	addPreference.setOnPreferenceClickListener(addCliskListener);
//        	fScreen.addPreference(addPreference);
//
//        	Preference delPreference = new Preference(this);
//        	delPreference.setTitle("Delete a side");
//        	delPreference.setKey("pref_folder_" + folder.getName() + "_del");
////        	SideAddDelClickListener delCliskListener = new SideAddDelClickListener (this, sp, folder.getName(), 
////        			fontCategory, SideAddDelClickListener.Operation.DEL);
////        	delPreference.setOnPreferenceClickListener(delCliskListener);
//        	fScreen.addPreference(delPreference);
//            Log.i("TAG", "create");
        }
	}

    public static void addFontParameters(Context context, String folderName, int side, PreferenceScreen sideScreen, String[] typeFaceStrings, SharedPreferences sp) {
    	String fontKey = "pref_folder_" + folderName + "_side" + (side + 1);
    	sideScreen.setTitle("Side " + (side + 1));
    	sideScreen.setOrder(side);
    	sideScreen.setKey(fontKey);
    	sideScreen.setPersistent(false);

//    	ListPreference fontPreference = new ListPreference(context);
//    	fontPreference.setKey(fontKey + "_typeface");
//    	fontPreference.setPersistent(true);
//    	fontPreference.setTitle(R.string.pref_fonts_typeface_title);
//    	fontPreference.setDefaultValue(context.getResources().getString(R.string.pref_fonts_typeface_default));
//		fontPreference.setEntries(typeFaceStrings);
//		fontPreference.setEntryValues(typeFaceStrings);
//		String fontValue = sp.getString (fontKey + "_typeface", "");
//		fontPreference.setSummary(fontValue);
//		sideScreen.addPreference(fontPreference);

    	PreferenceScreen fontPreference = ((PreferenceActivity)context).getPreferenceManager().createPreferenceScreen(context);
    	fontPreference.setKey(fontKey + "_typeface");
    	fontPreference.setPersistent(true);
    	fontPreference.setTitle(R.string.pref_fonts_typeface_title);
//    	fontPreference.setDefaultValue(context.getResources().getString(R.string.pref_fonts_typeface_default));
//		fontPreference.setEntries(typeFaceStrings);
//		fontPreference.setEntryValues(typeFaceStrings);
    	for (String string : typeFaceStrings) {
			Preference pref = new Preference(context);
			pref.setKey(fontKey + "_typeface:" + string);
			pref.setTitle(string);
			fontPreference.addPreference(pref);
			pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				private PreferenceScreen parent;
				private PreferenceScreen parentParent;
				public Preference.OnPreferenceClickListener setParents (PreferenceScreen parentParent, PreferenceScreen parent) {
					this.parent = parent;
					this.parentParent = parentParent;
					return this;
				}
				@Override
				public boolean onPreferenceClick(Preference preference) {
					String key = parent.getKey();
					String value = preference.getTitle().toString();
		            Log.i("TAG", String.format("fontPreference: [%s:%s]", key, value));
					Editor editor = preference.getSharedPreferences().edit();
					editor.putString(key, value);
					editor.commit();
					Toast.makeText(preference.getContext(), value, Toast.LENGTH_SHORT).show ();
					this.parent.getDialog().dismiss();
//					parentParent.setSummary(key + ":" + value);
//					parentParent.getDialog().getWindow().
		            Log.i("TAG", String.format("fontPreference [%s %s] : commit", key, value));
					return true;
				}
			}.setParents(sideScreen, fontPreference));
		}
		String fontValue = sp.getString (fontKey + "_typeface", "");
		fontPreference.setSummary(fontValue);
		sideScreen.addPreference(fontPreference);

//    	SpinnerDialogPreference fontPreference = new SpinnerDialogPreference (context);
//    	fontPreference.setKey(fontKey + "_typeface");
//    	fontPreference.setPersistent(true);
//    	fontPreference.setTitle(R.string.pref_fonts_typeface_title);
//    	fontPreference.setDefaultValue(context.getResources().getString(R.string.pref_fonts_typeface_default));
//		fontPreference.setEntryValues(typeFaceStrings);
//		String fontValue = sp.getString (fontKey + "_typeface", "");
//		fontPreference.setSummary(fontValue);
//		sideScreen.addPreference(fontPreference);

    	EditTextPreference sizePreference = new EditTextPreference(context);
    	sizePreference.setKey(fontKey + "_fontSize");
    	sizePreference.setPersistent(true);
    	sizePreference.setTitle(R.string.pref_fonts_size_title);
    	sizePreference.setDefaultValue(context.getResources().getString(R.string.pref_fonts_size_default));
    	String sizeValue = sp.getString (fontKey + "_fontSize", "");
		sizePreference.setSummary(sizeValue);
		sizePreference.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		sideScreen.addPreference(sizePreference);

    	sideScreen.setSummary(fontValue + ", " + sizeValue);
		//TODO add mdc
	}


	private void loadPreferenceScreen(String key) {
		finish();
		startActivity(getIntent());
		PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference(key);
        Log.i("TAG", "loadPreferenceScreen:" + preferenceScreen.getKey());
        setPreferenceScreen(preferenceScreen);
//		this.
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	@SuppressWarnings("unchecked")
		Map <String, Object> prefs = (Map<String, Object>) sharedPreferences.getAll();
    	Preference connectionPref = findPreference(key);
    	if (connectionPref != null) {
	    	Object value = prefs.get(key);
	    	if (value instanceof Boolean) {
	//            connectionPref.setSummary(sharedPreferences.getBoolean(key, true));
	            Log.i("TAG", String.format("preference changed: '%s' -> '%s'", key, value.toString()));
	    	}
	    	if (value instanceof String) {
	            connectionPref.setSummary(value.toString());
	            if (key.endsWith("_typeface") || key.endsWith("_fontSize")) {
	            	String fontKey = key.replaceAll("_(typeface|fontSize)$", "");
	            	String fontValue = (String) prefs.get(fontKey + "_typeface");
	            	String sizeValue = (String) prefs.get(fontKey + "_fontSize");
	            	PreferenceScreen sideScreen = (PreferenceScreen) findPreference(fontKey);
//	            	if (connectionPref instanceof ListPreference) {
//	            		ListPreference lp = (ListPreference) connectionPref;
////	            		Log.i("TAG", lp.getValue());
////	            		Log.i("TAG", lp.getEntry().toString());
////	            		fontValue = lp.getValue();
//	            		//TODO: Bug: doesn't update
//
//		            	sideScreen.setSummary(fontValue + ", " + sizeValue);
////	            		BaseAdapter userScreenListAdapter = (BaseAdapter)sideScreen.getRootAdapter();
////	            		
////	            		userScreenListAdapter.notifyDataSetChanged();
////	                    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
//	            	} else {
//	            	}
	            	sideScreen.setSummary(fontValue + ", " + sizeValue);
//	            	getListView().invalidate();
//	            	((BaseAdapter)getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
		            Log.i("TAG", String.format("preference changed: '%s' -> '%s'", key, value.toString()));
//	            	this.onContentChanged();
//		            loadPreferenceScreen (fontKey);
//		            super.onPause();
//		            super.onResume();
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
    public void onContentChanged() {
        Log.i("TAG", "onContentChanged");
        super.onContentChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("TAG", "on this.kd");
    	return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
    	getCurrentFocus ().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
		        Log.i("TAG", "on cf.key");
				return false;
			}
		});
    	getCurrentFocus ().setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
		        Log.i("TAG", "on cf.fch");
				
			}
		});
        Log.i("TAG", "onPreferenceTreeClick" + getCurrentFocus ().getClass().toString());
    	super.onPreferenceTreeClick(preferenceScreen, preference);
    	if (preference!=null) {
	    	if (preference instanceof PreferenceScreen) {
	    		PreferenceScreen pScreen = (PreferenceScreen) preference;

	    		pScreen.getDialog().getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
	    	            Log.i("TAG", "touch");
						return false;
					}
				});
	 /*   		.setOnKeyListener(new View.OnKeyListener() {
	    			
	    			@Override
	    			public boolean onKey(View v, int keyCode, KeyEvent event) {
	    		        Log.i("TAG", "on key");
	    				return false;
	    			}
	    		});*/
	    		pScreen.getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
	    			@Override
	    			public void onShow(DialogInterface dialog) {
	    	            Log.i("TAG", "sideScreen: dialog show");
	    			}
	    		});
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
