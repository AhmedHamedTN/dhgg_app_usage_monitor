package com.dhgg.appusagemonitor;

/**
 * A class to hold hard coded constants. Developers may modify the values based
 * on their usage.
 * 
 */
public interface Consts {

  /**
   * Set Project ID of your Google APIs Console Project.
   */
  public static final String PROJECT_ID = "appusagemonitor";

  /**
   * Set your Web Client ID for authentication at backend.
   * This is set as an environment variable set on the local machine.
   * See app/build.gradle
   */
  public static final String WEB_CLIENT_ID = BuildConfig.APPUSAGEMONITOR_WEBCLIENT_ID;

  /**
   * Auth audience for authentication on backend.
   */
  public static final String AUTH_AUDIENCE = "server:client_id:" + WEB_CLIENT_ID;

  /**
   * A flag to switch if the app should be run with local dev server or
   * production (cloud).
   */
  public static final boolean LOCAL_ANDROID_RUN = false;

  /**
   * Tag name for logging.
   */
  public static final String LOGTAG = "DHGG";
}
