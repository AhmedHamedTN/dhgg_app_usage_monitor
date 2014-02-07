package com.dhgg.appusagemonitor;


import java.io.IOException;
import java.util.List;

import com.appspot.appusagemonitor.appusagemonitor.model.AppusagemonitorApiMessagesAppUsageResponseMessage;

/**
 * A handler class to handle an asynchronous callback from Cloud Backend. Used
 * with {@link CloudBackendAsync} and {@link CloudBackendMessaging}.
 *
 */
public abstract class CloudCallbackHandler<T> {

  /**
   * Subclasses should override this to implement a handler method to process
   * the results. If not overridden, the result will be discarded.
   *
   * @param results
   *          The result value (usually, a {@link CloudEntity} or a {@link List}
   *          of CloudEntities)
   */
  public abstract void onComplete(T results);

  /**
   * Subclasses may override this to implement an exception handler. If not
   * overridden, the exception will be ignored.
   *
   * @param exception
   *          {@link IOException} that would be thrown on the request call.
   */
  public void onError(IOException exception) {
  }

}
