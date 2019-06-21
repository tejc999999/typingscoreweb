package jp.spring.boot.typingscore.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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

import jp.spring.boot.typingscore.cloudant.store.VCAPHelper;
import jp.spring.boot.typingscore.config.ParameterProperties;
import jp.spring.boot.typingscore.db.ScoreId;
import jp.spring.boot.typingscore.form.GameForm;
import jp.spring.boot.typingscore.form.ScoreForm;
import jp.spring.boot.typingscore.form.ScoreResultForm;
import jp.spring.boot.typingscore.form.SendForm;
import jp.spring.boot.typingscore.security.RoleName;
import jp.spring.boot.typingscore.service.GameService;
import jp.spring.boot.typingscore.service.ScoreService;
import jp.spring.boot.typingscore.service.SendService;
import jp.spring.boot.typingscore.service.UserService;

/**
 * データベース管理用コントローラクラス
 * 
 * @author tejc999999
 *
 */
@Controller
@RequestMapping("databases")
public class DatabaseController {
	
	/**
	 * パラメータ
	 */
	@Autowired
	ParameterProperties parameterPropaties;

	/**
	 * スコア用サービス
	 */
	@Autowired
	ScoreService scoreService;
	
	/**
	 * ゲーム区分サービス
	 */
	@Autowired
	GameService gameService;

	/**
	 * ユーザ用サービス
	 */
	@Autowired
	UserService userService;
	
	/**
	 * 送信用サービス
	 */
	@Autowired
	SendService sendService;

	/**
	 * データベース管理画面を表示する
	 * 
	 * @return 遷移先パス
	 */
	@GetMapping
	public String view(Model model) {
		
		model.addAttribute("activegamecode", parameterPropaties.getGameType().get( parameterPropaties.getActiveGameCode()));
		return "database/database";
	}

	/**
	 * データベースを初期化する
	 * 
	 * @return 遷移先パス
	 */
	@GetMapping(path = "initdbscore")
	public String initDbScore() {

		scoreService.deleteAll();

		return "redirect:/databases";
	}

	/**
	 * 初期ユーザを削除する
	 * 
	 * @return 遷移先ビュー名
	 */
	@GetMapping(path = "initdbdefaultuser")
	public String initDbDefaultUser() {

		userService.delete("demouser");

		return "redirect:/databases";
	}

	/**
	 * スコアデータ（CSV）をダウンロードする
	 * 
	 * @return 遷移先ビュー名
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
	 * スコアデータ（CSV）をアップロードする
	 * 
	 * @return 遷移先ビュー名
	 */
	@PostMapping(value = "upload/scorecsv")
	public String setScoreCsv(@RequestParam("upload_file") MultipartFile file) throws JsonProcessingException {

		if (file.isEmpty()) {
			// アップロードファイルが空の場合
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
					
					// IBM・ライトアカウントの場合は制限があるため、間隔をあける
					if (VCAPHelper.VCAP_SERVICES != null) {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		return "redirect:/databases";
	}
	
	/**
	 * ゲーム区分選択
	 * @param model
	 * @return
	 */
	@PostMapping(path = "game")
	public String game(Model model) {
		
		model.addAttribute("gamecode", parameterPropaties.getGameType());
		model.addAttribute("activegamecode", parameterPropaties.getGameType().get( parameterPropaties.getActiveGameCode()));
		return "database/game";
	}
	
	/**
	 * ゲーム区分更新
	 * @param gameForm
	 * @param model
	 * @return
	 */
	@PostMapping(path = "gameupdate")
	public String gameupdate(GameForm gameForm, Model model) {
		gameForm = gameService.update(gameForm);
		return view(model);
	}
	
}
