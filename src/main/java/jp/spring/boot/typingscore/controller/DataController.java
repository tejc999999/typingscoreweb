package jp.spring.boot.typingscore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * スコア用コントローラクラス
 * 
 * @author t.kawana
 *
 */
@Controller
@RequestMapping("datas")
public class DataController {

	/**
	 * 接続テスト用
	 * 
	 * @return 遷移先ビュー
	 */
	@GetMapping()
	public String test() {

		return "datas/test";
	}
}
