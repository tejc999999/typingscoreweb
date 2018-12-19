package jp.spring.boot.typingscore.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserForm {

	@NotNull
	@NotBlank(message = "必須入力です。")
	@Size(min = 6, max = 12, message = "{min} 文字以上 {max} 文字以下で入力してください。")
	private String username;
	@NotNull
	@NotBlank(message = "必須入力です。")
	@Size(min = 6, max = 12, message = "{min} 文字以上 {max} 文字以下で入力してください。")
	private String password;
	@NotNull
	@NotBlank(message = "必須選択です。")
	private String role;
}