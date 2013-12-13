package com.naens.dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.naens.mowords.R;

/*
 * Copyright (C) 2011-2012 George Yunaev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
public class DialogChooseDirectory implements OnItemClickListener, OnClickListener {
	public interface Result {
		void onChooseDirectory(String dir);
	}

	private List<File> m_entries = new ArrayList<File>();
	private File m_currentDir;
	private Context m_context;
	private ListView m_list;
	private Result m_result = null;
	private int viewResId;

	public class DirAdapter extends ArrayAdapter <File> {
		public DirAdapter(int resid) {
			super(m_context, resid, m_entries);
		}

		// This function is called to show each view item
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textview = (TextView) super.getView(position, convertView, parent);

			if (m_entries.get(position) == null) {
				textview.setText("..");
				textview.setCompoundDrawablesWithIntrinsicBounds(m_context.getResources().getDrawable(R.drawable.icon_parentdir),
						null, null, null);
			} else {
				textview.setText(m_entries.get(position).getName());
				textview.setCompoundDrawablesWithIntrinsicBounds(m_context.getResources().getDrawable(R.drawable.icon_dir),
						null, null, null);
			}
			return textview;
		}
	}

	private void listDirs() {
		m_entries.clear();

		// Get files
		File[] files = m_currentDir.listFiles();

		// Add the ".." entry
		if (m_currentDir.getParent() != null) {
			m_entries.add(new File(".."));
		}

		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					m_entries.add(file);
				}
			}
		}

		Collections.sort(m_entries, new Comparator<File>() {
			public int compare(File f1, File f2) {
				return f1.getName().toLowerCase()
						.compareTo(f2.getName().toLowerCase());
			}
		});
	}

	public DialogChooseDirectory(String title, Context ctx, Result res, String startDir, int viewResId) {
		m_context = ctx;
		m_result = res;
		this.viewResId = viewResId;

		if (startDir != null)
			m_currentDir = new File(startDir);
		else
			m_currentDir = Environment.getExternalStorageDirectory();

		listDirs();
		DirAdapter adapter = new DirAdapter(viewResId);

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title);
		builder.setAdapter(adapter, this);

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if (m_result != null)
					m_result.onChooseDirectory(m_currentDir.getAbsolutePath());
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog m_alertDialog = builder.create();
		m_list = m_alertDialog.getListView();
		m_list.setOnItemClickListener(this);
		m_alertDialog.show();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View list, int pos, long id) {
		if (pos < 0 || pos >= m_entries.size())
			return;

		if (m_entries.get(pos).getName().equals(".."))
			m_currentDir = m_currentDir.getParentFile();
		else
			m_currentDir = m_entries.get(pos);

		listDirs();
		DirAdapter adapter = new DirAdapter(viewResId);
		m_list.setAdapter(adapter);
	}

	public void onClick(DialogInterface dialog, int which) {
	}
}
