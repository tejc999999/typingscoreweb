/******************************************************************************
 * Copyright (c) 2017 IBM Corp.                                               *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 *    http://www.apache.org/licenses/LICENSE-2.0                              *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/
package jp.spring.boot.typingscore.cloudant.store;

/**
 * 
 * スコア用ストアファクトリ
 * IBM Cloudant以外のNoSQLサービスを利用する場合、個別のストア作成
 * 
 * @author tejc999999
 *
 */
public class ScoreStoreFactory {

    private static ScoreStore instance;

    public static ScoreStore getInstance() {
    	if(instance == null) {
        	// 他のデータベースシステムを使用する場合、システムごとに記述を分ける

        	CloudantScoreStore cvif = new CloudantScoreStore();
            if (cvif.getDB() != null) {
                instance = cvif;
            }
    	}
        return instance;
    }
}
