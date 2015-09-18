package com.naens.dialogs;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
	private Map<String, TextView> editMap = new TreeMap<String, TextView>(new CaseUnsensitiveComparator());
	private OnClickListener positiveListener;
	private Context context;

	public FindFolderDialog(Context context) {
		super(context);
		this.context = context;
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
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
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
				AlertDialog.Builder alertbox = new AlertDialog.Builder(getContext());
				alertbox.setTitle(context.getResources().getString(R.string.find_folder_new_directory_alert_box));

				final EditText input = new EditText(getContext());
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
							Toast.makeText(getContext(), 
									context.getResources().getString(R.string.find_folder_folder_name_empty), Toast.LENGTH_SHORT).show();
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

		renameButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final TextView edit = editMap.get(selectedFolder);
				if (edit != null) {
					AlertDialog.Builder alertbox = new AlertDialog.Builder(getContext());
					alertbox.setTitle(context.getResources().getString(R.string.find_folder_rename_directory_alert_box));

					final EditText input = new EditText(getContext());
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
				TextView fEdit = getNewEdit();
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
				okButton.setEnabled(true);
				// okButton = ((AlertDialog)
				// getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
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

	@Override
	public void show() {
		super.show();

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		// lp.height = WindowManager.LayoutParams.FILL_PARENT;
		getWindow().setAttributes(lp);
		setTitle(context.getResources().getString(R.string.find_folder_title));
		displayCurrentFolder();
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
}
