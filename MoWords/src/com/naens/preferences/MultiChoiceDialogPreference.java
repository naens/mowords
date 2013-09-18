package com.naens.preferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.naens.mowords.R;

public class MultiChoiceDialogPreference extends DialogPreference {

	private String[] entryValues;
	private LinearLayout layout;
	private Set <String> checkedValues;
	private Map <String, CheckBox> checkBoxMap= new HashMap <String, CheckBox>();

	public MultiChoiceDialogPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setDialogLayoutResource(R.layout.multichoice_dialog_preference);
	}

	public MultiChoiceDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.multichoice_dialog_preference);
	}

	public MultiChoiceDialogPreference(Context context) {
		this(context, null);
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		String value = "";
		if (restorePersistedValue) {
			value = this.getPersistedString("---");
		} else {
			value = (String) defaultValue;
			persistString(value);
		}
		checkedValues = new TreeSet <String> ();
		String [] tmp = value.split("[, ]+");
		for (String s : tmp) {
			if (!s.trim().equals("")) {
				checkedValues.add(s);
			}
		}
	}

	@Override
	protected View onCreateDialogView() {
		View root = super.onCreateDialogView();
		layout = (LinearLayout) root.findViewById(R.id.multichoice_layout);
		return root;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		String value = "";
		for (String entry : entryValues) {
			if (checkedValues.contains (entry)) {
				value += entry + ", ";
			}
		}
		if (positiveResult) {
			persistString(value.substring(0, value.length() - 2));
		}
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindView(view);
		for (String entry : entryValues) {
			CheckBox cb = new CheckBox(getContext());
			checkBoxMap.put(entry, cb);
			cb.setText(entry);
			cb.setChecked(checkedValues.contains(entry));
			cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						checkedValues.add((String) buttonView.getText());
					} else {
						checkedValues.remove((String) buttonView.getText());
					}
					((AlertDialog)getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!checkedValues.isEmpty());
				}
			});
			layout.addView(cb);
		}
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
		state.ev = entryValues;
		boolean [] checked = new boolean [entryValues.length];
		for (int i = 0; i < entryValues.length; ++i) {
			state.checked [i] = checkedValues.contains(entryValues [i]);
		}
		state.checked = checked;
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

		entryValues = state.ev;
		for (int i = 0; i < entryValues.length; ++i) {
			String value = entryValues [i]; 
			if (state.checked [i]) {
				checkedValues.add(value);
				checkBoxMap.get(value).setChecked(true);
			}
		}
	}

	private static class SavedState extends BaseSavedState {
		private String[] ev;
		private boolean [] checked;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		public SavedState(Parcel source) {
			super(source);
			int size = source.readInt();
			checked = new boolean [size];
			ev = new String [size];
			source.readBooleanArray(checked);
			source.readStringArray(ev);
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeStringArray(ev);
			dest.writeBooleanArray(checked);
			dest.writeInt(ev.length);
		}
	}

	public void setEntryValues(String[] values) {
		if (values == null || values.length < 1) {
			throw new RuntimeException("multi choice values = " + values);
		} else {
			this.entryValues = values;
		}
	}

}
