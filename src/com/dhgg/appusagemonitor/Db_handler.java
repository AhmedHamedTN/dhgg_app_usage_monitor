package com.dhgg.appusagemonitor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Db_handler extends SQLiteOpenHelper 
{	
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
    private static final String PROCESS_NAME_COLUMN = "process_name_col";
    private static final String DATE_COLUMN = "date_col";
    
    private static final int MAX_DAYS_TO_SAVE = 2; 
 
    // Constructor
    public Db_handler(Context context) 
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		// Prepare statement to make a table 
	    String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
	                           ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
	    		               NAME_COLUMN + " TEXT," +
	                           START_TIME_COLUMN + " INTEGER," +
	                           END_TIME_COLUMN + " INTEGER, "+
	                           PROCESS_NAME_COLUMN + " TEXT," +
	                           DATE_COLUMN + " INTEGER )";
	                           
	    db.execSQL(CREATE_TABLE);
	    
		// Add index to speed up queries
	    db.execSQL("CREATE INDEX IF NOT EXISTS 	date_idx on "+TABLE_NAME+" ("+DATE_COLUMN+")");
	 }
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int old_version, int new_version) 
	{
		// Temporarily stop the drop.
		// 
		// TODO: if a real upgrade is needed, uncomment this out.
		// Drop older table if existed
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		//db.execSQL("DROP INDEX IF EXISTS date_idx");
				
		// Create tables 
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
	    SQLiteDatabase db = this.getWritableDatabase();

	    GregorianCalendar gcalendar = new GregorianCalendar();
		int date = gcalendar.get(Calendar.YEAR) * 10000 +
				   (gcalendar.get(Calendar.MONTH)+1)  * 100 +
				   gcalendar.get(Calendar.DATE) ;

		// Set up the times with a minimum display time of 1 second.
    	long time = System.currentTimeMillis();
    	long end_time = time + 1000;
    	
	    // Add new data
	    ContentValues values = new ContentValues();
	    values.put(NAME_COLUMN, name);
	    values.put(START_TIME_COLUMN, time);
	    values.put(END_TIME_COLUMN, end_time);
	    values.put(PROCESS_NAME_COLUMN,process_name);	 
	    values.put(DATE_COLUMN, date);
 

		try 
		{
		    db.insert(TABLE_NAME, null, values);
		} 
		catch(Exception e) { }
		
	    db.close(); 
	}

    public ArrayList<Data_value> getData( String hist_pref ) 
    {

    	consolidate_old_data();

		GregorianCalendar gcalendar = new GregorianCalendar();
		gcalendar.add(Calendar.DATE, - 1);
		int yest_date = gcalendar.get(Calendar.YEAR) * 10000 +
		                (gcalendar.get(Calendar.MONTH)+1)  * 100 +
		                gcalendar.get(Calendar.DATE) ;

		String select_query = "SELECT  * FROM "+TABLE_NAME+" WHERE "+DATE_COLUMN + " ";		
    	boolean check_for_today = false;
		boolean check_for_24_hours = false; 
    	if ( hist_pref.equals("s_h_p_today"))
    	{
    		check_for_today = true;
    		select_query += " > " + yest_date;
    	}
    	else if (hist_pref.equals("s_h_p_24h"))
    	{
    		check_for_24_hours = true;
    		select_query += " >= " + yest_date;
    	}
    	
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);        

        long current_time = System.currentTimeMillis();
 
        // Looping through all rows and dumping out the info
        Map <String, Data_value> mp_obj=new HashMap<String, Data_value>();  
        
        if (cursor.moveToFirst()) 
        {
            do {	
            	String app_name = cursor.getString(1);
            	if (app_name.equals("screen_on") || app_name.equals("screen_off"))
            	{
            		continue;
            	}
            	
            	String process_name = cursor.getString(4);
            	int date = cursor.getInt(5);
            	if ( check_for_today )
            	{
            		if ( yest_date >= date )
            		{
            			continue;
            		}
            	}
            	else if ( check_for_24_hours )
            	{
            		int time_diff = (int) current_time - cursor.getInt(3);
            		if ( time_diff > 24 * 60 * 60 * 1000 )
            		{
            			continue;
            		}
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
        
        Collections.sort( data, new DataValueComparator());
        return data;
    }
    

    public ArrayList<Data_value> getAllData(  ) 
    {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);        
 
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
            	int time_diff = (cursor.getInt(3) - cursor.getInt(2) ) /1000;

            	// store name + object value map
            	if (mp_obj.containsKey(app_name))
            	{
            		Data_value dv = (mp_obj.get(app_name));
            		mp_obj.put(app_name, new Data_value(app_name, process_name,
            				                            dv.value 	+ time_diff));
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
        
        Collections.sort( data, new DataValueComparator());
        return data;
    }

    public void updateLast( ) 
    {
        String selectQuery = "SELECT "+ID_COLUMN + "," + END_TIME_COLUMN +
        		             " FROM " + TABLE_NAME +" ORDER BY "+END_TIME_COLUMN+" desc";
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and dumping out the info
        String id = "";
        long last_end_time = -999;
        if (cursor.moveToFirst()) 
        {
        	id = cursor.getString(0);
        	last_end_time = cursor.getLong(1);
        }
        
        long new_end_time = System.currentTimeMillis();
        if ( last_end_time == -999 || last_end_time > new_end_time )
        {
        	return;
        }
        
        String strFilter = "id=" + id;
        ContentValues args = new ContentValues();
        
        args.put(END_TIME_COLUMN, new_end_time);
        
		try 
		{
	        db.update(TABLE_NAME, args, strFilter, null);
		} 
		catch(Exception e) { }


        db.close();
    }
    
    public boolean do_update( String name ) 
    {
        // Select All Query
        String selectQuery = "SELECT "+NAME_COLUMN+","+DATE_COLUMN+
        		             " FROM " + TABLE_NAME +" ORDER BY "+END_TIME_COLUMN+" desc";
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and dumping out the info
        String prev_name = "";
        int db_date = 0;
        if (cursor.moveToFirst()) 
        {
        	prev_name = cursor.getString(0);
        	db_date = cursor.getInt(1);
        }
        db.close();
        
        // If it's from a new date, make a new row.
        GregorianCalendar gcalendar = new GregorianCalendar();
		int todays_date = gcalendar.get(Calendar.YEAR) * 10000 +
			              (gcalendar.get(Calendar.MONTH)+1)  * 100 +
			              gcalendar.get(Calendar.DATE) ;
		if (todays_date != db_date)
		{
			return false;
		}
		

        // If the name's are blank, make a new row
        if (name.isEmpty() || prev_name.isEmpty())
        {
        	return false;
        }
        

        // If it's a repeat name, do an update, not an add.
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
    
    public void consolidate_old_data(  )
    {
		GregorianCalendar gcalendar = new GregorianCalendar();
		gcalendar.add(Calendar.DATE, - MAX_DAYS_TO_SAVE);
		int old_date = gcalendar.get(Calendar.YEAR) * 10000 +
				         (gcalendar.get(Calendar.MONTH)+1)  * 100 +
				         gcalendar.get(Calendar.DATE) ;
		
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE "+
                             DATE_COLUMN + " < " + old_date ;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);        
 
        // Looping through all rows to get the data
        Map <String, Data_value> mp_obj=new HashMap<String, Data_value>();          
        if (cursor.moveToFirst()) 
        {
            do 
            {	
            	String app_name = cursor.getString(1);            	
            	int time_diff = (cursor.getInt(3) - cursor.getInt(2) );            	
            	String process_name = cursor.getString(4);
        
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

        // Delete old data.
	    SQLiteDatabase db_write = this.getWritableDatabase();
	    String delete_command = DATE_COLUMN + " < " + old_date; 
	    db_write.delete(TABLE_NAME, delete_command, null);       

        // Add consolidated data.
        for (Map.Entry<String, Data_value> entry : mp_obj.entrySet()) 
        {
        	Data_value dv = entry.getValue();
   		
        	ContentValues values = new ContentValues();
    	    values.put(NAME_COLUMN, dv.description);
    	    values.put(START_TIME_COLUMN, 0);
    	    values.put(END_TIME_COLUMN, dv.value);
    	    values.put(PROCESS_NAME_COLUMN,dv.process_name);	 
    	    values.put(DATE_COLUMN, 20120101);
    	    
			try 
			{
	    	    db_write.insert(TABLE_NAME, null, values);
 
			} catch(Exception e) { }

        }
        
	    db_write.close();
	    
    }

}
