/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2014-10-28 17:08:27 UTC)
 * on 2014-11-15 at 15:31:02 UTC 
 * Modify at your own risk.
 */

package com.appspot.appusagemonitor.appusagemonitor.model;

/**
 * ProtoRPC message definition to represent a appusage query.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the appusagemonitor. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class AppusagemonitorApiMessagesAppUsageListByDateRequest extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long appDate;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getAppDate() {
    return appDate;
  }

  /**
   * @param appDate appDate or {@code null} for none
   */
  public AppusagemonitorApiMessagesAppUsageListByDateRequest setAppDate(java.lang.Long appDate) {
    this.appDate = appDate;
    return this;
  }

  @Override
  public AppusagemonitorApiMessagesAppUsageListByDateRequest set(String fieldName, Object value) {
    return (AppusagemonitorApiMessagesAppUsageListByDateRequest) super.set(fieldName, value);
  }

  @Override
  public AppusagemonitorApiMessagesAppUsageListByDateRequest clone() {
    return (AppusagemonitorApiMessagesAppUsageListByDateRequest) super.clone();
  }

}
