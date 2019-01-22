package jp.spring.boot.typingscore.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.BeanUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.spring.boot.typingscore.db.ScoreId;
import jp.spring.boot.typingscore.form.ScoreForm;
import jp.spring.boot.typingscore.form.ScoreRankForm;
import jp.spring.boot.typingscore.form.ScoreResultForm;
import jp.spring.boot.typingscore.security.RoleName;
import jp.spring.boot.typingscore.service.ScoreService;

/**
 * スコア用コントローラクラス
 * 
 * @author tejc999999
 *
 */
@Controller
@RequestMapping("scores")
public class ScoreController {

	/**
	 * スコア用サービス
	 */
	@Autowired
	ScoreService scoreService;
	
	/**
	 * 多言語対応用メッセージソース
	 */
	@Autowired
	MessageSource  messageSource ;

	/**
	 * モデルを初期化する
	 * 
	 * @return ScoreForm モデルにセットするScoreForm
	 */
	@ModelAttribute
	ScoreForm setUpForm() {
		return new ScoreForm();
	}

	/**
	 * スコア登録画面に遷移する
	 * 
	 * @return 遷移先ビュー
	 */
	@GetMapping(path = "add")
	public String add() {
		return "scores/add";
	}

	/**
	 * スコアを登録する
	 * 
	 * @param form  スコア登録用ScoreForm
	 * @param  result エラーチェック結果
	 * @param model ユーザ名重複許可保存用モデル
	 * @return 遷移先ビュー名
	 */
	@PostMapping(path = "create")
	String create(@RequestParam String overlapFlg, @Validated ScoreForm form, BindingResult result, Model model) {
		
		if (result.hasErrors()) {
			// 入力値にエラーがある場合、スコア登録画面に戻す
			return "scores/add";
		}

		if (!"true".equals(overlapFlg)) {
			// ユーザ名の重複が許可されていない場合
			if (scoreService.findUsernameOverlapCnt(form.getUsername()) > 0) {
				// ユーザ名が重複している場合、重複を許可して一旦スコア登録画面に戻す
				model.addAttribute("overlapFlg", "true");
				return "scores/add";
			}
		}
		
		form.setCommittime(new Timestamp(DateUtils.truncate(new Date(), Calendar.SECOND).getTime()));

		ScoreForm createScoreForm = scoreService.create(form);

		ScoreForm highScoreForm =  scoreService.findHighScore(createScoreForm.getUsername());
	
		// 挑戦回数
		int tryCnt = scoreService.findUsernameOverlapCnt(createScoreForm.getUsername());
		// 最高スコア
		int maxPoint = highScoreForm.getPoint();
		// 最高スコアの入力時間（分）
		int maxInputTimeMin = highScoreForm.getInputtimeMin();
		// 最高スコアの入力時間（秒）
		int maxInputTimeSec = highScoreForm.getInputtimeSec();
		// 最高スコアのミスタイプ数
		int maxMissType = highScoreForm.getMisstype();

		// 全ユーザの最高スコアリスト（スコア順）
		List<ScoreForm> list = scoreService.findHighScoreList();
		if(createScoreForm.getPoint() != maxPoint) {
			// 今回スコアをリストに追加（スコア順の該当箇所に挿入）
			list = addHighScoreFormList(createScoreForm, list);
		}
		
		// 全ユーザ数
		int rankNum  = list.size();
		// 今回スコアの順位
		int rank = 0;
		// 最高スコアの順位
		int maxRank = 0;		
		
		int overlapRankNum = 0;
		int beforePoint = -1;
		int rankCnt = 0;
		for(ScoreForm scoreForm: list) {

			if(beforePoint == scoreForm.getPoint()) {
				// スコアが直前と同じ場合、同順処理のため一時的なカウンタに記録する
				overlapRankNum++;
			} else {
				// 順位を記録する
				beforePoint = scoreForm.getPoint();
				rankCnt = rankCnt + overlapRankNum + 1;
				overlapRankNum = 0;
			}
			
			if(scoreForm.getUsername().equals(createScoreForm.getUsername())) {
				// 今回スコアのユーザ名と同じ場合
				if(scoreForm.getPoint() != createScoreForm.getPoint()) {
					// 今回スコアのScoreFormではない場合（最高スコアの場合）
					maxRank = rankCnt;
				} else {
					// 今回スコアのScoreFormの場合
					rank = rankCnt;
					if(maxRank == 0) {
						// 最高スコアが未記録の場合、今回のスコアが最高スコアになる
						maxRank = rank;
					}
				}
			}
		}

		ScoreResultForm scoreResultForm = new ScoreResultForm();
		BeanUtils.copyProperties(createScoreForm, scoreResultForm);
		// 描画用入力時間文字列を設定する
        String minunitstr = this.messageSource.getMessage("html.scores.view.rank.inputtime.unitmin", null, LocaleContextHolder.getLocale());
        String secunitstr = this.messageSource.getMessage("html.scores.view.rank.inputtime.unitsec", null, LocaleContextHolder.getLocale());
		scoreResultForm.setInputtimeView(scoreResultForm.getInputtimeMin() + " " + minunitstr + "  " + scoreResultForm.getInputtimeSec() + " " + secunitstr);
		
		scoreResultForm.setTryCnt(tryCnt);
		scoreResultForm.setRank(rank);
		scoreResultForm.setMaxRank(maxRank);
		scoreResultForm.setRankNum(rankNum);
		scoreResultForm.setMaxPoint(maxPoint);
		scoreResultForm.setMaxInputtimeView(maxInputTimeMin + " " + minunitstr + "  " + maxInputTimeSec + " " + secunitstr);
		scoreResultForm.setMaxMisstype(maxMissType);
		
		model.addAttribute("resultform", scoreResultForm);
		return "scores/addcomp";
	}

