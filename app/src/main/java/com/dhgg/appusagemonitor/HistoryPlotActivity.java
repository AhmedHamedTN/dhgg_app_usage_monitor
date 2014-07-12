package com.dhgg.appusagemonitor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
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

    // Classes that this depends on.
    // Ideally, we'd use dependency injection to inject the object we want in.
    // DatePoints DatePoints = null;
    CloudBackendAsync m_cloudBackendAsync = null;
    DbHandler m_dbHandler = null;


    private void refreshData() {

        TimeSeries time_series = (TimeSeries) m_dataset.getSeriesAt(0);

        DatePoints datePoints = new DatePoints(m_local_points, m_cloud_points);
        DatePoint[] points = datePoints.getDatePoints();
        for ( int i = 0; i < points.length; i++ ) {
            time_series.add( points[i].x, points[i].y);
        }

        String units = datePoints.getUnits();
        String activityTitle = m_app_name+" usage in "+units;
        this.setTitle(activityTitle);

        m_graphicalView.repaint();

        // If we haven't tried yet, get the cloud data.
        if (m_tried_to_fetch == false) {
          fetchCloudData();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String logCategory = "HistoryPlotActivity::onCreate: ";

        // Get inputs
        Intent intent = getIntent();
        m_app_name = intent.getStringExtra("app_name");
        m_package_name = intent.getStringExtra("package_name");
        //Log.i("DHGG", logCategory + "a:"+m_app_name+" p:"+m_package_name);

        // Use mocked classes for testing.
        boolean isTest = intent.getBooleanExtra("isTest", false);
        if (isTest) {
            m_cloudBackendAsync = new CloudBackendAsync();
            m_dbHandler = new DbHandler(this);
        } else {
            m_cloudBackendAsync = new CloudBackendAsync();
            m_dbHandler = new DbHandler(this);
        }

        m_dataset = new XYMultipleSeriesDataset();
        m_dataset.addSeries(makeTimeSeries());
        m_graphicalView = ChartFactory.getTimeChartView(
                getApplicationContext(),
                m_dataset,
                makeRenderer(),
                "MMM dd");


        setContentView(R.layout.activity_history_plot);

        // Get local data.
        m_local_points = m_dbHandler.getHistoricalData( m_app_name );
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

        XYMultipleSeriesRenderer mrenderer = new XYMultipleSeriesRenderer();
        mrenderer.addSeriesRenderer(renderer);

        mrenderer.setXTitle( "" );
        mrenderer.setShowLegend( false );
        mrenderer.setShowAxes( false );

        mrenderer.setLabelsTextSize( 30 );
        mrenderer.setXLabelsColor( Color.BLACK );

        mrenderer.setAxisTitleTextSize( 30 );
        mrenderer.setYLabelsAlign( Align.RIGHT );
        mrenderer.setYLabelsColor( 0 , Color.BLACK );

        mrenderer.setApplyBackgroundColor( true );
        mrenderer.setBackgroundColor( Color.WHITE );

        mrenderer.setGridColor( Color.BLACK );
        mrenderer.setShowGridX( true );

        // Margins are for bottom, left, top, right
        mrenderer.setMargins(new int[] { 60, 60, 50, 20 });
        mrenderer.setMarginsColor( Color.LTGRAY );

        mrenderer.setShowCustomTextGrid( true );

        return mrenderer;
    }

    private void fetchCloudData() {
        String logCategory = "HistoryPlotActivity::fetchCloudData: ";

        m_tried_to_fetch = true;

        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(this, Consts.AUTH_AUDIENCE);

        SharedPreferences settings = getSharedPreferences(MainActivity.PREF_KEY_ACCOUNT_NAME,Context.MODE_PRIVATE);
        String accountName = settings.getString(MainActivity.PREF_KEY_ACCOUNT_NAME, null);

        if (accountName == null) {
            //Log.d("DHGG", logCategory + "No account, not using cloud data");
            setContentView(m_graphicalView);
            return;
        }

        // Get cloud data
        // If  we have credentials and we have internet connection
        credential.setSelectedAccountName(accountName);
        // Log.d("DHGG", logCategory + "Getting cloud info ...");

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

                        // Log.d("DHGG", logCategory + "AppUsageListByNameResponse onComplete");

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

                                // Log.d("DHGG", logCategory + "AppUsageListByNameResponse "+ i+" "+yyyymmdd+" "+totalForDay);
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
                        // Log.d("DHGG", logCategory + "AppUsageListByNameResponse onError:"+exception.toString());
                        setContentView(m_graphicalView);
                    }
                };

        // An async backend class is used here.
        CloudBackendAsync m_cloudBackendAsync = new CloudBackendAsync();

        // Execute the lookup with the handler, on callback, we'll open the new page
        m_cloudBackendAsync.setCredential(credential);
        m_cloudBackendAsync.listByName(request, handler);
    }
}
