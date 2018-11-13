package jp.spring.boot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TypingscoreController {

    @RequestMapping("/")
    public String index() {
        return "this is Spring Boot sample2.";
    }

}
