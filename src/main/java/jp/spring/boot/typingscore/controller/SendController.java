package jp.spring.boot.typingscore.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.spring.boot.typingscore.db.ScoreId;
import jp.spring.boot.typingscore.form.ScoreForm;
import jp.spring.boot.typingscore.form.SendForm;
import jp.spring.boot.typingscore.service.ScoreService;
import jp.spring.boot.typingscore.service.SendService;

@Controller
@RequestMapping("send")
public class SendController {
	
	@Autowired
	SendService sendService;
	
	@Autowired
	ScoreService scoreService;
	
	/**
	 * データベース管理画面を表示する
	 * 
	 * @return 遷移先パス
	 */
	@GetMapping
	public String list(Model model) {
		List<SendForm> totalranklist = sendService.getTotalRankingList();
		List<SendForm> japaneseranklist = sendService.getJapaneseRankingList();
		List<SendForm> englishranklist = sendService.getEnglishRankingList();
		
		model.addAttribute("totalrank", totalranklist);
		model.addAttribute("japaneserank", japaneseranklist);
		model.addAttribute("englishrank", englishranklist);
		return "send/list";
	}
	
	@PostMapping(value = "postrank")
	public String postRank(Model model) {
		List<SendForm> sendranklist = sendService.getTotalRankingList();
		sendranklist.addAll(sendService.getJapaneseRankingList());
		sendranklist.addAll(sendService.getEnglishRankingList());
		
		if(sendService.postRanking(sendranklist)) {
			System.out.println("Send RankingData Success");
		}
		else {
			System.out.println("Send RankingData Failed");
		}
		
		return "redirect:/send";
	}
	
	/**
	 * スコア編集画面に遷移する
	 * 
	 * @param username ユーザ名
	 * @param committime 登録日時
	 * @param Model スコア保存用モデル
	 * 
	 * @return 遷移先ビュー名
	 */
	@PostMapping(path = "edit")
	public String edit(@RequestParam String username, @RequestParam String committime, Model model) {
		
		ScoreId id = new ScoreId();
		id.setUsername(username);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp convCommittime = null;
		try {
			convCommittime = new Timestamp(format.parse(committime).getTime());
		} catch (ParseException e) {
			// 日時変換に失敗した場合、スコア一覧画面に戻す
			e.printStackTrace();
			return "redirect:/send";
		}
		id.setCommittime(convCommittime);

		ScoreForm form = scoreService.findById(id);
		model.addAttribute("scoreForm", form);
		model.addAttribute("username", username);
		
		return "send/edit";
	}
	
	/**
	 * スコア編集
	 * 
	 * @param oldusername 編集前ユーザ名.
	 * @param form 編集ScoreForm
	 * @return 遷移先ビュー名
	 */
	@PostMapping(path = "editprocess")
	public String editprocess(@RequestParam String oldusername,
			@Validated ScoreForm form, BindingResult result, Model model) {

		if (result.hasErrors()) {
			// 編集値にエラーがある場合、編集画面に戻す
			return "send/edit";
		}
		ScoreId id = new ScoreId();
		id.setUsername(oldusername);

		id.setCommittime(form.getCommittime());

		// 編集前のScoreFormから変動しない値（登録日時）を複製する
		form.setCommittime(id.getCommittime());

		scoreService.update(oldusername, form);
		return "redirect:/send";
	}

	/**
	 * スコア削除
	 * 
	 * @param username  削除対象ユーザ名
	 * @param committime 削除対象登録日時
	 * @return 遷移先ビュー名
	 */
	@PostMapping(path = "delete")
	String delete(@RequestParam String username, @RequestParam String committime) {
		ScoreId id = new ScoreId();
		id.setUsername(username);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp convCommittime = null;
		//
		try {
			convCommittime = new Timestamp(format.parse(committime).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			return "redirect:/send";
		}
		id.setCommittime(convCommittime);
		scoreService.delete(id);

		return "redirect:/send";
	}
	
}
