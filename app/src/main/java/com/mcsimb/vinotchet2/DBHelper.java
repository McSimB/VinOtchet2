package com.mcsimb.vinotchet2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class DBHelper extends SQLiteOpenHelper {

	static String MONTH = String.format("%tm", Calendar.getInstance());
	static String YEAR = String.format("%ty", Calendar.getInstance());
	static String DB_NAME = YEAR + ".db";
	static int DB_VER = 1;
	
	final static String ID = "_id";
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
		db.execSQL("CREATE TABLE " + SORTS_TABLE + " (" + ID + " INTEGER PRIMARY KEY, " +
				   WINE + " TEXT, " + ICON + " INTEGER)");
		db.execSQL("INSERT INTO " + SORTS_TABLE + " VALUES (1, ?, " + R.drawable.ic_0 + ")",
				   new String[] {mContext.getString(R.string.wine0)});
		db.execSQL("INSERT INTO " + SORTS_TABLE + " VALUES (2, ?, " + R.drawable.ic_1 + ")",
				   new String[] {mContext.getString(R.string.wine1)});
		db.execSQL("INSERT INTO " + SORTS_TABLE + " VALUES (3, ?, " + R.drawable.ic_2 + ")",
				   new String[] {mContext.getString(R.string.wine2)});
		db.execSQL("INSERT INTO " + SORTS_TABLE + " VALUES (4, ?, " + R.drawable.ic_3 + ")",
				   new String[] {mContext.getString(R.string.wine3)});
		createTables(db, MONTH);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	public void newMonth(String month) {
		SQLiteDatabase db = getWritableDatabase();
		createTables(db, month);
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
		String table = DATA_TABLE + MONTH + " INNER JOIN " + SORTS_TABLE + " ON " + WINE_ID + " = " + SORTS_TABLE + "." + ID;
		String[] columns = new String[] {WINE, VOLUME, COUNTER_1, COUNTER_2, ICON, DATA_TABLE + MONTH + "." + ID};
		Cursor cursor = db.query(table, columns, DAY + " = ?", new String[]{curDay}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				String[] str = new String[5];
				str[0] = cursor.getString(cursor.getColumnIndex(WINE)) +
					cursor.getString(cursor.getColumnIndex(VOLUME));
				str[1] = cursor.getString(cursor.getColumnIndex(COUNTER_1));
				str[2] = cursor.getString(cursor.getColumnIndex(COUNTER_2));
				str[3] = cursor.getString(cursor.getColumnIndex(ICON));
				str[4] = cursor.getString(cursor.getColumnIndex(ID));
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

	public List<Item> getItems(int position) {
		List<Item> items = new ArrayList<>();
		List<String[]> entries = getDayEntries(position);
		for (String[] str : entries) {
			items.add(new Item(str[0], str[1], str[2], Integer.decode(str[3]), str[4]));
		}
		return items;
	}
	
	public String[] getEntry() {
		String[] entry = new String[6];
		return entry;
	}
	
	public void addEntry(ContentValues values) {
		SQLiteDatabase db = getWritableDatabase();
		db.insert(DATA_TABLE + MONTH, null, values);
	}

	public void deleteEntry(String id) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(DATA_TABLE + MONTH, "_id = " + id, null);
	}
	
	private void createTables(SQLiteDatabase db, String month) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DATA_TABLE + month +
				   " (_id INTEGER PRIMARY KEY, " + DAY + " TEXT, " + WINE_ID + " INTEGER, " +
				   VOLUME + " REAL, " + COUNTER_1 + " INTEGER, " + COUNTER_2 + " INTEGER)");
	}
	
	public class Item {

		final String wine;
		final String counter1;
		final String counter2;
		final int iconId;
		final String id;

		Item(String wine, String counter1, String counter2, int iconId, String id) {
			this.wine = wine + "\nid = " + id;
			this.counter1 = counter1;
			this.counter2 = counter2;
			this.iconId = iconId;
			this.id = id;
		}
	}
	
}
