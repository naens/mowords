package com.naens.preferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.naens.mowords.R;

public class EditDialogPreference extends DialogPreference {

	private EditText editText;
	private Button button;
	private String value;
	private boolean closeOk = false;

	public EditDialogPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setDialogLayoutResource(R.layout.edit_dialog_preference);
	}

	public EditDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.edit_dialog_preference);
	}

	public EditDialogPreference(Context context) {
		this(context, null);
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		if (restorePersistedValue) {
			value = this.getPersistedString("---");
		} else {
			value = (String) defaultValue;
			persistString(value);
		}
	}

	@Override
	protected View onCreateDialogView() {
		View root = super.onCreateDialogView();
		editText = (EditText) root.findViewById(R.id.edit_dialog_edit);
		button = (Button) root.findViewById(R.id.edit_dialog_button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeOk = true;
				getDialog().dismiss();
			}
		});
		return root;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		value = editText.getText().toString();
		if (closeOk) {
			persistString(value);
		}
	}

	@Override
	protected void onBindDialogView(View view) {
		editText.setText(value);
		button.setText("OK...");
	}

	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
		builder.setPositiveButton(null, null);
		builder.setNegativeButton(null, null);
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
		state.val = value;
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

		value = state.val;
		editText.setText(value);
	}

	private static class SavedState extends BaseSavedState {
		private String val;

	    public SavedState(Parcelable superState) {
	        super(superState);
	    }

	    public SavedState(Parcel source) {
	        super(source);
	        val = source.readString();
	    }

	    @Override
	    public void writeToParcel(Parcel dest, int flags) {
	        super.writeToParcel(dest, flags);
	        dest.writeString(val);
	    }
	}

}
