package com.naens.preferences;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.text.Html;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.naens.mowords.R;
import com.naens.tools.CaseUnsensitiveComparator;

public class FindFolderDialogPreference extends DialogPreference {

	private String rootFolder = "/";
	private List<String> path;
	private LinearLayout layout;
	private ScrollView scrollView;
	private String selectedFolder = "";
	private Map<String, TextView> editMap = new TreeMap<String, TextView>(new CaseUnsensitiveComparator());
	private Button okButton;
	private Button newButton;
	private Button renameButton;
	private String pathString;
	private TextView statusTextView;
	private Context context;

	public FindFolderDialogPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		setDialogLayoutResource(R.layout.find_folder_dialog_preference);
	}

	@Override
	public void showDialog(Bundle state) {
		super.showDialog(state);
	}

	public FindFolderDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setDialogLayoutResource(R.layout.find_folder_dialog_preference);
	}

	public FindFolderDialogPreference(Context context) {
		this(context, null);
		this.context = context;
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		if (restorePersistedValue) {
			pathString = this.getPersistedString("");
		} else {
			pathString = (String) defaultValue;
			persistString(pathString);
		}
	}

	@Override
	protected View onCreateDialogView() {
		String[] pathFolders = pathString.split("/");
		path = new LinkedList<String>();
		for (String pathF : pathFolders) {
			if (!pathF.trim().equals("")) {
				path.add(pathF);
			}
		}
		if (path.size() >= 1) {
			selectedFolder = path.get(path.size() - 1);
			path.remove(path.size() - 1);
		} else {
			selectedFolder = "";
			okButton.setEnabled(false);
		}

		View root = super.onCreateDialogView();
		layout = (LinearLayout) root.findViewById(R.id.find_folder_view);
		scrollView = (ScrollView) root.findViewById(R.id.find_folder_scroll);
		statusTextView = (TextView) root.findViewById(R.id.find_folder_status);

		newButton = (Button) root.findViewById(R.id.find_folder_new_folder);
		newButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertbox = new AlertDialog.Builder(FindFolderDialogPreference.this.getDialog().getContext());
				alertbox.setTitle(context.getResources().getString(R.string.find_folder_new_directory_alert_box));

				final EditText input = new EditText(FindFolderDialogPreference.this.getDialog().getContext());
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(150,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(8, 8, 8, 8);
				input.setLayoutParams(lp);
				input.setBackgroundResource(android.R.drawable.editbox_background_normal);
				input.setCursorVisible(true);
				input.setInputType(InputType.TYPE_CLASS_TEXT);
				alertbox.setView(input);

				alertbox.setPositiveButton(context.getResources().getString(R.string.find_folder_new_directory_ok_button),
						new OnClickListener() { 
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String newName = input.getText().toString();
						TextView edit = getNewEdit();
						edit.setText(newName);

						String dirName = rootFolder + "/" + getPathString();
						File newF = new File(dirName, newName);
						if (newName.equals("")) {
							Toast.makeText(getContext(), context.getResources().getString(R.string.find_folder_folder_name_empty),
									Toast.LENGTH_SHORT).show();
						} else if (newF.exists()) {
							Toast.makeText(getContext(),
									context.getResources().getString(R.string.find_folder_folder_already_exists, newF.getName()),
									Toast.LENGTH_SHORT).show();
						} else if (!newName.equals(selectedFolder)){
							if (newF.mkdir()) {
								selectedFolder = newName;
							} else {
								Toast.makeText(getContext(),
										context.getResources().getString(R.string.find_folder_folder_not_created),
										Toast.LENGTH_SHORT).show();
							}
						}
						editMap.clear();
						layout.removeAllViews();
						displayCurrentFolder();
					}
				});
				alertbox.setNegativeButton(context.getResources().getString(R.string.find_folder_new_directory_cancel_button),
						new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				final AlertDialog alert = alertbox.create();
				alert.show();
			}
		});

		renameButton = (Button) root.findViewById(R.id.find_folder_rename_folder);
		renameButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final TextView edit = editMap.get(selectedFolder);
				if (edit != null) {
					AlertDialog.Builder alertbox = new AlertDialog.Builder(FindFolderDialogPreference.this.getDialog().getContext());
					alertbox.setTitle(context.getResources().getString(R.string.find_folder_rename_directory_alert_box));

					final EditText input = new EditText(FindFolderDialogPreference.this.getDialog().getContext());
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(150,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					lp.setMargins(8, 8, 8, 8);
					input.setLayoutParams(lp);
					input.setBackgroundResource(android.R.drawable.editbox_background_normal);
					input.setCursorVisible(true);
					input.setInputType(InputType.TYPE_CLASS_TEXT);
					input.setText(edit.getText());
					input.setSelection(edit.getText().length());
					alertbox.setView(input);

					alertbox.setPositiveButton(context.getResources().getString(R.string.find_folder_rename_directory_ok_button),
							new OnClickListener() { 
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String newName = input.getText().toString();
							edit.setText(newName);

							String dirName = rootFolder + "/" + getPathString();
							File newF = new File(dirName, newName);
							if (newName.equals("")) {
								Toast.makeText(getContext(), 
										context.getResources().getString(R.string.find_folder_folder_name_empty), Toast.LENGTH_SHORT).show();
							} else if (newF.exists()) {
								Toast.makeText(getContext(),
										context.getResources().getString(R.string.find_folder_folder_already_exists, newF.getName()),
										Toast.LENGTH_SHORT).show();
							} else if (!newName.equals(selectedFolder)){
								File f = new File(dirName, selectedFolder);
								if (f.renameTo(newF)) {
									selectedFolder = newName;
									String oldFilePath = getPathString() + selectedFolder;
									if (oldFilePath.equals(pathString)) {
										pathString = getPathString() + selectedFolder;
										persistString(pathString);
									}
								} else {
									Toast.makeText(getContext(), context.getResources().getString(R.string.find_folder_rename_failed),
											Toast.LENGTH_SHORT).show();
								}
							}
							editMap.clear();
							layout.removeAllViews();
							displayCurrentFolder();
						}
					});
					alertbox.setNegativeButton(context.getResources().getString(R.string.find_folder_rename_cancel_button), new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
					final AlertDialog alert = alertbox.create();
					alert.show();
				}
			}
		});

		return root;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (selectedFolder.equals("") && path.size() > 0) {
			selectedFolder = path.get(path.size() - 1);
			path.remove(path.size() - 1);
		}
		if (positiveResult) {
			// don't save root folder
			pathString = getPathString() + selectedFolder;
			persistString(pathString);
		}
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindView(view);
		displayCurrentFolder();
	}

	private String getPathString() {
		String pString = "";
		for (String f : path) {
			pString += f + "/";
		}
		return pString;
	}

	private void displayCurrentFolder() {
		File file = new File(rootFolder + "/" + getPathString());
		File[] fileList = file.listFiles();
		if (path.size() > 0) {
			TextView textView = (TextView) View.inflate(getContext(), R.layout.find_folder_dialog_item, null);
			textView.setText("..");
			textView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					selectedFolder = path.get(path.size() - 1);
					path.remove(path.size() - 1);
					editMap.clear();
					layout.removeAllViews();
					displayCurrentFolder();
					return true;
				}
			});
			layout.addView(textView);

			if (fileList.length >= 1) {
				View line = new View(getContext());
				line.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
				line.setBackgroundColor(Color.rgb(51, 51, 51));
				layout.addView(line);
			}
		}

		for (File f : fileList) {
			if (f.isDirectory() && !f.isHidden()) {
				String folderName = f.getName();
				TextView fEdit = getNewEdit ();
				fEdit.setText(folderName);

				editMap.put(folderName, fEdit);
				if (folderName.equals(selectedFolder)) {
					fEdit.setSelected(true);
				}
			}
		}

		int i = 0;
		for (TextView fEdit : editMap.values()) {
			layout.addView(fEdit);
			if (i < fileList.length - 1) {
				View line = new View(getContext());
				line.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
				line.setBackgroundColor(Color.rgb(51, 51, 51));
				layout.addView(line);
			}
			++i;
		}
		if (selectedFolder.equals("") && editMap.size() > 0) {
			TextView et = editMap.values().iterator().next();
			selectedFolder = et.getText().toString();
			et.setSelected(true);
		}

		String pstr = getPathString();
		pstr = pstr.equals("") ? "/" : pstr;
		String statusText = context.getResources().getString(R.string.find_folder_status_current_folder, pstr);
		statusTextView.setText(Html.fromHtml(statusText));
		scrollView.post(new Runnable() {
			@Override
			public void run() {
				if (!editMap.isEmpty() && selectedFolder != null && !selectedFolder.equals("")) {
					scrollSelected();
				}
				okButton = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
			}
		});
	}

	private TextView getNewEdit () {
		TextView fEdit = (TextView) View.inflate(getContext(), R.layout.find_folder_dialog_item, null);
		fEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView edit = (TextView) v;
				for (TextView t : editMap.values()) {
					if (!edit.getText().equals(t.getText())) {
						t.setSelected(false);
					}
				}
				edit.setSelected(true);
				selectedFolder = edit.getText().toString();
			}
		});
		fEdit.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				TextView tv = (TextView) v;
				path.add(tv.getText().toString());
				selectedFolder = "";
				editMap.clear();
				layout.removeAllViews();
				displayCurrentFolder();
				return true;
			}
		});
		fEdit.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					TextView view = (TextView)v;
					view.setSelected(true);
					String newName = view.getText().toString();
					selectedFolder = newName;
					for (TextView t : editMap.values()) {
						if (!t.getText().equals(view.getText())) {
							t.setSelected(false);
						}
					}
					return true;
				}
				return false;
			}
		});
		return fEdit;
	}

	private void scrollSelected() {
		int h = 0;
		int oldh = 0;
		for (TextView t : editMap.values()) {
			if (t.getText().toString().equals(selectedFolder)) {
				break;
			}
			oldh = t.getHeight();
			h += oldh;
		}
		scrollView.scrollTo(0, h - oldh / 2);
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
		state.rf = rootFolder;
		state.p = path;
		state.sf = selectedFolder;
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

		rootFolder = state.rf;
		path = state.p;
		selectedFolder = state.sf;
		displayCurrentFolder();
	}

	public void setRootFolder(String rootFolder) {
		this.rootFolder = rootFolder;
	}

	private static class SavedState extends BaseSavedState {
		private String rf;
		private List<String> p;
		private String sf;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		public SavedState(Parcel source) {
			super(source);
			sf = source.readString();
			p = new LinkedList<String>();
			source.readStringList(p);
			rf = source.readString();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeString(rf);
			dest.writeStringList(p);
			dest.writeString(sf);
		}
	}

}
