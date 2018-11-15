package jp.spring.boot.typingscore.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jp.spring.boot.typingscore.db.ScoreId;
import jp.spring.boot.typingscore.form.ScoreForm;
import jp.spring.boot.typingscore.form.UserForm;
import jp.spring.boot.typingscore.service.ScoreService;

@Controller
@RequestMapping("scores")
public class ScoreController {

	@Autowired
	ScoreService scoreService;

	@PostMapping(path="create")
	String create(@Validated ScoreForm form, BindingResult result, Model model) {
		if(result.hasErrors()) {
			return list(model);
		}
		scoreService.create(form);
		return "redirect:/scores";
	}
	
    @GetMapping(path="add")
    public String add(ScoreForm form, Model model) {
        return "scores/add";
    }
    @GetMapping(path="view")
    public String view() {
        return "scores/view";
    }

//    @GetMapping()
//    public String list() {
//        return "scores/list";
//    }
	@GetMapping
	String list(Model model) {
		model.addAttribute("scores", scoreService.findAll());
	    return "scores/list";
	}
    @PostMapping(path="edit")
    public String editlist() {
        return "scores/edit";
    }

	@PostMapping(path = "delete")
	String delete(@RequestParam String username, @RequestParam String committime) {
		ScoreId id = new ScoreId();
		System.out.println("username-value:" + username);
		System.out.println("username-type:" + username.getClass());
		System.out.println("committime-value:" + committime);
		System.out.println("committime-type:" + committime.getClass());
//		Date datecommittime = Date.
//		id.setUsername(username);
//		id.setCommittime(committime);
//		scoreService.delete(id);
	    return "redirect:/scores/list";
	}
	@PostMapping(path = "edit", params = "goToTop")
	String goToTop() {
	    return "redirect:/scores/list";
	}

}
