package jp.spring.boot.typingscore.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.spring.boot.typingscore.form.UserForm;
import jp.spring.boot.typingscore.security.RoleName;
import jp.spring.boot.typingscore.service.UserService;

/**
 * Controller class for user function.
 * 
 * @author tejc999999
 *
 */
@Controller
@RequestMapping("users")
public class UserController {

	/**
	 * User service.
	 */
	@Autowired
	UserService userService;

	/**
	 * Initialize Form to Model.
	 * 
	 * @return UserForm userform.
	 */
	@ModelAttribute
	UserForm setUpForm() {
		return new UserForm();
	}

	/**
	 * Transit to user registration screen.
	 * 
	 * @return Destination view.
	 */
	@GetMapping
	String add(Model model) {
		// Radio Button Options.
        Map<String, String> roleMap = new LinkedHashMap<String, String>();
        roleMap.put(RoleName.ROLE_USER.getString(), "user");
        roleMap.put(RoleName.ROLE_ADMIN.getString(), "administrator");
        
        model.addAttribute("selectedrole", RoleName.ROLE_USER.getString());
        model.addAttribute("roleMapItems",roleMap);

		return "users/add";
	}

	/**
	 * Register a user
	 * 
	 * @param form  Error check target.
	 * @param result Error check result.
	 * @return Destination view.
	 */
	@PostMapping(path = "create")
	String create(@Validated UserForm form, BindingResult result , Model model) {
		if (result.hasErrors()) {
			return add(model);
		}
		userService.create(form);
		return "redirect:/users";
	}

}