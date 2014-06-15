package com.dhgg.appusagemonitor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import android.os.Bundle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.graphics.Paint.Align;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import com.appspot.appusagemonitor.appusagemonitor.model.AppusagemonitorApiMessagesAppUsageHistResponseMessage;
import com.appspot.appusagemonitor.appusagemonitor.model.AppusagemonitorApiMessagesAppUsageListByNameRequest;
import com.appspot.appusagemonitor.appusagemonitor.model.AppusagemonitorApiMessagesAppUsageListByNameResponse;

public class HistoryPlotActivity extends Activity {
    private XYMultipleSeriesDataset m_dataset;
    private GraphicalView m_graphicalView;
    private String m_app_name;
    private String m_package_name;
    private Point[] m_local_points = null;
    private Point[] m_cloud_points = null;
    private boolean m_tried_to_fetch = false;

    private Point[] getMergedData() {

        ArrayList <Point> data = new ArrayList<Point>();

        int l = 0;
        int c = 0;
        while (l < m_local_points.length && m_cloud_points != null && c < m_cloud_points.length)
        {
            Point lPoint = m_local_points[l];
            Point cPoint = m_cloud_points[c];

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

        while (l < m_local_points.length)
        {
            Point lPoint = m_local_points[l];
            //Log.w("DHGG","AppListFragment::openChart finishing up localData "+lPoint.x);
            data.add( lPoint );
            l++;
        }

        while (m_cloud_points != null && c < m_cloud_points.length)
        {
            Point cPoint = m_cloud_points[c];
            //Log.w("DHGG","AppListFragment::openChart finishing up cloudData "+cPoint.x);
            data.add( cPoint );
            c++;
        }

        Point[] points = data.toArray(new Point[data.size()]);

        return points;
    }

    private void refreshData() {

        TimeSeries time_series = (TimeSeries) m_dataset.getSeriesAt(0);

        Point[] points = getMergedData();

        String units = "seconds";
        double factor = 1;
        for ( int i = 0; i < points.length; i++ )
        {
            if ( points[ i ].y > 60 )
            {
                units = "minutes";
                factor = 60;
            }
        }
        String activityTitle = m_app_name+" usage in "+units;
        this.setTitle(activityTitle);


        for ( int i = 0; i < points.length; i++ )
        {
            double y_value = points[ i ].y / factor;
            int x_value = points[i].x;
            time_series.add( new Date( (x_value / 10000) - 1900,
                    ((x_value / 100 ) % 100 )-  1,
                    x_value % 100 ),
                    y_value );
        }

        m_graphicalView.repaint();

        // If we haven't tried yet, get the cloud data.
        if (m_tried_to_fetch == false) {
          fetchCloudData();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get inputs
        Intent intent = getIntent();
        m_app_name = intent.getStringExtra("app_name");
        m_package_name = intent.getStringExtra("package_name");
        //Log.i("DHGG","HistoryPlotActivity::onCreate a:"+m_app_name+" p:"+m_package_name);

        m_dataset = new XYMultipleSeriesDataset();
        m_dataset.addSeries(makeTimeSeries());
        m_graphicalView = ChartFactory.getTimeChartView(
                getApplicationContext(),
                m_dataset,
                makeRenderer(),
                "MMM dd");

        //setContentView(m_graphicalView);
        setContentView(R.layout.activity_history_plot);

        // Get local data.
        Db_handler db_handler = new Db_handler( this );
        m_local_points = db_handler.getHistoricalData( m_app_name );
        refreshData();
    }

    private TimeSeries makeTimeSeries() {
        TimeSeries time_series = new TimeSeries("");
        return time_series;
    }

    private XYMultipleSeriesRenderer makeRenderer() {
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setPointStyle( PointStyle.CIRCLE );
        renderer.setFillPoints( true );
        renderer.setLineWidth( 5 );

        //renderer.setFillBelowLine( true );
        //renderer.setChartValuesTextSize( 10 );

        XYMultipleSeriesRenderer mrenderer = new XYMultipleSeriesRenderer();
        mrenderer.addSeriesRenderer(renderer);

        mrenderer.setXTitle( "" );
        mrenderer.setShowLegend( false );
        mrenderer.setShowAxes( false );
        //mrenderer.setAxesColor( Color.BLACK );

        mrenderer.setLabelsTextSize( 30 );
        mrenderer.setXLabelsColor( Color.BLACK );
        //mrenderer.setXLabelsAngle( 45 );

        mrenderer.setAxisTitleTextSize( 30 );
        mrenderer.setYLabelsAlign( Align.RIGHT );
        mrenderer.setYLabelsColor( 0 , Color.BLACK );
        //mrenderer.setAxesColor( Color.GREEN );

        //mrenderer.setLabelsColor( Color.BLACK );
        //mrenderer.setYTitle("Usage in seconds");

        mrenderer.setApplyBackgroundColor( true );
        mrenderer.setBackgroundColor( Color.WHITE );

        mrenderer.setGridColor( Color.BLACK );
        mrenderer.setShowGridX( true );
        //mrenderer.setShowGrid( true );

        // Margins are for bottom, left, top, right
        mrenderer.setMargins(new int[] { 60, 60, 50, 20 });
        mrenderer.setMarginsColor( Color.LTGRAY );

        //mrenderer.setChartTitle("App Usage (seconds)");
        //mrenderer.setChartTitleTextSize( 80 );

        mrenderer.setShowCustomTextGrid( true );

        return mrenderer;
    }

    private void fetchCloudData() {

        m_tried_to_fetch = true;

        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(this, Consts.AUTH_AUDIENCE);

        SharedPreferences settings = getSharedPreferences(MyFirstActivity.PREF_KEY_ACCOUNT_NAME,Context.MODE_PRIVATE);
        String accountName = settings.getString(MyFirstActivity.PREF_KEY_ACCOUNT_NAME, null);

        if (accountName == null) {
            //Log.d("DHGG","HistoryPlotActivity::fetchCloudData - no account, not using cloud data");
            setContentView(m_graphicalView);
            return;
        }

        // Get cloud data
        // If  we have credentials
        // and we have internet connection
        credential.setSelectedAccountName(accountName);
        //Log.w("DHGG", "HistoryPlotActivity::fetchCloudData getting cloud info");

        // Create the request
        AppusagemonitorApiMessagesAppUsageListByNameRequest request =
                new AppusagemonitorApiMessagesAppUsageListByNameRequest();
        request.setAppName(m_app_name);
        request.setPackageName(m_package_name);

        // Create a response handler that will receive the result or an error
        CloudCallbackHandler<AppusagemonitorApiMessagesAppUsageListByNameResponse> handler =
                new CloudCallbackHandler<AppusagemonitorApiMessagesAppUsageListByNameResponse>() {
                    @Override
                    public void onComplete(final AppusagemonitorApiMessagesAppUsageListByNameResponse result) {

                        //Log.d("DHGG","HistoryPlotActivity::fetchCloudData AppUsageListByNameResponse onComplete");

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

                                //Log.w("DHGG","HistoryPlotActivity::fetchCloudData AppUsageListByNameResponse "+ i+" "+yyyymmdd+" "+totalForDay);
                            }
                        }

                        if (data.size() > 0)
                        {
                            m_cloud_points = data.toArray(new Point[data.size()]);
                        }

                        refreshData();
                        setContentView(m_graphicalView);
                    }
                    @Override
                    public void onError(final IOException exception) {
                        //Log.w("DHGG","HistoryPlotActivity::fetchCloudData AppUsageListByNameResponse onError:"+exception.toString());
                        setContentView(m_graphicalView);
                    }
                };

        // execute the lookup with the handler, on callback, we'll open the new page
        CloudBackendAsync cloudBackend = new CloudBackendAsync();
        cloudBackend.setCredential(credential);
        cloudBackend.listByName(request, handler);
    }
}
