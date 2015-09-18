package com.naens.preferences;

import java.util.Map;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naens.dao.androiddao.WordFileAndroidDAO;
import com.naens.mdctools.InvalidMdCCodeException;
import com.naens.model.Word;
import com.naens.model.WordPair;
import com.naens.mowords.R;
import com.naens.mowords.WordsActivity;
import com.naens.tools.FontProvider;
import com.naens.tools.ToolUtilities;

public class WordPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	public static final String WORD_PREFERENCE_SIDE_KEY = "com.naens.preferences.WordPreferenceFragment.preferenceSide";
	private String folderName;
	private int side;
	private Integer sides;
	private int wordNumber;
	private Parcelable[] gamePairs;
	private PreviewPreference previewPreference;
	private CheckBoxPreference mdcPref;
	private EditTextPreference fontSizePref;
	private ListPreference typefacePref;
	private Activity context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getActivity();
		if (context == null) {
			throw new RuntimeException("FolderPreferenceFragment: context null");
		}

		Intent intent = context.getIntent();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        folderName = intent.getStringExtra(WordsActivity.WORD_FOLDER);
        side = intent.getIntExtra(WordsActivity.WORD_SIDE, -1);
    	wordNumber = intent.getIntExtra(WordsActivity.WORD_GAME_CURRENT_WORD, 0); 
    	gamePairs = intent.getParcelableArrayExtra(WordsActivity.WORD_GAME_WORDS);

    	sides = Integer.parseInt(sp.getString(SettingsValues.getSidesKey (folderName), "-1"));
    	PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(context);
		String sideTitle = getResources().getString(R.string.pref_side_title, side);
    	preferenceScreen.setTitle(sideTitle);

		SharedPreferences.Editor editor = sp.edit();
		editor.putString(WORD_PREFERENCE_SIDE_KEY, Integer.toString(side)); 
		editor.commit();

    	typefacePref = new ListPreference(context) {
    		@Override
    		protected void onBindView(View view) {
   				initVal ();
   				setEnabled(!current.isImage());
   				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
   				if (current.isImage()) {
   			    	setSummary("");
   				} else {	//is text
   					String typeFaceKey = SettingsValues.getFontNameKey(folderName, side);
   					setKey(typeFaceKey);
   				    String typefaceDef = getResources().getString(R.string.pref_side_typeface_default);
   				    String fontName = sp.getString(typeFaceKey, typefaceDef);
   			    	setSummary(fontName);
   			    	setValue(fontName);
   				}
    			super.onBindView(view);
    		}
    	};
    	typefacePref.setKey(SettingsValues.getFontNameKey(folderName, side));
    	typefacePref.setPersistent(true);
		FontProvider.loadFontsFromFiles();
    	typefacePref.setEntries(SettingsValues.getTypeFaceStrings(context));
    	typefacePref.setEntryValues(SettingsValues.getTypeFaceStrings(context));
    	String typefaceDef = context.getResources().getString(R.string.pref_side_typeface_default);
    	String typefaceValue = sp.getString(SettingsValues.getFontNameKey(folderName, side), typefaceDef);
    	typefacePref.setDefaultValue(typefaceDef);
    	typefacePref.setSummary(typefaceValue);
    	typefacePref.setTitle(R.string.pref_side_typeface_title);
    	preferenceScreen.addPreference(typefacePref);

    	fontSizePref = new EditTextPreference(context) {
    		@Override
    		protected void onBindView(View view) {
   				initVal ();
   				setEnabled(!current.isImage());
    			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
    			if (current.isImage()) {
    			   	setSummary("");
    			} else {	//is text
    				String fontSizeKey = SettingsValues.getFontSizeKey(folderName, side);
    				setKey(fontSizeKey);
    			    String fontSizeDef = getResources().getString(R.string.pref_side_font_size_default);
    			    String fontSizeValue = sp.getString(fontSizeKey, fontSizeDef);
    			   	setText(fontSizeValue);
    			   	setSummary(fontSizeValue);
    			}
    			super.onBindView(view);
    		}
    	};
    	fontSizePref.setKey(SettingsValues.getFontSizeKey(folderName, side));
    	fontSizePref.setPersistent(true);
    	fontSizePref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
    	String fontSizeDef = context.getResources().getString(R.string.pref_side_font_size_default);
    	String fontSizeValue = sp.getString(SettingsValues.getFontSizeKey(folderName, side), fontSizeDef);
    	fontSizePref.setDefaultValue(fontSizeDef);
    	fontSizePref.setSummary(fontSizeValue);
    	fontSizePref.setTitle(R.string.pref_side_font_size);
    	preferenceScreen.addPreference(fontSizePref);

    	mdcPref = new CheckBoxPreference(context) {
    		@Override
    		protected void onBindView(View view) {
    			initVal();
    			setEnabled(!current.isImage());
    			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
    			if (current.isImage()) {
    			   	setSummary("");
    			} else {	//is text or mdc
    				String mdcKey = SettingsValues.getMdCKey(folderName, side);
    				setKey(mdcKey);
    			    boolean mdcDef = getResources().getBoolean(R.bool.pref_side_mdc_default);
    			    boolean mdc = sp.getBoolean(mdcKey, mdcDef);
    			   	setChecked(mdc);
    			}
    			super.onBindView(view);
    		}
    	};
    	mdcPref.setKey(SettingsValues.getMdCKey(folderName, side));
    	mdcPref.setPersistent(true);
    	boolean encodingDef = context.getResources().getBoolean(R.bool.pref_side_mdc_default);
    	boolean encodingValue = sp.getBoolean(SettingsValues.getMdCKey(folderName, side), encodingDef);
    	mdcPref.setDefaultValue(encodingValue);
    	mdcPref.setTitle(R.string.pref_side_mdc_title);
    	preferenceScreen.addPreference(mdcPref);

		previewPreference = new PreviewPreference(context);
		previewPreference.setLayoutResource(R.layout.preview_preference_layout);
		String previewTitle = getResources().getString(R.string.pref_side_preview_title);
		previewPreference.setTitle(previewTitle);
    	preferenceScreen.addPreference(previewPreference);
    	
		ListPreference sidePreference = new ListPreference(context);
		sidePreference.setKey(WORD_PREFERENCE_SIDE_KEY);
		sidePreference.setPersistent(true);
		TreeSet <Integer> gameSides = ToolUtilities.findGameSides(gamePairs);
		Integer [] intSides = gameSides.toArray(new Integer[gameSides.size()]);
		String[] sideEntries = new String [gameSides.size()];
		for (int i = 0; i < intSides.length; i++) {
			sideEntries[i] = Integer.toString(intSides[i]);
		}
		sidePreference.setEntries(sideEntries);
		sidePreference.setEntryValues(sideEntries);
		sidePreference.setDefaultValue(Integer.toString(side));
		sidePreference.setSummary(Integer.toString(side));
		sidePreference.setTitle(R.string.pref_side_current_side);
    	preferenceScreen.addPreference(sidePreference);

		setPreferenceScreen(preferenceScreen);

	}

	private void initVal() {
		if (current == null) {
			WordPair wp = (WordPair) gamePairs [wordNumber];

			current = wp.getWordBySideNumber(side);
			if (current == null) {
				current = nextWord();
			}
		}
	}

	private Word nextWord () {
		int i = 0;
		while (i <= gamePairs.length) {
			wordNumber = (wordNumber + 1) % gamePairs.length;
			++ i;
			WordPair wp = (WordPair) gamePairs [wordNumber];
			Word w = wp.getWordBySideNumber(side);
			if (w != null) {
				return w;
			}
		}
		throw new RuntimeException("Word Preference Fragment: no word found");
	}

	private Word current = null;
	private class PreviewPreference extends Preference {
		public TextView previewView;

		public PreviewPreference(Context context) {
			super(context);
	    	setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					current = nextWord();
					updateView();
					typefacePref.setEnabled(!current.isImage());
					fontSizePref.setEnabled(!current.isImage());
					mdcPref.setEnabled(!current.isImage());
					return true;
				}
			});
		}

		@Override
		protected View onCreateView(ViewGroup parent) {
			View parentView = super.onCreateView(parent);
			return parentView;
		}


		@Override
		protected void onBindView(View view) {
			super.onBindView(view);
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
			previewView = (TextView) view.findViewById(R.id.preview_view);
			side = Integer.parseInt(sp.getString(WORD_PREFERENCE_SIDE_KEY, null));
			initVal ();
			if (current == null) {
				current = nextWord();
			}
			updateView();
		}

		private void updateView () {
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
			Typeface typeface = Typeface.DEFAULT;
			int fontSize = 3;
			String fontName = null;
			boolean mdc = false;
			if (!current.isImage()) {

				String typeFaceKey = SettingsValues.getFontNameKey(folderName, side);
			    String typefaceDef = getResources().getString(R.string.pref_side_typeface_default);
			    fontName = sp.getString(typeFaceKey, typefaceDef);
			    typeface = FontProvider.getFont(fontName, context);

				String fontSizeKey = SettingsValues.getFontSizeKey(folderName, side);
			    String fontSizeDef = getResources().getString(R.string.pref_side_font_size_default);
			    String fontSizeValue = sp.getString(fontSizeKey, fontSizeDef);
			    fontSize = Integer.parseInt(fontSizeValue);
				
				String mdcKey = SettingsValues.getMdCKey(folderName, side);
		    	boolean mdcDef = getResources().getBoolean(R.bool.pref_side_mdc_default);
				mdc = sp.getBoolean(mdcKey, mdcDef);
				if (!mdc) {
					previewView.setCompoundDrawables(null, null, null, null);
					previewView.setText(current.getString ());
				    previewView.setTypeface(typeface);
				    previewView.setTextSize(fontSize);
				}
				
			}
			if (current.isImage() || mdc) {
				Bitmap bmp = current.getImage();
				if (bmp == null || mdc) {
					if (mdc) {
						current.setFontName(fontName);
						current.setFontSize(fontSize);
//						current.setMdC(true);
					}
					try {
						new WordFileAndroidDAO(context).loadImage(current, getResources().getColor(android.R.color.primary_text_light));
					} catch (NotFoundException e) {
						e.printStackTrace();
					} catch (InvalidMdCCodeException e) {
						e.printStackTrace();
					}
					bmp = current.getImage();
				}
				if (bmp == null && mdc) {
					previewView.setCompoundDrawables(null, null, null, null);
					String errorMdCMessage = getResources().getString(R.string.pref_side_error_mdc, current.getString());
					previewView.setText(errorMdCMessage);
				    previewView.setTypeface(typeface);
				    previewView.setTextSize(fontSize);
				} else {
					Drawable d = new BitmapDrawable(null, bmp);
					d.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
					previewView.setCompoundDrawables(d, null, null, null);
					previewView.setText("");
				}
			}
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
		@SuppressWarnings("unchecked")
		Map <String, Object> prefs = (Map<String, Object>) sp.getAll();
    	Preference connectionPref = findPreference(key);
    	if (connectionPref != null) {
    		if (sides == null || side > sides) {
    			SharedPreferences.Editor editor = sp.edit();
    			editor.putString(SettingsValues.getSidesKey(folderName), Integer.toString(side)); 
    			editor.commit();
    		}
	    	Object value = prefs.get(key);
	    	if (value instanceof String) {
	            connectionPref.setSummary((String) value);
	    	}
	    	if (value instanceof Integer) {
	            connectionPref.setSummary(value.toString());
	    	}
	    	if (key.equals(WORD_PREFERENCE_SIDE_KEY)) {
			    side = Integer.parseInt(sp.getString(WORD_PREFERENCE_SIDE_KEY, null));
			    current = null;	//current not valid
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

	public Parcelable[] getPairs() {
		return gamePairs;
	}

	public String getFolderName() {
		return folderName;
	}

}
