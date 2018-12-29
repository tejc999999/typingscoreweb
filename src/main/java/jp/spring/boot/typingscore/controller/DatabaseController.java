package jp.spring.boot.typingscore.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import jp.spring.boot.typingscore.form.ScoreForm;
import jp.spring.boot.typingscore.service.ScoreService;
import jp.spring.boot.typingscore.service.UserService;

/**
 * データベース機能用コントローラクラス
 * 
 * @author t.kawana
 *
 */
@Controller
@RequestMapping("databases")
public class DatabaseController {

	@Autowired
	ScoreService scoreService;

	@Autowired
	UserService userService;

	/**
	 * データベース管理画面表示
	 * 
	 * @return 遷移先パス（データベース管理画面）
	 */
	@GetMapping
	public String view() {

		return "database/database";
	}

	/**
	 * データベース初期化
	 * 
	 * @return 遷移先パス（データベース管理画面）
	 */
	@GetMapping(path = "initdbscore")
	public String initDbScore() {

		scoreService.deleteAll();

		return "redirect:/databases";
	}

	/**
	 * 初期ユーザ削除
	 * 
	 * @param form   エラーチェック対象
	 * @param result エラーチェック結果
	 * @return 遷移先ビュー
	 */
	@GetMapping(path = "initdbdefaultuser")
	public String initDbDefaultUser() {

		userService.delete("demouser");

		return "redirect:/databases";
	}

	/**
	 * スコアデータ(CSV)ダウンロード
	 * 
	 * @return 遷移先ビュー
	 */
	@GetMapping(value = "download/score.csv", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
			+ "; charset=UTF-8; Content-Disposition: attachment")
	@ResponseBody
	public Object getScoreCsv() throws JsonProcessingException {

		List<ScoreForm> list = scoreService.findAll();

		CsvMapper mapper = new CsvMapper();
		CsvSchema schema = mapper.schemaFor(ScoreForm.class).withHeader();
		return mapper.writer(schema).writeValueAsString(list);

	}

	/**
	 * スコアデータ(CSV)アップロード
	 * 
	 * @return 遷移先ビュー
	 */
	@PostMapping(value = "upload/scorecsv")
	public String setScoreCsv(@RequestParam("upload_file") MultipartFile file) throws JsonProcessingException {

		if (file.isEmpty()) {
			// 異常終了時の処理
		} else {

			try {
				StringBuilder lines = new StringBuilder();
				String lineSeparator ="\n";
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
					for (String r = reader.readLine(); r != null; r = reader.readLine()) {
						lines.append(r).append(lineSeparator);
					}
				}

				CsvMapper mapper = new CsvMapper();
				CsvSchema schema = mapper.schemaFor(ScoreForm.class).withHeader().withLineSeparator(lineSeparator);
				MappingIterator<ScoreForm> mappingIte = mapper.readerFor(ScoreForm.class).with(schema).readValues(lines.toString());
				
				while (mappingIte.hasNext()) {
					ScoreForm form = mappingIte.next();
					scoreService.create(form);
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		return "redirect:/databases";
	}
	
}
