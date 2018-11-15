package jp.spring.boot.typingscore.controller;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.spring.boot.typingscore.db.ScoreId;
import jp.spring.boot.typingscore.form.ScoreForm;
import jp.spring.boot.typingscore.service.ScoreService;

/**
 * スコア用コントローラクラス
 * 
 * @author t.kawana
 *
 */
@Controller
@RequestMapping("scores")
public class ScoreController {

	@Autowired
	ScoreService scoreService;

	/**
	 * ModelにFormを初期セットする
	 * 
	 * @return ScoreForm
	 */
	@ModelAttribute 
	ScoreForm setUpForm() {
		return new ScoreForm();
	}

	@PostMapping(path="create")
	String create(@Validated ScoreForm form, BindingResult result, Model model) {
		if(result.hasErrors()) {
			 return "redirect:/scores";
		}
		scoreService.create(form);
		return "redirect:/scores";
	}

	/**
	 * スコア登録画面へ遷移する
	 * 
	 * @return 遷移先ビュー
	 */
    @GetMapping(path="add")
    public String add() {
        return "scores/add";
    }
    
    /**
     * 競技者用スコア一覧画面へ遷移する
     * 
     * @return 遷移先ビュー
     */
    @GetMapping(path="view")
    public String view() {
        return "scores/view";
    }

    /**
     * 管理用スコア一覧画面へ遷移する
     * 
     * @param model 結果一覧格納用モデル
     * @return 遷移先ビュー
     */
	@GetMapping
	String list(Model model) {
		model.addAttribute("scores", scoreService.findAll());
	    return "scores/list";
	}
	
	/**
	 * スコア編集画面へ遷移する
	 * 
	 * @return 遷移先ビュー
	 */
    @PostMapping(path="edit")
    public String editlist() {
        return "scores/edit";
    }

    /**
     * スコア削除処理
     * 
     * @param username 削除対象ユーザ名
     * @param committime 削除対象登録日時
     * @return 遷移先ビュー
     */
	@PostMapping(path = "delete")
	String delete(@RequestParam String username, @RequestParam String committime) {
		System.out.println(committime);
		ScoreId id = new ScoreId();
		id.setUsername(username);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		Date convCommittime = null;
		try {
			convCommittime = format.parse(committime);
		} catch (ParseException e) {
			convCommittime = new Date();
			e.printStackTrace();
		}
		id.setCommittime(convCommittime);
		scoreService.delete(id);

	    return "redirect:/scores";
	}
	
//	/**
//	 * スコア編集画面の戻るボタン処理
//	 * 
//	 * @return 遷移先ビュー
//	 */
//	@PostMapping(path = "edit", params = "goToTop")
//	String goToTop() {
//	    return "redirect:/scores/list";
//	}

}
