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
 * ユーザ用コントローラクラス
 * 
 * @author tejc999999
 *
 */
@Controller
@RequestMapping("users")
public class UserController {

	/**
	 * ユーザ用サービス
	 */
	@Autowired
	UserService userService;

	/**
	 * 多言語対応用メッセージソース
	 */
	@Autowired
	MessageSource  messageSource ;
	
	/**
	 * モデルを初期化する
	 * 
	 * @return UserForm モデルに設定するUserForm
	 */
	@ModelAttribute
	UserForm setUpForm() {
		return new UserForm();
	}

	/**
	 * ユーザ登録画面に遷移する
	 * 
	 * @return 遷移先ビュー名
	 */
	@GetMapping
	String add(Model model) {
		// ラジオボタンのオプション（名称）を設定する.
        Map<String, String> roleMap = new LinkedHashMap<String, String>();
        roleMap.put(RoleName.ROLE_USER.getString(), this.messageSource.getMessage("role.name.user", null, LocaleContextHolder.getLocale()));
        roleMap.put(RoleName.ROLE_ADMIN.getString(), this.messageSource.getMessage("role.name.admin", null, LocaleContextHolder.getLocale()));
        
        model.addAttribute("selectedrole", RoleName.ROLE_USER.getString());
        model.addAttribute("roleMapItems",roleMap);

		return "users/add";
	}

	/**
	 * ユーザを登録する
	 * 
	 * @param form 登録ユーザ用UserForm
	 * @param result エラーチェック結果
	 * @return 遷移先ビュー名
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