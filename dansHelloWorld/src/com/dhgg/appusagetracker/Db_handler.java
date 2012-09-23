package com.dhgg.appusagetracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Db_handler extends SQLiteOpenHelper {
	
    // Database Name and Version
    private static final String DATABASE_NAME = "test_database";
    private static final int DATABASE_VERSION = 3;
 
    // Contacts table name
    private static final String TABLE_NAME = "test_table";
 
    // Contacts Table Columns names
    private static final String ID_COLUMN = "id";
    private static final String NAME_COLUMN = "name";
    private static final String START_TIME_COLUMN = "start_time_col";
    private static final String END_TIME_COLUMN = "end_time_col";
    private static final String PROCESS_NAME_COLUMN = "process_name_col";
    private static final String DATE_COLUMN = "date_col";
 
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
	                           END_TIME_COLUMN + " INTEGER, "+
	                           PROCESS_NAME_COLUMN + " TEXT," +
	                           DATE_COLUMN + " INTEGER )";
	                           
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
	
	public void addData(String name, String process_name ) 
	{	
		System.out.println("addData:"+name+" process_name:"+process_name);
	    SQLiteDatabase db = this.getWritableDatabase();

	    // clear out old values
		GregorianCalendar gcalendar = new GregorianCalendar();
		int date = gcalendar.get(Calendar.YEAR) * 10000 +
				   (gcalendar.get(Calendar.MONTH)+1)  * 100 +
				   gcalendar.get(Calendar.DATE) ;
	    db.delete(TABLE_NAME, DATE_COLUMN+" < "+(date-3), null);	 

	    // Add new data
    	long time = System.currentTimeMillis();
	    ContentValues values = new ContentValues();
	    values.put(NAME_COLUMN, name);
	    values.put(START_TIME_COLUMN, time);
	    values.put(END_TIME_COLUMN, time);
	    values.put(PROCESS_NAME_COLUMN,process_name);	 
	    values.put(DATE_COLUMN, date);


	    db.insert(TABLE_NAME, null, values);
	    db.close(); 
	}
	
    public ArrayList<Data_value> getAllData() 
    {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);        

		GregorianCalendar gcalendar = new GregorianCalendar();
		int today_date = gcalendar.get(Calendar.YEAR) * 10000 +
				         (gcalendar.get(Calendar.MONTH)+1)  * 100 +
				         gcalendar.get(Calendar.DATE) ;
	
 
        // Looping through all rows and dumping out the info
        Map <String, Data_value> mp_obj=new HashMap<String, Data_value>();  
        
        if (cursor.moveToFirst()) {
            do {	
            	String app_name = cursor.getString(1);
            	if (app_name.equals("screen_on") || app_name.equals("screen_off"))
            	{
            		continue;
            	}
            	
            	String process_name = cursor.getString(4);
            	int date = cursor.getInt(5);
            	if (today_date - date > 1)
            	{
            		continue;
            	}
            	

            	int time_diff = (cursor.getInt(3) - cursor.getInt(2) ) /1000;

            	// store name + object value map
            	if (mp_obj.containsKey(app_name))
            	{
            		Data_value dv = (mp_obj.get(app_name));
            		mp_obj.put(app_name, new Data_value(app_name, process_name,
            				                            dv.value + time_diff));
            	}
            	else
            	{
            		Data_value dv = new Data_value(app_name, process_name, time_diff);
            		mp_obj.put(app_name, dv );
            	}

            } while (cursor.moveToNext());
        }
        db.close();

        ArrayList <Data_value> data = new ArrayList<Data_value>();
        for (Map.Entry<String, Data_value> entry : mp_obj.entrySet()) 
        {
        	data.add(0,entry.getValue());
        }
        return data;
    }

    public void updateLast( ) 
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
        ContentValues args = new ContentValues();
        
        long time = System.currentTimeMillis();
        args.put(END_TIME_COLUMN, time);
        db.update(TABLE_NAME, args, strFilter, null);
        
        db.close();
    }
    
    public boolean do_update( String name ) 
    {
    	System.out.println("do_update:"+name);
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
       
    public void update_or_add( String name, String process_name )
    {
    	if ( do_update(name) )
    	{
    		updateLast( );
    	}
    	else
    	{
    		addData( name, process_name );
    	}
    }
}
