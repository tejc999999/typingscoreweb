package jp.spring.boot.typingscore.cloudant.store;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.org.lightcouch.NoDocumentException;

import jp.spring.boot.typingscore.cloudant.User;


/**
 * IBM Cloudant Database connect.
 * Login User Data Store.
 * 
 * @author tejc999999
 *
 */
public class CloudantUserStore extends CloudantStore implements UserStore {

    /**
     * database name for login user.
     */
    private static final String databaseName = "userdb";

    /**
     * Constructor
     * 
     */
    public CloudantUserStore(){
        CloudantClient cloudant =  createClient();
        if(cloudant!=null){
        	// Cloudant接続クライアントが作成できている場合
        	
        	// DBインスタンス生成
        	setDB(cloudant.database(databaseName, true));
        }
    }

    /**
     * ユーザーの全件取得処理
     */
    @Override
    public Collection<User> getAll(){
        List<User> docs;
        try {
            docs = getDB().getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(User.class);
        } catch (IOException e) {
            return null;
        }
        return docs;
    }

    /**
     * 
     */
    @Override
    public User get(String id) throws NoDocumentException {
        return getDB().find(User.class, id);
    }

    @Override
    public User persist(User user) {
        String id = getDB().save(user).getId();
        return getDB().find(User.class, id);
    }

    @Override
    public User update(String id, User newUser) {
    	User user = getDB().find(User.class, id);
    	user.setUsername(newUser.getUsername());
    	user.setPassword(newUser.getPassword());
    	user.setRole(newUser.getRole());
    	getDB().update(user);
        return getDB().find(User.class, id);

    }

    @Override
    public void delete(String id) {
        User user = getDB().find(User.class, id);
        getDB().remove(id, user.get_rev());
    }

    @Override
    public int count() throws Exception {
        return getAll().size();
    }
}
