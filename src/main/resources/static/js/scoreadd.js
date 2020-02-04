function getSelectUserName(){
  var obj = document.getElementsByName("scores")[0];

  index = obj.selectedIndex;
  if (index != 0){
	  document.getElementById("username").value = obj.options[index].value;
  }
}