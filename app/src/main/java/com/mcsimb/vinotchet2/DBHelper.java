package com.mcsimb.vinotchet2;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DBHelper extends SQLiteOpenHelper {

	final static int DB_VER = 1;
	final static String DB_NAME = FileUtils.month + ".db";
	final String TABLE_NAME = FileUtils.dataBase.get(0)[0];
	final String WINE = "wine";
	final String VOLUME = "volume";
	final String COUNTER_1 = "counter_1";
	final String COUNTER_2 = "counter_2";
	final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
			"(_id INTEGER PRIMARY KEY, " +
			WINE + " TEXT, " + VOLUME + " TEXT, " + COUNTER_1 + " TEXT, " + COUNTER_2 + " TEXT)";
	final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

	Context mContext;

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VER);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
		fillData(db);
		db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_TABLE);
		onCreate(db);
	}

	private void fillData(SQLiteDatabase db) {
		if (db != null) {
			ContentValues values;
			String[] dat = FileUtils.dataBase.get(0);
			values = new ContentValues();
			values.put(WINE, dat[1]);
			values.put(VOLUME, dat[2]);
			values.put(COUNTER_1, dat[3]);
			values.put(COUNTER_2, dat[4]);
			db.insert(TABLE_NAME, null, values);

		} else {
			Log.d("DBHelper", "db null");
		}
	}

}
