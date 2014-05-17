package com.dhgg.appusagemonitor;

import android.util.Log;

import com.appspot.appusagemonitor.appusagemonitor.Appusagemonitor;
import com.appspot.appusagemonitor.appusagemonitor.model.AppusagemonitorApiMessagesAppUsageInsertRequest;
import com.appspot.appusagemonitor.appusagemonitor.model.AppusagemonitorApiMessagesAppUsageListByNameRequest;
import com.appspot.appusagemonitor.appusagemonitor.model.AppusagemonitorApiMessagesAppUsageListByNameResponse;
import com.appspot.appusagemonitor.appusagemonitor.model.AppusagemonitorApiMessagesAppUsageResponseMessage;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;

public class CloudBackend {


  static {
    // to prevent EOFException after idle
    // http://code.google.com/p/google-http-java-client/issues/detail?id=116
    System.setProperty("http.keepAlive", "false");
  }

  private GoogleAccountCredential credential;

  /**
   * Sets {@link GoogleAccountCredential} that will be used on all backend
   * calls. By setting null, all call will not be associated with user account
   * info.
   *
   * @param credential
   *          {@link GoogleAccountCredential}
   */
  public void setCredential(GoogleAccountCredential credential) {
    this.credential = credential;
  }

  /**
   * Returns {@link GoogleAccountCredential} that has been set to this backend.
   *
   * @return {@link GoogleAccountCredential}
   */
  public GoogleAccountCredential getCredential() {
    return this.credential;
  }

