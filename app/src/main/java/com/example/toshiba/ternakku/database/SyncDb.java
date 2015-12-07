package com.example.toshiba.ternakku.database;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.toshiba.ternakku.model.Model;
import com.example.toshiba.ternakku.util.Debug;

import java.util.ArrayList;


public class SyncDb extends Database {
	private static final String TBL_SKILL = "skill";
	private static final String TBL_CROP = "crop";

	public SyncDb(SQLiteDatabase sqlite) {
		super(sqlite);
	}

	public ArrayList<Model> getSkillList() {
		ArrayList<Model> list = null;

		if (mSqLite == null || !mSqLite.isOpen()) {
			return list;
		}

		String sql = "SELECT * FROM " + TBL_SKILL;

		Cursor c = mSqLite.rawQuery(sql, null);

		Debug.i(sql);

		if (c != null) {

			if (c.moveToFirst()) {
				list = new ArrayList<Model>();

				while (c.isAfterLast() == false) {
					Model model = new Model();

					model.id = c.getString(c.getColumnIndex("id"));
					model.name = c.getString(c.getColumnIndex("name"));

					Debug.i(model.id + " - " + model.name);

					list.add(model);

					c.moveToNext();
				}
			}

			c.close();
		}

		return list;
	}
	
	public boolean skillExist(String id) {
		boolean result = false;
		
		if (mSqLite == null || !mSqLite.isOpen()) {
			return false;
		}
		
		String sql 	= "SELECT * FROM skill WHERE id = '" + id + "'"; 
		
		Cursor c	= mSqLite.rawQuery(sql, null);
		
		if (c != null) {
			result = (c.getCount() == 0) ? false : true;
				
			c.close();
		}
		
		return result;	
	}
	
	public void updateSkill(ArrayList<Model> list) {
		if (mSqLite == null || !mSqLite.isOpen()) {
			return;
		}
		
		int insert 	= 0, update = 0;		
		int size 	= list.size();
			
		Debug.i("Updating table " + TBL_SKILL);			
		Debug.i("Total records: " + String.valueOf(size));
			
		for (int i = 0; i < size; i++) {
			Model model = list.get(i);
				
			ContentValues values = new ContentValues();
				
			values.put("id",    model.id);
			values.put("name",	model.name);
			
			if (skillExist(model.id)) {
				mSqLite.update(TBL_SKILL, values, "id = '" + model.id + "'", null);
				
				update++;
			} else {
				mSqLite.insert(TBL_SKILL, null, values);
				
				insert++;
			}			
		}
			
		Debug.i("Total insert " + String.valueOf(insert) + ", update " + String.valueOf(update));
	}
	
	public void updateCrop(ArrayList<Model> list) {
		if (mSqLite == null || !mSqLite.isOpen()) {
			return;
		}
		
		int insert 	= 0, update = 0;		
		int size 	= list.size();
			
		Debug.i("Updating table " + TBL_CROP);			
		Debug.i("Total records: " + String.valueOf(size));
			
		for (int i = 0; i < size; i++) {
			Model model = list.get(i);
				
			ContentValues values = new ContentValues();
				
			values.put("id",    model.id);
			values.put("name",	model.name);
			
			if (cropExist(model.id)) {
				mSqLite.update(TBL_CROP, values, "id = '" + model.id + "'", null);
				
				update++;
			} else {
				mSqLite.insert(TBL_CROP, null, values);
				
				insert++;
			}			
		}
			
		Debug.i("Total insert " + String.valueOf(insert) + ", update " + String.valueOf(update));
	}
	
	public boolean cropExist(String id) {
		boolean result = false;
		
		if (mSqLite == null || !mSqLite.isOpen()) {
			return false;
		}
		
		String sql 	= "SELECT * FROM crop WHERE id = '" + id + "'"; 
		
		Cursor c	= mSqLite.rawQuery(sql, null);
		
		if (c != null) {
			result = (c.getCount() == 0) ? false : true;
				
			c.close();
		}
		
		return result;	
	}
}
