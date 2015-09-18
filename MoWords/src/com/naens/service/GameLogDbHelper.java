package com.naens.service;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.naens.mowords.WordsActivity;


public class GameLogDbHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "mowords.db";

	private SQLiteOpenHelper helper;

	private static final String SQL_CREATE = "CREATE TABLE " + WordsActivity.GAMELOG_TABLE_NAME	+ " ("
			+ WordsActivity.GAMELOG_COLUMN_ID + "  INTEGER PRIMARY KEY,"
			+ WordsActivity.GAMELOG_COLUMN_FOLDER + " TEXT,"
			+ WordsActivity.GAMELOG_COLUMN_DATE + " INTEGER,"
			+ WordsActivity.GAMELOG_COLUMN_FILES + " TEXT,"
			+ WordsActivity.GAMELOG_COLUMN_INVERSE + " INTEGER,"
			+ WordsActivity.GAMELOG_COLUMN_DONE + " INTEGER,"
			+ WordsActivity.GAMELOG_COLUMN_TOTAL + " INTEGER," 
			+ WordsActivity.GAMELOG_COLUMN_GAME_TIME + " INTEGER,"
			+ WordsActivity.GAMELOG_COLUMN_SIDE + " INTEGER,"
			+ WordsActivity.GAMELOG_COLUMN_SIDES + " INTEGER)";

	public GameLogDbHelper(Context context) {
		helper = new SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

			@Override
			public void onCreate(SQLiteDatabase db) {
				db.execSQL(SQL_CREATE);
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			}
		};
//		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public SQLiteDatabase getReadableDatabase() {
		return helper.getReadableDatabase();
	}

	public void close() {
		helper.close();
	}

	public SQLiteDatabase getWritableDatabase() {
		return helper.getWritableDatabase();
	}

}