	/**
	 * ランキング画面に遷移する
	 * 
	 * @param model スコア一覧保存用モデル
	 * @return 遷移先ビュー名
	 */
	@GetMapping(path = "view")
	public String view(Model model) {

		List<ScoreForm> list = scoreService.findAllOrderByPoint();

		model.addAttribute("scores", list);

		return "scores/view";
	}

	/**
	 * ランキング画面のAjax用ランキング情報を取得する
	 * 
	 * @return 遷移先ビュー名
	 */
	@GetMapping(path = "/scoreload", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String scoreLoad() {

		List<ScoreForm> list = scoreService.findHighScoreList();

		Gson gson = new Gson();
		List<ScoreRankForm> rankList = new ArrayList<ScoreRankForm>();
		int rankNum = 0;
		int overlapRankNum = 0;
		int beforePoint = -1;

		for (ScoreForm form : list) {

			if(beforePoint == form.getPoint()) {
				// スコアが直前と同じ場合、同順処理のため一時的なカウンタに記録する
				overlapRankNum++;
			} else {
				// 順位を記録する
				beforePoint = form.getPoint();
				rankNum = rankNum + overlapRankNum + 1;
				overlapRankNum = 0;
			}

			ScoreRankForm rankForm = new ScoreRankForm();
			BeanUtils.copyProperties(form, rankForm);
			rankForm.setRank(rankNum);

			// ランキング画面に描画するアイコン表示HTMLを保存する
			if(rankNum == 1) {
				rankForm.setBlank("<span class=\"blinking\"><img src=\"/img/gold.png\" class=\"icon\" /></span>");
			} else if(rankNum == 2) {
				rankForm.setBlank("<span class=\"blinking\"><img src=\"/img/silver.png\" class=\"icon\" /></span>");
			} else if(rankNum == 3) {
				rankForm.setBlank("<span class=\"blinking\"><img src=\"/img/bronze.png\" class=\"icon\" /></span>");
			} else if(rankNum < 6) {
				rankForm.setBlank("<span class=\"blinking\"><img src=\"/img/green.png\" class=\"icon\" /></span>");
			} else {
				rankForm.setBlank("");
			}

			// 描画用入力時間文字列を設定する
	        String minunitstr = this.messageSource.getMessage("html.scores.view.rank.inputtime.unitmin", null, LocaleContextHolder.getLocale());
	        String secunitstr = this.messageSource.getMessage("html.scores.view.rank.inputtime.unitsec", null, LocaleContextHolder.getLocale());

			rankForm.setInputtimeView(rankForm.getInputtimeMin() + " " + minunitstr + "  " + rankForm.getInputtimeSec() + " " + secunitstr);
			
			rankList.add(rankForm);
		}

		return gson.toJson(rankList);
	}

	/**
	 * スコア一覧画面に遷移する
	 * 
	 * @param model スコア一覧保存用モデル
	 * @return 遷移先ビュー名
	 */
	@GetMapping
	String list(Model model) {

		List<ScoreForm> list = scoreService.findAllOrderByCommittime();

		model.addAttribute("scores", list);

		return "scores/list";
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
			return "scores/";
		}
		id.setCommittime(convCommittime);

		ScoreForm form = scoreService.findById(id);
		model.addAttribute("scoreForm", form);
		model.addAttribute("username", username);
		
		return "scores/edit";
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
			return "scores/edit";
		}
		ScoreId id = new ScoreId();
		id.setUsername(oldusername);

		id.setCommittime(form.getCommittime());

		// 編集前のScoreFormから変動しない値（登録日時）を複製する
		form.setCommittime(id.getCommittime());

		scoreService.update(oldusername, form);
		return "redirect:/scores";
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
			return "redirect:/scores";
		}
		id.setCommittime(convCommittime);
		scoreService.delete(id);

		return "redirect:/scores";
	}
	
	/**
	 * 
	 * スコア順に並んだリストの適切な順番に新しいScoreFormを挿入する
	 * 
	 * @param scoreForm 挿入するScoreForm
	 * @param list スコア順に並んだリスト
	 * @return 挿入後のリスト
	 */
	private List<ScoreForm> addHighScoreFormList(ScoreForm scoreForm, List<ScoreForm> list) {
		
		int index = 0;
		for(ScoreForm tempScoreForm : list) {
			if(tempScoreForm.getPoint() >= scoreForm.getPoint()) {
				break; 
			}
			index++;
		}
		list.add(index, scoreForm);
		
		return list;
	}
}
