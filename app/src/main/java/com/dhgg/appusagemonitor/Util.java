package com.dhgg.appusagemonitor;

/**
 * Utility class that holds helper functions.
 */
public class Util {

    public DataValue[] get_data_slices(DataValue[] data_arr, int max_data_size) {
		int num_values = data_arr.length;
		float total = 0;
		for ( int i = 0; i < num_values; i++ ) {
			total += data_arr[i].value;
		}

		int normal_data_arr_size = num_values;
		if ( num_values > max_data_size ) {
			normal_data_arr_size = max_data_size ;
		}

		DataValue[] normal_data_arr = new DataValue[ normal_data_arr_size ];
		System.arraycopy( data_arr, 0, normal_data_arr, 0, normal_data_arr_size );

		int subtotal = 0;
		for ( int i = 0; i < normal_data_arr_size; i++) {
			subtotal += normal_data_arr[ i ].value;

			float fraction = (float)normal_data_arr[ i ].value / total;
			int percent = (int)(fraction * 100);
			normal_data_arr[ i ].value = percent;
		}

		if ( normal_data_arr_size == max_data_size) {
			float remaining = total - subtotal;

			float fraction = remaining / total;

			int percent = (int) ( fraction * 100);

			normal_data_arr[ normal_data_arr_size -1 ].value = percent;
			normal_data_arr[ normal_data_arr_size -1 ].description = "Other ...";
		}

		return normal_data_arr;
	}

    public String get_time_str( int time_in_seconds) {
    	int total_secs = time_in_seconds;

        int hours = total_secs / 3600;
        int mins = (total_secs - (hours * 3600))/ 60;
        int secs = total_secs - (hours * 3600) - (mins * 60);

        String time_str = "";
        if (hours > 0) {
        	time_str += hours + "h ";
        }

        if (mins > 0) {
        	time_str += mins + "m ";
        }

        time_str += secs + "s";

    	return time_str;
    }
}
