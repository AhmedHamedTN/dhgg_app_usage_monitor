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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
    	//System.out.println("+++ ApplistFragment::onCreateView +++" );  
    	
        // Inflate the layout for this fragment
        m_view = inflater.inflate(R.layout.app_list, container, false);
	 
  	    return m_view;
    }
    
    @Override
    public void onAttach(Activity activity)
    {
    	super.onAttach(activity);
    	
    	//System.out.println("+++ ApplistFragment::onAttach +++" );
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
			    
			    /*
				if (mCredential == null)
				{
				    Log.w("DHGG", "no credentials set. exiting early");
					return;
				}

				// Create the request
		        AppusagemonitorApiMessagesAppUsageListByNameRequest request =  
			        new AppusagemonitorApiMessagesAppUsageListByNameRequest();
		        request.setAppName(app_name);
		        
                // Create a response handler that will receive the result or an error
		        CloudCallbackHandler<AppusagemonitorApiMessagesAppUsageListByNameResponse> handler = 
		            new CloudCallbackHandler<AppusagemonitorApiMessagesAppUsageListByNameResponse>() {
					@Override
                    public void onComplete(final AppusagemonitorApiMessagesAppUsageListByNameResponse result) {

						ArrayList <Point> data = new ArrayList<Point>();
					    int yyyymmdd = 0;
					    long totalForDay = 0;

						int numItems = result.getItems().size();
						for (int i = 0; i < numItems; i++)
						{
						    AppusagemonitorApiMessagesAppUsageHistResponseMessage h = 
						    		 result.getItems().get(i);
							Date dt = new Date(h.getStart());

							Calendar cal = Calendar.getInstance();
                            cal.setTime(dt);
							  
							int tmp_yyyymmdd = ( (cal.get(Calendar.YEAR) * 10000) + 
									             (cal.get(Calendar.MONTH) + 1) * 100 + 
									             cal.get(Calendar.DATE) );
									        		   

							if (i == 0)
							{
								yyyymmdd = tmp_yyyymmdd;
							}

							if (tmp_yyyymmdd == yyyymmdd)
								totalForDay += (h.getDuration() / 1000);
							else
							{
								data.add( new Point( yyyymmdd, (int) totalForDay ) );

								yyyymmdd = tmp_yyyymmdd;
								totalForDay = (h.getDuration() / 1000);
							}

							if (i == (numItems -1))
								data.add( new Point( yyyymmdd, (int) totalForDay ) );
						}

						Point[] point_arr = data.toArray(new Point[data.size()]);
						
						MyLineChart aChart = new MyLineChart(app_name);
					    aChart.addData(app_name, point_arr);
					    Intent aChartIntent = aChart.getIntent( getActivity().getApplicationContext() );
					    startActivity( aChartIntent );
					}
			        @Override
					public void onError(final IOException exception) {
						Log.w("DHGG","AppListFragment AppUsageListByNameResponse onError:"+exception.toString());
					}
		        };
				*/

				/*
			    // execute the lookup with the handler, on callback, we'll open the new page
				CloudBackendAsync cloudBackend = new CloudBackendAsync();
			    cloudBackend.setCredential(mCredential);
				cloudBackend.listByName(request, handler);
				*/
		
				Db_handler db_handler = new Db_handler( getActivity() );
				Point[] points = db_handler.getHistoricalData( app_name );

				MyLineChart aChart = new MyLineChart(app_name);
			    aChart.addData(app_name, points);
			    Intent aChartIntent = aChart.getIntent( getActivity().getApplicationContext() );
			    startActivity( aChartIntent );
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