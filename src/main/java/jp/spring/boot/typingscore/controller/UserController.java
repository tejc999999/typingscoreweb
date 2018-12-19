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
 * ユーザ用コントローラクラス
 * 
 * @author t.kawana
 *
 */
@Controller
@RequestMapping("users")
public class UserController {

	@Autowired
	UserService userService;

	/**
	 * ModelにFormを初期セットする
	 * 
	 * @return UserForm
	 */
	@ModelAttribute
	UserForm setUpForm() {
		return new UserForm();
	}

	/**
	 * ユーザ登録画面へ遷移する
	 * 
	 * @return 遷移先ビュー
	 */
	@GetMapping
	String add(Model model) {
		// ラジオボタンの選択肢
        Map<String, String> roleMap = new LinkedHashMap<String, String>();
        roleMap.put(RoleName.ROLE_USER.getString(), "一般ユーザ");
        roleMap.put(RoleName.ROLE_ADMIN.getString(), "管理者");
        
        model.addAttribute("selectedrole", RoleName.ROLE_USER.getString());
        model.addAttribute("roleMapItems",roleMap);

		return "users/add";
	}

	/**
	 * ユーザ登録
	 * 
	 * @param form   エラーチェック対象
	 * @param result エラーチェック結果
	 * @return 遷移先ビュー
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