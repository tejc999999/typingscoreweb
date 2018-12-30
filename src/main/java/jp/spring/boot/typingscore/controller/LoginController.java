package jp.spring.boot.typingscore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller class for login.
 * 
 * @author tejc999999
 *
 */
@Controller
public class LoginController {

	/**
	 * Display the login screen.
	 * 
	 * @return  Transition destination path
	 */
	@GetMapping(path = "login")
	String loginForm() {
		return "login";
	}
}
