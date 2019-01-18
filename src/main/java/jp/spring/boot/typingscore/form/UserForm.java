package jp.spring.boot.typingscore.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザ用Form
 * 
 * @author tejc999999
 *
 */
@Data
@NoArgsConstructor
public class UserForm {

	/**
	 * ユーザ名
	 */
	@NotNull
	@NotBlank(message = "required input.")
	@Size(min = 6, max = 12, message = "Enter at least {min} character and not more than {max} character.")
	private String username;
	
	/**
	 * パスワード
	 */
	@NotNull
	@NotBlank(message = "required input.")
	@Size(min = 6, max = 12, message = "Enter at least {min} character and not more than {max} character.")
	private String password;
	
	/**
	 * 権限
	 * ROLE_ADMINまたはROLE_USER.
	 */
	@NotNull
	@NotBlank(message = "required input.")
	private String role;
	
	/**
	 * ログイン失敗回数
	 */
	private int loginfailurecnt;
	
	/**
	 * 非ロック情報
	 */
	private boolean accountNonLocked = true;
}