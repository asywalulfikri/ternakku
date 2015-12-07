package com.example.toshiba.ternakku.database;

import android.database.sqlite.SQLiteDatabase;

public class Database {

	protected SQLiteDatabase mSqLite;
	
	public Database(SQLiteDatabase sqlite) {
		mSqLite = sqlite;
	}
	
	public void reload(SQLiteDatabase sqlite) {
		mSqLite = sqlite;
	}
}
