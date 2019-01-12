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
 * Controller class for database function.
 * 
 * @author tejc999999
 *
 */
@Controller
@RequestMapping("databases")
public class DatabaseController {

	/**
	 * Score Service.
	 */
	@Autowired
	ScoreService scoreService;

	/**
	 * User Service.
	 */
	@Autowired
	UserService userService;

	/**
	 * Display the database management screen.
	 * 
	 * @return Transition destination path
	 */
	@GetMapping
	public String view() {

		return "database/database";
	}

	/**
	 * Initialize the database.
	 * 
	 * @return Transition destination path
	 */
	@GetMapping(path = "initdbscore")
	public String initDbScore() {

		scoreService.deleteAll();

		return "redirect:/databases";
	}

	/**
	 * Delete initial user.
	 * 
	 * @param form   Error check target.
	 * @param result Error check result.
	 * @return Destination view.
	 */
	@GetMapping(path = "initdbdefaultuser")
	public String initDbDefaultUser() {

		userService.delete("demouser");

		return "redirect:/databases";
	}

	/**
	 * Download score data (CSV).
	 * 
	 * @return Destination view
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
	 * Upload score data (CSV)
	 * 
	 * @return Destination view
	 */
	@PostMapping(value = "upload/scorecsv")
	public String setScoreCsv(@RequestParam("upload_file") MultipartFile file) throws JsonProcessingException {

		if (file.isEmpty()) {
			// Processing at abnormal termination
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
					
					// We set a waiting time of 0.1 second for free account
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		return "redirect:/databases";
	}
	
}
