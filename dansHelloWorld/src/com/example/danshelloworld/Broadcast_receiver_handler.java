package com.example.danshelloworld;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Broadcast_receiver_handler extends BroadcastReceiver 
{	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_SCREEN_ON))
        {
        	System.out.println("Screen on !!!");
        	
        	// Start service to check what the current app is
          	Intent myIntent = new Intent(context, Service_handler.class);
           	context.startService(myIntent);
        }
        else if(action.equals(Intent.ACTION_SCREEN_OFF))
        {
        	System.out.println("Screen off !!!");
        	
        	// stop service.
        }	
	 }
}
