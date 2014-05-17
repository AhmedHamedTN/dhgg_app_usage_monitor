package com.dhgg.appusagemonitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.appspot.appusagemonitor.appusagemonitor.model.AppusagemonitorApiMessagesAppUsageHistResponseMessage;
import com.appspot.appusagemonitor.appusagemonitor.model.AppusagemonitorApiMessagesAppUsageListByNameRequest;
import com.appspot.appusagemonitor.appusagemonitor.model.AppusagemonitorApiMessagesAppUsageListByNameResponse;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AppListFragment extends Fragment 
{	
	public static View m_view;
	private Data_value[] m_data_arr;
	private Time_log[] m_data_audit_arr;

	private GoogleAccountCredential mCredential;
	
    @Override
    public void onAttach(Activity activity)
    {
    	super.onAttach(activity);
    	
    	//System.out.println("+++ ApplistFragment::onAttach +++" );
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
    	//System.out.println("+++ ApplistFragment::onCreateView +++" );  
    	
        // Inflate the layout for this fragment
        m_view = inflater.inflate(R.layout.app_list, container, false);
	 
  	    return m_view;
    }
    
    private void openChart(String appName, Point [] localPoints, Point[] cloudPoints)
    {
        //Log.d("DHGG","AppListFragment::openChart");
        ArrayList <Point> data = new ArrayList<Point>();
        
        int l = 0; 
        int c = 0;
        while (l < localPoints.length && cloudPoints != null && c < cloudPoints.length)
        {
        	Point lPoint = localPoints[l];
        	Point cPoint = cloudPoints[c];
        	 
        	if (lPoint.x < cPoint.x)
        	{
                //Log.w("DHGG","AppListFragment::openChart adding from localData "+lPoint.x);
                data.add( lPoint );
                l++;
        	}
        	else if (lPoint.x > cPoint.x )
        	{
                //Log.w("DHGG","AppListFragment::openChart adding from cloudData "+cPoint.x);
        		data.add( cPoint );
        		c++;
        	}
        	else
        	{
        		// Data found in the cloud and on the local machine. 
        		// Default to using the local data.
        		// May want to change to using cloud or summed data later.
                //Log.w("DHGG","AppListFragment::openChart default from localData "+lPoint.x);
        		data.add(lPoint);
        		
        		// Move to next item. 
        		l++;
        		c++;
        	}
        }

        while (l < localPoints.length)
        {
        	Point lPoint = localPoints[l];
            //Log.w("DHGG","AppListFragment::openChart finishing up localData "+lPoint.x);
            data.add( lPoint );
            l++;
        }

        while (cloudPoints != null && c < cloudPoints.length)
        {
        	Point cPoint = cloudPoints[c];
            //Log.w("DHGG","AppListFragment::openChart finishing up cloudData "+cPoint.x);
            data.add( cPoint );
            c++;
        }

        Point[] points = data.toArray(new Point[data.size()]);

        // Open chart 
        MyLineChart aChart = new MyLineChart(appName);
        aChart.addData(appName, points);
        Intent aChartIntent = aChart.getIntent( getActivity().getApplicationContext() );
        startActivity( aChartIntent );
    }

    public int refresh_screen( Data_value[] data_arr, boolean show_chart, GoogleAccountCredential cred)
	{
    	m_data_arr = data_arr ;
    	mCredential = cred;

    	View view = m_view;
    	if (view == null)
    	{
    		//System.out.println("+++ ApplistFragment::refresh_screen view=null exit +++" );	    
    	    return -2;
    	}

    	Data_value_adapter adapter = new Data_value_adapter( getActivity(), R.layout.name_value_row, m_data_arr);    	
		ListView list_view = (ListView) getActivity().findViewById(R.id.task_list_view);
		if ( list_view == null )
		{
	    	//System.out.println("+++ AppListFragment::refresh_screen list_view==null exit +++" );
	    	return -3;
		}

		// Add rows to the list view.
		adapter.set_use_colors_flag( show_chart );
		list_view.setAdapter(null);
		list_view.setAdapter(adapter);
		
		list_view.setOnItemClickListener(new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			{
				if (position >= m_data_arr.length)
				{
					return;
				}

			    final String app_name = m_data_arr[ position ].description;
			    final String packageName = m_data_arr[ position ].process_name;
                //Log.d("DHGG", "AppListFragment::onItemClick "+app_name+" "+packageName);
			    
                // Get local data. 
                Db_handler db_handler = new Db_handler( getActivity() );
                final Point[] localPoints = db_handler.getHistoricalData( app_name );
			    
			    // Get cloud data
			    // If  we have credentials
			    // and we have internet connection
			    // get historical info from app engine 
                if (mCredential != null)
                {
                    //Log.w("DHGG", "AppListFragment::onItemClick getting cloud info");
	
					// Create the request
			        AppusagemonitorApiMessagesAppUsageListByNameRequest request =  
				        new AppusagemonitorApiMessagesAppUsageListByNameRequest();
			        request.setAppName(app_name);
			        request.setPackageName(packageName);
		        
	                // Create a response handler that will receive the result or an error
			        CloudCallbackHandler<AppusagemonitorApiMessagesAppUsageListByNameResponse> handler = 
			            new CloudCallbackHandler<AppusagemonitorApiMessagesAppUsageListByNameResponse>() {
						@Override
	                    public void onComplete(final AppusagemonitorApiMessagesAppUsageListByNameResponse result) {

							//Log.d("DHGG","AppListFragment onItemClick AppUsageListByNameResponse onComplete");
							
							ArrayList <Point> data = new ArrayList<Point>();
							if (!result.isEmpty() && result.getItems() != null )
							{
                                int numItems = result.getItems().size();
                                for (int i = 0; i < numItems; i++)
                                {
                                    AppusagemonitorApiMessagesAppUsageHistResponseMessage h = result.getItems().get(i);
                                    long yyyymmdd = h.getStart();
                                    long totalForDay = h.getDuration() / 1000;
                                    data.add( new Point( (int) yyyymmdd, (int) totalForDay ));

                                    //Log.w("DHGG","AppListFragment onItemClick AppUsageListByNameResponse "+ i+" "+yyyymmdd+" "+totalForDay);
                                }
                            }

							Point[] cloudPoints = null;
							if (data.size() > 0)
							{
                                cloudPoints = data.toArray(new Point[data.size()]);
							}
							
							// Open chart using both local and cloud data
							openChart(app_name, localPoints, cloudPoints);
						}
				        @Override
						public void onError(final IOException exception) {
							Log.w("DHGG","AppListFragment onItemClick AppUsageListByNameResponse onError:"+exception.toString());

							openChart(app_name, localPoints, null);
						}
			        };

				    // execute the lookup with the handler, on callback, we'll open the new page
					CloudBackendAsync cloudBackend = new CloudBackendAsync();
				    cloudBackend.setCredential(mCredential);
					cloudBackend.listByName(request, handler);
			    }
			    else
			    {
                    Log.d("DHGG","AppListFragment::OnItemClick not using cloud data");
                    openChart(app_name, localPoints, null);
			    }
            }
	    });
		return -1;
	}

    public int refresh_screen_audit( Time_log[] data_arr ) 
	{    	
    	m_data_audit_arr = data_arr ;
    	View view = m_view;
    	if (view == null)
    	{
    		//System.out.println("+++ ApplistFragment::refresh_screen view=null exit +++" );	    
    	    return -2;
    	}

    	Time_log_adapter adapter = new Time_log_adapter( getActivity(), R.layout.name_value_row, m_data_audit_arr);    	
		ListView list_view = (ListView) getActivity().findViewById(R.id.task_list_view);
		if ( list_view == null )
		{
	    	//System.out.println("+++ AppListFragment::refresh_screen list_view==null exit +++" );
	    	return -3;
		}

		// Add rows to the list view.
		list_view.setAdapter(null);
		list_view.setAdapter(adapter);
		
		/*
		list_view.setOnItemClickListener(new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			{
			    String app_name = m_data_arr[ position ].description;
			    
				Db_handler db_handler = new Db_handler( getActivity() );
				Point[] points = db_handler.getHistoricalData( app_name );
			    
				MyLineChart aChart = new MyLineChart( app_name, points );			    
			    Intent aChartIntent = aChart.getIntent( getActivity().getApplicationContext() );
			    
			    startActivity( aChartIntent );
            }
	    });
	    */
		return -1;
	}
}
