package jp.spring.boot.typingscore.form;

import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SendForm {
	
		/**
		 * ユーザ名
		 * 複合主キーの一部
		 */
		private String username;
		
		/**
		 * 学科名
		 */
		private String department;
		
		/**
		 * ユーザーの名前
		 */
		private String usernamename;
		
		/**
		 *　ゲーム区分
		 */
		private String gamecode;
		
		/**
		 * 順位
		 */
		private int rank;
		
		/**
		 * スコア
		 * 計算方法 = 入力時間 + (ミスタイプ数 * 2)
		 */
		private int point;
		
		/**
		 * 登録日時
		 * 複合主キーの一部
		 */
		private Timestamp committime;
		
}
