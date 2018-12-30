package jp.spring.boot.typingscore.bean;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login User Bean.
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
	 * username.
	 * primary key.
	 */
	@Id
	private String username;

	/**
	 * password.
	 * 
	 */
	@JsonIgnore
	private String password;

	/**
	 * role.
	 * 
	 * ROLE_ADMIN or ROLE_USER.
	 */
	private String role;

}