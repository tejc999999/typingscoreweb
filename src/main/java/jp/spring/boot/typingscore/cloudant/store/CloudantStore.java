package jp.spring.boot.typingscore.cloudant.store;

import java.net.URL;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.JsonObject;

/**
 * 
 * スコア用ストア（IBM Cloudant用）
 * 
 * @author tejc999999
 *
 */
public abstract class CloudantStore {
	
	/**
	 * DBオブジェクト
	 */
    private Database db = null;
    
    /**
     * IBM Cloudant Client create.
     * 
     * @return IBM Cloudant Client.
     */
    protected static  CloudantClient createClient() {
        
        String url = null;
        if (System.getenv("VCAP_SERVICES") != null) {
            //環境変数VCAP_SERVICESが存在する場合

        	// 環境変数VCAP_SERVICES内のバインドされた接続サービス資格情報からJSON構造を解析する
            JsonObject cloudantCredentials = VCAPHelper.getCloudCredentials("cloudant");
            if(cloudantCredentials == null){
            	// IBM Cloudant接続用の資格情報が存在しない場合
            	
                System.out.println("No cloudant database service bound to this application");
                return null;
            }
            // Make the URL information the contents of the environment variable VCAP_SERVICES
            url = cloudantCredentials.get("url").getAsString();
        } else if (System.getenv("CLOUDANT_URL") != null) {
        	// 環境変数CLOUDANT_URLが存在する場合

        	// URL情報を環境変数CLOUDANT_URLの値にする
            url = System.getenv("CLOUDANT_URL");
        } else {
        	// 環境変数VCAP_SERVICES、CLOUDANT_URLの両方が存在しない場合
        	
            System.out.println("Running locally. Looking for credentials in cloudant.properties");
            //ファイルcloudant.properties内のcloudant_urlからURL情報を作成する
            try {
            	url = VCAPHelper.getLocalProperties("cloudant.properties").getProperty("cloudant_url");
            } catch(NullPointerException e) {
            	// プロパティが存在しない場合
            }
            if(url == null || url.length()==0){
            	// ファイルcloudant.properties内にcloudant_urlが存在しない場合
                System.out.println("To use a database, set the Cloudant url in src/main/resources/cloudant.properties");
                return null;
            }
        }
        
        try {
        	// IBM Cloudantに接続する
            System.out.println("Connecting to Cloudant");
            CloudantClient client = ClientBuilder.url(new URL(url)).build();
            
            // プロキシ環境の場合は以下を設定する
            // 例）proxy url=http://(IPアドレス):(ポート番号)
            // 認証プロキシではない場合は「.proxyUser（ "認証プロキシユーザーID"）.proxyPassword（ "認証プロキシユーザーパスワード"）」は必須ではない
            //  CloudantClient client = ClientBuilder.url(new URL(url)).proxyURL(new URL("http://(IPアドレス): (ポート番号)")).proxyUser("認証プロキシユーザーID").proxyPassword("認証プロキシユーザーパスワード").build();
            return client;
        } catch (Exception e) {
            System.out.println("Unable to connect to database");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * データベースオブジェクトを取得する
     * 
     * @return データベースオブジェクト
     */
    public Database getDB(){
        return db;
    }

    /**
     * データベースオブジェクトを設定する
     * 
     * @param データベースオブジェクト
     */
    public void setDB(Database db) {
    	this.db = db;
    }
}
