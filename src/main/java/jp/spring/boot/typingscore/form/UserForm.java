package jp.spring.boot.typingscore.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login user form.
 * 
 * @author tejc999999
 *
 */
@Data
@NoArgsConstructor
public class UserForm {

	/**
	 * username.
	 */
	@NotNull
	@NotBlank(message = "required input.")
	@Size(min = 6, max = 12, message = "Enter at least {min} character and not more than {max} character.")
	private String username;
	
	/**
	 * password.
	 */
	@NotNull
	@NotBlank(message = "required input.")
	@Size(min = 6, max = 12, message = "Enter at least {min} character and not more than {max} character.")
	private String password;
	
	/**
	 * Role.
	 * ROLE_ADMIN or ROLE_USER.
	 */
	@NotNull
	@NotBlank(message = "required input.")
	private String role;
}