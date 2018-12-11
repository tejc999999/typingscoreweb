package jp.spring.boot.typingscore.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.spring.boot.typingscore.db.ScoreId;
import jp.spring.boot.typingscore.form.ScoreForm;
import jp.spring.boot.typingscore.form.ScoreRankForm;
import jp.spring.boot.typingscore.form.ScoreResultForm;
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
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
	@GetMapping(path = "add")
	public String add() {
		return "scores/add";
	}

	/**
	 * スコア登録処理
	 * 
	 * @param form  エラーチェック対象スコアForm
	 * @param       result エラーチェック結果
	 * @param model スコア格納用モデル
	 * @return
	 */
	@PostMapping(path = "create")
	String create(@RequestParam String overlapFlg, @Validated ScoreForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "scores/add";
		}

		// ユーザ名重複チェックが済んでいない場合、ユーザ名重複チェック
		if (!"true".equals(overlapFlg)) {
			if (scoreService.findUsernameOverlap(form.getUsername())) {
				// ユーザ名が重複しているので、重複チェック済みとして一旦登録画面に戻す
				model.addAttribute("overlapFlg", "true");
				return "scores/add";
			}
		}
		ScoreForm createScoreForm = scoreService.create(form);

		// 今回のランク
		int rank = 0;
		// 過去を含めた最高ランク
		int maxRank = 0;
		// 過去を含めた最高ポイント
		int maxPoint = createScoreForm.getPoint();
		// 挑戦回数
		int tryCnt = 0;

		List<ScoreForm> list = scoreService.findAllOrderByPoint();
		List<ScoreRankForm> rankList = new ArrayList<ScoreRankForm>();
		List<String> checkUserNameList = new ArrayList<String>();
		int rankNum = 0;
		int overlapRankNum = 0;
		int beforePoint = -1;

		for (ScoreForm tempForm : list) {
			// １ユーザは１回だけ（一人のランクは１つだけ）
			// 重複するユーザ名の場合はスコアの良い方だけをリストに追加する
			if (!checkUserNameList.contains(tempForm.getUsername())) {
				// 同順位対応
				if(beforePoint == tempForm.getPoint()) {
					// 直前ユーザと同ポイントの場合、同順位数をカウント
					overlapRankNum++;
				} else {
					// ランキングの順位は同順位数を含めて+1する
					beforePoint = tempForm.getPoint();
					rankNum = rankNum + overlapRankNum + 1;
					overlapRankNum = 0;
				}
				
				ScoreRankForm rankForm = new ScoreRankForm();
				BeanUtils.copyProperties(tempForm, rankForm);
				rankForm.setRank(rankNum);
				rankForm.setBlank("");
				rankList.add(rankForm);
				checkUserNameList.add(tempForm.getUsername());
			}

			// 過去の同名の成績があれば、最高ポイントを保持する
			if (createScoreForm.getUsername().equals(tempForm.getUsername())) {
				tryCnt++;
				if (tryCnt == 1) {
					// 初回（最高成績）のみ順位とポイントを保持
					maxRank = rankNum;
					maxPoint = tempForm.getPoint();
				}
			}
			// 今回のランクを格納
			if (createScoreForm.getUsername().equals(tempForm.getUsername())
					&& createScoreForm.getCommittime().equals(tempForm.getCommittime())) {
				rank = rankNum;
			}
		}

		ScoreResultForm scoreResultForm = new ScoreResultForm();
		BeanUtils.copyProperties(createScoreForm, scoreResultForm);
		scoreResultForm.setTryCnt(tryCnt);
		scoreResultForm.setRank(rank);
		scoreResultForm.setMaxRank(maxRank);
		scoreResultForm.setRankNum(rankNum + overlapRankNum);
		scoreResultForm.setMaxPoint(maxPoint);

		model.addAttribute("resultform", scoreResultForm);
		return "scores/addcomp";
	}

	/**
	 * 競技者用スコア一覧画面へ遷移する
	 * 
	 * @param model スコア一覧格納用モデル
	 * @return 遷移先ビュー
	 */
	@GetMapping(path = "view")
	public String view(Model model) {

		List<ScoreForm> list = scoreService.findAllOrderByPoint();

		model.addAttribute("scores", list);

		return "scores/view";
	}

	/**
	 * 競技者用スコア一覧画面のランキング情報Ajax更新用
	 * 
	 * @param param
	 * @return
	 */
	@GetMapping(path = "/scoreload", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String scoreLoad() {

		List<ScoreForm> list = scoreService.findAllOrderByPoint();

		Gson gson = new Gson();
		List<ScoreRankForm> rankList = new ArrayList<ScoreRankForm>();
		List<String> checkUserNameList = new ArrayList<String>();
		int rankNum = 0;
		int overlapRankNum = 0;
		int beforePoint = -1;

		for (ScoreForm form : list) {
			// １ユーザは１回だけ（一人のランクは１つだけ）
			// 重複するユーザ名の場合はスコアの良い方だけをリストに追加する
			if (!checkUserNameList.contains(form.getUsername())) {
				// 同順位対応
				if(beforePoint == form.getPoint()) {
					// 直前ユーザと同ポイントの場合、同順位数をカウント
					overlapRankNum++;
				} else {
					// ランキングの順位は同順位数を含めて+1する
					beforePoint = form.getPoint();
					rankNum = rankNum + overlapRankNum + 1;
					overlapRankNum = 0;
				}

				ScoreRankForm rankForm = new ScoreRankForm();
				BeanUtils.copyProperties(form, rankForm);
				rankForm.setRank(rankNum);
				if(rankNum == 1) {
					rankForm.setBlank("<img src=\"/img/gold.png\" class=\"icon\" />");
				} else if(rankNum == 2) {
					rankForm.setBlank("<img src=\"/img/silver.png\" class=\"icon\" />");
				} else if(rankNum == 3) {
					rankForm.setBlank("<img src=\"/img/bronze.png\" class=\"icon\" />");
				} else if(rankNum < 6) {
					rankForm.setBlank("<img src=\"/img/green.png\" class=\"icon\" />");
				} else {
					rankForm.setBlank("");
				}

				rankList.add(rankForm);
				checkUserNameList.add(form.getUsername());
			}
		}

		return gson.toJson(rankList);
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
	 * @param username   ユーザ名
	 * @param committime 登録日時
	 * @param スコア格納用モデル
	 * 
	 * @return 遷移先ビュー
	 */
	@PostMapping(path = "edit")
	public String edit(@RequestParam String username, @RequestParam String committime, Model model) {
		ScoreId id = new ScoreId();
		id.setUsername(username);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date convCommittime = null;
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
	 * @param form        スコアForm
	 * @return 遷移先ビュー
	 */
	@PostMapping(path = "editprocess")
	public String editprocess(@RequestParam String oldusername,
			/* @RequestParam String committime, */ @Validated ScoreForm form, BindingResult result, Model model) {

		if (result.hasErrors()) {
			return "scores/edit";
		}
		ScoreId id = new ScoreId();
		id.setUsername(oldusername);
		;
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
	 * @param username   削除対象ユーザ名
	 * @param committime 削除対象登録日時
	 * @return 遷移先ビュー
	 */
	@PostMapping(path = "delete")
	String delete(@RequestParam String username, @RequestParam String committime) {
		ScoreId id = new ScoreId();
		id.setUsername(username);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
}
