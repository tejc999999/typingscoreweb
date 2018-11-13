package jp.spring.boot.typingscore.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("score")
public class TypingscoreController {

	@PostMapping(path="add")
    public String add() {
        return "this is score input page.";
    }

    @GetMapping(path="view")
    public String view() {
        return "this is score list view page.";
    }

    @GetMapping()
    public String list() {
        return "this is list page.";
    }

    @PostMapping(path="edit")
    public String editlist() {
        return "this is score edit page.";
    }

	@PostMapping(path = "delete")
	String delete(@RequestParam Integer id) {
	   //  bookService.delete(id);
	    return "redirect:/score/list";
	}
	@PostMapping(path = "edit", params = "goToTop")
	String goToTop() {
	    return "redirect:/score/list";
	}

}
