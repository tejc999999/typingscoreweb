package jp.spring.boot.typingscore.cloudant.store;

/**
 * 
 * ユーザ用ストアファクトリ
 * IBM Cloudant以外のNoSQLサービスを利用する場合、個別のストア作成
 * 
 * @author tejc999999
 *
 */
public class UserStoreFactory {
	
    private static UserStore instance;

    public static UserStore getInstance() {
    	if(instance == null) {
        	// 他のデータベースシステムを使用する場合、システムごとに記述を分ける
    		
        	CloudantUserStore cvif = new CloudantUserStore();
            if (cvif.getDB() != null) {
                instance = cvif;
            }
    	}
        return instance;
    }
}
