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
   * Set Project Number of your Google APIs Console Project.
   */
  public static final String PROJECT_NUMBER = "*** ENTER YOUR PROJECT NUMBER ***";

  /**
   * Set your Web Client ID for authentication at backend.
   */
  public static final String WEB_CLIENT_ID = "*** ENTER YOUR WEB CLIENT ID ***";

  /**
   * Set default user authentication enabled or disabled.
   */
  public static final boolean IS_AUTH_ENABLED = false;

  /**
   * Auth audience for authentication on backend.
   */
  public static final String AUTH_AUDIENCE = "server:client_id:" + WEB_CLIENT_ID;

  /**
   * Endpoint root URL
   */
  public static final String ENDPOINT_ROOT_URL = "https://" + PROJECT_ID + ".appspot.com/_ah/api/";

  /**
   * A flag to switch if the app should be run with local dev server or
   * production (cloud).
   */
  public static final boolean LOCAL_ANDROID_RUN = false;

  /**
   * SharedPreference key for CloudBackend.
   */
  public static final String PREF_KEY_CLOUD_BACKEND = "PREF_KEY_CLOUD_BACKEND";

  /**
   * Tag name for logging.
   */
  public static final String TAG = "CloudBackend";
}