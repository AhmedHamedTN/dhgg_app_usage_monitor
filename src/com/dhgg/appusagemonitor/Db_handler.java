package com.dhgg.appusagemonitor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
public class Db_handler extends SQLiteOpenHelper 
{	
	// Version number
    private static final int DATABASE_VERSION = 3;
	
    // Database name 
    private static final String DATABASE_NAME = "test_database";
 
    // Table names
    private static final String TABLE_NAME = "test_table";
    private static final String MAPPING_TABLE_NAME = "app_to_process_table";
    private static final String APP_HISTORY_TABLE = "app_history_table";
 
    // Column names
    private static final String ID_COLUMN = "id";
    private static final String NAME_COLUMN = "name";
    private static final String START_TIME_COLUMN = "start_time_col";
    private static final String END_TIME_COLUMN = "end_time_col";
    private static final String DATE_COLUMN = "date_col";
    
    private static final String PROCESS_NAME_COLUMN = "process_name_col";
    
    private static final String VALUE_COLUMN = "value_col"; 
    
    private static final int MAX_DB_ROWS_SYNC = 1500;
    private static final int MAX_SYNC_SEND = 150;
 
    // Constructor
    public Db_handler(Context context) 
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
	private void add_data(String name, String process_name ) 
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
	    values.put(DATE_COLUMN, date);
	    
	    //Log.w("DHGG","add_data n:"+name+" p:"+process_name);

		try 
		{
		    db.insert(TABLE_NAME, null, values);
		} 
		catch(Exception e) 
		{
			Log.e("DHGG","Error. Db_handler::add_data."+e);
		}
		
