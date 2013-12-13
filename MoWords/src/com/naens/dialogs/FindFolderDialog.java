package com.naens.dialogs;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.naens.mowords.R;
import com.naens.tools.CaseUnsensitiveComparator;

public class FindFolderDialog extends Dialog {

	private String pathString = "";
	private Button okButton;
	private Button cancelButton;
	private Button newButton;
	private Button renameButton;
	private LinearLayout layout;
	private ScrollView scrollView;
	private TextView statusTextView;
	private String rootFolder = "/";
	private List<String> path;
	private String selectedFolder = "";
	private boolean editMode = false;
	private Map<String, EditText> editMap = new TreeMap<String, EditText>(new CaseUnsensitiveComparator());
	private OnClickListener positiveListener;
	private EditText currentEdit;

	public FindFolderDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_folder_dialog);
		layout = (LinearLayout) findViewById(R.id.find_folder_view);
		scrollView = (ScrollView) findViewById(R.id.find_folder_scroll);
		statusTextView = (TextView) findViewById(R.id.find_folder_status);

		newButton = (Button) findViewById(R.id.find_folder_new_folder);
		renameButton = (Button) findViewById(R.id.find_folder_rename_folder);
		okButton = (Button) findViewById(R.id.find_folder_ok_btn);
		cancelButton = (Button) findViewById(R.id.find_folder_cancel_btn);
		Log.i("TAG", "create:" + rootFolder);
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("TAG", "ok:click0");
				positiveListener.onClick(FindFolderDialog.this, Dialog.BUTTON_POSITIVE);
				dismiss();
			}
		});
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FindFolderDialog.this.cancel();
			}
		});

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

		newButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				currentEdit = getNewEdit();
				currentEdit.setCursorVisible(true);
				currentEdit.setFocusable(true);
				currentEdit.setFocusableInTouchMode(true);
				currentEdit.requestFocus();
				InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputMethodManager.toggleSoftInputFromWindow(layout.getApplicationWindowToken(),
						InputMethodManager.SHOW_IMPLICIT, 0);
				editMode = true;
				layout.addView(currentEdit);
				selectedFolder = "";
				scrollView.scrollTo(0, layout.getHeight());
			}
		});

		renameButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText edit = editMap.get(selectedFolder);
				if (edit != null) {
					scrollSelected();
					edit.setCursorVisible(true);
					edit.setFocusable(true);
					edit.setFocusableInTouchMode(true);
					edit.requestFocus();
					edit.setSelection(edit.getText().length());
					InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(
							Context.INPUT_METHOD_SERVICE);
					inputMethodManager.toggleSoftInputFromWindow(layout.getApplicationWindowToken(),
							InputMethodManager.SHOW_FORCED, 0);
					editMode = true;
					currentEdit = edit;
				}
			}
		});

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
			EditText textView = (EditText) View.inflate(getContext(), R.layout.find_folder_dialog_item, null);
			textView.setText("..");
			textView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					if (!editMode) {
						selectedFolder = path.get(path.size() - 1);
						path.remove(path.size() - 1);
						editMap.clear();
						layout.removeAllViews();
						displayCurrentFolder();
					}
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
				EditText fEdit = getNewEdit();
				fEdit.setText(folderName);

				editMap.put(folderName, fEdit);
				if (folderName.equals(selectedFolder)) {
					fEdit.setSelected(true);
				}
			}
		}

		int i = 0;
		for (EditText fEdit : editMap.values()) {
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
			EditText et = editMap.values().iterator().next();
			selectedFolder = et.getText().toString();
			et.setSelected(true);
		}

		String pathString = getPathString();
		statusTextView.setText("Current folder: " + (pathString.equals("") ? "." : pathString));
		scrollView.post(new Runnable() {
			@Override
			public void run() {
				if (!editMap.isEmpty() && selectedFolder != null && !selectedFolder.equals("")) {
					scrollSelected();
				}
				okButton.setEnabled(true);
				// okButton = ((AlertDialog)
				// getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
			}
		});
		currentEdit = null;
	}

	private EditText getNewEdit() {
		EditText fEdit = (EditText) View.inflate(getContext(), R.layout.find_folder_dialog_item, null);
		fEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText edit = (EditText) v;
				if (!editMode) {
					for (EditText t : editMap.values()) {
						if (!edit.getText().equals(t.getText())) {
							t.setSelected(false);
						}
					}
					edit.setSelected(true);
					selectedFolder = edit.getText().toString();
				} else {
					if (!edit.equals(currentEdit)) {
						editMode = false;
						for (EditText t : editMap.values()) {
							if (!edit.getText().equals(t.getText())) {
								t.setSelected(false);
								t.clearFocus();
								t.setCursorVisible(false);
								t.setFocusable(false);
								t.setFocusableInTouchMode(false);
							}
						}
						if (selectedFolder == "") {
							layout.removeView(currentEdit);
						}
						selectedFolder = edit.getText().toString();
						edit.setSelected(true);
					}
				}
			}
		});
		fEdit.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (!editMode) {
					EditText tv = (EditText) v;
					path.add(tv.getText().toString());
					selectedFolder = "";
					editMap.clear();
					layout.removeAllViews();
					displayCurrentFolder();
				}
				return true;
			}
		});
		fEdit.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					String dirName = rootFolder + "/" + getPathString();
					String newName = ((EditText) v).getText().toString();
					File newF = new File(dirName, newName);
					if (newName.equals("")) {
						Toast.makeText(getContext(), "Folder name empty", Toast.LENGTH_SHORT).show();
					} else if (newF.exists()) {
						Toast.makeText(getContext(), "File or Folder already exists: " + newF.getName(),
								Toast.LENGTH_SHORT).show();
					} else if (!newName.equals(selectedFolder)) {
						if (selectedFolder == "") { // create new
							if (newF.mkdir()) {
								selectedFolder = newName;
							} else {
								Toast.makeText(getContext(), "Folder not created", Toast.LENGTH_SHORT).show();
							}
						} else { // rename
							File f = new File(dirName, selectedFolder);
							if (f.renameTo(newF)) {
								selectedFolder = newName;
							} else {
								Toast.makeText(getContext(), "Rename failed", Toast.LENGTH_SHORT).show();
							}
						}
					}
					currentEdit = null;
					InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(
							Context.INPUT_METHOD_SERVICE);
					inputMethodManager.toggleSoftInputFromWindow(layout.getApplicationWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS, 0);
					editMap.clear();
					layout.removeAllViews();
					displayCurrentFolder();
					editMode = false;
					return true;
				}
				return false;
			}
		});
		return fEdit;
	}

	public void setPositiveButton(String string, final OnClickListener onClickListener) {
		this.positiveListener = onClickListener;
	}

	public CharSequence getFolderPath() {
		return getPathString() + selectedFolder;
	}

	public void setRootFolder(String rootFolder) {
		this.rootFolder = rootFolder;
	}

	public void setPath(String path) {
		pathString = path;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void show() {
		super.show();

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		// lp.height = WindowManager.LayoutParams.FILL_PARENT;
		getWindow().setAttributes(lp);
		setTitle("Choose folder:");
		displayCurrentFolder();
	}

	private void scrollSelected() {
		int h = 0;
		int oldh = 0;
		for (EditText t : editMap.values()) {
			if (t.getText().toString().equals(selectedFolder)) {
				break;
			}
			oldh = t.getHeight();
			h += oldh;
		}
		scrollView.scrollTo(0, h - oldh / 2);
	}
}
