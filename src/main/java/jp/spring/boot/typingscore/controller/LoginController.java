package jp.spring.boot.typingscore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ログイン用コントローラクラス
 * 
 * @author tejc999999
 *
 */
@Controller
public class LoginController {

	/**
	 * ログイン画面に遷移する
	 * 
	 * @return  遷移先ビュー名
	 */
	@GetMapping(path = "login")
	String loginForm() {
		return "login";
	}
}
