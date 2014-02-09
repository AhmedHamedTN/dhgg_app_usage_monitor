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
 * (build: 2013-12-19 23:55:21 UTC)
 * on 2014-02-01 at 17:30:42 UTC 
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
  private java.lang.Long endDateTime;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long startDateTime;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getEndDateTime() {
    return endDateTime;
  }

  /**
   * @param endDateTime endDateTime or {@code null} for none
   */
  public AppusagemonitorApiMessagesAppUsageListByDateRequest setEndDateTime(java.lang.Long endDateTime) {
    this.endDateTime = endDateTime;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getStartDateTime() {
    return startDateTime;
  }

  /**
   * @param startDateTime startDateTime or {@code null} for none
   */
  public AppusagemonitorApiMessagesAppUsageListByDateRequest setStartDateTime(java.lang.Long startDateTime) {
    this.startDateTime = startDateTime;
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