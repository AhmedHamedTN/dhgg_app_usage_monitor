package com.dhgg.appusagemonitor;


import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AppChartFragment extends Fragment 
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
        m_view = inflater.inflate(R.layout.app_chart, container, false);
	  
  	    return m_view;
    }
    
    public int refreshScreen( String hist_pref ) 
	{
    	/*
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

		List<PieItem> pie_data = new ArrayList<PieItem>(0);
		int max_items = data.size();
		int total_count = 0;
		for (int i = 0; i < max_items; i++) 
		{
			PieItem Item = new PieItem();
		    Item.Count = data_arr[i].value;
		    Item.Label = data_arr[i].description;
		    
		    Item.Color = 0xff000000 + 256*256*i + 256*i + i;
		    pie_data.add(Item);
		    
		    total_count += data_arr[i].value;
		    System.out.println(data_arr[i].description+" "+data_arr[i].value);
		}

		//-----------------------------------------------
		//int OverlayId = R.drawable.cam_overlay;

		View_PieChart pieChart = new View_PieChart( this.getActivity().getApplicationContext() );
		
		int size = 50;
		pieChart.setLayoutParams(new LayoutParams(size, size));
		//pieChart.setGeometry(size, size, 5, 5, 5, 5, OverlayId);
		
		pieChart.setSkinParams(BgColor);
		
		pieChart.setData(pie_data, total_count);
		
		pieChart.invalidate();
		
		return data.size();
		*/
    	return 10;
	}

} 