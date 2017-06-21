package com.mcsimb.vinotchet2;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DBHelper extends SQLiteOpenHelper {

	final static int DB_VER = 1;
	final static String DB_NAME = "2017.db";
	final static String DAY = "day";
	final static String WINE = "wine";
	final static String VOLUME = "volume";
	final static String COUNTER_1 = "counter_1";
	final static String COUNTER_2 = "counter_2";
	final static String ICON = "icon";
	final String SORTS_TABLE = "sorts";
	final String DATA_TABLE = "data";

	Context mContext;

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VER);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + SORTS_TABLE + " (_id INTEGER PRIMARY KEY, " + 
				   WINE + " INTEGER, " + ICON + " INTEGER)");
		db.execSQL("INSERT INTO " + SORTS_TABLE + " (1, " + R.string.wine0 + ", " + R.drawable.ic_0 + ")");
		db.execSQL("INSERT INTO " + SORTS_TABLE + " (2, " + R.string.wine1 + ", " + R.drawable.ic_1 + ")");
		db.execSQL("INSERT INTO " + SORTS_TABLE + " (3, " + R.string.wine2 + ", " + R.drawable.ic_2 + ")");
		db.execSQL("INSERT INTO " + SORTS_TABLE + " (4, " + R.string.wine3 + ", " + R.drawable.ic_3 + ")");
		db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public void newMonth(String month) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("CREATE TABLE " + DATA_TABLE + month +
				   " (_id INTEGER PRIMARY KEY, " + DAY + " TEXT, " + WINE + " TEXT, " + 
				   VOLUME + " TEXT, " + COUNTER_1 + " INTEGER, " + COUNTER_2 + " INTEGER)");
		db.close();
	}

	public void add() {

	}

}
