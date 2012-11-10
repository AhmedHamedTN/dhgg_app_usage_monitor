package com.dhgg.appusagemonitor;



import java.util.ArrayList;

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
    
    public int refreshScreen( Data_value[] data_arr  ) 
	{
    	View view = m_view;
    	if (view == null)
    	{
    		System.out.println("+++ ApplistFragment::refreshScreen view=null exit +++" );	    
    	    return -2;
    	}
    	
    	Data_value_adapter adapter = new Data_value_adapter( getActivity(), R.layout.name_value_row, data_arr);
    	
		ListView list_view = (ListView) getActivity().findViewById(R.id.task_list_view);
		if ( list_view == null )
		{
	    	System.out.println("+++ AppListFragment::refreshScreen list_view==null exit +++" );
	    	return -3;
		}

		// Add rows to the list view.
		list_view.setAdapter(null);
		list_view.setAdapter(adapter);
		
		return -1;
	}

} 