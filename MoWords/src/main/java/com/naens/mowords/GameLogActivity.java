package com.naens.mowords;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.naens.mowords.MoWordsHorizontalScrollView.HorizontalScrollViewListener;
import com.naens.service.GameLogDbHelper;

public class GameLogActivity extends Activity {

	public static final String INTENT_FOLDER = "com.naens.mowords.folder_name";
	private TableLayout headerTable;
	private TableLayout table;
	private TextView additionalTableItem;
	private TextView additionalRowItem;
	private TextView headerView;
	private int MARG = -19574561;
	private TableLayout firstColumnTable;
	private TableRow firstColumnEmptyRow;
	private TextView firstColumnEmptyView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_game_log);

		MARG = getResources().getDimensionPixelSize(R.dimen.game_log_table_margin);

		Intent intent = getIntent();
		String folderName = intent.getStringExtra(INTENT_FOLDER);

		final GameLogDbHelper dbHelper = new GameLogDbHelper(this);
		final SQLiteDatabase db = dbHelper.getReadableDatabase();

		TableRow.LayoutParams p0 = new TableRow.LayoutParams(0, 0);

		headerTable = (TableLayout) findViewById(R.id.game_log_header_table);
		TableRow headerRowH = createTableHeader();
		headerTable.addView(headerRowH);

		headerView = (TextView) findViewById(R.id.game_log_header_view1);

		table = (TableLayout) findViewById(R.id.game_log_table);
		TableRow headerRow = createTableHeader();
		additionalTableItem = new TextView(this);
		additionalTableItem.setLayoutParams(p0);
		headerRow.addView(additionalTableItem);
		table.addView(headerRow);

		TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
				TableRow.LayoutParams.WRAP_CONTENT);
		params.setMargins(MARG, MARG, MARG, MARG);

		String[] columns = { WordsActivity.GAMELOG_COLUMN_ID, WordsActivity.GAMELOG_COLUMN_DATE,
				WordsActivity.GAMELOG_COLUMN_FILES, WordsActivity.GAMELOG_COLUMN_INVERSE,
				WordsActivity.GAMELOG_COLUMN_DONE, WordsActivity.GAMELOG_COLUMN_TOTAL,
				WordsActivity.GAMELOG_COLUMN_GAME_TIME, WordsActivity.GAMELOG_COLUMN_SIDE,
				WordsActivity.GAMELOG_COLUMN_SIDES, };

		String sortOrder = WordsActivity.GAMELOG_COLUMN_DATE + " ASC";

		String where = WordsActivity.GAMELOG_COLUMN_FOLDER + "='" + folderName + "'";

		Cursor cursor = db.query(WordsActivity.GAMELOG_TABLE_NAME, columns, where, null, null, null, sortOrder);
		cursor.moveToFirst();
		
		firstColumnTable = (TableLayout) findViewById(R.id.game_log_first_column);
		
		ImageView delImage = null;
		TextView totalTimeText = null;
		while (!cursor.isAfterLast()) {
			long id = cursor.getLong(cursor.getColumnIndexOrThrow(WordsActivity.GAMELOG_COLUMN_ID));
			long date = cursor.getLong(cursor.getColumnIndexOrThrow(WordsActivity.GAMELOG_COLUMN_DATE));
			String files = cursor.getString(cursor.getColumnIndexOrThrow(WordsActivity.GAMELOG_COLUMN_FILES));
			boolean inverse = false;
			inverse = cursor.getInt(cursor.getColumnIndexOrThrow(WordsActivity.GAMELOG_COLUMN_INVERSE)) > 0;
			int done = cursor.getInt(cursor.getColumnIndexOrThrow(WordsActivity.GAMELOG_COLUMN_DONE));
			int total = cursor.getInt(cursor.getColumnIndexOrThrow(WordsActivity.GAMELOG_COLUMN_TOTAL));
			int gameTime = cursor.getInt(cursor.getColumnIndexOrThrow(WordsActivity.GAMELOG_COLUMN_GAME_TIME));
			int side = cursor.getInt(cursor.getColumnIndexOrThrow(WordsActivity.GAMELOG_COLUMN_SIDE));
			int sides = cursor.getInt(cursor.getColumnIndexOrThrow(WordsActivity.GAMELOG_COLUMN_SIDES));
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(date);
			TableRow row = new TableRow(this);
			row.setGravity(Gravity.CENTER_VERTICAL);
			row.setBackgroundResource(side == sides ? R.drawable.game_log_simple : R.drawable.game_log_second);

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

			//external first row
			TextView newChild = new TextView(GameLogActivity.this);
			newChild.setText(dateFormat.format(c.getTime()));
			newChild.setPadding(MARG, MARG, MARG, MARG);
			newChild.setGravity(Gravity.CENTER_VERTICAL);

			TableRow newRow = new TableRow(GameLogActivity.this);
			newRow.setBackgroundResource(side == sides ? R.drawable.game_log_fc_simple : R.drawable.game_log_fc_second);
			newRow.addView(newChild);
			firstColumnTable.addView(newRow);

			//table
			TextView text0 = new TextView(this);
			text0.setLayoutParams(params);
			text0.setText(dateFormat.format(c.getTime()));
			row.addView(text0);

			TextView text1 = new TextView(this);
			text1.setLayoutParams(params);
			if (side == 1) {
				text1.setText(files);
			}
			row.addView(text1);

			ImageView imageInverse = new ImageView(this);
			if (side == 1) {
				imageInverse.setImageDrawable(getResources().getDrawable(inverse ? 
						R.drawable.arrow_left_e : R.drawable.arrow_right_e));
			} else {
				imageInverse.setImageDrawable(getResources().getDrawable(inverse ? 
						R.drawable.arrow_left : R.drawable.arrow_right));
			}
			row.addView(imageInverse);

			TextView text3 = new TextView(this);
			text3.setLayoutParams(params);
			text3.setText(String.format("%d/%d (%.2f", done, total, 100.0 * (float) done / (float) total) + "%)");
			row.addView(text3);

			TextView text4 = new TextView(this);
			text4.setLayoutParams(params);
			text4.setText(getTimeString(gameTime));
			row.addView(text4);

			if (side == 1) {
				totalTimeText = new TextView(this);
				totalTimeText.setLayoutParams(params);
				totalTimeText.setTypeface(null, Typeface.BOLD);
				row.addView(totalTimeText);
				if (sides == 1) {
					totalTimeText.setText(getTimeString(gameTime));
					totalTimeText = null;
				} else {
					totalTimeText.setTag(gameTime);
				}
			} else {
				row.addView(new TextView(this));
			}

			if (side == 2) { // is second side
				delImage.setTag(R.string.game_log_id2, id);
				delImage.setTag(R.string.game_log_row1, row);
				Log.i("TAG", "delImage tag = " + id);
				delImage.setLayoutParams(params);
				delImage = null;
				totalTimeText.setText(getTimeString(gameTime + (Integer) totalTimeText.getTag()));
				totalTimeText = null;
			} else { // is first or only side
				delImage = new ImageView(this);
				delImage.setLayoutParams(params);
				delImage.setTag(R.string.game_log_row2, row);
				delImage.setImageResource(android.R.drawable.ic_delete);
				delImage.setTag(R.string.game_log_id1, id);
				Log.i("TAG", "delImage tag = " + id);
				delImage.setOnLongClickListener(new View.OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						final ImageView delImage = (ImageView) v;

						String message = getResources().getString(R.string.game_log_delete_game_log);
						String positiveMessage = getResources().getString(R.string.game_log_delete_game_log_positive);
						String negativeMessage = getResources().getString(R.string.game_log_delete_game_log_negative);

						AlertDialog.Builder alertbox = new AlertDialog.Builder(GameLogActivity.this);
						alertbox.setMessage(message);
						alertbox.setPositiveButton(positiveMessage, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								GameLogDbHelper dbHelper = new GameLogDbHelper(GameLogActivity.this);
								SQLiteDatabase db = dbHelper.getReadableDatabase();
								Long id1 = (Long) delImage.getTag(R.string.game_log_id1);
								String sqlString = "delete from " + WordsActivity.GAMELOG_TABLE_NAME + " where "
										+ WordsActivity.GAMELOG_COLUMN_ID + "=" + id1;
								db.execSQL(sqlString);

								Long id2 = (Long) delImage.getTag(R.string.game_log_id2);
								if (id2 != null) {
									sqlString = "delete from " + WordsActivity.GAMELOG_TABLE_NAME + " where "
											+ WordsActivity.GAMELOG_COLUMN_ID + "=" + id2;
									db.execSQL(sqlString);
									Log.i("TAG", "delete: " + id2);
								}
								db.close();
								dbHelper.close();
								TableRow row1 = (TableRow) delImage.getTag(R.string.game_log_row1);
								table.removeView(row1);
								TableRow row2 = (TableRow) delImage.getTag(R.string.game_log_row2);
								if (row2 != null) {
									table.removeView(row2);
								}
							}
						});

						alertbox.setNegativeButton(negativeMessage, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
							}
						});
						alertbox.show();
						return true;
					}
				});
				row.addView(delImage);
				if (sides == 1) {
					delImage = null;
				}
			}

			table.addView(row);

			cursor.moveToNext();
		}
		
		firstColumnEmptyRow = new TableRow(this);
		firstColumnEmptyRow.setBackgroundResource(R.drawable.game_log_fc_empty);
		firstColumnEmptyView = new TextView(this);
		firstColumnEmptyView.setLayoutParams(p0);
		firstColumnEmptyRow.addView(firstColumnEmptyView);
		firstColumnTable.addView(firstColumnEmptyRow);
		TableRow.LayoutParams firstColumnEmptyRowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
				TableRow.LayoutParams.WRAP_CONTENT);
		firstColumnEmptyRow.setLayoutParams(firstColumnEmptyRowParams);

		TableRow additionalRow = new TableRow(GameLogActivity.this);
		additionalRow.setBackgroundResource(R.drawable.game_log_second);
		additionalRowItem = new TextView(this);
		additionalRowItem.setLayoutParams(p0);
		additionalRow.addView(additionalRowItem);
		table.addView(additionalRow);
		db.close();
		dbHelper.close();

		final MoWordsHorizontalScrollView tableHorizontalScrollView = (MoWordsHorizontalScrollView) findViewById(R.id.game_log_table_horizontal_scroll_view);
		final MoWordsHorizontalScrollView headerHorizontalScrollView = (MoWordsHorizontalScrollView) findViewById(R.id.game_log_header_horizontal_scroll_view);

		tableHorizontalScrollView.setHorizontalScrollViewListener(new HorizontalScrollViewListener() {
			
			@Override
			public void onScrollChanged(int l, int t, int oldl, int oldt) {
				headerHorizontalScrollView.scrollTo(l, t);
			}
		});

		headerHorizontalScrollView.setHorizontalScrollViewListener(new HorizontalScrollViewListener() {
			
			@Override
			public void onScrollChanged(int l, int t, int oldl, int oldt) {
				tableHorizontalScrollView.scrollTo(l, t);
			}
		});

	}

	private TableRow createTableHeader () {
		TableRow result = new TableRow(this);
		result.setBackgroundResource(R.drawable.game_log_header);

		TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
				TableRow.LayoutParams.WRAP_CONTENT);
		params.setMargins(MARG, MARG, MARG, MARG);

		TextView dateTimeHeader = new TextView(this);
		dateTimeHeader.setLayoutParams(params);
		dateTimeHeader.setGravity(Gravity.LEFT);
		dateTimeHeader.setTypeface(null, Typeface.BOLD);
		dateTimeHeader.setText(getResources().getString(R.string.game_log_header_datetime));
		result.addView(dateTimeHeader);

		TextView filesHeader = new TextView(this);
		filesHeader.setLayoutParams(params);
		dateTimeHeader.setGravity(Gravity.LEFT);
		filesHeader.setTypeface(null, Typeface.BOLD);
		filesHeader.setText(getResources().getString(R.string.game_log_header_files));
		result.addView(filesHeader);

		TextView directionHeader = new TextView(this);
		directionHeader.setLayoutParams(params);
		dateTimeHeader.setGravity(Gravity.LEFT);
		directionHeader.setTypeface(null, Typeface.BOLD);
		directionHeader.setText(getResources().getString(R.string.game_log_header_direction));
		result.addView(directionHeader);

		TextView doneHeader = new TextView(this);
		doneHeader.setLayoutParams(params);
		dateTimeHeader.setGravity(Gravity.LEFT);
		doneHeader.setTypeface(null, Typeface.BOLD);
		doneHeader.setText(getResources().getString(R.string.game_log_header_done));
		result.addView(doneHeader);

		TextView gameTimeHeader = new TextView(this);
		gameTimeHeader.setLayoutParams(params);
		dateTimeHeader.setGravity(Gravity.LEFT);
		gameTimeHeader.setTypeface(null, Typeface.BOLD);
		gameTimeHeader.setText(getResources().getString(R.string.game_log_header_side_time));
		result.addView(gameTimeHeader);

		TextView totalTimeHeader = new TextView(this);
		totalTimeHeader.setLayoutParams(params);
		dateTimeHeader.setGravity(Gravity.LEFT);
		totalTimeHeader.setTypeface(null, Typeface.BOLD);
		totalTimeHeader.setText(getResources().getString(R.string.game_log_header_total_time));
		result.addView(totalTimeHeader);

		TextView delHeader = new TextView(this);
		delHeader.setLayoutParams(params);
		dateTimeHeader.setGravity(Gravity.LEFT);
		delHeader.setTypeface(null, Typeface.BOLD);
		result.addView(delHeader);

		return result;
	}

	@Override
	protected void onResume() {
		getScrollView().post(new Runnable() {

			@Override
			public void run() {
				getScrollView().fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
		ViewTreeObserver viewTreeObserver = table.getViewTreeObserver();
		if (viewTreeObserver.isAlive()) {
		  viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				table.getViewTreeObserver().removeGlobalOnLayoutListener(this);

				TableLayout layout = (TableLayout) findViewById(R.id.game_log_layout);
				TableRow headerRowH = (TableRow) headerTable.getChildAt(0);
				TableRow headerRow = (TableRow) table.getChildAt(0);

				//widths
				final int layoutWidth = layout.getWidth();
				Log.i("TAG", "layoutWidth=" + layoutWidth);
				final int headerTableWidth = headerTable.getWidth();
				Log.i("TAG", "headerTableWidth=" + headerTableWidth);
				final int tableFirstColumnWidth = headerRow.getChildAt(0).getWidth()+ 2 * MARG;
				Log.i("TAG", "tableFirstColumnWidth=" + tableFirstColumnWidth);
				final int tableWidth = table.getWidth();
				Log.i("TAG", "tableWidth=" + tableWidth);
				final int tableDataWidth = tableWidth - tableFirstColumnWidth;
				Log.i("TAG", "tableDataWidth=" + tableDataWidth);

				//heights
				final int tableHeaderHeight = headerRow.getHeight();

				//copy table first column width + margins and header height to header view
				TableRow.LayoutParams headerViewParameters = 
						new TableRow.LayoutParams(tableFirstColumnWidth, tableHeaderHeight);
				headerView.setLayoutParams(headerViewParameters);

				//create header
				int hiddenHeaderHeight = 0;
				int hiddenFirstColumnWidth = 0;
				for (int i = 0; i < headerRowH.getChildCount(); ++ i) {
					TextView vH = (TextView) headerRowH.getChildAt(i);
					TextView v = (TextView) headerRow.getChildAt(i);
					TableRow.LayoutParams vp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, hiddenHeaderHeight);
					vp.setMargins(MARG, 0, MARG, 0);
					v.setLayoutParams(vp);
					int width = v.getWidth();
					int height = v.getHeight();
					TableRow.LayoutParams params = new TableRow.LayoutParams(width,	height);
					params.setMargins(MARG, MARG, MARG, MARG);
					vH.setLayoutParams(params);
				}

				//create first column
				TableRow row0 = (TableRow) table.getChildAt(0);
				TextView child0 = (TextView) row0.getChildAt(0);
				child0.setLayoutParams(new TableRow.LayoutParams(hiddenFirstColumnWidth, hiddenHeaderHeight));
				int removedWidth = child0.getWidth() - hiddenFirstColumnWidth;
				for (int i = 1; i < table.getChildCount() - 1; ++ i) {
					TableRow row = (TableRow) table.getChildAt(i);
					TextView child = (TextView) row.getChildAt(0);

					TableRow newRow = (TableRow) firstColumnTable.getChildAt(i - 1);
					TextView newChild = (TextView) newRow.getChildAt(0);

					TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
							row.getHeight());
					newChild.setLayoutParams(params);

					TableRow.LayoutParams rowp = new TableRow.LayoutParams(tableFirstColumnWidth, row.getHeight());
					newRow.setLayoutParams(rowp);
					if (child.getWidth() - hiddenFirstColumnWidth > removedWidth) {
						removedWidth = child.getWidth() - hiddenFirstColumnWidth;
					}

					child.setLayoutParams(new TableRow.LayoutParams(hiddenFirstColumnWidth, row.getHeight()));
				}
				TextView vH = (TextView) headerRowH.getChildAt(0);
				vH.setLayoutParams(new TableRow.LayoutParams(hiddenFirstColumnWidth, headerRowH.getHeight()));

				int headerTableHeight = headerRow.getHeight();
				int placeForTable = layoutWidth - tableFirstColumnWidth;
				Log.i("TAG", "placeForTable=" + placeForTable);
				if (tableDataWidth > placeForTable){ //adjust with weight
				} else {
						//table smaller than layout => make header smaller
					Log.i("TAG", "tableDataWidth < placeForTable");
					TableRow.LayoutParams headerTableParams = new TableRow.LayoutParams(placeForTable, headerTableHeight);
					headerRowH.setLayoutParams(headerTableParams);

					int addwidth = placeForTable - tableDataWidth;
					Log.i("TAG", "addwidth=" + addwidth);
					TableRow.LayoutParams p = new TableRow.LayoutParams(addwidth, hiddenHeaderHeight);
					additionalTableItem.setLayoutParams(p);
				}

				//add additional row if table not high enough
				table.post(new Runnable() {
					
					@Override
					public void run() {
						TableLayout layout = (TableLayout) findViewById(R.id.game_log_layout);
						int additionalHeight = layout.getHeight() - (headerTable.getHeight() + table.getHeight());
						if (additionalHeight < 0) {
							additionalHeight = 0;
						}
						Log.i("TAG", "View W=" + firstColumnTable.getWidth());
						Log.i("TAG", "Table W=" + table.getWidth());
						Log.i("TAG", "Header W=" + headerTable.getWidth());
						additionalRowItem.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, additionalHeight));
						firstColumnEmptyView.setLayoutParams(new TableRow.LayoutParams(firstColumnTable.getWidth(), additionalHeight));
					}
				});
			}
		  });
		}
		super.onResume();
	}

	private ScrollView getScrollView() {
		return (ScrollView) findViewById(R.id.game_table_scroll_view);
	}

	private String getTimeString(int seconds) {
		int hours = seconds / 3600;
		String timeString = String.format("%02d:%02d", (seconds % 3600) / 60, seconds % 60);
		if (hours > 0) {
			timeString = hours + ":" + timeString;
		}
		return timeString;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
}
