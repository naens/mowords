package com.naens.preferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.naens.mowords.R;

public class TypefaceAndSizeDialogPreference extends DialogPreference {

	private EditText sizeEdit;
	private String[] entryValues;
	private RadioGroup radioGroup;
	private String defSize;
	private String defFont;
	private String sizeValue;
	private String fontValue;

	public static final String TYPEFACE_SUFFIX = "_typeface";
	public static final String FONTSIZE_SUFFIX = "_fontSize";
	private String key;

	public TypefaceAndSizeDialogPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setDialogLayoutResource(R.layout.typeface_and_size_dialog_preference);
	}

	public TypefaceAndSizeDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.typeface_and_size_dialog_preference);
	}

	public TypefaceAndSizeDialogPreference(Context context) {
		this(context, null);
	}

	@Override
	protected View onCreateDialogView() {
		View root = super.onCreateDialogView();
		sizeEdit = (EditText) root.findViewById(R.id.tas_dialog_edit);
		radioGroup = (RadioGroup) root.findViewById(R.id.tas_dialog_group);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton checkdRadioButton = (RadioButton) group.findViewById(checkedId);
				if (checkdRadioButton != null) { 
					fontValue = checkdRadioButton.getText().toString();
				/*	for (int i = 0; i < radioGroup.getChildCount(); ++ i) {
						RadioButton rb = (RadioButton) radioGroup.getChildAt(i);
						if (rb.getId() != checkedId) {
							rb.setChecked(false);
						}
					}*/
				}
			}
		});
		return root;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		sizeValue = sizeEdit.getText().toString();
//		Log.i("TAG", "dialog close");
		if (positiveResult && isPersistent()) {
//			Log.i("TAG", "dialog save");
			SharedPreferences sp = getSharedPreferences();
			Editor e = sp.edit();
			e.putString(key + TYPEFACE_SUFFIX, fontValue);
			e.putString(key + FONTSIZE_SUFFIX, sizeValue);
			e.commit();
			setSummary(fontValue + ", " + sizeValue);
		} else {
			Log.i("TAG", "dialog not save");
		}
	}

	@Override
	protected void onBindDialogView(View view) {
		SharedPreferences sp = getSharedPreferences();
		fontValue = sp.getString(key + TYPEFACE_SUFFIX, defFont);
		sizeValue = sp.getString(key + FONTSIZE_SUFFIX, defSize);
		RadioButton cb = null;
		for (String entry : entryValues) {
			RadioButton radioButton = new RadioButton(radioGroup.getContext());
			radioButton.setText(entry);
			if (entry != null && entry.equals(fontValue)) {
				cb = radioButton;
			}
			radioGroup.addView(radioButton);
		}
		if (cb != null) {
			cb.setChecked(true);
		}
		sizeEdit.setText(sizeValue);
	}

	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
		super.onPrepareDialogBuilder(builder);
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getString(index);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		if (isPersistent()) {
			return superState;
		}

		final SavedState state = new SavedState(superState);
		state.f = fontValue;
		state.s = sizeValue;
		return state;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable parcelable) {
		if (parcelable == null || !parcelable.getClass().equals(SavedState.class)) {
			super.onRestoreInstanceState(parcelable);
			return;
		}

		SavedState state = (SavedState) parcelable;
		super.onRestoreInstanceState(state.getSuperState());

		fontValue = state.f;
		sizeValue = state.s;
		sizeEdit.setText(sizeValue);
	}

	public void setEntryValues(String[] values) {
		if (values == null || values.length < 1) {
			this.entryValues = new String[1];
			this.entryValues[0] = "default";
		} else {
			this.entryValues = values;
		}
	}

	public void setDefaultSize(String sz) {
		this.defSize = sz;
	}

	public void setDefaultFont(String font) {
		this.defFont = font;
	}

	private static class SavedState extends BaseSavedState {
		private String f;
		private String s;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		public SavedState(Parcel source) {
			super(source);
			s = source.readString();
			f = source.readString();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeString(f);
			dest.writeString(s);
		}
	}

}
