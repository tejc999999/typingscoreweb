function setAlertColors(){
	//ランキング同順位アラート
	var tiechecked = document.getElementsByName("tiechecked")[0];
	var tiecheckedtext = tiechecked.textContent;
	
	if(tiecheckedtext.length > 0){
		tiechecked.className = "alert alert-warning";
	}
	
	//送信結果のアラート切り替え
	var result = document.getElementsByName("result")[0];
	var resulttext = result.textContent;
	var pattern = "HTTP";
	
	if(resulttext.indexOf(pattern) > 0){
		result.className = "alert alert-danger";
	}
	else if(resulttext.length > 0){
		result.className = "alert alert-success";
	}

}