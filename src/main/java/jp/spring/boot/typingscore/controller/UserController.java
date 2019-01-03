package jp.spring.boot.typingscore.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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

	
	@Autowired
	MessageSource  messageSource ;
	
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
        roleMap.put(RoleName.ROLE_USER.getString(), this.messageSource.getMessage("role.name.user", null, LocaleContextHolder.getLocale()));
        roleMap.put(RoleName.ROLE_ADMIN.getString(), this.messageSource.getMessage("role.name.admin", null, LocaleContextHolder.getLocale()));
        
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
		System.out.println("DEBUG:" + form);
		userService.create(form);
		return "redirect:/users";
	}

}