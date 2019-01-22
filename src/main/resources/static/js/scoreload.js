$(function() {
  var POLLLING_INVERVAL_TIME_IN_MILLIS = 10000;// 10s
  (function polling() {
	    if(!document.hidden) {
	    	test();
	    }
    window.setTimeout(polling, POLLLING_INVERVAL_TIME_IN_MILLIS);
  }());

  function test() {
	  
      var modalesectiondata = null;
      $(function(){
          // DataTableの設定を変更する
          var modalesectiontable = $("#ranktable").DataTable({
        	  "bDestroy": true,
              "bPaginate": false,
              "bLengthChange": false,
              "bFilter": false,
              "bSort": false,
              "bInfo": false,
              "bAutoWidth": false,
              "language": {
                      "url": 'https://cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Japanese.json'
              },
              "ajax": { url: 'scoreload', dataSrc: '' },
              "columns": [
                  { data: "blank" },
                  { data: "rank" },
                  { data: "username" },
                  { data: "point" },
                  { data: "inputtimeMin" },
                  { data: "inputtimeSec" },
                  { data: "misstype" },
                  { data: "blank" },
              ],
      		"columnDefs": [
    			// Make the width of the second column (which is 1 because it
				// starts from 0) to 100 px
    			// { targets: 1, width: 4 },
    		],
// "columnDefs": [
// { targets: 0, width: 20 },
// { targets: 1, width: 10 },
// { targets: 6, width: 20 },
// {targets:'_all',className : 'dt-head-center'},
// ],
              "createdRow": function(row, data, dataIndex) {
            	  $(row).find("td").eq(0).addClass('iconcell');
            	  $(row).find("td").eq(7).addClass('iconcell');
            	  if(dataIndex == 0) {
                	  $(row).find("td").eq(1).addClass('numalign borderTopNone rank');
// $(row).find("td").eq(2).addClass('stralign borderTopNone
// rank').text(escapeHtml($(row).find("td").eq(2).children().prop("outerHTML")));
                	  $(row).find("td").eq(2).addClass('stralign borderTopNone rank').text(escapeHtml($(row).find("td").eq(2).prop("innerHTML")));
                	  $(row).find("td").eq(3).addClass('numalign borderTopNone score');
                	  $(row).find("td").eq(4).addClass('numalign borderTopNone score-time-min');
                	  $(row).find("td").eq(5).addClass('numalign borderTopNone score-time-sec');
                	  $(row).find("td").eq(6).addClass('numalign borderTopNone score-miss');
            	  } else {
                	  $(row).find("td").eq(1).addClass('numalign rank');
                	  $(row).find("td").eq(2).addClass('stralign rank');
                	  $(row).find("td").eq(3).addClass('numalign score');
                	  $(row).find("td").eq(4).addClass('numalign score-time-min');
                	  $(row).find("td").eq(5).addClass('numalign score-time-sec');
                	  $(row).find("td").eq(6).addClass('numalign score-miss');
            	  }
              }
          });
      })
  }

  function getCountUp() {
    $.ajax({
    type : "GET",
    url : "scoreload",
    content : "application/json",
    dataType : "json",
  }).done(function(list) {
	  
/*
 * <tr th:each="score, scoreStat : ${scores}"> <td></td>
 * <td th:text="${scoreStat.index} + 1 + '/' + ${scoreStat.size} + '位'"></td>
 * <td th:text="${score.point}">0</td> <td th:text="${score.inputtime}">0</td>
 * <td th:text="${score.misstype}">0</td> <td th:text="${score.username}">user</td>
 * <td></td> </tr>
 */
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
// SQLインジェクション対策として文字列をHTMLエスケープする
function escapeHtml(str){
	  console.log(str);
	  str = str.replace('&/g', '&amp;');
	  str = str.replace('>/g', '&gt;');
	  str = str.replace('</g', '&lt;');
	  str = str.replace('"/g', '&quot;');
	  str = str.replace('\'/g', '&#x27;');
	  str = str.replace('`/g', '&#x60;');
	  return str;
}