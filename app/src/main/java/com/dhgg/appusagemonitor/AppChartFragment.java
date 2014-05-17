package com.dhgg.appusagemonitor;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AppChartFragment extends Fragment 
{	
	public static View m_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {	
        // Inflate the layout for this fragment
        m_view = inflater.inflate(R.layout.app_chart, container, false);
	  
  	    return m_view;
    }
    
    public int refresh_screen( Data_value[] data_arr  ) 
	{
    	View view = m_view;
    	if (view == null)
    	{
    		//System.out.println("+++ AppChartFragment::refresh_screen view=null exit +++" );	    
    	    return -2;
    	}

    	
		PieChart pie_chart_view = (PieChart) getActivity().findViewById(R.id.pie_chart);
		if ( pie_chart_view == null )
		{
	    	//System.out.println("+++ AppListFragment::refresh_screen list_view==null exit +++" );
	    	return -3;
		}
		
		pie_chart_view.set_data( data_arr );
			
    	return 1;
	}

} 