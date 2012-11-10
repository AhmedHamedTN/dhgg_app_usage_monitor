package com.dhgg.appusagemonitor;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class AppListFragment extends Fragment 
{	
	public static View m_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
    	System.out.println("+++ ApplistFragment::onCreateView +++" );  
    	
        // Inflate the layout for this fragment
        m_view = inflater.inflate(R.layout.app_list, container, false);
	 
  	    return m_view;
    }
    
    @Override
    public void onAttach(Activity activity)
    {
    	super.onAttach(activity);
    	
    	System.out.println("+++ ApplistFragment::onAttach +++" );
    }
    
    public int refreshScreen( Data_value[] data_arr, boolean show_chart  ) 
	{
    	View view = m_view;
    	if (view == null)
    	{
    		System.out.println("+++ ApplistFragment::refreshScreen view=null exit +++" );	    
    	    return -2;
    	}

    	Data_value[] normal_data_arr;
    	if ( show_chart )
    	{
	    	// Get data for slices
			int num_values = data_arr.length;
			float total = 0;
			for ( int i = 0; i < num_values; i++ )
			{
				total += data_arr[i].value;
			}
			
			int normal_data_arr_size = num_values;
			if ( num_values > 10 )
			{
				normal_data_arr_size = 10;
			}
			/*
			normal_data_arr = new Data_value[ normal_data_arr_size ];
			System.arraycopy( data_arr, 0, normal_data_arr, 0, normal_data_arr_size );
			
			int subtotal = 0;
			for ( int i = 0; i < normal_data_arr_size; i++)
			{
				subtotal += normal_data_arr[ i ].value;
				normal_data_arr[ i ].value = (int)((float)normal_data_arr[ i ].value / total) * 100; 
			}
			
			if ( normal_data_arr_size == 10 )
			{
				normal_data_arr[ normal_data_arr_size - 1 ].value = (1 - (int)((float)subtotal / (float)total ))* 100; 
			}
			
			*/
			normal_data_arr = data_arr;
    	}
    	else 
    	{
    		normal_data_arr = data_arr;
    	}
		

    	Data_value_adapter adapter = new Data_value_adapter( getActivity(), R.layout.name_value_row, normal_data_arr);    	
		ListView list_view = (ListView) getActivity().findViewById(R.id.task_list_view);
		if ( list_view == null )
		{
	    	System.out.println("+++ AppListFragment::refreshScreen list_view==null exit +++" );
	    	return -3;
		}

		// Add rows to the list view.
		adapter.set_use_colors_flag( show_chart );
		list_view.setAdapter(null);
		list_view.setAdapter(adapter);
		
		return -1;
	}

} 