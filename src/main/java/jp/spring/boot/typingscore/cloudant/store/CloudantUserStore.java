package jp.spring.boot.typingscore.cloudant.store;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.org.lightcouch.NoDocumentException;

import jp.spring.boot.typingscore.cloudant.User;


/**
 * ユーザ用ストア（IBM Cloudant用）
 * 
 * @author tejc999999
 *
 */
public class CloudantUserStore extends CloudantStore implements UserStore {

    /**
     * データベース名
     */
    private static final String databaseName = "userdb";

    /**
     * コンストラクタ
     * 
     */
    public CloudantUserStore(){
        CloudantClient cloudant =  createClient();
        if(cloudant!=null){
        	// IBM Cloudantへの接続に成功した場合
        	
        	// DBインスタンス生成
        	setDB(cloudant.database(databaseName, true));
        }
    }

    /**
     * 全てのユーザ情報を取得する
     * 
     * @return 全ユーザ情報リスト
     */
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
     * ユーザ情報を取得する
     * 
     * @param 対象ユーザID
     * @return ユーザ情報
     */
    public User get(String id) throws NoDocumentException {    	
        return getDB().find(User.class, id);
    }

    /**
     * ユーザ情報を登録する
     * 
     * @param user 対象ユーザBean
     * @return 登録ユーザBean
     */
    public User persist(User user) {
        String id = getDB().save(user).getId();
        return getDB().find(User.class, id);
    }

    /**
     * ユーザ情報を更新する
     * 
     * @param id 更新対象ユーザID
     * @param user 更新ユーザBean
     * @return 更新ユーザBean
     */
    public User update(String id, User newUser) {
    	User user = getDB().find(User.class, id);
    	user.setUsername(newUser.getUsername());
    	user.setPassword(newUser.getPassword());
    	user.setLoginfailurecnt(newUser.getLoginfailurecnt());
    	user.setAccountNonLocked(newUser.isAccountNonLocked());
    	user.setRole(newUser.getRole());
    	getDB().update(user);
        return getDB().find(User.class, id);

    }

    /**
     * ユーザ情報を削除する
     * 
     * @param id 削除対象ユーザID
     */
    public void delete(String id) {
        User user = getDB().find(User.class, id);
        getDB().remove(id, user.get_rev());
    }

    /**
     * ユーザ数を取得する
     * 
     * @return ユーザ数
     */
    public int count() throws Exception {
        return getAll().size();
    }
}
