<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>itxxz lucene web demo</title>
<style type="text/css">
table.gridtable {
	font-family: verdana,arial,sans-serif;
	font-size:11px;
	color:#333333;
	border-width: 1px;
	border-color: #666666;
	border-collapse: collapse;
}
table.gridtable th {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #666666;
	background-color: #dedede;
}
table.gridtable td {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #666666;
	background-color: #ffffff;
}
</style>
		<script type="text/javascript">
			function changePage(value){
				document.getElementById("changepage").value=value;
				document.getElementByIdx_x('itxxzFrom').submit();
			}
		</script>
	</head>
	<body>
	<center>
	<h2><font color="blue">IT学习者 Lucene 4.10.2 实例Demo</font></h2>
		<form method="POST" action="itxxzFrom" id="itxxzFrom">
			<input id="changepage" name="changepage" type="hidden">
			<input id="startparam" name="startparam" type="hidden" value="${startparam }">
			<button onclick="changePage('p')">上一页</button>
			<button onclick="changePage('n')">下一页</button>
			<input id="searchkey" name="searchkey" type="text" value="${searchkey }">
			<button onclick="search()">搜索</button><br/><br/>
			
			<table class="gridtable">
				<tr>
					<th>序号</th>
					<th>标题</th>
					<th>路径</th>
					<th>描述</th>
				</tr>
				<c:forEach items="${dataList}" var="info" varStatus="status">
					<tr>
						<td>${info.id}</td>
						<td>${info.title}</td>
						<td>${info.path}</td>
						<td width="550px">${info.description}</td>
					</tr>
				</c:forEach>
			</table>
		</form>
	</center>
	</body>
</html>
