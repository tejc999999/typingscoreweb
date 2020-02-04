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
		private String splitusername;
		
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
		 * 日本語スコア
		 * 計算方法 = 入力時間 + (ミスタイプ数 * 2)
		 */
		private int jppoint;
		
		/**
		 * 英語スコア
		 * 計算方法 = 入力時間 + (ミスタイプ数 * 2)
		 */
		private int enpoint;
		
		/**
		 * 登録日時
		 * 複合主キーの一部
		 */
		private Timestamp committime;
		
		
		/**
		 * 英語区分のユーザ名
		 * 複合主キーの一部
		 */
		private String enusername;
		
		/**
		 * 英語区分の登録日時
		 * 複合主キーの一部
		 */
		private Timestamp encommittime;
		
		
}
