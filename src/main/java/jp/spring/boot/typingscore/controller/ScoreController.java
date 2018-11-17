package jp.spring.boot.typingscore.controller;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
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

    @InitBinder
    public void dateBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
        binder.registerCustomEditor(Date.class, editor);
    }
	
	/**
	 * ModelにFormを初期セットする
	 * 
	 * @return ScoreForm
	 */
	@ModelAttribute 
	ScoreForm setUpForm() {
		return new ScoreForm();
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
     * スコア登録処理
     * 
     * @param form エラーチェック対象スコアForm
     * @param result　エラーチェック結果
     * @param model スコア格納用モデル
     * @return
     */
	@PostMapping(path="create")
	String create(@Validated ScoreForm form, BindingResult result, Model model) {
		if(result.hasErrors()) {
			 return "redirect:/scores";
		}


		scoreService.create(form);
		return "redirect:/scores";
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
     * @param model スコア一覧格納用モデル
     * @return 遷移先ビュー
     */
	@GetMapping
	String list(Model model) {

		List<ScoreForm> list = scoreService.findAllOrderByCommittime();
		
		model.addAttribute("scores", list);
		
	    return "scores/list";
	}
	
	/**
	 * スコア編集画面へ遷移する
	 * 
	 * @param username ユーザ名
	 * @param committime 登録日時
	 * @param スコア格納用モデル
	 * 
	 * @return 遷移先ビュー
	 */
    @PostMapping(path="edit")
    public String edit(@RequestParam String username, @RequestParam String committime, Model model) {
    	ScoreId id = new ScoreId();
    	id.setUsername(username);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		Date convCommittime = null;
		//
		try {
			convCommittime = format.parse(committime);
		} catch (ParseException e) {
			convCommittime = new Date();
			e.printStackTrace();
		}
    	id.setCommittime(convCommittime);
    	ScoreForm form = scoreService.findById(id);
    	model.addAttribute("scoreForm", form);
    	model.addAttribute("username", username);
        return "scores/edit";
    }

	/**
	 * スコア編集処理
	 * 
	 * @param oldusername 旧ユーザ名
	 * @param form スコアForm
	 * @return 遷移先ビュー
	 */
    @PostMapping(path="editprocess")
    public String editprocess(@RequestParam String oldusername,/* @RequestParam String committime,*/ @Validated ScoreForm form, BindingResult result, Model model) {
    	
    	if(result.hasErrors()) {
			 return "redirect:/scores";
		}
    	ScoreId id = new ScoreId();
    	id.setUsername(oldusername);;
		id.setCommittime(form.getCommittime());

    	// 変更不可能項目（登録日時）を旧データからコピー
    	form.setCommittime(id.getCommittime());
    	
    	// 更新（識別情報自体を更新する場合があるので、旧データを削除してから更新（新規作成もあり））
    	scoreService.delete(id);
		scoreService.update(form);
		return "redirect:/scores";
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
		//
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
