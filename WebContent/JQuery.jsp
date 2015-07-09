<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>struts2</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <script type="text/javascript" src="js/jquery-1.9.1.js"></script>
    <script type="text/javascript">
    //数据源，json的格式.
var user=[{"id": 1, "name": "张三","age":"25"},
          {"id": 2, "name": "李四","age":"35"},
          {"id": 3, "name": "王五","age":"20"},
          {"id": 4, "name": "老王","age":"20"},
          {"id": 5, "name": "老张","age":"25"},
          {"id": 6, "name": "李四","age":"35"},
          {"id": 7, "name": "王五","age":"20"},
          {"id": 8, "name": "老王","age":"20"},
          {"id": 9, "name": "abc","age":"25"},
          {"id": 10, "name": "李b四","age":"35"},
          {"id": 11, "name": "125","age":"20"},
          {"id": 12, "name": "246","age":"20"},
          {"id": 13, "name": "张三","age":"25"},
          {"id": 14, "name": "李四","age":"35"},
          {"id": 15, "name": "王五","age":"20"},
          {"id": 16, "name": "老王","age":"20"},
          {"id": 17, "name": "张三","age":"25"},
          {"id": 18, "name": "李四","age":"35"},
          {"id": 19, "name": "王五","age":"20"},
          {"id": 20, "name": "老王","age":"20"}];
$(document).ready(function () {
	var seach = $("#seach");
	seach.keyup(function (event) {
		//var keyEvent=event || window.event;
		//var keyCode=keyEvent.keyCode;
		// 数字键：48-57
		// 字母键：65-90
		// 删除键：8
		// 后删除键：46
		// 退格键：32
		// enter键：13
		//if((keyCode>=48&&keyCode<=57)||(keyCode>=65&&keyCode<=90)||keyCode==8||keyCode==13||keyCode==32||keyCode==46){
		//获取当前文本框的值
		var seachText = $("#seach").val();
		if (seachText != "") {
			//构造显示页面
			var tab = "<table width='300' border='1' cellpadding='0' cellspacing='0'><tr align='center'><td>编号</td><td>名称</td><td>年龄</td></tr>";
			//遍历解析json
			$.each(user, function (id, item) {
				//如果包含则为table赋值
				if (item.name.indexOf(seachText) != -1) {
					tab += "<tr align='center'><td>" + item.id + "</td><td>" + item.name + "</td><td>" + item.age + "</td></tr>";
				}
			})
			tab += "</table>";
			$("#div").html(tab);
			//重新覆盖掉，不然会追加
			tab = "<table width='300' border='1' cellpadding='0' cellspacing='0'><tr align='center'><td>编号</td><td>名称</td><td>年龄</td></tr>";
		} else {
			$("#div").html("");
		}
		// }
	})
});

    </script>
  </head>
  
  <body>
      名字：<input id="seach" />
      <br/><br/>
      <div id="div"></div>
  </body>
</html>