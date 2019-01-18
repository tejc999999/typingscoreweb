package jp.spring.boot.typingscore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.spring.boot.typingscore.bean.UserBean;

/**
 * ユーザ用リポジトリ
 * 
 * @author tejc999999
 *
 */
public interface UserRepository extends JpaRepository<UserBean, String> {
}