package com.dhgg.appusagemonitor;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

public class MyLineChart 
{
	private String m_app_name;
	private Point[] m_points;
	
	public MyLineChart( String app_name )
	{
		set_app_name( app_name );
	}

	public void addData(String app_name, Point[] points)
	{
		if (app_name == m_app_name)
		{
			set_points( points );
		}
	}
	
    public Intent getIntent( Context context )
    {
    	String units = "seconds";
    	double factor = 1;
    	for ( int i = 0; i < m_points.length; i++ )
        {      
    		if ( m_points[ i ].y > 60 )
    		{
    			units = "minutes";
    			factor = 60;
    		}
        }
    	
        //TimeSeries series = new TimeSeries("Line 1");
        TimeSeries time_series = new TimeSeries("");
        double y_min = 999999;
        double y_max = 0;
        int x_min = 30000000;
        int x_max = 20000000;
        for ( int i = 0; i < m_points.length; i++ )
        {      	
        	//series.add( m_points[i].x, m_points[i].y );
        	
        	double y_value = m_points[ i ].y / factor;
        	int x_value = m_points[i].x; 
        	time_series.add( new Date( (x_value / 10000) - 1900, 
 		                     ((x_value / 100 ) % 100 )-  1,
			                 x_value % 100 ),
			                 y_value );
        	if ( y_value < y_min )
        	{
        		y_min = y_value;
        	}
        	
        	if ( y_value > y_max )
        	{
        		y_max = y_value;
        	}
        	
        	if ( x_value < x_min )
        	{
        		x_min = x_value;
        	}
        	
        	if ( x_value > x_max )
        	{
        		x_max = x_value;
        	}
        }
        
        // Add zeros to the front and end of the line chart.
        time_series.add( increment_date( x_min, -1 ), 0 );
        time_series.add( increment_date( x_max,  1 ), 0 );
        
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(time_series);
        //dataset.addSeries(series);
  
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setPointStyle( PointStyle.CIRCLE );
        renderer.setFillPoints( true );
        renderer.setLineWidth( 5 );
        
        //renderer.setFillBelowLine( true );
        //renderer.setChartValuesTextSize( 10 );
        
        XYMultipleSeriesRenderer mrenderer = new 
            XYMultipleSeriesRenderer();
        mrenderer.addSeriesRenderer(renderer);

        if ( y_max > 0 )
        {
        	mrenderer.setYAxisMax( y_max * 1.10 );
        }
        
        if ( y_min < y_max )
        {
        	mrenderer.setYAxisMin( y_min * .8 );
        }
        
        mrenderer.setXTitle( "" );
        mrenderer.setShowLegend( false );
        mrenderer.setShowAxes( false );
        //mrenderer.setAxesColor( Color.BLACK );
        
        mrenderer.setLabelsTextSize( 30 );
        mrenderer.setXLabelsColor( Color.BLACK );
        //mrenderer.setXLabelsAngle( 45 );
        
        mrenderer.setAxisTitleTextSize( 30 );
        mrenderer.setYLabelsAlign( Align.RIGHT );
        mrenderer.setYLabelsColor( 0 , Color.BLACK );
        //mrenderer.setAxesColor( Color.GREEN );
        //mrenderer.setLabelsColor( Color.BLACK );
        //mrenderer.setYTitle("Usage in seconds");
        
        mrenderer.setApplyBackgroundColor( true );
        mrenderer.setBackgroundColor( Color.WHITE );

        mrenderer.setGridColor( Color.BLACK );
        mrenderer.setShowGridX( true );
        //mrenderer.setShowGrid( true );

        // Margins are for bottom, left, top, right
        mrenderer.setMargins(new int[] { 60, 60, 50, 20 });
        mrenderer.setMarginsColor( Color.LTGRAY );
        
        //mrenderer.setChartTitle("App Usage (seconds)");
        //mrenderer.setChartTitleTextSize( 80 );
        
        mrenderer.setShowCustomTextGrid( true );
        
        //Intent intent = ChartFactory.getLineChartIntent(context, dataset,mrenderer, "Usage for: "+m_app_name );
        String activityTitle = m_app_name+" usage in "+units;
        Intent intent = ChartFactory.getTimeChartIntent(context, dataset, mrenderer, "MMM dd", activityTitle );

        return intent;
    }

    private void set_app_name( String app_name )
    {
    	m_app_name = app_name;
    }
    
    private void set_points( Point[] points )
    {
    	m_points = points;
    }
    

    Date increment_date( int date_num, int increment )
    {
    	GregorianCalendar gcalendar = new GregorianCalendar( 
    			(date_num / 10000) ,
    			((date_num / 100 ) % 100 ) -  1,
                date_num % 100  );
    	
        gcalendar.add( Calendar.DATE, increment );
 
    	Date d = new Date( gcalendar.get( Calendar.YEAR ) - 1900, 
    			           (gcalendar.get( Calendar.MONTH )  ),
    			           gcalendar.get( Calendar.DATE ) );
    	return d;
    }
}
