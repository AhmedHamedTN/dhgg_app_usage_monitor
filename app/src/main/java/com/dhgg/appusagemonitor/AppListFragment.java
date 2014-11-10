package com.dhgg.appusagemonitor;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.util.Log;

public class AppListFragment extends Fragment 
{	
	public static View m_view;
	private DataValue[] m_data_arr;
    private TimeLog[] m_data_audit_arr;
    Activity m_activity;

	private GoogleAccountCredential mCredential;
	
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
        m_activity = activity;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_view = inflater.inflate(R.layout.app_list, container, false);
	 
  	    return m_view;
    }
    
    public int refresh_screen( DataValue[] data_arr, boolean show_chart, GoogleAccountCredential cred) {
    	m_data_arr = data_arr ;
    	mCredential = cred;

    	View view = m_view;
    	if (view == null) {
    	    return -2;
    	}

    	DataValueAdapter adapter = new DataValueAdapter( getActivity(), R.layout.name_value_row, m_data_arr);
		ListView list_view = (ListView) getActivity().findViewById(R.id.task_list_view);
		if ( list_view == null) {
	    	return -3;
		}

		// Add rows to the list view.
		adapter.set_use_colors_flag( show_chart );
		list_view.setAdapter(null);
		list_view.setAdapter(adapter);

        final FragmentActivity fActivity = this.getActivity();

		list_view.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

				if (position >= m_data_arr.length) {
					return;
				}

			    final String app_name = m_data_arr[ position ].description;
			    final String packageName = m_data_arr[ position ].process_name;

                //Log.d(Consts.LOGTAG, "AppListFragment::onItemClick "+app_name+" "+packageName);

                Intent intent = new Intent(fActivity, HistoryPlotActivity.class);
                intent.putExtra("app_name",app_name);
                intent.putExtra("package_name", packageName);

                startActivity(intent);
            }
	    });
		return -1;
	}

    public int refresh_screen_audit( TimeLog[] data_arr ) {
    	m_data_audit_arr = data_arr ;
    	View view = m_view;
    	if (view == null)
    	{
    		//System.out.println("+++ ApplistFragment::refresh_screen view=null exit +++" );	    
    	    return -2;
    	}

    	TimeLogAdapter adapter = new TimeLogAdapter( getActivity(), R.layout.name_value_row, m_data_audit_arr);
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
