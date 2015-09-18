package com.naens.preferences;

import java.util.Set;

import android.content.Context;

import com.naens.mowords.R;
import com.naens.tools.FontProvider;

public class SettingsValues {

    public static final String SIDE_INTERFIX = "_side";
	public static final String SIDES_SUFFIX = "_sides";
	public static final String KEY_PREF_FONTS = "pref_fonts";
    public static final String KEY_PREF_ADD_SIDE = "pref_add_side";
    public static final String KEY_PREF_DEL_SIDE = "pref_del_side";

	public static final String FOLDER_PREFIX = "pref_folder_";
	public static final String TYPEFACE_SUFFIX = "_typeface";
	public static final String FONTSIZE_SUFFIX = "_fontSize";
	public static final String MDC_SUFFIX = "_mdc";
	public static final String VISIBLE_SUFFIX = "_visible";
	public static final String ONE_DIR_SUFFIX = "_one_dir";
	public static final String LIMITS_SUFFIX = "_limits";
	public static final String KEY_PREF_ROOT_DIRECTORY = "pref_root_dir";
	public static final String DEF_LIMIT_SUFFIX = "_def_limit";
	private static final String DRAW_SUFFIX = "_showdraw";
	private static final String DRAW_HEIGHT_SUFFIX = "_drawheight";
	public static final String KEY_GOOGLE_EMAIL = "google_email";
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

	public static String getShowDrawKey(String folderName) {
		return FOLDER_PREFIX + folderName + DRAW_SUFFIX;
	}

	public static String getDrawHeightKey(String folderName) {
		return FOLDER_PREFIX + folderName + DRAW_HEIGHT_SUFFIX;
	}

	public static String getFontSizeKey(String folderName, int side) {
		return FOLDER_PREFIX + folderName + SIDE_INTERFIX + side + FONTSIZE_SUFFIX;
	}

	public static String getFontNameKey(String folderName, int side) {
		return FOLDER_PREFIX + folderName + SIDE_INTERFIX + side + TYPEFACE_SUFFIX;
	}

	public static String getMdCKey(String folderName, int side) {
		return FOLDER_PREFIX + folderName + SIDE_INTERFIX + side + MDC_SUFFIX;
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
