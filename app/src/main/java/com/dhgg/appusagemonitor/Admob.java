package com.dhgg.appusagemonitor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;

/**
 * Class that adds a Admob view to a given layout.
 * Has wrapper classes to forward destroy, pause, and resume signals.
 * Has ability to periodically show or hide ads.
 */
public class Admob {
    private AdView m_adView = null;
    private final static String ADMOB_PREFS = "ui_prefs";
    private final static String RUN_MODULUS = "run_modulus";

    // Will show ads ONCE out of every (SHOW_AD_INTERVAL) times.
    private final static int SHOW_AD_INTERVAL = 3;

    public Admob(Intent intent, Context context, LinearLayout layout) {

        boolean setupAdmob = intent.getBooleanExtra("startAdmob", true);
        if (setupAdmob) {
            init(context, layout);
        } else {
            Log.w(Consts.LOGTAG, "Admob: setupAdmob is false, not doing anything");
        }
    }

    public void onDestroy() {
        if (m_adView != null) {
            m_adView.destroy();
        }
    }

    public void onPause() {
        if (m_adView != null) {
            m_adView.pause();
        }
    }

    public void onResume() {
        if (m_adView != null) {
            m_adView.resume();
        }
    }

    ///////////////////////////////////////////////////////
    // Start of private class functions.
    ///////////////////////////////////////////////////////
    private void init(Context context, LinearLayout layout) {

        SharedPreferences ui_prefs = context.getSharedPreferences(ADMOB_PREFS, 0);
        int numTimesRun = ui_prefs.getInt(RUN_MODULUS, 1);
        if (numTimesRun < SHOW_AD_INTERVAL) {
            // Log.e(Consts.LOGTAG, "Admob::init not showing ads numTimesRun=" + numTimesRun);
            // Not showing the ad. Increment counter and exit.
            numTimesRun++;

            SharedPreferences.Editor uiPrefsEditor = ui_prefs.edit();
            uiPrefsEditor.putInt(RUN_MODULUS,numTimesRun);
            uiPrefsEditor.commit();

            return;
        } else {
            // Log.e(Consts.LOGTAG, "Admob::init will show ads numTimesRun=" + numTimesRun);
            // Showing the ad. Reset counter so it doesn't show next time.
            // Allow function to continue.
            SharedPreferences.Editor uiPrefsEditor = ui_prefs.edit();
            uiPrefsEditor.putInt(RUN_MODULUS,1);
            uiPrefsEditor.commit();
        }

        m_adView = new AdView(context);
        m_adView.setAdSize(AdSize.BANNER);
        m_adView.setAdUnitId("a150686c4e8460b");
        if (layout != null) {
            layout.addView(m_adView);
            AdRequest adRequest = new AdRequest.Builder().build();
			m_adView.loadAd(adRequest);
		}
	}
}

