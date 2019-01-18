package jp.spring.boot.typingscore.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
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
			if (scoreService.findUsernameOverlapCnt(form.getUsername()) > 0) {
				// Since the user names are duplicated,
				// once the duplication check has been completed, the registration screen is once returned
				model.addAttribute("overlapFlg", "true");
				return "scores/add";
			}
		}
		
		form.setCommittime(new Timestamp(DateUtils.truncate(new Date(), Calendar.SECOND).getTime()));

		ScoreForm createScoreForm = scoreService.create(form);

		ScoreForm highScoreForm =  scoreService.findHighScore(createScoreForm.getUsername());
	
		// Number of challenges
		int tryCnt = scoreService.findUsernameOverlapCnt(createScoreForm.getUsername());
		// The highest point including the past
		int maxPoint = highScoreForm.getPoint();
		// Input time at the highest point including the past
		int maxInputTime = highScoreForm.getInputtime();
		// Number of mistypes at the highest point including the past
		int maxMissType = highScoreForm.getMisstype();

		// This rank
		int rank = 0;
		// Highest rank including past
		int maxRank = 0;		

		int overlapRankNum = 0;
		int beforePoint = -1;
		int rankCnt = 0;
		
		List<ScoreForm> list = scoreService.findHighScoreList();

		if(createScoreForm.getPoint() != maxPoint) {

			list = addHighScoreFormList(createScoreForm, list);
		}
		
		// 全ユーザ数
		int rankNum  = list.size();
		
		for(ScoreForm scoreForm: list) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Same order correspondence
			if(beforePoint == scoreForm.getPoint()) {
				// In the case of the same point as the previous user, the same ranking number is counted
				overlapRankNum++;
			} else {
				// The ranking ranking is incremented by 1 including the ranking number.
				beforePoint = scoreForm.getPoint();
				rankCnt = rankCnt + overlapRankNum + 1;
				overlapRankNum = 0;
			}
			
			System.out.println("DEBUG-A:" + scoreForm.getUsername() + "," + createScoreForm.getUsername());
			if(scoreForm.getUsername().equals(createScoreForm.getUsername())) {
				System.out.println("DEBUG-B:" + scoreForm.getPoint() + "," + createScoreForm.getPoint());
				System.out.println("maxRank=" + maxRank + ", rank=" + rank);
				if(scoreForm.getPoint() != createScoreForm.getPoint()) {
					System.out.println("A-exe");
					maxRank = rankCnt;
				} else {
					System.out.println("B-exe");
					rank = rankCnt;
					if(maxRank == 0) {
						System.out.println("C-exe");
						maxRank = rank;
					}
				}
			}
		}

		ScoreResultForm scoreResultForm = new ScoreResultForm();
		BeanUtils.copyProperties(createScoreForm, scoreResultForm);

		scoreResultForm.setTryCnt(tryCnt);
		scoreResultForm.setRank(rank);
		scoreResultForm.setMaxRank(maxRank);
		scoreResultForm.setRankNum(rankNum);
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

		List<ScoreForm> list = scoreService.findHighScoreList();

		Gson gson = new Gson();
		List<ScoreRankForm> rankList = new ArrayList<ScoreRankForm>();
		int rankNum = 0;
		int overlapRankNum = 0;
		int beforePoint = -1;

		for (ScoreForm form : list) {

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

		scoreService.update(oldusername, form);
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
