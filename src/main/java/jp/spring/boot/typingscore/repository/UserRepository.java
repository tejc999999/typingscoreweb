package jp.spring.boot.typingscore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.spring.boot.typingscore.bean.UserBean;

public interface UserRepository extends JpaRepository<UserBean, String>{
}