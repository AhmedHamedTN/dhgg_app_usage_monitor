package com.dhgg.appusagemonitor;



import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class AppListFragment extends Fragment 
{
	public static Db_handler m_db_handler;
	public static String TURN_OFF_UPDATES = "turn_off_updates";
	public static String SHOW_HIST_PREFS = "show_hist_prefs";

	public static String SHOW_HIST_PREF_TODAY = "s_h_p_today";
	public static String SHOW_HIST_PREF_24_H = "s_h_p_24h";
	public static String SHOW_HIST_PREF_ALL = "s_h_p_all";
	
	public static View m_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
        // Inflate the layout for this fragment
        m_view = inflater.inflate(R.layout.app_list, container, false);
	  
  	    return m_view;
    }
  
    public int refreshScreen( String hist_pref ) 
	{
    	View view = m_view;
    	
		// Get data to display
		Db_handler db_handler = new Db_handler(this.getActivity());
		ArrayList<Data_value> data = db_handler.getData( hist_pref );
		
		Data_value[] data_arr = data.toArray(new Data_value[data.size()]);
		Data_value_adapter adapter = new Data_value_adapter(this.getActivity(),
				R.layout.name_value_row, data_arr);

		ListView list_view = (ListView) view.findViewById(R.id.task_list_view);

		// Add rows to the list view.
		list_view.setAdapter(null);
		list_view.setAdapter(adapter);
		
		return data.size();
	}

} 