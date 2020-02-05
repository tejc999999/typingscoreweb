// HTMLページの確認ダイアログ用
function confirmdialog(check_message, confirm_messsage, cancel_message) {
	if(confirm(check_message)) {
		
		alert(confirm_message);
		
		return true;
	} else {
		
		alert(cancel_message);
		
		return false;
	}
}
