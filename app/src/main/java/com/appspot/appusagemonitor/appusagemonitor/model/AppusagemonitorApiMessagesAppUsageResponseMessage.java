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
 * ProtoRPC message definition to represent a AppUsage that is stored. This is passed from the
 * backend server to the client (phone, tablet).
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the appusagemonitor. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class AppusagemonitorApiMessagesAppUsageResponseMessage extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long appDate;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long appDuration;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String appName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String createdAt;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String packageName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String phoneName;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getAppDate() {
    return appDate;
  }

  /**
   * @param appDate appDate or {@code null} for none
   */
  public AppusagemonitorApiMessagesAppUsageResponseMessage setAppDate(java.lang.Long appDate) {
    this.appDate = appDate;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getAppDuration() {
    return appDuration;
  }

  /**
   * @param appDuration appDuration or {@code null} for none
   */
  public AppusagemonitorApiMessagesAppUsageResponseMessage setAppDuration(java.lang.Long appDuration) {
    this.appDuration = appDuration;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getAppName() {
    return appName;
  }

  /**
   * @param appName appName or {@code null} for none
   */
  public AppusagemonitorApiMessagesAppUsageResponseMessage setAppName(java.lang.String appName) {
    this.appName = appName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCreatedAt() {
    return createdAt;
  }

  /**
   * @param createdAt createdAt or {@code null} for none
   */
  public AppusagemonitorApiMessagesAppUsageResponseMessage setCreatedAt(java.lang.String createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public AppusagemonitorApiMessagesAppUsageResponseMessage setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPackageName() {
    return packageName;
  }

  /**
   * @param packageName packageName or {@code null} for none
   */
  public AppusagemonitorApiMessagesAppUsageResponseMessage setPackageName(java.lang.String packageName) {
    this.packageName = packageName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPhoneName() {
    return phoneName;
  }

  /**
   * @param phoneName phoneName or {@code null} for none
   */
  public AppusagemonitorApiMessagesAppUsageResponseMessage setPhoneName(java.lang.String phoneName) {
    this.phoneName = phoneName;
    return this;
  }

  @Override
  public AppusagemonitorApiMessagesAppUsageResponseMessage set(String fieldName, Object value) {
    return (AppusagemonitorApiMessagesAppUsageResponseMessage) super.set(fieldName, value);
  }

  @Override
  public AppusagemonitorApiMessagesAppUsageResponseMessage clone() {
    return (AppusagemonitorApiMessagesAppUsageResponseMessage) super.clone();
  }

}
