package jp.spring.boot.typingscore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.spring.boot.typingscore.config.ParameterProperties;
import jp.spring.boot.typingscore.form.GameForm;

@Service
public class GameService {
	
	/**
	 * ゲーム区分取得用
	 */
	@Autowired
	ParameterProperties parameterPropaties;
	
	
	/**
	 * 選択されているゲーム区分の更新
	 * 
	 * @param gameForm
	 * @return
	 */
	public GameForm update(GameForm gameForm) {
		
		parameterPropaties.setActiveGameCode(gameForm.getGamecode());
		
		return gameForm;
	}
}
