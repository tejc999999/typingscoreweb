# タイピングスコアを管理するWEBシステム
成績表示画面の文字画像（src / main / resources / img / charactor.png）は、日本の東北の団体に限り申請なしで使用できる画像のため、該当しない場合は画像を差し替える必要がある。
ランキング表示画面の注意画像（src / main / resouces / img / sign.png）は、画像内に直接日本語を描画しているため、多言語環境には対応していない。
同フォルダ内のsign_org.pngを編集し、sign.pngとして利用する。

## environment
* フレームワーク: SpringBoot
* 開発言語: java, html, css, javascript(datatables, ajax)
* DB: IBM CloudantまたはH2 database(永続化なし)(模試環境変数VCAP_SERVICESが設定されていない場合、自動的に組込みH2データベースを使用する)
* ライブラリ管理: Maven

## ライセンス
IBM Cloudant関連のプログラムがApache 2.0ライセンスを使用しているため、本システムも同ライセンスとなる。

Licensed under the Apache License, Version 2.0 (the "License");  
you may not use this file except in compliance with the License.  
You may obtain a copy of the License at  
http://www.apache.org/licenses/LICENSE-2.0  
Unless required by applicable law or agreed to in writing, software  
distributed under the License is distributed on an "AS IS" BASIS,  
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  
See the License for the specific language governing permissions and  
limitations under the License.

## 初期設定
ログインユーザ 
* ユーザID: demouser
* パスワード: password
* 権限: 管理者

## 主な機能
* スコア登録、編集、削除、一覧表示
* スコアランキング表示
* スコアアップロード（CSV）
* スコアダウンロード（CSV）
* スコア全削除
* ログイン、ログアウト
* ログインユーザ登録
* 初期ユーザ削除
* ユーザ管理権限（管理者または一般）
* ログイン失敗制限（10回でロック）
* DB切り替え（IBM Cloudantまたはh2データベース)
* 多言語対応（日本語または英語）

## 備考
* 同じユーザー名のスコアは、最高スコアのみランキングに表示される
* スコアの計算方法は入力時間+（ミスタイプ数×2）で、小さい方がランキング上位となる
* ロックされたアカウントはDBから直接DBから解除する必要がある（ユーザごとのログイン失敗回数を修正）
