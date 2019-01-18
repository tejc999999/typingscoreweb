package jp.spring.boot.typingscore.bean;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザ用Bean
 * 
 * @author tejc999999
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserBean {

	/**
	 * ユーザ名
	 * 主キー
	 */
	@Id
	private String username;

	/**
	 * パスワード
	 * 
	 */
	@JsonIgnore
	private String password;
	
	/**
	 * ログイン失敗回数
	 * 
	 */
	@JsonIgnore
	private int loginfailurecnt;

	/**
	 * 非ロック情報
	 */
	private boolean accountNonLocked = true;
	
	/**
	 * 権限
	 * 
	 * ROLE_ADMINまたはROLE_USER.
	 */
	private String role;

}