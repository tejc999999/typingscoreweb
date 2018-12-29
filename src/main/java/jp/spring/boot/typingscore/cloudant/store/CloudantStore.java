package jp.spring.boot.typingscore.cloudant.store;

import java.net.URL;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.JsonObject;

/**
 * 
 * IBM Cloudans Store Common Base Class.
 * 
 * @author tejc999999
 *
 */
public abstract class CloudantStore {
	
    private Database db = null;
    
    /**
     * IBM Cloudant Client create.
     * 
     * @return IBM Cloudant Client.
     */
    protected static  CloudantClient createClient() {
        
        String url = null;
        if (System.getenv("VCAP_SERVICES") != null) {
            //case: environments variable VCAP_SERVICES exists.

        	// When running in IBM Cloud, the VCAP_SERVICES env var will have the credentials for all bound/connected services
            // Parse the VCAP JSON structure looking for cloudant.
            JsonObject cloudantCredentials = VCAPHelper.getCloudCredentials("cloudant");
            if(cloudantCredentials == null){
            	// case: environments variable VCAP_SERVICES not contains cloudant
            	
                System.out.println("No cloudant database service bound to this application");
                return null;
            }
            // Make the URL information the contents of the environment variable VCAP_SERVICES
            url = cloudantCredentials.get("url").getAsString();
        } else if (System.getenv("CLOUDANT_URL") != null) {
        	// case: environment variable VCAP_SERVICES does not exist
        	//         and the environment variable CLOUDANT_URL exists

        	// Make the URL information the contents of the environment variable CLOUDANT_URL
            url = System.getenv("CLOUDANT_URL");
        } else {
        	// case: Neither environment variable VCAP_ERVICES nor cloudant.url in cloudant.properties exists
        	
            System.out.println("Running locally. Looking for credentials in cloudant.properties");
            // Make URL information cloudant_url contents of cloudant.properties
            try {
            	url = VCAPHelper.getLocalProperties("cloudant.properties").getProperty("cloudant_url");
            } catch(NullPointerException e) {
            	// Exception handling when property is not set (using different DB)
            }
            if(url == null || url.length()==0){
            	// case: cloudant_url did not exist in cloudant.properties
                System.out.println("To use a database, set the Cloudant url in src/main/resources/cloudant.properties");
                return null;
            }
        }
        
        try {
        	// Connect cloudant.
            System.out.println("Connecting to Cloudant");
            CloudantClient client = ClientBuilder.url(new URL(url)).build();
            
            // case: For a proxy environment:
            // ex）proxy url=http://(ip address):(port number)
            //      Not an authentication proxy、「.proxyUser("auth proxy user id").proxyPassword("auth proxy user password")」is not required.
//            CloudantClient client = ClientBuilder.url(new URL(url)).proxyURL(new URL("http://(ip address): (port number)")).proxyUser("auth proxy user id").proxyPassword("auth proxy user password").build();
            return client;
        } catch (Exception e) {
            System.out.println("Unable to connect to database");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get the target db object.
     * 
     * @return Database.
     */
    public Database getDB(){
        return db;
    }

    /**
     * Set the target db object.
     * 
     * @param Database db object.
     */
    public void setDB(Database db) {
    	this.db = db;
    }
}
