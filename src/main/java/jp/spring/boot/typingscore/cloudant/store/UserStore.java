package jp.spring.boot.typingscore.cloudant.store;

import java.util.Collection;

import jp.spring.boot.typingscore.cloudant.User;

/**
 * ユーザ用ストア（IBM Cloudant用）インターフェース
 * 
 * @author tejc999999
 *
 */
public interface UserStore {

    /**
     * データベースオブジェクトを取得する
     * 
     * @return データベースオブジェクト
     * @throws Exception 
     */
    public Object getDB();

  
    /**
     * 全てのユーザ情報を取得する
     * 
     * @return 全ユーザ情報リスト
     */
    public Collection<User> getAll();

    /**
     * ユーザ情報を取得する
     * 
     * @param 対象ユーザID
     * @return ユーザ情報
     */
    public User get(String id);

    /**
     * ユーザ情報を登録する
     * 
     * @param user 対象ユーザBean
     * @return 登録ユーザBean
     */
    public User persist(User user);

    /**
     * ユーザ情報を更新する
     * 
     * @param id 更新対象ユーザID
     * @param user 更新ユーザBean
     * @return 更新ユーザBean
     */
    public User update(String id, User user);

    /**
     * ユーザ情報を削除する
     * 
     * @param id 削除対象ユーザID
     */
    public void delete(String id);
  
    /**
     * ユーザ数を取得する
     * 
     * @return ユーザ数
     */
    public int count() throws Exception;
}
