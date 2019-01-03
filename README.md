# WEB system for typing performance management
Character image (src / main / resources / img / charactor.png) of the grade display screen can be used without application for Japanese Tohoku companies only.
In case of use by other companies, it is necessary to replace the image.  
Since the notice image (src / main / resouces / img / sign.png) on the ranking display screen draws Japanese directly in the image, it does not correspond to the multilingual environment

## environment
* framework: SpringBoot
* language: java, html, css, javascript(datatables, ajax)
* DB: IBM Cloudant or H2 database(No persistence)(If environment variable VCAP_SERVICES is not set, it operates in h2 database)
* Library management: Maven

## license
Using Apache 2.0 licensed program for IBM Cloudant related process.

Licensed under the Apache License, Version 2.0 (the "License");  
you may not use this file except in compliance with the License.  
You may obtain a copy of the License at  
http://www.apache.org/licenses/LICENSE-2.0  
Unless required by applicable law or agreed to in writing, software  
distributed under the License is distributed on an "AS IS" BASIS,  
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  
See the License for the specific language governing permissions and  
limitations under the License.

## default settings
Login users exist only below  
* User: demouser
* Password: password
* Role: Administrator

## Main functions
* Score registration
* Score editing
* Delete score
* Score list
* Score Ranking display
* Score upload
* Score download
* Delete all scores
* Login, logout
* Create login user
* Initial login user deleted
* User's administrative authority (administrator or general)
* Login attempts limit(Lock if it fails 10 times)
* DB switching (IBM Cloudant or h2 database)
* Multilingual support (Japanese / English)

## Remarks
* Only the highest score with the same user name ranking is displayed in ranking
* Score = input time + (number of mistypes × 2), whichever has a smaller value is higher
* The locked account is directly released from the DB from the DB (fix the number of failed logins at that time)