  // building CloudBackend endpoints and configuring authentication and
  // exponential back-off policy
  private Appusagemonitor getMBSEndpoint() {

    // check if credential has account name
    final GoogleAccountCredential gac = credential == null
        || credential.getSelectedAccountName() == null ? null : credential;
    
    // create HttpRequestInitializer
    /*
    HttpRequestInitializer hri = new HttpRequestInitializer() {
      @Override
      public void initialize(HttpRequest request) throws IOException {
        request.setBackOffPolicy(new ExponentialBackOffPolicy());
        if (gac != null) {
          gac.initialize(request);
        }
      }
    };
    */

    return updateBuilder( new Appusagemonitor.Builder(
		AndroidHttp.newCompatibleTransport(), 
		new GsonFactory(), 
		gac), Consts.LOCAL_ANDROID_RUN).build();
}
  
public static <B extends AbstractGoogleClient.Builder> B updateBuilder(B builder, boolean is_local_run) 
{
  if (is_local_run) {
	  Log.i("DHGG", "setting up local run");
	  builder.setRootUrl("http://localhost:8080/_ah/api");
  }

  // only enable GZip when connecting to remote server
  final boolean enableGZip = builder.getRootUrl().startsWith("https:");

  builder.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
    public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
	    if (!enableGZip) {
	      request.setDisableGZipContent(true);
	    }
	}
  });

  return builder;
}
  

  /**
   * Makes request to backend synchronously.
   * @param handler 
   *
   * @param ce
   *          {@link CloudEntity} for inserting a CloudEntity.
   * @return {@link CloudEntity} that has updated fields (like updatedAt and new
   *         Id).
   * @throws IOException
   *           When the call had failed for any reason.
   */

  public AppusagemonitorApiMessagesAppUsageResponseMessage insert(
    AppusagemonitorApiMessagesAppUsageInsertRequest request) throws IOException 
  {
      AppusagemonitorApiMessagesAppUsageResponseMessage result = getMBSEndpoint().appusages().insert(request).execute();
      return result;
  }

  public AppusagemonitorApiMessagesAppUsageListByNameResponse listByName(
    AppusagemonitorApiMessagesAppUsageListByNameRequest request) throws IOException 
  {
      AppusagemonitorApiMessagesAppUsageListByNameResponse result = getMBSEndpoint().appusagesbyname().list(request).execute();
      return result;
  }

  /**
   * Updates the specified {@link CloudEntity} on the backend synchronously. If
   * it does not have any Id, it creates a new Entity. If it has, find the
   * existing entity and update it.
   *
   * @param ce
   *          {@link CloudEntity} for updating a CloudEntity.
   * @return {@link CloudEntity} that has updated fields (like updatedAt and new
   *         Id).
   * @throws IOException
   *           When the call had failed for any reason.
   */
  /*
  public CloudEntity update(CloudEntity ce) throws IOException {
    EntityDto resultEntityDto = getMBSEndpoint().update(ce.getKindName(), ce.getEntityDto())
        .execute();
    CloudEntity resultCo = CloudEntity.createCloudEntityFromEntityDto(resultEntityDto);
    Log.i(Consts.TAG, "update: updated: " + resultCo);
    return resultCo;
  }
  */

  /**
   * Inserts multiple {@link CloudEntity}s on the backend synchronously. Works
   * just the same as {@link #insert(CloudEntity)}.
   *
   * @param ceList
   *          {@link List} that holds {@link CloudEntity}s to save.
   * @return {@link List} that has updated {@link CloudEntity}s.
   * @throws IOException
   *           When the call had failed for any reason.
   */
  /*
  public List<CloudEntity> insertAll(List<CloudEntity> ceList) throws IOException {

    // prepare for EntityListDto
    List<EntityDto> cdList = new LinkedList<EntityDto>();
    for (CloudEntity co : ceList) {
      cdList.add(co.getEntityDto());
    }
    EntityListDto cdl = new EntityListDto();
    cdl.setEntries(cdList);

    // execute saveAll
    EntityListDto resultCdl;
    resultCdl = getMBSEndpoint().insertAll(cdl).execute();
    Log.i(Consts.TAG, "saveAll: saved: " + resultCdl.getEntries());
    List<CloudEntity> resultCoList = getListOfEntityDto(resultCdl);
    return resultCoList;
  }
  */

  /**
   * Updates multiple {@link CloudEntity}s on the backend synchronously. Works
   * just the same as {@link #update(CloudEntity)}.
   *
   * @param coList
   *          {@link List} that holds {@link CloudEntity}s to save.
   * @return {@link List} that has updated {@link CloudEntity}s.
   * @throws IOException
   *           When the call had failed for any reason.
   */
  /*
  public List<CloudEntity> updateAll(List<CloudEntity> coList) throws IOException {

    // prepare for EntityListDto
    List<EntityDto> cdList = new LinkedList<EntityDto>();
    for (CloudEntity co : coList) {
      cdList.add(co.getEntityDto());
    }
    EntityListDto cdl = new EntityListDto();
    cdl.setEntries(cdList);

    // execute saveAll
    EntityListDto resultCdl;
    resultCdl = getMBSEndpoint().updateAll(cdl).execute();
    Log.i(Consts.TAG, "saveAll: saved: " + resultCdl.getEntries());
    List<CloudEntity> resultCoList = getListOfEntityDto(resultCdl);
    return resultCoList;
  }
  */

  /**
   * Reads the specified {@link CloudEntity} synchronously.
   *
   * @param kindName
   *          Name of the table for the CloudEntity to get.
   * @param id
   *          Id of the CloudEntity to find.
   * @return {@link CloudEntity}.
   * @throws IOException
   *           When the call had failed for any reason.
   */
  /*
  public CloudEntity get(String kindName, String id) throws IOException {
    EntityDto cd = getMBSEndpoint().get(kindName, id).execute();
    CloudEntity co = CloudEntity.createCloudEntityFromEntityDto(cd);
    Log.i(Consts.TAG, "get: result: " + co);
    return co;
  }
  */

  /**
   * Reads all the {@link CloudEntity}s synchronously specified by the
   * {@link List} of Ids.
   *
   * @param kindName
   *          Name of the table for the CloudEntities to get.
   * @param idList
   *          {@link List} of Ids of the CloudEntities to find.
   * @return {@link List} of the found {@link CloudEntity}s.
   * @throws IOException
   *           When the call had failed for any reason.
   */
  /*
  public List<CloudEntity> getAll(String kindName, List<String> idList) throws IOException {

    // prepare for EntityListDto
    EntityListDto cdl = createEntityListDto(kindName, idList);

    // execute getAll
    EntityListDto resultCdl;
    resultCdl = getMBSEndpoint().getAll(cdl).execute();
    Log.i(Consts.TAG, "getAll: result: " + resultCdl.getEntries());
    return getListOfEntityDto(resultCdl);
  }

  private EntityListDto createEntityListDto(String kindName, List<String> idList) {
    List<EntityDto> l = new LinkedList<EntityDto>();
    for (String id : idList) {
      EntityDto cd = new EntityDto();
      cd.setId(id);
      cd.setKindName(kindName);
      l.add(cd);
    }
    EntityListDto cdl = new EntityListDto();
    cdl.setEntries(l);
    return cdl;
  }

  private List<CloudEntity> getListOfEntityDto(EntityListDto cdl) {
    List<CloudEntity> l = new LinkedList<CloudEntity>();
    if (cdl.getEntries() != null) { // production returns null when its
      // empty
      for (EntityDto cd : cdl.getEntries()) {
        l.add(CloudEntity.createCloudEntityFromEntityDto(cd));
      }
    }
    return l;
  }
  */

  /**
   * Deletes the specified {@link CloudEntity} synchronously.
   *
   * @param kindName
   *          Name of the table for the CloudEntity to delete.
   * @param id
   *          Id of the CloudEntity to delete.
   * @throws IOException
   *           When the call had failed for any reason.
   */
  /*
  public void delete(String kindName, String id) throws IOException {
    getMBSEndpoint().delete(kindName, id).execute();
    Log.i(Consts.TAG, "delete: deleted: " + kindName + "/" + id);
  }
  */

  /**
   * Deletes the specified {@link CloudEntity} synchronously.
   *
   * @param co
   *          {@link CloudEntity} to delete
   * @throws IOException
   *           When the call had failed for any reason.
   */
  /*
  public void delete(CloudEntity co) throws IOException {
    getMBSEndpoint().delete(co.getKindName(), co.getId()).execute();
    Log.i(Consts.TAG, "delete: deleted: " + co);
  }
  */

  /**
   * Deletes all the specified {@link CloudEntity}s synchronously.
   *
   * @param kindName
   *          Name of the table for the CloudEntity to delete.
   * @param idList
   *          {@link List} that contains a list of Ids to delete.
   * @throws IOException
   *           When the call had failed for any reason.
   */
  /*
  public void deleteAllById(String kindName, List<String> idList) throws IOException {

    // prepare for EntityListDto
    EntityListDto cdl = createEntityListDto(kindName, idList);

    // delete
    getMBSEndpoint().deleteAll(cdl).execute();
    Log.i(Consts.TAG, "deleteAll: deleted: " + kindName + ": " + idList);
  }
  */

  /**
   * Deletes all the specified {@link CloudEntity}s synchronously.
   *
   * @param kindName
   *          Name of the table for the CloudEntity to delete.
   * @param coList
   *          {@link List} that contains a list of Cloud to delete.
   * @throws IOException
   *           When the call had failed for any reason.
   */
  /*
  public void deleteAll(String kindName, List<CloudEntity> coList) throws IOException {
    List<String> idList = new LinkedList<String>();
    for (CloudEntity co : coList) {
      idList.add(co.getId());
    }
    deleteAllById(kindName, idList);
  }
  */

  /**
   * Executes a query synchronously with specified {@link CloudQuery}.
   *
   * @param query
   *          {@link CloudQuery} to execute.
   * @return {@link List} of {@link CloudEntity} of the result.
   * @throws IOException
   *           When the call had failed for any reason.
   */
  /*
  public List<CloudEntity> list(CloudQuery query) throws IOException {

    // execute the query
    EntityListDto cbList;
    QueryDto cq = query.convertToQueryDto();
    Log.i(Consts.TAG, "list: executing query: " + cq);
    cbList = getMBSEndpoint().list(cq).execute();
    Log.i(Consts.TAG, "list: result: " + cbList.getEntries());

    // convert the result to List
    List<CloudEntity> coList = new LinkedList<CloudEntity>();
    if (cbList.getEntries() != null) {
      for (EntityDto cd : cbList.getEntries()) {
        coList.add(CloudEntity.createCloudEntityFromEntityDto(cd));
      }
    }
    return coList;
  }
  */

}