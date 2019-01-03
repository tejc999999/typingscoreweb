package jp.spring.boot.typingscore.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.spring.boot.typingscore.db.ScoreId;
import jp.spring.boot.typingscore.form.ScoreForm;
import jp.spring.boot.typingscore.form.ScoreRankForm;
import jp.spring.boot.typingscore.form.ScoreResultForm;
import jp.spring.boot.typingscore.service.ScoreService;

/**
 * Controller class for score function.
 * 
 * @author tejc999999
 *
 */
@Controller
@RequestMapping("scores")
public class ScoreController {

	@Autowired
	ScoreService scoreService;

//	@InitBinder
//	public void dateBinder(WebDataBinder binder) {
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
//		binder.registerCustomEditor(Date.class, editor);
//	}

	/**
	 * Initialize Form to Model
	 * 
	 * @return ScoreForm score form.
	 */
	@ModelAttribute
	ScoreForm setUpForm() {
		return new ScoreForm();
	}

	/**
	 * Transit to the score registration screen
	 * 
	 * @return Destination view
	 */
	@GetMapping(path = "add")
	public String add() {
		return "scores/add";
	}

	/**
	 * Score registration processing
	 * 
	 * @param form  Error check target score Form
	 * @param  result Error check result
	 * @param model Model for storing scores
	 * @return Destination view
	 */
	@PostMapping(path = "create")
	String create(@RequestParam String overlapFlg, @Validated ScoreForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "scores/add";
		}

		// If the user name duplication check has not been completed, the user name duplication check
		if (!"true".equals(overlapFlg)) {
			if (scoreService.findUsernameOverlap(form.getUsername())) {
				// Since the user names are duplicated,
				// once the duplication check has been completed, the registration screen is once returned
				model.addAttribute("overlapFlg", "true");
				return "scores/add";
			}
		}
		ScoreForm createScoreForm = scoreService.create(form);

		// This rank
		int rank = 0;
		// Highest rank including past
		int maxRank = 0;
		// The highest point including the past
		int maxPoint = createScoreForm.getPoint();
		// Input time at the highest point including the past
		int maxInputTime = createScoreForm.getInputtime();
		// Number of mistypes at the highest point including the past
		int maxMissType = createScoreForm.getMisstype();
		// Number of challenges
		int tryCnt = 0;

		List<ScoreForm> list = scoreService.findAllOrderByPoint();
		List<ScoreRankForm> rankList = new ArrayList<ScoreRankForm>();
		List<String> checkUserNameList = new ArrayList<String>();
		int rankNum = 0;
		int overlapRankNum = 0;
		int beforePoint = -1;

		for (ScoreForm tempForm : list) {
			// One user only once (only one rank is one)
			// In case of a duplicate user name, only the one with the better score is added to the list
			if (!checkUserNameList.contains(tempForm.getUsername())) {
				// Same order correspondence
				if(beforePoint == tempForm.getPoint()) {
					// In the case of the same point as the previous user, the same ranking number is counted
					overlapRankNum++;
				} else {
					// The ranking ranking is incremented by 1 including the ranking number
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

			// Hold the highest points if there is a record of the same name in the past
			if (createScoreForm.getUsername().equals(tempForm.getUsername())) {
				tryCnt++;
				if (tryCnt == 1) {
					// Keep rank and points only for first time (highest grades)
					maxRank = rankNum;
					maxPoint = tempForm.getPoint();
					maxInputTime = tempForm.getInputtime();
					maxMissType = tempForm.getMisstype();
				}
				
				// Store this rank
				if (createScoreForm.getCommittime().equals(tempForm.getCommittime())) {
					if(checkUserNameList.size() > 1 && maxPoint < createScoreForm.getPoint()) {
						// Add self position.
						rank = rankNum + 1;
					} else {
						rank = rankNum;
					}
				}
			}
		}

		ScoreResultForm scoreResultForm = new ScoreResultForm();
		BeanUtils.copyProperties(createScoreForm, scoreResultForm);
		scoreResultForm.setTryCnt(tryCnt);
		scoreResultForm.setRank(rank);
		scoreResultForm.setMaxRank(maxRank);
		scoreResultForm.setRankNum(rankNum + overlapRankNum);
		scoreResultForm.setMaxPoint(maxPoint);
		scoreResultForm.setMaxInputtime(maxInputTime);
		scoreResultForm.setMaxMisstype(maxMissType);

		model.addAttribute("resultform", scoreResultForm);
		return "scores/addcomp";
	}

	/**
	 * Transition to competitor score list screen
	 * 
	 * @param model Model for storing score list
	 * @return Destination view
	 */
	@GetMapping(path = "view")
	public String view(Model model) {

		List<ScoreForm> list = scoreService.findAllOrderByPoint();

		model.addAttribute("scores", list);

		return "scores/view";
	}

	/**
	 * For updating the ranking information Ajax of the competitor score list screen
	 * 
	 * @return Destination view
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
			// One user only once (only one rank is one)
			// In case of a duplicate user name, only the one with the better score is added to the list
			if (!checkUserNameList.contains(form.getUsername())) {
				// Same order correspondence
				if(beforePoint == form.getPoint()) {
					// In the case of the same point as the previous user, the same ranking number is counted
					overlapRankNum++;
				} else {
					// The ranking ranking is incremented by 1 including the ranking number.
					beforePoint = form.getPoint();
					rankNum = rankNum + overlapRankNum + 1;
					overlapRankNum = 0;
				}

				ScoreRankForm rankForm = new ScoreRankForm();
				BeanUtils.copyProperties(form, rankForm);
				rankForm.setRank(rankNum);
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

				rankList.add(rankForm);
				checkUserNameList.add(form.getUsername());
			}
		}

		return gson.toJson(rankList);
	}

	/**
	 * Transit to the management score list screen.
	 * 
	 * @param model Model for storing score list.
	 * @return Destination view.
	 */
	@GetMapping
	String list(Model model) {

		List<ScoreForm> list = scoreService.findAllOrderByCommittime();

		model.addAttribute("scores", list);

		return "scores/list";
	}

	/**
	 * Transit to score edit screen.
	 * 
	 * @param username user name.
	 * @param committime Registered Date.
	 * @param Model for storing scores.
	 * 
	 * @return Destination view.
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
	 * Edit score.
	 * 
	 * @param oldusername old user name.
	 * @param form score form.
	 * @return Destination view
	 */
	@PostMapping(path = "editprocess")
	public String editprocess(@RequestParam String oldusername,
			/* @RequestParam String committime, */ @Validated ScoreForm form, BindingResult result, Model model) {

		if (result.hasErrors()) {
			return "scores/edit";
		}
		ScoreId id = new ScoreId();
		id.setUsername(oldusername);

		id.setCommittime(form.getCommittime());

		// Copy the unchangeable item (registration date and time) from the old data
		form.setCommittime(id.getCommittime());

		// Update
		// (Since the identification information itself may be updated, delete old data and create a new one)
		scoreService.delete(id);
		scoreService.create(form);
		return "redirect:/scores";
	}

	/**
	 * Delete score.
	 * 
	 * @param username   Delete target user name
	 * @param committime Delete target registration date and time
	 * @return Destination view
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
}
