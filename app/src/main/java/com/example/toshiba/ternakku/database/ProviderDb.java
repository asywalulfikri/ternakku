package com.example.toshiba.ternakku.database;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.toshiba.ternakku.model.Model;
import com.example.toshiba.ternakku.util.Debug;

import java.util.ArrayList;


public class ProviderDb extends Database {
	
	public ProviderDb(SQLiteDatabase sqlite) {
		super(sqlite);
	}

	public boolean providerTableExist() {
		boolean result = false;
		
		if (mSqLite == null || !mSqLite.isOpen()) {
			return false;
		}
		
		String sql 	= "SELECT name FROM sqlite_master WHERE type = 'table' AND name='provider'"; 
		
		Cursor c	= mSqLite.rawQuery(sql, null);
		
		Debug.i(sql);
		
		if (c != null) {
			result = (c.getCount() == 0) ? false : true;
				
			c.close();
		}
		
		return result;	
	}
	
	public boolean createTable() {
		boolean result = false;
		
		if (mSqLite == null || !mSqLite.isOpen()) {
			return false;
		}
		
		String sql = "CREATE TABLE provider (name TEXT, shortnum TEXT)";
		
		Debug.i(sql);
		
		mSqLite.execSQL(sql);
		
		return result;	
	}
	
	public void update(ArrayList<Model> list) {
		if (mSqLite == null || !mSqLite.isOpen()) {
			return;
		}
		
		if (list == null) {
			return;
		}
			
		Debug.i("Updating provider ");

		mSqLite.delete("provider", "1", null);
		
		int size = list.size();
		
		for (int i = 0; i < size; i++) {
			Model model				= list.get(i);
			ContentValues values 	= new ContentValues();
					
			values.put("name",    	model.id);
			values.put("shortnum",  model.name);
			
			mSqLite.insert("provider", null, values);
		}
	}
	
	public ArrayList<Model> getList() {
		ArrayList<Model> list = null;
		
		if (mSqLite == null || !mSqLite.isOpen()) {
			return list;
		}
		
		String sql	= "SELECT * FROM provider";
	
		Cursor c 	= mSqLite.rawQuery(sql, null);
	
		Debug.i(sql);
		
		if (c != null) {
			
			if (c.moveToFirst()) {
				list = new ArrayList<Model>();				
				
				while (c.isAfterLast()  == false) {
					Model model 	= new Model();
					
					model.id 		= c.getString(c.getColumnIndex("name"));
					model.name 		= c.getString(c.getColumnIndex("shortnum"));
					
					Debug.i("Provider " + model.name);
					
					list.add(model);
					
					c.moveToNext();
				}
			}
			
			c.close();
		}
	
		return list;
	}
}
