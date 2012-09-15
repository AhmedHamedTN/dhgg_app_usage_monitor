package com.example.danshelloworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Db_handler extends SQLiteOpenHelper {
	
    // Database Name and Version
    private static final String DATABASE_NAME = "test_database";
    private static final int DATABASE_VERSION = 2;
 
    // Contacts table name
    private static final String TABLE_NAME = "test_table";
 
    // Contacts Table Columns names
    private static final String ID_COLUMN = "id";
    private static final String NAME_COLUMN = "name";
    private static final String START_TIME_COLUMN = "start_time_col";
    private static final String END_TIME_COLUMN = "end_time_col";
 
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
	                           START_TIME_COLUMN + " INTEGER," +
	                           END_TIME_COLUMN + " INTEGER )";
	    
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


	public void clear_data() 
	{
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_NAME, "1=1", null);	 
	    db.close();
	}
	
	public void addData(String name, int start, int end) 
	{
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(NAME_COLUMN, name);
	    values.put(START_TIME_COLUMN, start);
	    values.put(END_TIME_COLUMN, end);
	 
	    db.insert(TABLE_NAME, null, values);
	    db.close(); 
	}
	
    public ArrayList<Data_value> getAllData() 
    {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and dumping out the info
        Map <String, Integer> mp=new HashMap<String, Integer>();                
        if (cursor.moveToFirst()) {
            do {
            	System.out.println("ID: "+cursor.getString(0));
            		
            	String app_name = cursor.getString(1);
            	if (app_name.equals("screen_on") || app_name.equals("screen_off"))
            	{
            		continue;
            	}
            	

            	int time_diff = (cursor.getInt(3) - cursor.getInt(2) ) /1000;
            	if (mp.containsKey(app_name))
            	{
            		int value = (Integer)(mp.get(app_name));
            		mp.put(app_name, new Integer(value + time_diff));
            	}
            	else
            	{
            		mp.put(app_name, time_diff);
            	}

            } while (cursor.moveToNext());
        }
        db.close();

        ArrayList <Data_value> data = new ArrayList<Data_value>();
        
        for (Map.Entry<String, Integer> entry : mp.entrySet()) 
        {
        	Data_value dv = new Data_value( entry.getKey(), entry.getValue());
        	data.add(0,dv);
        }
    	
        return data;
    }


    public String getLastRowName() 
    {
        String selectQuery = "SELECT Name FROM " + TABLE_NAME +" ORDER BY "+END_TIME_COLUMN+" desc";
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and dumping out the info
        String name = "";
        if (cursor.moveToFirst()) 
        {
        	name = cursor.getString(0);
        }
        
        db.close();
        return name;
    }

    public void updateLast( int value ) 
    {
        // Select All Query
        String selectQuery = "SELECT id FROM " + TABLE_NAME +" ORDER BY "+END_TIME_COLUMN+" desc";
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and dumping out the info
        String id = "";
        if (cursor.moveToFirst()) 
        {
        	id = cursor.getString(0);
        }
        
        String strFilter = "id=" + id;
        System.out.println("updating with filter:"+strFilter);
        ContentValues args = new ContentValues();
        args.put(END_TIME_COLUMN, value);
        db.update(TABLE_NAME, args, strFilter, null);
        
        db.close();
    }
    
    public boolean do_update( String name ) 
    {
        // Select All Query
        String selectQuery = "SELECT "+NAME_COLUMN+" FROM " + TABLE_NAME +" ORDER BY "+END_TIME_COLUMN+" desc";
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and dumping out the info
        String prev_name = "";
        if (cursor.moveToFirst()) 
        {
        	prev_name = cursor.getString(0);
        }
        
        db.close();
        
        if (name.isEmpty() || prev_name.isEmpty())
        {
        	return false;
        }
        
        return name.equals( prev_name );        
    }
        
}
