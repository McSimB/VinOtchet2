package com.mcsimb.vinotchet2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import static com.mcsimb.vinotchet2.MainActivity.MONTH;

@SuppressWarnings("WeakerAccess")
public class DBHelper extends SQLiteOpenHelper {

	final static int DB_VER = 1;
	final static String DB_NAME = "2017.db";
	final static String DAY = "day";
	final static String WINE = "wine";
	final static String WINE_ID = "wine_id";
	final static String VOLUME = "volume";
	final static String COUNTER_1 = "counter_1";
	final static String COUNTER_2 = "counter_2";
	final static String ICON = "icon";
	final String SORTS_TABLE = "sorts";
	final String DATA_TABLE = "data";

	private Context mContext;

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VER);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + SORTS_TABLE + " (_id INTEGER PRIMARY KEY, " +
				   WINE + " TEXT, " + ICON + " INTEGER)");
		db.execSQL("INSERT INTO " + SORTS_TABLE + " VALUES (1, ?, " + R.drawable.ic_0 + ")",
				   new String[] {mContext.getString(R.string.wine0)});
		db.execSQL("INSERT INTO " + SORTS_TABLE + " VALUES (2, ?, " + R.drawable.ic_1 + ")",
				   new String[] {mContext.getString(R.string.wine1)});
		db.execSQL("INSERT INTO " + SORTS_TABLE + " VALUES (3, ?, " + R.drawable.ic_2 + ")",
				   new String[] {mContext.getString(R.string.wine2)});
		db.execSQL("INSERT INTO " + SORTS_TABLE + " VALUES (4, ?, " + R.drawable.ic_3 + ")",
				   new String[] {mContext.getString(R.string.wine3)});
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public void newMonth(String month) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("CREATE TABLE " + DATA_TABLE + month +
				   " (_id INTEGER PRIMARY KEY, " + DAY + " TEXT, " + WINE_ID + " INTEGER, " +
				   VOLUME + " REAL, " + COUNTER_1 + " INTEGER, " + COUNTER_2 + " INTEGER)");
	}

	public List<String> getDays() {
		List<String> days = new ArrayList<>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(true, DATA_TABLE + MONTH, new String[]{DAY},
								 null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				days.add(cursor.getString(cursor.getColumnIndex(DAY)));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return days;
	}

	public List<String[]> getDayEntries(int position) {
		List<String[]> entries = new ArrayList<>();
		String curDay = getDays().get(position);
		SQLiteDatabase db = getReadableDatabase();
		String table = DATA_TABLE + MONTH + " INNER JOIN " + SORTS_TABLE + " ON " + SORTS_TABLE + "._id = " + WINE_ID;
		String[] columns = new String[] {WINE, VOLUME, COUNTER_1, COUNTER_2, ICON};
		Cursor cursor = db.query(table, columns, DAY + " = ?", new String[]{curDay},
								null, null, null);
		if (cursor.moveToFirst()) {
			do {
				String[] str = new String[4];
				str[0] = cursor.getString(cursor.getColumnIndex(WINE)) +
					cursor.getString(cursor.getColumnIndex(VOLUME));
				str[1] = cursor.getString(cursor.getColumnIndex(COUNTER_1));
				str[2] = cursor.getString(cursor.getColumnIndex(COUNTER_2));
				str[3] = cursor.getString(cursor.getColumnIndex(ICON));
				entries.add(str);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return entries;
	}

	public String[] getSorts() {
		String[] sorts = new String[0];
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(SORTS_TABLE, null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			sorts = new String[cursor.getCount()];
			int i = 0;
			do {
				sorts[i] = cursor.getString(cursor.getColumnIndex(WINE));
				i++;
			} while (cursor.moveToNext());
		}
		cursor.close();
		return sorts;
	}

	public void addEntry(ContentValues values) {
		SQLiteDatabase db = getWritableDatabase();
		db.insert(DATA_TABLE + MONTH, null, values);
	}

}
