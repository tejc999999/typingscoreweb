$(function() {
  var POLLLING_INVERVAL_TIME_IN_MILLIS = 10000;//10s
  (function polling() {
	    if(!document.hidden) {
	    	test();
	    }
    window.setTimeout(polling, POLLLING_INVERVAL_TIME_IN_MILLIS);
  }());

  function test() {
	  
      var modalesectiondata = null;
      $(function(){
          // datatableの設定を変更
          var modalesectiontable = $("#ranktable").DataTable({
        	  "bDestroy": true,
              "bPaginate": false,
              "bLengthChange": false,
              "bFilter": false,
              "bSort": false,
              "bInfo": false,
              "bAutoWidth": false,
              "language": {
                      "url": 'http://cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Japanese.json'
              },
              "ajax": { url: 'scoreload', dataSrc: '' },
              "columns": [
                  { data: "blank" },
                  { data: "rank" },
                  { data: "username" },
                  { data: "point" },
                  { data: "inputtime" },
                  { data: "misstype" },
                  { data: "blank" },
              ],
              "columnDefs": [
                  { targets: 0, width: 60 },
                  { targets: 1, width: 180 },
                  {targets:'_all',className : 'dt-head-center'},
              ],
              "createdRow": function(row, data, dataIndex) {
            	  if(dataIndex == 0) {
            		  $(row).find("td").addClass('firstRow');
            	  } else if(dataIndex == 1) {
            		  $(row).find("td").addClass('secondRow');
            	  } else if(dataIndex == 2) {
            		  $(row).find("td").addClass('thirdRow');
            	  }
              }
          });
      })
  }
  
  function Employee ( name, position, salary, office ) {
	    this.name = name;
	    this.position = position;
	    this.salary = salary;
	    this._office = office;
	 
	    this.office = function () {
	        return this._office;
	    }
	};
  
  function getCountUp() {
    $.ajax({
    type : "GET",
    url : "scoreload",
    content : "application/json",
    dataType : "json",
  }).done(function(list) {
	  

/*	  <tr th:each="score, scoreStat : ${scores}">
        <td></td>
      	<td th:text="${scoreStat.index} + 1 + '/' + ${scoreStat.size} + '位'"></td>
        <td th:text="${score.point}">0</td>
        <td th:text="${score.inputtime}">0</td>
        <td th:text="${score.misstype}">0</td>
        <td th:text="${score.username}">user</td>
        <td></td>
   </tr>*/
	  console.log(list);
	  var response = "";
	  for(var form of list) {
		  response = response + form.point + "," + form.inputtime + "," + form.misstype + "," + form.username + "<BR>";
	  }
	  $("#ranktable").DataTable({
		  
		  
		  
	  });
	  
  }).fail(function(jqXHR, textStatus) {
    $("#test").text("error occured");
    });
  }
});