	    db.close(); 
	}
	
	public void clear_data() 
	{
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_NAME, "1=1", null);	 
	    db.close();
	}
	
	private void consolidate_old_data( int date_to_archive )
    {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE "+
                             DATE_COLUMN + " = " + date_to_archive;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);        
 
        // Get data to archive
        Map <String, Data_value> mp_obj=new HashMap<String, Data_value>();          
        if (cursor.moveToFirst()) 
        {
            do 
            {	
            	String app_name = cursor.getString(1);
            	if (app_name.equals("screen_on") || app_name.equals("screen_off"))
            	{
            		continue;
            	}
            	
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
	    String delete_command = DATE_COLUMN + " <= " + date_to_archive; 
	    db_write.delete(TABLE_NAME, delete_command, null);       

        // Move consolidated data to archive.
        for (Map.Entry<String, Data_value> entry : mp_obj.entrySet()) 
        {
        	Data_value dv = entry.getValue();
   		
        	ContentValues values = new ContentValues();
    	    values.put( NAME_COLUMN, dv.description );	 
    	    values.put( DATE_COLUMN, date_to_archive );
    	    values.put( VALUE_COLUMN, dv.value );
    	    
			try 
			{
	    	    db_write.insert( APP_HISTORY_TABLE, null, values);
			} catch(Exception e) 
			{
				System.out.println("Error. Db_handler::consolidate_old_data."+e);
			}
        }
        
	    db_write.close();
	    
    }
	
	private void create_history_table(SQLiteDatabase db)
	{
		String DROP_INDEX = "DROP INDEX IF EXISTS history_name_idx";
		String DROP_TABLE = "DROP TABLE IF EXISTS " + APP_HISTORY_TABLE;
		
	    String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + APP_HISTORY_TABLE + "(" +
	                           ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
	    		               NAME_COLUMN + " TEXT," + 
	    		               DATE_COLUMN + " INTEGER," +
	                           VALUE_COLUMN + " INTEGER )";
	    String CREATE_INDEX = "CREATE INDEX IF NOT EXISTS history_name_idx on "+
	                           APP_HISTORY_TABLE+" ("+NAME_COLUMN+")";  
	    
		try 
		{
			db.beginTransaction();

			db.execSQL( DROP_INDEX );
			db.execSQL( DROP_TABLE );
			
		    db.execSQL( CREATE_TABLE );
		    db.execSQL( CREATE_INDEX );
		    
		    db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
	    }	
	}

	private void create_main_table(SQLiteDatabase db)
	{
		String DROP_INDEX = "DROP INDEX IF EXISTS date_idx";
		String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
		
	    String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
	                           ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
	    		               NAME_COLUMN + " TEXT," +
	                           START_TIME_COLUMN + " INTEGER," +
	                           END_TIME_COLUMN + " INTEGER, "+
	                           DATE_COLUMN + " INTEGER )";
	    String CREATE_INDEX = "CREATE INDEX IF NOT EXISTS 	date_idx on "+
	                           TABLE_NAME+" ("+DATE_COLUMN+")";  
	    
		try 
		{
			db.beginTransaction();

			db.execSQL( DROP_INDEX );
			db.execSQL( DROP_TABLE );
			
		    db.execSQL( CREATE_TABLE );
		    db.execSQL( CREATE_INDEX );
		    
		    db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
	    }	
	}
		
	private void create_mapping_table(SQLiteDatabase db) 
	{
		String DROP_INDEX = "DROP INDEX IF EXISTS mapping_name_idx";
		
		String DROP_TABLE = "DROP TABLE IF EXISTS " + MAPPING_TABLE_NAME;
		
		
	    String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + MAPPING_TABLE_NAME + "(" +
	                           ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
	    		               NAME_COLUMN + " TEXT," +
	                           PROCESS_NAME_COLUMN + " TEXT )";
	    
	    String CREATE_INDEX = "CREATE INDEX IF NOT EXISTS mapping_name_idx on "+
	                           MAPPING_TABLE_NAME+" ("+NAME_COLUMN+")";
	    		
	    try 
		{
			db.beginTransaction();
				
			// Clean up first 
			db.execSQL( DROP_INDEX );
			db.execSQL( DROP_TABLE );
			
		    db.execSQL(CREATE_TABLE);
		    db.execSQL(CREATE_INDEX);
		    
		    db.setTransactionSuccessful(  );
		}
		finally
		{
			db.endTransaction();
	    }
	    
	}
		
	private boolean do_update( String name ) 
    {
        // Select All Query
        String selectQuery = "SELECT "+NAME_COLUMN+","+DATE_COLUMN+
        		             " FROM " + TABLE_NAME +" ORDER BY "+END_TIME_COLUMN+" desc";
        //Log.w("DHGG","Db_handler::do_update n:"+name);
 
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
	
	// use only for upgrading database. 
	// eventually delete this
	private void drop_column( SQLiteDatabase db )
	{ 
		try 
		{
			db.beginTransaction();
			db.execSQL("CREATE TEMPORARY TABLE tmp_table("+ID_COLUMN+", "+
			           NAME_COLUMN+", "+START_TIME_COLUMN+", "+
					   END_TIME_COLUMN+", "+DATE_COLUMN+")");
			db.execSQL("INSERT INTO tmp_table SELECT "+ID_COLUMN+", "+
			           NAME_COLUMN+", "+START_TIME_COLUMN+", "+
					   END_TIME_COLUMN+", "+DATE_COLUMN+" FROM "+TABLE_NAME);
			
			db.execSQL("DROP TABLE "+TABLE_NAME);
			db.execSQL("CREATE TABLE "+TABLE_NAME+" ("+ID_COLUMN+", "+
			           NAME_COLUMN+", "+START_TIME_COLUMN+", "+
					   END_TIME_COLUMN+", "+DATE_COLUMN+") ");
			
			db.execSQL("INSERT INTO "+TABLE_NAME+" SELECT "+ID_COLUMN+", "+
			           NAME_COLUMN+", "+START_TIME_COLUMN+", "+
					   END_TIME_COLUMN+", "+DATE_COLUMN+" FROM tmp_table");
	
			db.execSQL("DROP TABLE tmp_table");
			
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
		}
	}
	
	public Map<String,String> get_app_to_process_map(String componentName) 
    {
    	//Log.w("DHGG","Db_handler::get_app_to_process_map");
		String select_query = 
				"SELECT " + NAME_COLUMN +"," + PROCESS_NAME_COLUMN +
				" FROM " +MAPPING_TABLE_NAME + " WHERE " + 
				" '" +componentName+ "' " +
				" LIKE " +
			    " '%' || " + PROCESS_NAME_COLUMN + " || '%' " +
				" "	;
    	
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);        
        
        Map<String,String> output_obj = new HashMap<String,String>();
        if (cursor.moveToFirst()) 
        {
            do {	
            	String app_name = cursor.getString(0);
            	String process_name = cursor.getString(1);
            	output_obj.put(app_name, process_name);	
		    	//Log.w("DHGG","Db_handler::get_app_to_process_map a:"+app_name+" p:"+process_name);
            	break;
            } while (cursor.moveToNext());
        }
        db.close();
        
        return output_obj;
    }
	
    private Map <String, Data_value> get_archive_data( Map<String, Data_value> input_obj )
    {
    	Map <String, Data_value> output_obj = input_obj;

    	String select_query = "SELECT " + 
                              " a." + NAME_COLUMN +
                              ", a." + VALUE_COLUMN + 
                              ", b." + PROCESS_NAME_COLUMN + 
                              "  FROM " +
                              " " + APP_HISTORY_TABLE + " a " + 
                              " JOIN "+ MAPPING_TABLE_NAME +" b ON " +
                              " a."+NAME_COLUMN+" = b."+NAME_COLUMN;
    	                      
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(select_query, null);        
		
		if (cursor.moveToFirst()) 
		{
			do {				
				String app_name = cursor.getString( 0 );
				int value = cursor.getInt( 1 ) / 1000;
			
				// store name + object value map
				if ( output_obj.containsKey( app_name ) )
				{
					Data_value dv = (output_obj.get( app_name ));
					output_obj.put(app_name, new Data_value(app_name, dv.process_name,
	  						                                dv.value + value));
				}
				else
				{
					String process_name = cursor.getString( 2 );
					
					Data_value dv = new Data_value(app_name, process_name, value);
					output_obj.put(app_name, dv );
				}
			
			} while (cursor.moveToNext());
		}
		db.close();
		   	
    	
    	return output_obj;
    }

    public ArrayList<Data_value> getData( String hist_pref, String input_app_name ) 
    {
		GregorianCalendar gcalendar = new GregorianCalendar( );
		gcalendar.add( Calendar.DATE, - 1 );
		int yest_date =  gcalendar.get( Calendar.YEAR ) * 10000 +
		                (gcalendar.get( Calendar.MONTH ) + 1 )  * 100 +
		                 gcalendar.get( Calendar.DATE ) ;

		gcalendar.add( Calendar.DATE, - 1 );
		int archive_date =  gcalendar.get( Calendar.YEAR ) * 10000 +
		                    (gcalendar.get( Calendar.MONTH ) + 1 )  * 100 +
		                    gcalendar.get( Calendar.DATE ) ;

		consolidate_old_data( archive_date );
		
		String select_query = "SELECT " + 
		                      "  a." + NAME_COLUMN +
							  ", a." + DATE_COLUMN +
				              ", b." + PROCESS_NAME_COLUMN +
				              ", a." + START_TIME_COLUMN + 
				              ", a." + END_TIME_COLUMN +
				              "  FROM " +
				              " ( SELECT  * FROM "+TABLE_NAME; ;
				              
    	boolean check_for_today = false;
		boolean check_for_24_hours = false; 
		
    	if ( hist_pref.equals("s_h_p_yest"))
    	{
    		select_query += " WHERE "+DATE_COLUMN + " = " + yest_date;
    	}
    	else if ( hist_pref.equals("s_h_p_today"))
    	{
    		check_for_today = true;
    		select_query += " WHERE "+DATE_COLUMN + " > " + yest_date;
    	}
    	else if (hist_pref.equals("s_h_p_24h"))
    	{
    		check_for_24_hours = true;
    	}
    	
    	select_query += ") a JOIN "+MAPPING_TABLE_NAME+" b ON "+
    	                "a."+NAME_COLUMN+" = b."+NAME_COLUMN;
    	
    	if ( !input_app_name.equals("") )
    	{
    		select_query += " AND a."+NAME_COLUMN+" = '"+input_app_name+"'";
    	}
    	
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);        

        long current_time = System.currentTimeMillis();
 
        // Looping through all rows and dumping out the info
        Map <String, Data_value> mp_obj=new HashMap<String, Data_value>();  
        
        if (cursor.moveToFirst()) 
        {
            do {	
            	String app_name = cursor.getString( 0 ).replaceAll("''", "'");
            	if (app_name.equals("screen_on") || app_name.equals("screen_off"))
            	{
            		continue;
            	}
            	
            	int date = cursor.getInt(1);
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

            	String process_name = cursor.getString( 2 ).replaceAll("''", "'");
            	int time_diff = (cursor.getInt(4) - cursor.getInt(3) ) /1000;

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

        // Get data from history archive 
        if ( hist_pref.equals( "s_h_p_all" ) )
        {
            mp_obj = get_archive_data( mp_obj );
        }
        	
        ArrayList <Data_value> data = new ArrayList<Data_value>();
        for (Map.Entry<String, Data_value> entry : mp_obj.entrySet()) 
        {
        	data.add(0,entry.getValue());
        }
        
        Collections.sort( data, new DataValueComparator());
        return data;
    }

    public Point[] getHistoricalData( String app_name )
    {
    	//Log.w("DHGG","getHistoricalData a:"+app_name);
    	app_name = app_name.replaceAll("'", "''");
    	
    	String select_query = "SELECT " + 
                              "  a." + DATE_COLUMN +
                              ", a." + VALUE_COLUMN + 
                              "  FROM " +
                              " " + APP_HISTORY_TABLE + " a " +
                              "  WHERE a." + NAME_COLUMN + 
                              " = '" + app_name + "' "+
                              " ";
    	
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(select_query, null);        
		
		ArrayList <Point> data = new ArrayList<Point>();
		if (cursor.moveToFirst()) 
		{
			do {				
				int date = cursor.getInt( 0 );
				int value = cursor.getInt( 1 ) / 1000;
			
				data.add( new Point( date, value ) );
			} while (cursor.moveToNext());
		}
		db.close();
		
		// Add data for today as well.
		GregorianCalendar gcalendar = new GregorianCalendar( );
		int today_date =  gcalendar.get( Calendar.YEAR ) * 10000 +
		                  (gcalendar.get( Calendar.MONTH ) + 1 )  * 100 +
		                  gcalendar.get( Calendar.DATE ) ;
		gcalendar.add( Calendar.DATE, - 1 );
		int yest_date =  gcalendar.get( Calendar.YEAR ) * 10000 +
		                (gcalendar.get( Calendar.MONTH ) + 1 )  * 100 +
		                 gcalendar.get( Calendar.DATE ) ;

	    ArrayList<Data_value> yest_data = getData( "s_h_p_yest", app_name );
	    if ( yest_data.size() > 0 )
	    {
	    	data.add( new Point( yest_date, yest_data.get(0).value ) );
	    }		
		
	    ArrayList<Data_value> today_data = getData( "s_h_p_today", app_name );
	    if ( today_data.size() > 0 )
	    {
	    	data.add( new Point( today_date, today_data.get(0).value ) );
	    }		
		
		Point[] point_arr = data.toArray(new Point[data.size()]);
    	return point_arr;
    }

    public ArrayList<Time_log> getTimeLog( String hist_pref, String input_app_name ) {
		GregorianCalendar gcalendar = new GregorianCalendar( );
		gcalendar.add( Calendar.DATE, - 1 );
		int yest_date =  gcalendar.get( Calendar.YEAR ) * 10000 +
		                (gcalendar.get( Calendar.MONTH ) + 1 )  * 100 +
		                 gcalendar.get( Calendar.DATE ) ;

		gcalendar.add( Calendar.DATE, - 1 );
		int archive_date =  gcalendar.get( Calendar.YEAR ) * 10000 +
		                    (gcalendar.get( Calendar.MONTH ) + 1 )  * 100 +
		                    gcalendar.get( Calendar.DATE ) ;

		consolidate_old_data( archive_date );
		
		String select_query = "SELECT " + 
		                      "  a." + NAME_COLUMN +
							  ", a." + DATE_COLUMN +
				              ", b." + PROCESS_NAME_COLUMN +
				              ", a." + START_TIME_COLUMN + 
				              ", a." + END_TIME_COLUMN +
				              "  FROM " +
				              " ( SELECT  * FROM "+TABLE_NAME; ;
				              
    	boolean check_for_today = false;
		boolean check_for_24_hours = false; 
		
    	if ( hist_pref.equals("s_h_p_yest"))
    	{
    		select_query += " WHERE "+DATE_COLUMN + " = " + yest_date;
    	}
    	else if ( hist_pref.equals("s_h_p_today"))
    	{
    		check_for_today = true;
    		select_query += " WHERE "+DATE_COLUMN + " > " + yest_date;
    	}
    	else if (hist_pref.equals("s_h_p_24h"))
    	{
    		check_for_24_hours = true;
    	}
    	
    	select_query += ") a JOIN "+MAPPING_TABLE_NAME+" b ON "+
    	                "a."+NAME_COLUMN+" = b."+NAME_COLUMN;
    	
    	if ( !input_app_name.equals("") )
    	{
    		select_query += " AND a."+NAME_COLUMN+" = '"+input_app_name+"'";
    	}
    	//Log.w("DHGG","getTimeLog q:"+select_query);
    	
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);        

        long current_time = System.currentTimeMillis();
 
        ArrayList <Time_log> data = new ArrayList<Time_log>();
        if (cursor.moveToFirst()) {
            do {	
            	String app_name = cursor.getString( 0 ).replaceAll("''", "'");
            	if (app_name.equals("screen_on") || app_name.equals("screen_off"))
            	{
            		continue;
            	}
            	
            	int date = cursor.getInt(1);
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

            	String process_name = cursor.getString( 2 ).replaceAll("''", "'");
            	long startTime = cursor.getLong(3);
            	long endTime = cursor.getLong(4);
                //Log.w("DHGG","getTimeLog s:"+startTime+" :e"+endTime );
            	
            	Time_log t = new Time_log(app_name, process_name, startTime, endTime);

            	int num_points = data.size();
            	if (num_points == 0)
            	{
            		data.add(t);
            		continue;
            	}
            	
            	Time_log prev_log = data.get(num_points -1);
            	if (t.description.equals(prev_log.description) &&
            	    Math.abs(prev_log.end_time - t.start_time) < 1000)
            	{
            		prev_log.end_time = t.end_time;
            		data.set(num_points - 1,  prev_log);
            	}
            	else
            	{
            		data.add(t);
            	}
            	
            	
            } while (cursor.moveToNext());
        }
        db.close();

        return data;
    }
   
    public ArrayList<Time_log> getTimeLogFromTime(long lastAppEndTime) 
    {
		String select_query = "SELECT " + 
		                      "  a." + NAME_COLUMN +
							  ", a." + DATE_COLUMN +
				              ", b." + PROCESS_NAME_COLUMN +
				              ", a." + START_TIME_COLUMN + 
				              ", a." + END_TIME_COLUMN +
				              "  FROM " +
				              " ( SELECT  * FROM "+TABLE_NAME; ;
				              
    	if (lastAppEndTime > 0)
    	{
    		select_query += " WHERE "+END_TIME_COLUMN + " > " + lastAppEndTime;
    	}
    	select_query += " ORDER BY "+END_TIME_COLUMN+" ASC ";
    	select_query += " LIMIT  " + MAX_DB_ROWS_SYNC;
    	select_query += ") a JOIN "+MAPPING_TABLE_NAME+" b ON "+
    	                "a."+NAME_COLUMN+" = b."+NAME_COLUMN;

    	//Log.w("DHGG","getTimeLog q:"+select_query);
    	
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);        

        ArrayList <Time_log> data = new ArrayList<Time_log>();
        if (cursor.moveToFirst()) {
            do {	
            	String app_name = cursor.getString( 0 ).replaceAll("''", "'");
            	if (app_name.equals("screen_on") || app_name.equals("screen_off"))
            	{
            		continue;
            	}
            	
            	String process_name = cursor.getString( 2 ).replaceAll("''", "'");
            	long startTime = cursor.getLong(3);
            	long endTime = cursor.getLong(4);
                //Log.w("DHGG","getTimeLog s:"+startTime+" :e"+endTime );
            	
            	Time_log t = new Time_log(app_name, process_name, startTime, endTime);

            	int num_points = data.size();
            	if (num_points == 0)
            	{
            		data.add(t);
            		continue;
            	}
            	
            	Time_log prev_log = data.get(num_points -1);
            	if (t.description.equals(prev_log.description) &&
            	    Math.abs(prev_log.end_time - t.start_time) < 5000)
            	{
            		prev_log.end_time = t.end_time;
            		data.set(num_points - 1,  prev_log);
            	}
            	else
            	{
            		data.add(t);
            	}
            	
            	// Break early 
            	if (data.size() > MAX_SYNC_SEND)
            	{
            		break;
            	}
            	
            } while (cursor.moveToNext());
        }
        db.close();

        return data;
    }

    
    @Override
	public void onCreate(SQLiteDatabase db) 
	{
		// System.out.println("Db_handler::onCreate");		
		create_main_table( db );
		
		create_mapping_table( db );
		
		create_history_table( db );
	}
    
    @Override
	public void onUpgrade(SQLiteDatabase db, int old_version, int new_version) 
	{
		// System.out.println("Db_handler::onUpgrade "+" "+old_version+" "+new_version);
		// create new mapping table		
		
		// From version 2 --> 3, we are 
		// removing a column from the main table 
		// and making a new mapping table.
		if (old_version <= 2 && new_version == 3)
		{
			create_mapping_table( db );
			
			create_history_table( db );
			
			try
			{
				populate_app_process_map( db );
			}
			catch ( Exception e ) 
			{
				System.out.println("Error. while doing populate_app_process_map."+e);
			}
			
			// drop column from main table
			drop_column( db );			
		}
		else if (old_version == 3 && new_version == 4)
		{
		
		}
		else
		{
			// Create tables 
			onCreate(db);
		}
		
	
	}
    
    // use only for upgrading database.
	// eventually delete this
	private void populate_app_process_map( SQLiteDatabase db )
	{
		/*
		String selectQuery = "SELECT "+" "+NAME_COLUMN+", "+
		                     PROCESS_NAME_COLUMN+" FROM "+ 
				             TABLE_NAME;
		 
        Cursor cursor = db.rawQuery(selectQuery, null);        
 
        // Looping through all rows and dumping out the info
        Map <String, String> mp_obj=new HashMap<String, String>();  
        
        if (cursor.moveToFirst()) 
        {
            do 
            {	
            	String app_name = cursor.getString(0);
            	if (app_name.equals("screen_on") || app_name.equals("screen_off"))
            	{
            		continue;
            	}
            	

            	if (mp_obj.containsKey(app_name))
            	{
            		continue;
            	}
            	
            	String process_name = cursor.getString(1);            	
            	mp_obj.put(app_name, process_name );            	

            } while (cursor.moveToNext());
        }
        
	
        // Move mapped data to archive.
	    for (Map.Entry<String, String> entry : mp_obj.entrySet())
	    {
	    	ContentValues values = new ContentValues();
	    	values.put( NAME_COLUMN, entry.getKey() );	 
	    	values.put( PROCESS_NAME_COLUMN, entry.getValue() );
	    	    
	    	try 
			{
		        db.insert( MAPPING_TABLE_NAME, null, values);
			} 
	    	catch(Exception e) 
	    	{ 
	    		System.out.println("Error in populate_app_process_map:"+e);
	    	}
	        
	    }
	    */
	}
       
    private void update_last( ) 
    {
    	//Log.w("DHGG","Db_handler::update_last");
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
		catch(Exception e) 
		{
			System.out.println("Error. Db_handler::update_last:"+e);			
		}


        db.close();
    }
    
    public void update_or_add( String name, String process_name )
    {
    	//Log.w("DHGG","update_or_add n:"+name+" p:"+process_name);
    	if ( do_update(name) )
    	{
    		update_last( );
    	}
    	else
    	{
    		add_data( name, process_name );
    	}
    	
    	update_or_add_to_mapping_table( name, process_name );
    }
    
    private void update_or_add_to_mapping_table( String name, String process_name ) 
    {
    	String search_name = name.replaceAll("'", "''");

    	//Log.w("DHGG","update_or_add_to_mapping_table n:"+search_name+" p:"+process_name);
        String selectQuery = "SELECT "+PROCESS_NAME_COLUMN+" FROM " + 
    	                     MAPPING_TABLE_NAME +" WHERE "+
        		             NAME_COLUMN +" = '"+ search_name + "'";
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and dumping out the info
        String prev_process_name = "";
        if (cursor.moveToFirst()) 
        {
        	prev_process_name = cursor.getString(0);
        }
        db.close();
    	
        if ( prev_process_name.equals( process_name ))
        {
        	// do nothing 
        	return;
        }


	    SQLiteDatabase db_write = this.getWritableDatabase();
        if ( prev_process_name.equals("") )
        {
    	    // Add new data
    	    ContentValues values = new ContentValues();
    	    values.put(NAME_COLUMN, name);
    	    values.put(PROCESS_NAME_COLUMN,process_name);
    		try 
    		{
    		    db_write.insert(MAPPING_TABLE_NAME, null, values);
    		} 
    		catch(Exception e) 
    		{
    			System.out.println("Error. Db_handler::update_or_add_to_mapping add:"+e);
    		}
        }
        else 
        {
	    	// Update		    
			String strFilter = NAME_COLUMN+"=" + name;
			ContentValues args = new ContentValues();
			args.put(PROCESS_NAME_COLUMN, process_name);
			
			try 
			{
				db_write.update(MAPPING_TABLE_NAME, args, strFilter, null);
			} 
			catch(Exception e) 
			{
				System.out.println("Error. Db_handler::update_or_add_to_mapping update:"+e);
			}
        }
		db_write.close();
    }
}
