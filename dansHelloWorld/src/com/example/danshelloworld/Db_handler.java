package com.example.danshelloworld;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Db_handler extends SQLiteOpenHelper {
	
    // Database Name and Version
    private static final String DATABASE_NAME = "test_database";
    private static final int DATABASE_VERSION = 1;
 
    // Contacts table name
    private static final String TABLE_NAME = "test_table";
 
    // Contacts Table Columns names
    private static final String ID_COLUMN = "id";
    private static final String NAME_COLUMN = "name";
    private static final String VALUE_COLUMN = "value";
 
    // Constructor
    public Db_handler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		// Prepare statement to make a table 
	    String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
	                           ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
	    		               NAME_COLUMN + " TEXT," +
	                           VALUE_COLUMN + " INTEGER )";
	    
	    db.execSQL(CREATE_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int old_version, int new_version) 
	{
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
 
        // Create tables again
        onCreate(db);
	}

	public void addData(String name, int value) 
	{
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(NAME_COLUMN, name);
	    values.put(VALUE_COLUMN, value);
	 
	    db.insert(TABLE_NAME, null, values);
	    db.close(); 
	}
	
    public void dumpAllData() 
    {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and dumping out the info
        if (cursor.moveToFirst()) {
            do {
                System.out.println("ID: "+Integer.parseInt(cursor.getString(0)));
                System.out.println("Name: "+cursor.getString(1));
                System.out.println("VALUE: "+Integer.parseInt(cursor.getString(2)));
            } while (cursor.moveToNext());
        }
    }
	
    public ArrayList<String> getAllData() 
    {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and dumping out the info
        ArrayList <String> data = new ArrayList<String>();
        
        if (cursor.moveToFirst()) {
            do {
            	String text = "ID: "+cursor.getString(0);
            	text += " Name: "+cursor.getString(1);
            	text += " VALUE: "+cursor.getString(2);
            	data.add(text);
            } while (cursor.moveToNext());
        }
        
        return data;
    }
	

}
